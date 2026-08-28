package com.novalang.workspace;

import com.novalang.runtime.Function0;
import com.novalang.runtime.NovaScheduler;
import com.novalang.runtime.SchedulerHolder;
import com.novalang.runtime.interpreter.Interpreter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link RuntimeWorkspace} 完整加载、执行、回调和销毁重建测试。
 */
@DisplayName("Runtime Workspace")
class RuntimeWorkspaceTest {

    @TempDir
    Path tempDirectory;

    @BeforeEach
    void installScheduler() {
        SchedulerHolder.set(WorkspaceTestSupport.directScheduler());
    }

    @AfterEach
    void clearScheduler() {
        Interpreter.resetGlobalSchedulerState();
    }

    @Test
    @DisplayName("加载跨目录模块并调用导出函数")
    void shouldLoadModuleGraphAndInvokeFunction() throws Exception {
        WorkspaceTestSupport.write(tempDirectory, "lib/math.nova",
                "fun double(value: Int): Int = value * 2");
        WorkspaceTestSupport.write(tempDirectory, "main.nova",
                "import \"@/lib/math\"\nfun calculate(value: Int): Int = double(value)");
        Path config = WorkspaceTestSupport.writeConfig(tempDirectory, "caller", "  - \"main\"\n");
        RuntimeWorkspace workspace = new RuntimeWorkspace(config, nova -> { });
        try {
            workspace.load();

            Object result = workspace.invoke("main", "calculate",
                    Collections.<String, Object>emptyMap(), null, 21);

            assertEquals(42, ((Number) result).intValue());
            assertEquals(WorkspaceState.ACTIVE, workspace.getState());
            assertEquals(2, workspace.getGeneration().getModuleGraph().getModules().size());
        } finally {
            workspace.dispose();
        }
    }

    @Test
    @DisplayName("编译前使用 WorkspaceHost 的定义类加载器")
    void shouldInstallWorkspaceHostClassLoaderBeforeHostBindings() throws Exception {
        WorkspaceTestSupport.write(tempDirectory, "main.nova", "fun value(): Int = 1");
        Path config = WorkspaceTestSupport.writeConfig(tempDirectory, "caller", "  - \"main\"\n");
        ClassLoader expected = RuntimeWorkspaceTest.class.getClassLoader();
        WorkspaceHost host = nova -> assertEquals(expected, nova.getScriptClassLoader());
        RuntimeWorkspace workspace = new RuntimeWorkspace(config, host);
        try {
            workspace.load();
            assertEquals(1, ((Number) workspace.invoke("main", "value",
                    Collections.<String, Object>emptyMap(), null)).intValue());
        } finally {
            workspace.dispose();
        }
    }

    @Test
    @DisplayName("每次调用使用隔离绑定且不回写输入 Map")
    void shouldIsolateInvocationBindings() throws Exception {
        WorkspaceTestSupport.write(tempDirectory, "main.nova",
                "fun next(): Int { requestValue = requestValue + 1; return requestValue }");
        Path config = WorkspaceTestSupport.writeConfig(tempDirectory, "parallel-safe", "  - \"main\"\n");
        RuntimeWorkspace workspace = new RuntimeWorkspace(config, nova -> nova.set("requestValue", 0));
        try {
            workspace.load();
            Map<String, Object> first = new HashMap<String, Object>();
            first.put("requestValue", 10);
            Map<String, Object> second = new HashMap<String, Object>();
            second.put("requestValue", 20);

            assertEquals(11, ((Number) workspace.invoke("main", "next", first, null)).intValue());
            assertEquals(21, ((Number) workspace.invoke("main", "next", second, null)).intValue());
            assertEquals(10, first.get("requestValue"));
            assertEquals(20, second.get("requestValue"));
        } finally {
            workspace.dispose();
        }
    }

    @Test
    @DisplayName("入口初始化期间自动登记 Generation 资源")
    void shouldRegisterResourceDuringEntryInitialization() throws Exception {
        AtomicInteger disposed = new AtomicInteger();
        WorkspaceTestSupport.write(tempDirectory, "main.nova",
                "fun main() { installResource() }");
        Path config = WorkspaceTestSupport.writeConfig(tempDirectory, "caller", "  - \"main\"\n");
        RuntimeWorkspace workspace = new RuntimeWorkspace(config, nova -> nova.defineFunction(
                "installResource", new Function0<Object>() {
                    @Override
                    public Object invoke() {
                        WorkspaceExecutionContext.requireScope().register(() -> disposed.incrementAndGet());
                        return null;
                    }
                }));

        workspace.load();
        assertEquals(1, workspace.getGeneration().getRootScope().getResourceCount());

        workspace.dispose();
        assertEquals(1, disposed.get());
    }

