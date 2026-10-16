package com.novalang.runtime.compilation;

import com.novalang.runtime.SchedulerHolder;
import com.novalang.runtime.interpreter.Interpreter;
import com.novalang.runtime.interpreter.ModuleLoader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 单入口依赖图中共享模块只编译一次的回归测试。
 */
@DisplayName("Compilation 共享模块冷编译")
class CompilationSharedModuleCompilationTest {

    private static final int ACTION_COUNT = 20;

    /**
     * 验证 Compilation 将依赖模块的 Java 外层类导入转发到消费模块后，嵌套类型仍可解析。
     *
     * @throws Exception 测试源码写入、编译或执行失败。
     */
    @Test
    @DisplayName("Compilation 依赖模块导出的 Java 外层类支持嵌套类型")
    void shouldResolveNestedJavaTypeExportedByDependency() throws Exception {
        CompilationTestSupport.write(tempDirectory, "java-api.nova",
                "import java java.lang.Thread\n"
                        + "fun threadState(): String { return Thread.State.NEW.name() }\n");
        CompilationTestSupport.write(tempDirectory, "entry.nova",
                "import \"@/java-api\"\n"
                        + "fun entryExecute(): String { return threadState() }\n");
        Path configFile = CompilationTestSupport.writeMain(tempDirectory, "entry.nova");
        CompilationHarness compilation = new CompilationHarness(configFile, nova -> { });
        try {
            compilation.compileAndRun();
            assertEquals("NEW", compilation.call("entryExecute", Collections.<String, Object>emptyMap()));
        } finally {
            compilation.close();
        }
    }

    /**
     * 菱形依赖只能转发原始扩展一次，不能为每一层链接重新生成导出身份。
     * @throws Exception 测试源码写入、编译或执行失败。
     */
    @Test
    void shouldDeduplicateOriginalExtensionsThroughDiamondDependencies() throws Exception {
        CompilationTestSupport.write(tempDirectory, "base.nova",
                "import java java.io.File\n"
                        + "fun File.getEntityManager(): String { return this.getPath() }\n"
                        + "val File.label: String get() = this.getPath()\n");
        CompilationTestSupport.write(tempDirectory, "left.nova",
                "import \"@/base\"\nfun left(): String { return File(\"left\").getEntityManager() }\n");
        CompilationTestSupport.write(tempDirectory, "right.nova",
                "import \"@/base\"\nfun right(): String { return File(\"right\").label }\n");
        CompilationTestSupport.write(tempDirectory, "entry.nova",
                "import \"@/base\"\nimport \"@/left\"\nimport \"@/right\"\n"
                        + "fun entryExecute(): String { return left() + right() + File(\"direct\").getEntityManager() + File(\"property\").label }\n");
        CompilationTestSupport.write(tempDirectory, "left-entry.nova",
                "import \"@/left\"\nfun left_entryExecute(): String { return left() }\n");
        CompilationTestSupport.write(tempDirectory, "right-entry.nova",
                "import \"@/right\"\nfun right_entryExecute(): String { return right() }\n");
        Path configFile = CompilationTestSupport.writeMain(tempDirectory, "entry.nova", "left-entry.nova", "right-entry.nova");
        CompilationHarness compilation = new CompilationHarness(configFile, nova -> { });
        try {
            compilation.compileAndRun();
            assertEquals("leftrightdirectproperty", compilation.call("entryExecute", Collections.<String, Object>emptyMap()));
            assertEquals("left", compilation.call("left_entryExecute", Collections.<String, Object>emptyMap()));
            assertEquals("right", compilation.call("right_entryExecute", Collections.<String, Object>emptyMap()));
        } finally {
            compilation.close();
        }
    }

