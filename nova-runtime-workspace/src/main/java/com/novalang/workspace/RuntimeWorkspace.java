package com.novalang.workspace;

import com.novalang.runtime.CompiledNova;
import com.novalang.runtime.Nova;
import com.novalang.runtime.NovaScheduler;
import com.novalang.runtime.SchedulerHolder;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Nova 服务端脚本的模块、编译、执行和资源生命周期边界。
 *
 * <p>每个实例只允许加载一次，不提供 {@code reload()}。上层业务需要重载时必须停止
 * 路由旧实例、调用 {@link #dispose()}，然后创建并加载全新的 RuntimeWorkspace。</p>
 */
public final class RuntimeWorkspace implements AutoCloseable {

    private final Path configFile;
    private final WorkspaceHost host;
    private final WorkspaceConfigLoader configLoader;
    private final WorkspaceModuleResolver moduleResolver;
    private final ReentrantLock lifecycleLock = new ReentrantLock(true);
    private final List<SourceUnit> virtualSources = new ArrayList<SourceUnit>();
    private final List<String> virtualEntries = new ArrayList<String>();
    private volatile WorkspaceState state = WorkspaceState.NEW;
    private volatile WorkspaceConfig config;
    private volatile WorkspaceGeneration generation;

    /**
     * 创建一次性 Runtime Workspace。
     *
     * @param configFile {@code nova.config.yml} 路径
     * @param host Host Binding 安装器
     * @throws IllegalArgumentException 任一参数为空时抛出
     */
    public RuntimeWorkspace(Path configFile, WorkspaceHost host) {
        if (configFile == null) {
            throw new IllegalArgumentException("configFile must not be null");
        }
        if (host == null) {
            throw new IllegalArgumentException("host must not be null");
        }
        this.configFile = configFile.toAbsolutePath().normalize();
        this.host = host;
        this.configLoader = new WorkspaceConfigLoader();
        this.moduleResolver = new WorkspaceModuleResolver();
    }

    /** @return 当前 Workspace 状态 */
    public WorkspaceState getState() {
        return state;
    }

    /**
     * 获取已加载配置。
     *
     * @return 当前配置
     * @throws WorkspaceException Workspace 尚未加载或已销毁时抛出
     */
    public WorkspaceConfig getConfig() {
        WorkspaceConfig current = config;
        if (current == null) {
            throw new WorkspaceException("The Workspace config is not loaded");
        }
        return current;
    }

    /**
     * 获取当前唯一 Generation。
     *
     * @return 活跃 Generation
     * @throws WorkspaceException Workspace 不处于 ACTIVE 时抛出
     */
    public WorkspaceGeneration getGeneration() {
        return requireActiveGeneration();
    }

    /**
     * 在加载前登记 YAML 等业务配置生成的虚拟源码。
     *
     * @param sourceUnit 虚拟源码单元
     * @param entry 是否将该源码作为独立编译入口
     * @return 当前 Workspace
     * @throws WorkspaceException Workspace 已经开始加载时抛出
     */
    public RuntimeWorkspace registerVirtualSource(SourceUnit sourceUnit, boolean entry) {
        if (sourceUnit == null) {
            throw new IllegalArgumentException("sourceUnit must not be null");
        }
        lifecycleLock.lock();
        try {
            requireState(WorkspaceState.NEW, "register virtual sources");
            for (SourceUnit existing : virtualSources) {
                if (existing.getModuleId().equals(sourceUnit.getModuleId())) {
                    throw new WorkspaceException("Duplicate virtual module ID: " + sourceUnit.getModuleId());
                }
            }
            virtualSources.add(sourceUnit);
            if (entry) {
                virtualEntries.add(sourceUnit.getModuleId());
            }
            return this;
        } finally {
            lifecycleLock.unlock();
        }
    }

    /**
     * 读取配置、构建完整模块图、编译入口并执行入口初始化。
     *
     * <p>加载失败后实例进入 FAILED，不能重试；调用方应销毁该实例并创建新实例。</p>
     *
     * @throws WorkspaceException 配置、调度器、模块解析、编译或入口初始化失败时抛出
     */
    public void load() {
        lifecycleLock.lock();
        try {
            requireState(WorkspaceState.NEW, "load");
            state = WorkspaceState.LOADING;
            Nova nova = null;
            WorkspaceGeneration candidate = null;
            try {
                // 第一步严格读取配置并确认进程级调度器已经由宿主安装。
                WorkspaceConfig loadedConfig = configLoader.load(configFile);
                NovaScheduler scheduler = SchedulerHolder.get();
                if (scheduler == null) {
                    throw new WorkspaceException("The global NovaScheduler is not installed");
                }
                if (scheduler.mainExecutor() == null) {
                    throw new WorkspaceException("The global NovaScheduler has no main executor");
                }
                if (scheduler.asyncExecutor() == null) {
                    throw new WorkspaceException("The global NovaScheduler has no async executor");
                }

                // 第二步在任何脚本执行前构建完整依赖图并完成路径安全校验。
                WorkspaceModuleGraph graph = moduleResolver.resolve(
                        loadedConfig, virtualSources, virtualEntries);
                // 第三步创建本实例独占的编译环境，安装稳定 Host Binding。
                nova = new Nova(loadedConfig.createSecurityPolicy());
                nova.setScheduler(scheduler);
                nova.enableCompilationCache();
                host.install(nova);

                // 注册规范模块标识，保证其他 Nova 编译入口也能复用同一解析结果。
                for (WorkspaceModule module : graph.getModules().values()) {
                    nova.registerModule(module.getSourceUnit().getModuleId(),
                            module.getTransformedSource());
                }

                // 第四步为每个入口生成依赖闭包、Source Map 和隔离字节码程序。
                Map<String, WorkspaceProgram> programs = compileEntries(nova, graph);
                candidate = new WorkspaceGeneration(loadedConfig.getName(),
                        loadedConfig.getRootDirectory(),
                        loadedConfig.getExecutionPolicy(), graph, programs, nova);
                // 第五步执行入口初始化；全部成功后才原子发布 ACTIVE Generation。
                candidate.activate();

                config = loadedConfig;
                generation = candidate;
                state = WorkspaceState.ACTIVE;
            } catch (RuntimeException exception) {
                // 失败实例不可重试；释放半成品后保留 FAILED 供上层诊断。
                state = WorkspaceState.FAILED;
                disposeFailedCandidate(candidate, nova, exception);
                throw exception;
            }
        } finally {
            lifecycleLock.unlock();
        }
    }

    /**
     * 在指定父作用域下创建业务资源作用域。
     *
     * @param parent 父作用域；传 {@code null} 时使用 Generation 根
     * @param type 子作用域类型
     * @param ownerId 宿主所有者标识
     * @return 新作用域
     * @throws WorkspaceException Workspace 不活跃或父作用域属于其他实例时抛出
     */
    public ResourceScope openScope(ResourceScope parent, ScopeType type, String ownerId) {
        return requireActiveGeneration().openScope(parent, type, ownerId);
    }

    /**
     * 使用配置默认策略调用入口函数。
     *
     * @param entryName 入口名称
     * @param functionName 函数名称
     * @param bindings 本次隔离绑定
     * @param scope 资源所有者作用域
     * @param arguments 函数参数
     * @return 函数返回值
     * @throws WorkspaceException Workspace、入口或作用域不可用时抛出
     */
    public Object invoke(String entryName,
                         String functionName,
                         Map<String, Object> bindings,
                         ResourceScope scope,
                         Object... arguments) {
        return requireActiveGeneration().invoke(entryName, functionName,
                bindings, scope, null, arguments);
    }

    /**
     * 使用明确策略调用入口函数。
     *
     * @param entryName 入口名称
     * @param functionName 函数名称
     * @param bindings 本次隔离绑定
     * @param scope 资源所有者作用域
     * @param policy 固定执行策略
     * @param arguments 函数参数
     * @return 函数返回值
     * @throws WorkspaceException Workspace、入口、作用域或执行策略不可用时抛出
     */
    public Object invoke(String entryName,
                         String functionName,
                         Map<String, Object> bindings,
                         ResourceScope scope,
                         ExecutionPolicy policy,
                         Object... arguments) {
        return requireActiveGeneration().invoke(entryName, functionName,
                bindings, scope, policy, arguments);
    }

    /**
     * 创建绑定当前 Workspace 生命周期的稳定回调。
     *
     * @param entryName 入口名称
     * @param functionName 函数名称
     * @param capturedBindings 捕获绑定
     * @param scope 资源所有者作用域
     * @param policy 固定执行策略
     * @return 稳定 Nova 回调
     * @throws WorkspaceException Workspace、入口或作用域不可用时抛出
     */
    public NovaCallback createCallback(String entryName,
                                       String functionName,
                                       Map<String, Object> capturedBindings,
                                       ResourceScope scope,
                                       ExecutionPolicy policy) {
        return requireActiveGeneration().createCallback(entryName, functionName,
                capturedBindings, scope, policy);
    }

    /**
     * 销毁整个 Workspace。方法会等待当前同步调用退出，再释放全部资源及程序引用。
     *
     * @throws WorkspaceException 某个宿主资源释放失败时抛出；其他资源仍会完成清理
     */
    public void dispose() {
        lifecycleLock.lock();
        try {
            if (state == WorkspaceState.DISPOSED) {
                return;
            }
            RuntimeException failure = null;
            WorkspaceGeneration current = generation;
            if (current != null) {
                // Generation 写锁会等待已进入的同步调用退出，再递归销毁资源树。
                try {
                    current.dispose();
                } catch (RuntimeException exception) {
                    failure = exception;
                }
            }
            generation = null;
            config = null;
            // 清除业务生成源码引用，确保旧 Workspace 不再持有 YAML 配置对象。
            virtualSources.clear();
            virtualEntries.clear();
            state = WorkspaceState.DISPOSED;
            if (failure != null) {
                throw failure;
            }
        } finally {
            lifecycleLock.unlock();
        }
    }

    /**
     * {@link AutoCloseable} 入口，等价于 {@link #dispose()}。
     */
    @Override
    public void close() {
        dispose();
    }

    /**
     * 将模块图中的每个入口编译为独立的隔离字节码程序。
     *
     * @param nova 当前 Workspace 独占的 Nova 编译门面
     * @param graph 完整模块图
     * @return 入口名称到已编译程序的映射
     */
    private Map<String, WorkspaceProgram> compileEntries(Nova nova,
                                                          WorkspaceModuleGraph graph) {
        Map<String, WorkspaceProgram> programs = new LinkedHashMap<String, WorkspaceProgram>();
        WorkspaceBundleBuilder bundleBuilder = new WorkspaceBundleBuilder();
        for (Map.Entry<String, String> entry : graph.getEntries().entrySet()) {
            WorkspaceModule module = graph.requireModule(entry.getValue());
            WorkspaceBundle bundle = bundleBuilder.build(graph, entry.getValue());
            try {
                CompiledNova compiled = nova.compileToBytecode(
                        bundle.getSource(), module.getSourceUnit().getModuleId());
                programs.put(entry.getKey(), new WorkspaceProgram(
                        entry.getKey(), entry.getValue(), compiled, bundle.getSourceMap()));
            } catch (RuntimeException exception) {
                throw bundle.getSourceMap().mapFailure(
                        "Failed to compile Workspace entry '" + entry.getKey() + "'", exception);
            }
        }
        return programs;
    }

    /**
     * 清理加载失败时尚未发布的 Generation 或编译环境。
     *
     * @param candidate 已创建的候选 Generation；可能为 {@code null}
     * @param nova 已创建的编译门面；可能为 {@code null}
     * @param original 原始加载异常，用于挂载清理失败
     */
    private void disposeFailedCandidate(WorkspaceGeneration candidate,
                                        Nova nova,
                                        RuntimeException original) {
        try {
            if (candidate != null) {
                candidate.dispose();
            } else if (nova != null) {
                nova.clearCompilationCache();
                nova.getInterpreter().cleanup();
            }
        } catch (RuntimeException cleanupFailure) {
            original.addSuppressed(cleanupFailure);
        }
    }

    /**
     * 获取当前活跃 Generation。
     *
     * @return 活跃 Generation
     * @throws WorkspaceException Workspace 不活跃时抛出
     */
    private WorkspaceGeneration requireActiveGeneration() {
        WorkspaceGeneration current = generation;
        if (state != WorkspaceState.ACTIVE || current == null) {
            throw new WorkspaceException("RuntimeWorkspace is not active: " + state);
        }
        return current;
    }

    /**
     * 校验一次性 Workspace 当前处于指定状态。
     *
     * @param expected 预期状态
     * @param operation 英文操作描述
     * @throws WorkspaceException 状态不匹配时抛出
     */
    private void requireState(WorkspaceState expected, String operation) {
        if (state != expected) {
            throw new WorkspaceException("RuntimeWorkspace cannot " + operation
                    + " from state " + state);
        }
    }
}