    @Test
    @DisplayName("稳定回调合并捕获绑定并在 Workspace 销毁后失效")
    void shouldInvalidateCallbackAfterWorkspaceDispose() throws Exception {
        WorkspaceTestSupport.write(tempDirectory, "main.nova",
                "fun calculate(): Int = captured + dynamicValue");
        Path config = WorkspaceTestSupport.writeConfig(tempDirectory, "caller", "  - \"main\"\n");
        RuntimeWorkspace workspace = new RuntimeWorkspace(config, nova -> {
            nova.set("captured", 0);
            nova.set("dynamicValue", 0);
        });
        workspace.load();
        Map<String, Object> captured = new HashMap<String, Object>();
        captured.put("captured", 3);
        NovaCallback callback = workspace.createCallback("main", "calculate", captured,
                null, ExecutionPolicy.CALLER_THREAD);
        Map<String, Object> invocation = new HashMap<String, Object>();
        invocation.put("dynamicValue", 4);

        assertEquals(7, ((Number) callback.invokeWithBindings(invocation)).intValue());
        assertTrue(callback.isValid());

        workspace.dispose();
        assertFalse(callback.isValid());
        assertThrows(WorkspaceException.class, callback::invoke);
    }

    @Test
    @DisplayName("入口初始化阶段可以创建在激活后使用的回调")
    void shouldCreateCallbackDuringEntryInitialization() throws Exception {
        AtomicReference<NovaCallback> callbackReference = new AtomicReference<NovaCallback>();
        WorkspaceTestSupport.write(tempDirectory, "main.nova",
                "fun onEvent(): Int = 9\nfun main() { captureCallback() }");
        Path config = WorkspaceTestSupport.writeConfig(tempDirectory, "caller", "  - \"main\"\n");
        RuntimeWorkspace workspace = new RuntimeWorkspace(config, nova -> nova.defineFunction(
                "captureCallback", new Function0<Object>() {
                    @Override
                    public Object invoke() {
                        WorkspaceGeneration generation = WorkspaceExecutionContext.currentGeneration();
                        NovaCallback callback = generation.createCallback("main", "onEvent",
                                Collections.<String, Object>emptyMap(),
                                WorkspaceExecutionContext.requireScope(), ExecutionPolicy.CALLER_THREAD);
                        callbackReference.set(callback);
                        return null;
                    }
                }));
        try {
            workspace.load();

            NovaCallback callback = callbackReference.get();
            assertNotNull(callback);
            assertTrue(callback.isValid());
            assertEquals(9, ((Number) callback.invoke()).intValue());
        } finally {
            workspace.dispose();
        }
    }

    @Test
    @DisplayName("上层通过销毁旧实例并创建新实例完成重载")
    void shouldRebuildWithANewWorkspaceInstance() throws Exception {
        Path script = WorkspaceTestSupport.write(tempDirectory, "main.nova",
                "fun version(): Int = 1");
        Path config = WorkspaceTestSupport.writeConfig(tempDirectory, "caller", "  - \"main\"\n");
        RuntimeWorkspace first = new RuntimeWorkspace(config, nova -> { });
        first.load();
        long firstGeneration = first.getGeneration().getId();
        assertEquals(1, ((Number) first.invoke("main", "version",
                Collections.<String, Object>emptyMap(), null)).intValue());
        first.dispose();

        WorkspaceTestSupport.write(tempDirectory, "main.nova", "fun version(): Int = 2");
        RuntimeWorkspace second = new RuntimeWorkspace(config, nova -> { });
        try {
            second.load();
            assertEquals(2, ((Number) second.invoke("main", "version",
                    Collections.<String, Object>emptyMap(), null)).intValue());
            assertNotEquals(firstGeneration, second.getGeneration().getId());
        } finally {
            second.dispose();
        }
        assertNotNull(script);
    }