    @Test
    void shouldKeepSameNamedExtensionPropertiesOnDifferentReceiversSeparate() throws Exception {
        CompilationTestSupport.write(tempDirectory, "labels.nova",
                "import java java.io.File\nimport java java.lang.StringBuilder\n"
                        + "val File.label: String get() = this.getPath()\n"
                        + "val StringBuilder.label: String get() = this.toString()\n"
                        + "fun source(file: Boolean): Any { if (file) { return File(\"file\") }; return StringBuilder(\"builder\") }\n");
        for (String name : new String[] {"entry", "other"}) {
            CompilationTestSupport.write(tempDirectory, name + ".nova",
                    "import \"@/labels\"\nfun " + name + "Execute(): String { return source(true).label + source(false).label }\n");
        }
        Path configFile = CompilationTestSupport.writeMain(tempDirectory, "entry.nova", "other.nova");
        CompilationHarness compilation = new CompilationHarness(configFile, nova -> { });
        try {
            compilation.compileAndRun();
            assertEquals("filebuilder", compilation.call("entryExecute", Collections.<String, Object>emptyMap()));
            assertEquals("filebuilder", compilation.call("otherExecute", Collections.<String, Object>emptyMap()));
        } finally {
            compilation.close();
        }
    }

    @Test
    void shouldRejectInvalidExtensionPropertyGetterType() throws Exception {
        CompilationTestSupport.write(tempDirectory, "entry.nova",
                "import java java.io.File\nval File.tools: Int get() = \"wrong\"\nfun entryExecute() { }\n");
        Path configFile = CompilationTestSupport.writeMain(tempDirectory, "entry.nova");
        CompilationHarness compilation = new CompilationHarness(configFile, nova -> { });
        try {
            assertThrows(CompilationException.class, compilation::compileAndRun);
        } finally {
            compilation.close();
        }
    }

    @Test
    void shouldLinkChainedExtensionPropertiesAcrossCompilationGroups() throws Exception {
        CompilationTestSupport.write(tempDirectory, "tools.nova",
                "import java java.io.File\n"
                        + "class Tools(val context: File) { }\n"
                        + "val File.tools: Tools get() = Tools(this)\n"
                        + "fun ctx(): File { return File(\"fixture\") }\n");
        CompilationTestSupport.write(tempDirectory, "mob.nova",
                "import \"@/tools\"\n"
                        + "class Mobs(val tools: Tools) { fun spawn(level: Int = 1): String { return tools.context.getPath() + level } }\n"
                        + "val Tools.mob: Mobs get() = Mobs(this)\n");
        CompilationTestSupport.write(tempDirectory, "entry.nova",
                "import \"@/mob\"\nfun entryExecute(): String { return ctx().tools.mob.spawn() }\n");
        CompilationTestSupport.write(tempDirectory, "other.nova",
                "import \"@/mob\"\nfun otherExecute(): String { return ctx().tools.mob.spawn() }\n");
        Path configFile = CompilationTestSupport.writeMain(tempDirectory, "entry.nova", "other.nova");
        CompilationHarness compilation = new CompilationHarness(configFile, nova -> { });
        try {
            compilation.compileAndRun();
            assertEquals("fixture1", compilation.call("entryExecute", Collections.<String, Object>emptyMap()));
            assertEquals("fixture1", compilation.call("otherExecute", Collections.<String, Object>emptyMap()));
        } finally {
            compilation.close();
        }
    }

    @Test
    void shouldCaptureLocalWithSameNameAsContextFunction() throws Exception {
        CompilationTestSupport.write(tempDirectory, "entry.nova",
                "fun ctx(): String { return \"global\" }\n"
                        + "fun capture(ctx: String): String { val read = { ctx }; return read() }\n"
                        + "fun entryExecute(): String { return capture(\"local\") }\n");
        Path configFile = CompilationTestSupport.writeMain(tempDirectory, "entry.nova");
        CompilationHarness compilation = new CompilationHarness(configFile, nova -> { });
        try {
            compilation.compileAndRun();
            assertEquals("local", compilation.call("entryExecute", Collections.<String, Object>emptyMap()));
        } finally {
            compilation.close();
        }
    }

