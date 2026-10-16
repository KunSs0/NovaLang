package com.novalang.runtime.compilation;

import com.novalang.runtime.CompiledNova;
import com.novalang.runtime.Function0;
import com.novalang.runtime.Nova;
import com.novalang.runtime.SchedulerHolder;
import com.novalang.runtime.ScriptCallback;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

/** 验证编译元数据可以完全脱离配置文件、服务端调度器及额外运行容器。 */
class CompilerMetadataTest {
    @TempDir
    Path root;

    /** 验证纯内存配置能通过 Nova 编译，且编译过程不执行初始化。 */
    @Test
    void compilesWithoutConfigFileOrSchedulerAndInitializesExplicitly() {
        SchedulerHolder.clear();
        AtomicInteger initialized = new AtomicInteger();
        Nova nova = new Nova();
        nova.defineFunction("initialized", (Function0<Object>) () -> initialized.incrementAndGet());
        CompilationConfig.Builder builder = new CompilationConfig.Builder();
        SourceUnit entry = SourceUnit.virtual("entry", "fun main() { initialized() }\nfun value() = 7",
                null, null, 1, 0);
        try (CompiledNova program = nova.compileToBytecode(entry, builder.build())) {
            assertNull(SchedulerHolder.get());
            assertEquals(0, initialized.get());
            program.run();
            assertEquals(1, initialized.get());
            assertEquals(7, program.call("value"));
            assertEquals(1, program.getCompilationMetadata().getModuleGraph().getModules().size());
        }
    }

    /** 验证复用同一个 Nova 和字节码缓存仍然返回静态状态独立的程序。 */
    @Test
    void sameCompilerAndCacheCreateIndependentPrograms() {
        Nova nova = new Nova();
        BytecodeArtifactCache cache = new BytecodeArtifactCache();
        ClassLoader originalLoader = nova.getScriptClassLoader();
        CompilationConfig.Builder builder = new CompilationConfig.Builder();
        SourceUnit entry = SourceUnit.virtual("entry",
                "object Counter { var value = 0 }\nfun next(): Int { Counter.value = Counter.value + 1; return Counter.value }",
                null, null, 1, 0);
        CompilationConfig config = builder.build();
        try (CompiledNova first = nova.compileToBytecode(entry, config, cache);
             CompiledNova second = nova.compileToBytecode(entry, config, cache)) {
            first.run();
            second.run();
            assertEquals(1, first.call("next"));
            assertEquals(2, first.call("next"));
            assertEquals(1, second.call("next"));
            first.close();
            assertEquals(2, second.call("next"));
            assertEquals(1, cache.size());
            assertSame(originalLoader, nova.getScriptClassLoader());
        }
    }

    /** 验证单入口函数调用复用 Nova 的独立绑定，无需指定入口名。 */
    @Test
    void isolatesInvocationBindings() {
        Nova nova = new Nova();
        nova.set("request", 0);
        SourceUnit entry = SourceUnit.virtual("main", "fun value(): Int { request = request + 1; return request }",
                null, null, 1, 0);
        try (CompiledNova program = nova.compileToBytecode(entry, new CompilationConfig.Builder().build())) {
            program.run();
            assertEquals(11, program.callIsolated("value", Collections.singletonMap("request", 10)));
            assertEquals(21, program.callIsolated("value", Collections.singletonMap("request", 20)));
            assertEquals(1, program.call("value"));
            assertThrows(RuntimeException.class, () -> program.call("missing"));
        }
    }

    /** 验证关闭程序直接使回调失效并释放资源，其他程序继续有效。 */
    @Test
    void programOwnsCallbacksAndResources() {
        CompilationConfig.Builder builder = new CompilationConfig.Builder();
        SourceUnit entry = SourceUnit.virtual("entry",
                "import java com.novalang.runtime.ScriptCallbacks\nfun value() = 7\nfun bind() { return ScriptCallbacks.bind(::value) }",
                null, null, 1, 0);
        AtomicInteger closed = new AtomicInteger();
        CompiledNova program = new Nova().compileToBytecode(entry, builder.build());
        try {
            ScriptCallback callback = (ScriptCallback) program.call("bind");
            program.registerResource(closed::incrementAndGet);
            assertEquals(7, callback.invoke());
            program.close();
            program.close();
            assertEquals(1, closed.get());
            assertFalse(callback.isValid());
            assertThrows(IllegalStateException.class, callback::invoke);
            assertThrows(IllegalStateException.class, () -> program.call("value"));
        } finally {
            program.close();
        }
    }