    @Test
    @DisplayName("同一实例不能重复 load 且 API 不包含 reload")
    void shouldRemainSingleUseWithoutReloadApi() throws Exception {
        WorkspaceTestSupport.write(tempDirectory, "main.nova", "fun value(): Int = 1");
        Path config = WorkspaceTestSupport.writeConfig(tempDirectory, "caller", "  - \"main\"\n");
        RuntimeWorkspace workspace = new RuntimeWorkspace(config, nova -> { });
        try {
            workspace.load();

            assertThrows(WorkspaceException.class, workspace::load);
            assertThrows(NoSuchMethodException.class,
                    () -> RuntimeWorkspace.class.getDeclaredMethod("reload"));
            assertThrows(IllegalArgumentException.class,
                    () -> WorkspaceState.valueOf("RELOADING"));
        } finally {
            workspace.dispose();
        }
    }

    @Test
    @DisplayName("编译失败进入 FAILED 且实例不可重试")
    void shouldFailPermanentlyAfterCompilationError() throws Exception {
        WorkspaceTestSupport.write(tempDirectory, "main.nova", "fun broken( =");
        Path config = WorkspaceTestSupport.writeConfig(tempDirectory, "caller", "  - \"main\"\n");
        RuntimeWorkspace workspace = new RuntimeWorkspace(config, nova -> { });
        try {
            assertThrows(WorkspaceException.class, workspace::load);
            assertEquals(WorkspaceState.FAILED, workspace.getState());
            assertThrows(WorkspaceException.class, workspace::load);
        } finally {
            workspace.dispose();
        }
        assertEquals(WorkspaceState.DISPOSED, workspace.getState());
    }

    @Test
    @DisplayName("依赖模块编译错误映射到原始文件行")
    void shouldMapDependencyCompilationFailureToOriginalFile() throws Exception {
        Path dependency = WorkspaceTestSupport.write(tempDirectory, "lib/broken.nova",
                "fun valid(): Int = 1\nfun broken( =");
        WorkspaceTestSupport.write(tempDirectory, "main.nova",
                "import \"@/lib/broken\"\nfun value(): Int = valid()");
        Path config = WorkspaceTestSupport.writeConfig(tempDirectory, "caller", "  - \"main\"\n");
        RuntimeWorkspace workspace = new RuntimeWorkspace(config, nova -> { });
        try {
            WorkspaceException exception = assertThrows(WorkspaceException.class, workspace::load);

            String expectedLocation = dependency.toRealPath() + ":2";
            assertTrue(exception.getMessage().contains(expectedLocation),
                    "Expected location: " + expectedLocation + "\n" + describeFailure(exception));
        } finally {
            workspace.dispose();
        }
    }

    @Test
    @DisplayName("虚拟源码编译错误映射到 YAML key 和原始行")
    void shouldMapVirtualCompilationFailureToYamlOrigin() throws Exception {
        WorkspaceTestSupport.write(tempDirectory, "placeholder.nova", "fun placeholder(): Int = 0");
        Path config = WorkspaceTestSupport.writeConfig(
                tempDirectory, "caller", "  - \"placeholder\"\n");
        Path yaml = WorkspaceTestSupport.write(tempDirectory, "skills.yml", "skills: {}");
        SourceUnit generated = new SourceUnit("@generated/broken",
                "// generated wrapper\n// generated function\nfun broken( =",
                yaml, "skills.broken.action", 40, 2, null);
        RuntimeWorkspace workspace = new RuntimeWorkspace(config, nova -> { });
        workspace.registerVirtualSource(generated, true);
        try {
            WorkspaceException exception = assertThrows(WorkspaceException.class, workspace::load);

            assertTrue(exception.getMessage().contains(
                    yaml.toAbsolutePath().normalize() + " [skills.broken.action]:40"),
                    describeFailure(exception));
        } finally {
            workspace.dispose();
        }
    }

    @Test
    @DisplayName("未安装全局调度器时加载直接失败")
    void shouldRequireGlobalScheduler() throws Exception {
        WorkspaceTestSupport.write(tempDirectory, "main.nova", "fun value(): Int = 1");
        Path config = WorkspaceTestSupport.writeConfig(tempDirectory, "caller", "  - \"main\"\n");
        SchedulerHolder.clear();
        RuntimeWorkspace workspace = new RuntimeWorkspace(config, nova -> { });
        try {
            WorkspaceException exception = assertThrows(WorkspaceException.class, workspace::load);
            assertEquals("The global NovaScheduler is not installed", exception.getMessage());
            assertEquals(WorkspaceState.FAILED, workspace.getState());
        } finally {
            workspace.dispose();
        }
    }