    @Test
    void shouldCallScriptExtensionOnImportedJavaReceiverAcrossModules() throws Exception {
        CompilationTestSupport.write(tempDirectory, "lib/context.nova",
                "import java java.io.File\n"
                        + "import java java.lang.StringBuilder\n"
                        + "fun ctx(): File { return File(\"fixture\") }\n"
                        + "fun File.getManager(): StringBuilder { return StringBuilder(this.getPath()) }\n");
        CompilationTestSupport.write(tempDirectory, "entry.nova",
                "import \"@/lib/context\"\n"
                        + "fun entryExecute(): String { return ctx().getManager().toString() }\n");
        CompilationTestSupport.write(tempDirectory, "other.nova",
                "import \"@/lib/context\"\nfun otherExecute(): String { return ctx().getManager().toString() }\n");
        Path configFile = CompilationTestSupport.writeMain(tempDirectory, "entry.nova", "other.nova");
        CompilationHarness compilation = new CompilationHarness(configFile, nova -> { });
        try {
            compilation.compileAndRun();
            assertEquals("fixture", compilation.call("entryExecute", Collections.<String, Object>emptyMap()));
            assertEquals("fixture", compilation.call("otherExecute", Collections.<String, Object>emptyMap()));
        } finally {
            compilation.close();
        }
    }

    /**
     * 验证跨模块导出的 Java Vector 参数与调用方同名导入类型保持兼容。
     *
     * @throws Exception 工作区源码写入、编译或执行失败。
     */
    @Test
    void shouldKeepJavaVectorParameterCompatibleAcrossModules() throws Exception {
        CompilationTestSupport.write(tempDirectory, "vector-api.nova",
                "import java java.util.Vector\n"
                        + "object VectorApi {\n"
                        + "    fun acceptVector(direction: Vector): Int { return direction.size() }\n"
                        + "}\n");
        CompilationTestSupport.write(tempDirectory, "entry.nova",
                "import java java.util.Vector\n"
                        + "import \"@/vector-api\"\n"
                        + "fun entryExecute(): Int { return VectorApi.acceptVector(Vector()) }\n");
        Path configFile = CompilationTestSupport.writeMain(tempDirectory, "entry.nova");
        CompilationHarness compilation = new CompilationHarness(configFile, nova -> { });
        try {
            compilation.compileAndRun();
            assertEquals(0, compilation.call("entryExecute", Collections.<String, Object>emptyMap()));
        } finally {
            compilation.close();
        }
    }

    @Test
    void shouldRejectInvalidJavaReceiverScriptExtensionCalls() throws Exception {
        for (String expression : new String[] {
                "File(\"fixture\").getManager(1)",
                "StringBuilder().getManager()",
                "File(\"fixture\").missingManager()"
        }) {
            CompilationTestSupport.write(tempDirectory, "entry.nova",
                    "import java java.io.File\n"
                            + "import java java.lang.StringBuilder\n"
                            + "fun File.getManager(): String { return this.getPath() }\n"
                            + "fun entryExecute() { " + expression + " }\n");
            Path configFile = CompilationTestSupport.writeMain(tempDirectory, "entry.nova");
            CompilationHarness compilation = new CompilationHarness(configFile, nova -> { });
            try {
                assertThrows(CompilationException.class, compilation::compileAndRun, expression);
            } finally {
                compilation.close();
            }
        }
    }

    @TempDir
    Path tempDirectory;

    @BeforeEach
    void installScheduler() {
        SchedulerHolder.set(CompilationTestSupport.directScheduler());
    }

    @AfterEach
    void clearScheduler() {
        ModuleLoader.unregisterSharedModule("sample.core");
        Interpreter.resetGlobalSchedulerState();
    }

    @Test
    @DisplayName("Compilation 必须在编译期拒绝未声明的全局函数")
    void shouldRejectUnknownGlobalFunctionAtCompilationCompileTime() throws Exception {
        CompilationTestSupport.write(tempDirectory, "entry.nova",
                "fun entryExecute() { regex(\"\\\\s+\") }\n");
        Path configFile = CompilationTestSupport.writeMain(tempDirectory, "entry.nova");
        CompilationHarness compilation = new CompilationHarness(configFile,
                nova -> nova.setRejectUnknownGlobalCalls(true));

        try {
            CompilationException exception = assertThrows(CompilationException.class, compilation::compileAndRun);
            String message = describeFailure(exception);
            assertTrue(message.contains("未声明的全局函数: 'regex'"));
            assertTrue(message.contains("modules: ["));
            assertTrue(message.contains("entry.nova"));
        } finally {
            compilation.close();
        }
    }

