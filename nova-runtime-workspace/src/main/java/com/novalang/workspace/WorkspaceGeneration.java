package com.novalang.workspace;

import com.novalang.runtime.Nova;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 一个 Workspace 实例内部唯一的已编译程序代际。
 *
 * <p>Generation 使用读写锁保证销毁会等待已经进入的同步调用退出，并在状态切换为
 * DISPOSING 后拒绝所有新调用。Workspace 不会在该对象中原地重载。</p>
 */
public final class WorkspaceGeneration implements AutoCloseable {

    private static final AtomicLong NEXT_ID = new AtomicLong(1L);

    private final long id;
    private final String workspaceName;
    private final Path rootDirectory;
    private final ExecutionPolicy defaultPolicy;
    private final ResourceScope rootScope;
    private final ReentrantReadWriteLock lifecycleLock = new ReentrantReadWriteLock(true);
    private final Map<String, WorkspaceProgram> programs;
    private final List<WorkspaceProgram> initializers;
    private volatile GenerationState state = GenerationState.LOADING;
    private volatile WorkspaceModuleGraph moduleGraph;
    private Nova nova;

    /**
     * 创建尚未激活的代际。
     *
     * @param workspaceName Workspace 名称
     * @param rootDirectory Workspace 配置根目录
     * @param defaultPolicy 默认执行策略
     * @param moduleGraph 已解析模块图
     * @param programs 已编译入口程序
     * @param nova 当前代际独占的 Nova 编译门面
     */
    WorkspaceGeneration(String workspaceName,
                        Path rootDirectory,
                        ExecutionPolicy defaultPolicy,
                        WorkspaceModuleGraph moduleGraph,
                        Map<String, WorkspaceProgram> programs,
                        Nova nova) {
        this(workspaceName, rootDirectory, defaultPolicy, moduleGraph,
                programs, new ArrayList<WorkspaceProgram>(programs.values()), nova);
    }

    WorkspaceGeneration(String workspaceName,
                        Path rootDirectory,
                        ExecutionPolicy defaultPolicy,
                        WorkspaceModuleGraph moduleGraph,
                        Map<String, WorkspaceProgram> programs,
                        List<WorkspaceProgram> initializers,
                        Nova nova) {
        this.id = NEXT_ID.getAndIncrement();
        this.workspaceName = workspaceName;
        this.rootDirectory = rootDirectory.toAbsolutePath().normalize();
        this.defaultPolicy = defaultPolicy;
        this.moduleGraph = moduleGraph;
        this.programs = new LinkedHashMap<String, WorkspaceProgram>(programs);
        this.initializers = new ArrayList<WorkspaceProgram>(initializers);
        this.nova = nova;
        this.rootScope = ResourceScope.generation(workspaceName + "#" + id);
    }

    /** @return 进程内单调递增代际标识 */
    public long getId() {
        return id;
    }

    /** @return Workspace 名称 */
    public String getWorkspaceName() {
        return workspaceName;
    }

    /** @return Workspace 配置文件所在的绝对规范目录 */
    public Path getRootDirectory() {
        return rootDirectory;
    }

    /** @return 当前生命周期状态 */
    public GenerationState getState() {
        return state;
    }

    /** @return Generation 根资源作用域 */
    public ResourceScope getRootScope() {
        return rootScope;
    }

    /**
     * 获取当前模块图。
     *
     * @return 不可变模块图
     * @throws WorkspaceException Generation 已销毁时抛出
     */
    public WorkspaceModuleGraph getModuleGraph() {
        WorkspaceModuleGraph graph = moduleGraph;
        if (graph == null) {
            throw new WorkspaceException("The Workspace module graph has been disposed");
        }
        return graph;
    }

    /** @return 不可变入口名称集合 */
    public Set<String> getEntryNames() {
        lifecycleLock.readLock().lock();
        try {
            return Collections.unmodifiableSet(new LinkedHashSet<String>(programs.keySet()));
        } finally {
            lifecycleLock.readLock().unlock();
        }
    }

    /**
     * 执行全部入口的 main 初始化并激活代际。
     *
     * @throws WorkspaceException Generation 状态非法或入口初始化失败时抛出
     */
    void activate() {
        if (state != GenerationState.LOADING) {
            throw new WorkspaceException("Workspace Generation cannot be activated from state " + state);
        }
        for (final WorkspaceProgram program : initializers) {
            // 初始化也安装根 ResourceScope，确保脚本注册的常驻资源可统一销毁。
            Callable<Object> action = new Callable<Object>() {
                @Override
                public Object call() {
                    WorkspaceExecutionContext.ContextHandle handle = WorkspaceExecutionContext.install(
                            WorkspaceGeneration.this, rootScope, Collections.<String, Object>emptyMap());
                    try {
                        return program.run(Collections.<String, Object>emptyMap());
                    } finally {
                        handle.close();
                    }
                }
            };
            WorkspaceExecutionDispatcher.execute(defaultPolicy, rootScope, action);
        }
        state = GenerationState.ACTIVE;
    }