    @Test
    @DisplayName("调度器缺少异步执行器时加载直接失败")
    void shouldRejectSchedulerWithoutAsyncExecutor() throws Exception {
        WorkspaceTestSupport.write(tempDirectory, "main.nova", "fun value(): Int = 1");
        Path config = WorkspaceTestSupport.writeConfig(tempDirectory, "caller", "  - \"main\"\n");
        SchedulerHolder.clear();
        SchedulerHolder.set(new NovaScheduler() {
            @Override
            public Executor mainExecutor() {
                return Runnable::run;
            }

            @Override
            public boolean isMainThread() {
                return true;
            }

            @Override
            public Cancellable scheduleLater(long delayMs, Runnable task) {
                throw new UnsupportedOperationException("Not used");
            }

            @Override
            public Cancellable scheduleRepeat(long delayMs, long periodMs, Runnable task) {
                throw new UnsupportedOperationException("Not used");
            }
        });
        RuntimeWorkspace workspace = new RuntimeWorkspace(config, nova -> { });
        try {
            WorkspaceException exception = assertThrows(WorkspaceException.class, workspace::load);
            assertEquals("The global NovaScheduler has no async executor", exception.getMessage());
        } finally {
            workspace.dispose();
        }
    }

    @Test
    @DisplayName("拒绝使用其他 Workspace 的 ResourceScope")
    void shouldRejectScopeFromAnotherWorkspace() throws Exception {
        WorkspaceTestSupport.write(tempDirectory, "main.nova", "fun value(): Int = 1");
        Path config = WorkspaceTestSupport.writeConfig(tempDirectory, "caller", "  - \"main\"\n");
        RuntimeWorkspace first = new RuntimeWorkspace(config, nova -> { });
        RuntimeWorkspace second = new RuntimeWorkspace(config, nova -> { });
        try {
            first.load();
            second.load();
            ResourceScope foreign = first.openScope(null, ScopeType.INVOCATION, "foreign");

            WorkspaceException exception = assertThrows(WorkspaceException.class,
                    () -> second.invoke("main", "value",
                            Collections.<String, Object>emptyMap(), foreign));

            assertTrue(exception.getMessage().startsWith(
                    "ResourceScope belongs to a different Workspace Generation"));
        } finally {
            first.dispose();
            second.dispose();
        }
    }

    @Test
    @DisplayName("SERIAL_SCOPE 保证同一业务作用域内调用串行")
    void shouldSerializeCallsWithinBusinessScope() throws Exception {
        AtomicInteger active = new AtomicInteger();
        AtomicInteger maximum = new AtomicInteger();
        WorkspaceTestSupport.write(tempDirectory, "main.nova", "fun execute(): Int = enter()");
        Path config = WorkspaceTestSupport.writeConfig(tempDirectory, "parallel-safe", "  - \"main\"\n");
        RuntimeWorkspace workspace = new RuntimeWorkspace(config, nova -> nova.defineFunction(
                "enter", new Function0<Object>() {
                    @Override
                    public Object invoke() {
                        int current = active.incrementAndGet();
                        maximum.accumulateAndGet(current, Math::max);
                        try {
                            Thread.sleep(5L);
                            return current;
                        } catch (InterruptedException exception) {
                            Thread.currentThread().interrupt();
                            throw new IllegalStateException(exception);
                        } finally {
                            active.decrementAndGet();
                        }
                    }
                }));
        ExecutorService executor = Executors.newFixedThreadPool(6);
        try {
            workspace.load();
            ResourceScope business = workspace.openScope(
                    null, ScopeType.BUSINESS_INSTANCE, "business");
            List<Future<Object>> futures = new ArrayList<Future<Object>>();
            for (int index = 0; index < 30; index++) {
                futures.add(executor.submit(() -> workspace.invoke("main", "execute",
                        Collections.<String, Object>emptyMap(), business,
                        ExecutionPolicy.SERIAL_SCOPE)));
            }
            for (Future<Object> future : futures) {
                assertEquals(1, ((Number) future.get()).intValue());
            }
            assertEquals(1, maximum.get());
        } finally {
            executor.shutdownNow();
            workspace.dispose();
        }
    }