    @Test
    @DisplayName("Compilation 应解析 ModuleLoader 注册的共享逻辑模块")
    void shouldResolveSharedModuleRegisteredByModuleLoader() throws Exception {
        CompilationTestSupport.write(tempDirectory, "entry.nova",
                "import \"sample.core\"\n"
                        + "fun entryExecute(): Int { return sampleValue() }\n");
        Path configFile = CompilationTestSupport.writeMain(tempDirectory, "entry.nova");
        ModuleLoader.registerSharedModule("sample.core",
                "fun sampleValue(): Int { return 42 }\n");
        CompilationHarness compilation = new CompilationHarness(configFile, nova -> { });

        try {
            compilation.compileAndRun();
            assertEquals(42, ((Number) compilation.call("entryExecute", Collections.<String, Object>emptyMap())).intValue());
        } finally {
            compilation.close();
        }
    }

    @Test
    @DisplayName("共享模块导出的 Nova 构造器属性 getter 必须在编译期被拒绝")
    void shouldRejectMissingExportedNovaPropertyGetter() throws Exception {
        CompilationTestSupport.write(tempDirectory, "lib/frame.nova",
                "class Frame(val x: Double, val y: Double, val z: Double) { }\n"
                        + "fun createFrame(): Frame { return Frame(1.0, 2.0, 3.0) }\n");
        CompilationTestSupport.write(tempDirectory, "lib/consumer.nova",
                "import \"@/lib/frame\"\n"
                        + "fun frameTotal(frame: Frame): Double {\n"
                        + "    return frame.getX() + frame.getY() + frame.getZ()\n"
                        + "}\n");
        CompilationTestSupport.write(tempDirectory, "entry-a.nova",
                "import \"@/lib/frame\"\n"
                        + "fun entry_aExecute(): Double { return createFrame().x }\n");
        CompilationTestSupport.write(tempDirectory, "entry-b.nova",
                "import \"@/lib/consumer\"\n"
                        + "fun entry_bExecute(): Double { return frameTotal(createFrame()) }\n");
        Path configFile = CompilationTestSupport.writeMain(tempDirectory, "entry-a.nova", "entry-b.nova");
        CompilationHarness compilation = new CompilationHarness(configFile, nova -> { });

        try {
            CompilationException exception = assertThrows(CompilationException.class, compilation::compileAndRun);
            String message = describeFailure(exception);
            assertTrue(message.contains("No matching Java method overload found for 'getX'"));
            assertTrue(message.contains("No matching Java method overload found for 'getY'"));
            assertTrue(message.contains("No matching Java method overload found for 'getZ'"));
        } finally {
            compilation.close();
        }
    }

    @Test
    @DisplayName("共享模块函数返回的 Nova 类型必须在消费组保留静态类型")
    void shouldRejectMissingSetterOnTypeReturnedBySharedFunction() throws Exception {
        CompilationTestSupport.write(tempDirectory, "lib/runtime.nova",
                "class RuntimeState {\n"
                        + "    var value: String? = null\n"
                        + "}\n"
                        + "fun runtimeState(): RuntimeState { return RuntimeState() }\n");
        CompilationTestSupport.write(tempDirectory, "entry.nova",
                "import \"@/lib/runtime\"\n"
                        + "fun entryExecute() {\n"
                        + "    val runtime = runtimeState()\n"
                        + "    runtime.setValue(\"invalid\")\n"
                        + "}\n");
        Path configFile = CompilationTestSupport.writeMain(tempDirectory, "entry.nova");
        CompilationHarness compilation = new CompilationHarness(configFile, nova -> { });

        try {
            CompilationException exception = assertThrows(CompilationException.class, compilation::compileAndRun);
            String message = describeFailure(exception);
            assertTrue(message.contains("setValue"), message);

        } finally {
            compilation.close();
        }
    }