    /**
     * 在指定父作用域下创建业务子作用域。
     *
     * @param parent 父作用域；传 {@code null} 时使用 Generation 根
     * @param type 子作用域类型
     * @param ownerId 宿主所有者标识
     * @return 新的资源作用域
     * @throws WorkspaceException Generation 或父作用域不可用时抛出
     */
    public ResourceScope openScope(ResourceScope parent, ScopeType type, String ownerId) {
        lifecycleLock.readLock().lock();
        try {
            requireActive();
            ResourceScope actualParent = parent == null ? rootScope : parent;
            requireOwnedScope(actualParent);
            return actualParent.openChild(type, ownerId);
        } finally {
            lifecycleLock.readLock().unlock();
        }
    }

    /**
     * 调用入口程序导出的函数。
     *
     * @param entryName 配置或虚拟入口名称
     * @param functionName 函数名称
     * @param bindings 本次隔离绑定
     * @param scope 资源归属作用域；传 {@code null} 时使用 Generation 根
     * @param policy 执行策略；传 {@code null} 时使用配置默认策略
     * @param arguments 函数参数
     * @return 函数返回值
     * @throws WorkspaceException Generation、入口或作用域不可用时抛出
     */
    public Object invoke(final String entryName,
                         final String functionName,
                         final Map<String, Object> bindings,
                         ResourceScope scope,
                         ExecutionPolicy policy,
                         final Object... arguments) {
        lifecycleLock.readLock().lock();
        try {
            requireActive();
            final WorkspaceProgram program = requireProgram(entryName);
            final ResourceScope actualScope = scope == null ? rootScope : scope;
            requireOwnedScope(actualScope);
            final Map<String, Object> actualBindings = bindings == null
                    ? Collections.<String, Object>emptyMap()
                    : new LinkedHashMap<String, Object>(bindings);
            ExecutionPolicy actualPolicy = policy == null ? defaultPolicy : policy;

            // 读锁覆盖调度等待和脚本执行，dispose 必须等待本次同步调用完整退出。
            Callable<Object> action = new Callable<Object>() {
                @Override
                public Object call() {
                    requireActive();
                    WorkspaceExecutionContext.ContextHandle handle = WorkspaceExecutionContext.install(
                            WorkspaceGeneration.this, actualScope, actualBindings);
                    try {
                        return program.call(functionName, actualBindings, arguments);
                    } finally {
                        handle.close();
                    }
                }
            };
            return WorkspaceExecutionDispatcher.execute(actualPolicy, actualScope, action);
        } finally {
            lifecycleLock.readLock().unlock();
        }
    }

    /**
     * 创建可跨越初始函数返回时点的稳定回调。
     *
     * @param entryName 入口名称
     * @param functionName 函数名称
     * @param capturedBindings 捕获绑定
     * @param scope 回调资源所有者
     * @param policy 固定执行策略
     * @return 稳定 Nova 回调
     * @throws WorkspaceException Generation、入口或作用域不可用时抛出
     */
    public NovaCallback createCallback(String entryName,
                                       String functionName,
                                       Map<String, Object> capturedBindings,
                                       ResourceScope scope,
                                       ExecutionPolicy policy) {
        lifecycleLock.readLock().lock();
        try {
            requireCallbackCreationState();
            requireProgram(entryName);
            ResourceScope actualScope = scope == null ? rootScope : scope;
            requireOwnedScope(actualScope);
            ExecutionPolicy actualPolicy = policy == null ? defaultPolicy : policy;
            return new NovaCallback(this, entryName, functionName,
                    capturedBindings, actualScope, actualPolicy);
        } finally {
            lifecycleLock.readLock().unlock();
        }
    }

    /**
     * 在当前代际的默认执行策略和资源上下文中执行原生调度回调。
     *
     * @param scope 任务归属作用域
     * @param bindings 任务创建时捕获的绑定快照
     * @param callback 原生调度回调
     */
    void executeScheduledCallback(ResourceScope scope,
                                  Map<String, Object> bindings,
                                  Runnable callback) {
        lifecycleLock.readLock().lock();
        try {
            requireActive();
            requireOwnedScope(scope);
            Callable<Void> action = new Callable<Void>() {
                @Override
                public Void call() {
                    WorkspaceExecutionContext.ContextHandle handle = WorkspaceExecutionContext.install(
                            WorkspaceGeneration.this, scope, bindings);
                    try {
                        callback.run();
                        return null;
                    } finally {
                        handle.close();
                    }
                }
            };
            WorkspaceExecutionDispatcher.execute(defaultPolicy, scope, action);
        } finally {
            lifecycleLock.readLock().unlock();
        }
    }