    @Test
    @DisplayName("dispose 等待正在执行的同步调用退出")
    void shouldWaitForActiveInvocationBeforeDispose() throws Exception {
        CountDownLatch entered = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        WorkspaceTestSupport.write(tempDirectory, "main.nova", "fun execute(): Int = blockCall()");
        Path config = WorkspaceTestSupport.writeConfig(tempDirectory, "parallel-safe", "  - \"main\"\n");
        RuntimeWorkspace workspace = new RuntimeWorkspace(config, nova -> nova.defineFunction(
                "blockCall", new Function0<Object>() {
                    @Override
                    public Object invoke() {
                        entered.countDown();
                        try {
                            if (!release.await(5, TimeUnit.SECONDS)) {
                                throw new IllegalStateException("Timed out waiting for release");
                            }
                            return 1;
                        } catch (InterruptedException exception) {
                            Thread.currentThread().interrupt();
                            throw new IllegalStateException(exception);
                        }
                    }
                }));
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            workspace.load();
            Future<Object> invocation = executor.submit(() -> workspace.invoke(
                    "main", "execute", Collections.<String, Object>emptyMap(), null));
            assertTrue(entered.await(5, TimeUnit.SECONDS));
            Future<?> disposal = executor.submit(workspace::dispose);
            Thread.sleep(100L);
            assertFalse(disposal.isDone());

            release.countDown();
            assertEquals(1, ((Number) invocation.get(5, TimeUnit.SECONDS)).intValue());
            disposal.get(5, TimeUnit.SECONDS);
            assertEquals(WorkspaceState.DISPOSED, workspace.getState());
        } finally {
            release.countDown();
            executor.shutdownNow();
            workspace.dispose();
        }
    }

    @Test
    @DisplayName("虚拟 SourceUnit 可以作为独立入口编译执行")
    void shouldCompileVirtualEntry() throws Exception {
        WorkspaceTestSupport.write(tempDirectory, "placeholder.nova", "fun placeholder(): Int = 0");
        Path config = WorkspaceTestSupport.writeConfig(
                tempDirectory, "caller", "  - \"placeholder\"\n");
        Path yaml = WorkspaceTestSupport.write(tempDirectory, "skills.yml", "skills: {}");
        SourceUnit generated = new SourceUnit("@generated/fire",
                "fun damage(): Int = 9", yaml, "skills.fire.damage", 12, 0, null);
        RuntimeWorkspace workspace = new RuntimeWorkspace(config, nova -> { });
        workspace.registerVirtualSource(generated, true);
        try {
            workspace.load();

            assertEquals(9, ((Number) workspace.invoke("@generated/fire", "damage",
                    Collections.<String, Object>emptyMap(), null)).intValue());
        } finally {
            workspace.dispose();
        }
    }

    @Test
    @DisplayName("dispose 清除当前配置和 Generation 引用")
    void shouldClearReferencesOnDispose() throws Exception {
        WorkspaceTestSupport.write(tempDirectory, "main.nova", "fun value(): Int = 1");
        Path config = WorkspaceTestSupport.writeConfig(tempDirectory, "caller", "  - \"main\"\n");
        RuntimeWorkspace workspace = new RuntimeWorkspace(config, nova -> { });
        workspace.load();
        WorkspaceGeneration generation = workspace.getGeneration();

        workspace.dispose();

        assertEquals(GenerationState.DISPOSED, generation.getState());
        assertThrows(WorkspaceException.class, workspace::getGeneration);
        assertThrows(WorkspaceException.class, workspace::getConfig);
        assertThrows(WorkspaceException.class, generation::getModuleGraph);
        assertNull(WorkspaceExecutionContext.currentScope());
    }

    private String describeFailure(Throwable failure) {
        StringBuilder description = new StringBuilder();
        Throwable current = failure;
        while (current != null) {
            if (description.length() > 0) {
                description.append(" -> ");
            }
            description.append(current.getClass().getName())
                    .append(": ").append(current.getMessage());
            current = current.getCause();
        }
        return description.toString();
    }
}