    @Test
    @DisplayName("共享模块函数返回的 Nova 类型支持属性写入")
    void shouldWritePropertyOnTypeReturnedBySharedFunction() throws Exception {
        CompilationTestSupport.write(tempDirectory, "lib/runtime.nova",
                "class RuntimeState {\n"
                        + "    var value: String? = null\n"
                        + "}\n"
                        + "fun runtimeState(): RuntimeState { return RuntimeState() }\n");
        CompilationTestSupport.write(tempDirectory, "entry.nova",
                "import \"@/lib/runtime\"\n"
                        + "fun entryExecute(): String? {\n"
                        + "    val runtime = runtimeState()\n"
                        + "    runtime.value = \"valid\"\n"
                        + "    return runtime.value\n"
                        + "}\n");
        Path configFile = CompilationTestSupport.writeMain(tempDirectory, "entry.nova");
        CompilationHarness compilation = new CompilationHarness(configFile, nova -> { });

        try {
            compilation.compileAndRun();
            assertEquals("valid", compilation.call("entryExecute", Collections.<String, Object>emptyMap()));
        } finally {
            compilation.close();
        }
    }

    @Test
    @DisplayName("共享模块导出的可空 Nova 属性允许写入 null")
    void shouldClearNullablePropertyOnTypeReturnedBySharedFunction() throws Exception {
        CompilationTestSupport.write(tempDirectory, "lib/runtime.nova",
                "class Notice { }\n"
                        + "class RuntimeState {\n"
                        + "    var value: Notice? = null\n"
                        + "}\n"
                        + "fun runtimeState(): RuntimeState { return RuntimeState() }\n");
        CompilationTestSupport.write(tempDirectory, "entry.nova",
                "import \"@/lib/runtime\"\n"
                        + "fun entryExecute(): Notice? {\n"
                        + "    val runtime = runtimeState()\n"
                        + "    runtime.value = null\n"
                        + "    return runtime.value\n"
                        + "}\n");
        Path configFile = CompilationTestSupport.writeMain(tempDirectory, "entry.nova");
        CompilationHarness compilation = new CompilationHarness(configFile, nova -> { });

        try {
            compilation.compileAndRun();
            assertNull(compilation.call("entryExecute", Collections.<String, Object>emptyMap()));
        } finally {
            compilation.close();
        }
    }

    @Test
    @DisplayName("共享模块导出的 Nova object 方法必须校验参数类型")
    void shouldRejectWrongArgumentTypeForExportedNovaObjectMethod() throws Exception {
        CompilationTestSupport.write(tempDirectory, "lib/lifecycle.nova",
                "class Callback { }\n"
                        + "object Lifecycle {\n"
                        + "    fun register(callback: Callback): String { return \"registered\" }\n"
                        + "}\n");
        CompilationTestSupport.write(tempDirectory, "entry-a.nova",
                "import \"@/lib/lifecycle\"\n"
                        + "fun entry_aExecute() {\n"
                        + "    Lifecycle.register(listOf(Callback()))\n"
                        + "}\n");
        CompilationTestSupport.write(tempDirectory, "entry-b.nova",
                "import \"@/lib/lifecycle\"\n"
                        + "fun entry_bExecute(): String { return \"ready\" }\n");
        Path configFile = CompilationTestSupport.writeMain(tempDirectory, "entry-a.nova", "entry-b.nova");
        CompilationHarness compilation = new CompilationHarness(configFile, nova -> { });

        try {
            CompilationException exception = assertThrows(CompilationException.class, compilation::compileAndRun);
            String message = describeFailure(exception);
            assertTrue(message.contains("No matching Java method overload found for 'register'"));
        } finally {
            compilation.close();
        }
    }

    @Test
    @DisplayName("共享模块导出的 Nova object 方法支持正确参数调用")
    void shouldInvokeExportedNovaObjectMethodWithTypedArgument() throws Exception {
        CompilationTestSupport.write(tempDirectory, "lib/lifecycle.nova",
                "class Callback { }\n"
                        + "object Lifecycle {\n"
                        + "    fun register(callback: Callback): String { return \"registered\" }\n"
                        + "}\n");
        CompilationTestSupport.write(tempDirectory, "entry-a.nova",
                "import \"@/lib/lifecycle\"\n"
                        + "fun entry_aExecute(): String {\n"
                        + "    return Lifecycle.register(Callback())\n"
                        + "}\n");
        CompilationTestSupport.write(tempDirectory, "entry-b.nova",
                "import \"@/lib/lifecycle\"\n"
                        + "fun entry_bExecute(): String { return \"ready\" }\n");
        Path configFile = CompilationTestSupport.writeMain(tempDirectory, "entry-a.nova", "entry-b.nova");
        CompilationHarness compilation = new CompilationHarness(configFile, nova -> { });

        try {
            compilation.compileAndRun();
            assertEquals("registered", compilation.call("entry_aExecute", Collections.<String, Object>emptyMap()));
        } finally {
            compilation.close();
        }
    }