    /**
     * 创建保存显式接口实例的稳定回调。
     */
    WorkspaceDirectCallback createDirectCallback(Map<String, Object> capturedBindings,
                                                 ResourceScope scope,
                                                 WorkspaceEventCallback callback) {
        lifecycleLock.readLock().lock();
        try {
            requireCallbackCreationState();
            ResourceScope actualScope = scope == null ? rootScope : scope;
            requireOwnedScope(actualScope);
            return new WorkspaceDirectCallback(this, actualScope, capturedBindings,
                    defaultPolicy, callback);
        } finally {
            lifecycleLock.readLock().unlock();
        }
    }

    /**
     * 在捕获的 Workspace 上下文中执行显式接口回调。
     */
    Object invokeDirectCallback(ResourceScope scope,
                                Map<String, Object> bindings,
                                ExecutionPolicy policy,
                                final WorkspaceEventCallback callback,
                                final Object value) {
        lifecycleLock.readLock().lock();
        try {
            requireActive();
            requireOwnedScope(scope);
            if (callback == null) {
                throw new IllegalArgumentException("callback must not be null");
            }
            final Map<String, Object> actualBindings = bindings == null
                    ? Collections.<String, Object>emptyMap()
                    : new LinkedHashMap<String, Object>(bindings);
            final ExecutionPolicy actualPolicy = policy == null ? defaultPolicy : policy;
            Callable<Object> action = new Callable<Object>() {
                @Override
                public Object call() {
                    requireActive();
                    WorkspaceExecutionContext.ContextHandle handle = WorkspaceExecutionContext.install(
                            WorkspaceGeneration.this, scope, actualBindings);
                    try {
                        return callback.invoke(value);
                    } finally {
                        handle.close();
                    }
                }
            };
            return WorkspaceExecutionDispatcher.execute(actualPolicy, scope, action);
        } finally {
            lifecycleLock.readLock().unlock();
        }
    }

    /**
     * 判断当前代际是否仍可接受调度回调。
     *
     * @return 代际处于 ACTIVE 状态时返回 {@code true}
     */
    boolean isActive() {
        return state == GenerationState.ACTIVE;
    }

    /**
     * 销毁代际，等待正在执行的同步调用退出并释放全部资源及程序引用。
     *
     * @throws WorkspaceException 某个宿主资源释放失败时抛出
     */
    public void dispose() {
        lifecycleLock.writeLock().lock();
        try {
            if (state == GenerationState.DISPOSED || state == GenerationState.DISPOSING) {
                return;
            }
            state = GenerationState.DISPOSING;
            RuntimeException failure = null;
            try {
                rootScope.dispose();
            } catch (RuntimeException exception) {
                failure = exception;
            }

            // 即使资源清理失败也必须断开程序、模块图和编译器引用。
            programs.clear();
            initializers.clear();
            moduleGraph = null;
            if (nova != null) {
                nova.clearCompilationCache();
                nova.getInterpreter().cleanup();
                nova = null;
            }
            state = GenerationState.DISPOSED;

            if (failure != null) {
                throw failure;
            }
        } finally {
            lifecycleLock.writeLock().unlock();
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
     * 获取指定入口的已编译程序。
     *
     * @param entryName 入口名称
     * @return 已编译程序
     * @throws WorkspaceException 入口不存在时抛出
     */
    private WorkspaceProgram requireProgram(String entryName) {
        WorkspaceProgram program = programs.get(entryName);
        if (program == null) {
            throw new WorkspaceException("Workspace entry does not exist: " + entryName);
        }
        return program;
    }

    /**
     * 校验作用域属于当前 Generation 且仍然活跃。
     *
     * @param scope 待校验作用域
     * @throws WorkspaceException 作用域跨代际或已销毁时抛出
     */
    private void requireOwnedScope(ResourceScope scope) {
        if (!scope.belongsTo(rootScope)) {
            throw new WorkspaceException("ResourceScope belongs to a different Workspace Generation: "
                    + scope.getOwnerId());
        }
        if (scope.getState() != ResourceScopeState.ACTIVE) {
            throw new WorkspaceException("ResourceScope is not active: " + scope.getOwnerId());
        }
    }

    /**
     * 校验 Generation 已完成初始化并处于活跃状态。
     *
     * @throws WorkspaceException Generation 不活跃时抛出
     */
    private void requireActive() {
        if (state != GenerationState.ACTIVE) {
            throw new WorkspaceException("Workspace Generation is not active: " + state);
        }
    }

    /**
     * 校验当前状态允许入口初始化或业务调用创建稳定回调。
     *
     * @throws WorkspaceException Generation 正在销毁或已销毁时抛出
     */
    private void requireCallbackCreationState() {
        if (state != GenerationState.LOADING && state != GenerationState.ACTIVE) {
            throw new WorkspaceException("Workspace Generation cannot create callbacks from state " + state);
        }
    }
}