    /** 验证初始化失败保留 YAML 源位置，关闭程序时仍释放已经创建的资源。 */
    @Test
    void mapsVirtualFailureAndReleasesPartiallyInitializedResources() {
        AtomicInteger closed = new AtomicInteger();
        Nova nova = new Nova();
        nova.defineFunction("ownResource", (Function0<Object>) () -> {
            com.novalang.runtime.ScriptCallbackContexts.require().register(closed::incrementAndGet);
            return null;
        });
        CompilationConfig.Builder builder = new CompilationConfig.Builder();
        SourceUnit entry = SourceUnit.virtual("action", "ownResource()\nerror(\"broken\")",
                root.resolve("business.yml"), "actions.open", 12, 0);
        try (CompiledNova program = nova.compileToBytecode(entry, builder.build())) {
            CompilationException failure = assertThrows(CompilationException.class, program::run);
            assertTrue(failure.getMessage().contains("business.yml"));
            assertTrue(failure.getMessage().contains("actions.open"));
        }
        assertEquals(1, closed.get());
    }

    /** 验证标准 Nova 的 direct 和 isolated 初始化入口均执行全部模块。 */
    @Test
    void directAndIsolatedRunUseCompleteModuleInitializationPlan() throws Exception {
        Nova nova = new Nova();
        nova.set("request", 0);
        CompilationConfig.Builder builder = new CompilationConfig.Builder();
        CompilationTestSupport.write(root, "dependency.nova", "request = request + 1");
        SourceUnit entry = SourceUnit.virtual("main", "import \"./dependency\"\nrequest = request + 1\nrequest",
                root.resolve("business.yml"), null, 1, 0);
        try (CompiledNova program = nova.compileToBytecode(entry, builder.build())) {
            Map<String, Object> live = new LinkedHashMap<>();
            live.put("request", 10);
            assertEquals(12, program.runDirect(live));
            assertEquals(12, live.get("request"));
            Map<String, Object> isolated = new LinkedHashMap<>();
            isolated.put("request", 20);
            assertEquals(22, program.runIsolated(isolated));
            assertEquals(22, isolated.get("request"));
            assertEquals(0, program.get("request"));
        }
    }
    /** 验证无需配置即可编译入口及相对依赖，未引用的错误文件不参与编译。 */
    @Test
    void compilesOnlyReachableFilesWithoutMetadata() throws Exception {
        CompilationTestSupport.write(root, "shared.nova", "fun shared() = 7");
        CompilationTestSupport.write(root, "feature/helper.nova", "import \"../shared\"\nfun helper() = shared()");
        CompilationTestSupport.write(root, "unused.nova", "this is deliberately invalid Nova");
        Path entry = CompilationTestSupport.write(root, "feature/main.nova",
                "import \"./helper\"\nimport \"../shared\"\nfun value() = helper() + shared()");
        try (CompiledNova program = new Nova().compileToBytecode(entry)) {
            program.run();
            assertEquals(14, program.call("value"));
            assertEquals(3, program.getCompilationMetadata().getModuleGraph().getModules().size());
        }
    }

    /** 未使用的 Alias 不会自动加入编译依赖图或触发目录校验。 */
    @Test
    void ignoresUnreferencedAliases() throws Exception {
        CompilationConfig.Builder builder = new CompilationConfig.Builder();
        builder.alias("@unused", root.resolve("missing-directory"));
        Path entry = CompilationTestSupport.write(root, "main.nova", "fun value() = 7");
        try (CompiledNova program = new Nova().compileToBytecode(entry, builder.build())) {
            assertEquals(7, program.call("value"));
            assertEquals(1, program.getCompilationMetadata().getModuleGraph().getModules().size());
        }
    }

    /** 依赖函数签名改变时，缓存必须同时使调用模块失效并重新链接。 */
    @Test
    void invalidatesConsumersWhenDependencySignatureChanges() throws Exception {
        CompilationTestSupport.write(root, "helper.nova", "fun helper(): Int = 7");
        Path entry = CompilationTestSupport.write(root, "main.nova", "import \"./helper\"\nfun value() = helper()");
        Nova nova = new Nova();
        CompilationConfig config = new CompilationConfig.Builder().build();
        BytecodeArtifactCache cache = new BytecodeArtifactCache();
        try (CompiledNova first = nova.compileToBytecode(entry, config, cache)) {
            assertEquals(7, first.call("value"));
        }
        CompilationTestSupport.write(root, "helper.nova", "fun helper(): String = \"seven\"");
        try (CompiledNova second = nova.compileToBytecode(entry, config, cache)) {
            assertEquals("seven", second.call("value"));
        }
        assertEquals(4, cache.size());
    }

}