    @Test
    @DisplayName("共享模块导出的 Nova 构造器属性可以通过属性语法读取")
    void shouldReadExportedNovaPropertyThroughPropertySyntax() throws Exception {
        CompilationTestSupport.write(tempDirectory, "lib/frame.nova",
                "class Frame(val x: Double, val y: Double, val z: Double) { }\n"
                        + "fun createFrame(): Frame { return Frame(1.0, 2.0, 3.0) }\n");
        CompilationTestSupport.write(tempDirectory, "lib/consumer.nova",
                "import \"@/lib/frame\"\n"
                        + "fun frameTotal(frame: Frame): Double {\n"
                        + "    return frame.x + frame.y + frame.z\n"
                        + "}\n");
        CompilationTestSupport.write(tempDirectory, "entry-a.nova",
                "import \"@/lib/frame\"\n"
                        + "fun entry_aExecute(): Double { return createFrame().x }\n");
        CompilationTestSupport.write(tempDirectory, "entry-b.nova",
                "import \"@/lib/consumer\"\n"
                        + "fun entry_bExecute(): Double { return frameTotal(createFrame()) }\n");
        Path configFile = CompilationTestSupport.writeMain(tempDirectory, "entry-a.nova", "entry-b.nova");
        CompilationHarness compilation = new CompilationHarness(configFile, nova -> { });

        try {
            compilation.compileAndRun();
            assertEquals(1.0D, ((Number) compilation.call("entry_aExecute", Collections.<String, Object>emptyMap())).doubleValue());
            assertEquals(6.0D, ((Number) compilation.call("entry_bExecute", Collections.<String, Object>emptyMap())).doubleValue());
        } finally {
            compilation.close();
        }
    }

    @Test
    @DisplayName("公共模块在单入口依赖图的编译输入中只出现一次")
    void shouldIncludeSharedModuleInOnlyOneCompilationInput() throws Exception {
        CompilationTestSupport.write(tempDirectory, "lib/shared.nova",
                "fun sharedValue(): Int {\n"
                        + "    return 42\n"
                        + "}\n");
        CompilationTestSupport.write(tempDirectory, "main.nova",
                "import \"@/lib/shared\"\n"
                        + "fun mainValue(): Int {\n"
                        + "    return sharedValue()\n"
                        + "}\n");
        Path configFile = CompilationTestSupport.writeMain(tempDirectory, "main.nova");
        CompilationConfig config = new CompilationConfig.Builder().alias("@", tempDirectory).build();

        java.util.List<String> registeredModules = new java.util.ArrayList<>();
        try {
            StringBuilder main = new StringBuilder();
            for (int index = 0; index < ACTION_COUNT; index++) {
                String moduleId = "@generated/action-" + index;
                String source = "import \"@/main\"\nfun action" + index + "(): Int { return mainValue() + " + index + " }";
                ModuleLoader.registerSharedModule(moduleId, source);
                registeredModules.add(moduleId);
                main.append("import \"").append(moduleId).append("\"\n");
            }
            SourceUnit entry = SourceUnit.virtual("test-main", main.toString(), null, null, 1, 0);
            ModuleGraph graph = new ModuleResolver().resolve(entry, config);
            CompilationPlan plan = new CompilationPlanner().build(
                    graph, configFile.toString());
            CompilationGroupBuilder bundleBuilder =
                    new CompilationGroupBuilder();
            Map<String, CompilationExports> exports =
                    new LinkedHashMap<String, CompilationExports>();
            int sharedDeclarationOccurrences = 0;
            for (CompilationPlan.Group group : plan.getGroups()) {
                CompilationBundle bundle = bundleBuilder.build(graph, group, exports);
                sharedDeclarationOccurrences += countOccurrences(
                        bundle.getSource(), "fun sharedValue(): Int");
                exports.put(group.getId(), CompilationExports.empty());
            }

            assertEquals(ACTION_COUNT + 3, graph.getModules().size());
            assertEquals(1, sharedDeclarationOccurrences,
                    "同一主文件引用的公共模块不能重复进入编译管线");
        } finally {
            for (String moduleId : registeredModules) {
                ModuleLoader.unregisterSharedModule(moduleId);
            }
        }
    }

    @Test
    @DisplayName("主文件导入的模块保留各自类型和状态并调用共享模块")
    void shouldIsolateEntryTypesAndStateWhileLinkingSharedModule() throws Exception {
        CompilationTestSupport.write(tempDirectory, "lib/shared.nova",
                "import java com.novalang.runtime.compilation.SourceUnit\n"
                        + "object SharedValues {\n"
                        + "    fun base(): Int { return 40 }\n"
                        + "    fun zero(): Int { return 0 }\n"
                        + "}\n"
                        + "fun sharedSource(): SourceUnit? { return null }\n"
                        + "fun sharedValue(): Int { return SharedValues.base() }\n");
        CompilationTestSupport.write(tempDirectory, "main.nova",
                "import \"@/lib/shared\"\n"
                        + "fun mainValue(): Int { return sharedValue() }\n");
        Path configFile = CompilationTestSupport.writeMain(tempDirectory, "main.nova");
        BytecodeArtifactCache cache = new BytecodeArtifactCache();
        CompilationHarness compilation = new CompilationHarness(configFile, nova -> { }, cache);
        compilation.importModule("@generated/first", entryWithPrivateRecord(1, "executeFirst"));
        compilation.importModule("@generated/second", entryWithPrivateRecord(2, "executeSecond"));

        try {
            compilation.compileAndRun();

            assertEquals(42, ((Number) compilation.call("executeFirst", Collections.<String, Object>emptyMap())).intValue());
            assertEquals(43, ((Number) compilation.call("executeFirst", Collections.<String, Object>emptyMap())).intValue());
            assertEquals(43, ((Number) compilation.call("executeSecond", Collections.<String, Object>emptyMap())).intValue());
            assertEquals(5, cache.size(),
                    "主文件、两个依赖和两个共享模块各编译一次");
        } finally {
            compilation.close();
        }
    }

    @Test
    @DisplayName("只有 import 的编译组作为无操作链接节点加载")
    void shouldLoadImportOnlyCompilationGroupAsNoOpLinkNode() throws Exception {
        CompilationTestSupport.write(tempDirectory, "shared.nova",
                "fun sharedValue(): Int { return 42 }\n");
        CompilationTestSupport.write(tempDirectory, "entry-a.nova",
                "import \"@/shared\"\n");
        CompilationTestSupport.write(tempDirectory, "entry-b.nova",
                "import \"@/shared\"\n"
                        + "fun entry_bExecute(): Int { return sharedValue() }\n");
        Path configFile = CompilationTestSupport.writeMain(tempDirectory, "entry-a.nova", "entry-b.nova");
        CompilationHarness compilation = new CompilationHarness(configFile, nova -> { });

        try {
            compilation.compileAndRun();

            assertEquals(42, ((Number) compilation.call("sharedValue", Collections.<String, Object>emptyMap())).intValue());
            assertEquals(42, ((Number) compilation.call("entry_bExecute", Collections.<String, Object>emptyMap())).intValue());
        } finally {
            compilation.close();
        }
    }

    private String entryWithPrivateRecord(int offset, String functionName) {
        return "import \"@/main\"\n"
                + "val records = mutableListOf<Int>()\n"
                + "class CaptionRecord(val value: Int) {\n"
                + "    fun total(): Int { return value + records.size }\n"
                + "}\n"
                + "fun " + functionName + "(): Int {\n"
                + "    records.add(" + offset + ")\n"
                + "    val sharedSourceValue: SourceUnit? = sharedSource()\n"
                + "    val record = CaptionRecord(mainValue())\n"
                + "    val zeroSupplier = { SharedValues.zero() }\n"
                + "    return record.total() + " + offset + " + zeroSupplier()\n"
                + "}\n";
    }

    private int countOccurrences(String source, String expected) {
        int count = 0;
        int offset = 0;
        while (true) {
            int found = source.indexOf(expected, offset);
            if (found < 0) {
                return count;
            }
            count++;
            offset = found + expected.length();
        }
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
