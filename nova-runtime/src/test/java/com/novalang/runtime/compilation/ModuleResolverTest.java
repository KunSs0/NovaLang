package com.novalang.runtime.compilation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link ModuleResolver} Alias、边界和依赖图测试。
 */
@DisplayName("Compilation 模块解析")
class ModuleResolverTest {

    @TempDir
    Path tempDirectory;

    private ModuleResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new ModuleResolver();
    }

    @Test
    @DisplayName("解析 @ Alias 并补充 .nova 扩展名")
    void shouldResolveAliasAndAppendExtension() throws Exception {
        CompilationTestSupport.write(tempDirectory, "lib/core.api.nova", "fun value() = 7");
        CompilationTestSupport.write(tempDirectory, "main.nova",
                "import \"@/lib/core.api\"\nfun result() = value()");
        CompilationConfig config = loadConfig("  \"@\": \".\"\n");
        Path entryFile = tempDirectory.resolve("main.nova");

        ModuleGraph graph = resolver.resolve(entryFile, config);

        assertEquals(2, graph.getModules().size());
        SourceModule entry = graph.requireModule(graph.getEntryModuleId());
        assertTrue(entry.getTransformedSource().contains(
                "lib/core.api.nova"));
        assertEquals(tempDirectory.resolve("lib/core.api.nova").toRealPath().toUri().toString(), entry.getDependencies().get(0));
    }

    @Test
    @DisplayName("多个 Alias 匹配时使用最长名称")
    void shouldUseLongestAlias() throws Exception {
        CompilationTestSupport.write(tempDirectory, "shared/default/tool.nova", "fun wrong() = 0");
        CompilationTestSupport.write(tempDirectory, "shared/special/tool.nova", "fun right() = 1");
        CompilationTestSupport.write(tempDirectory, "main.nova",
                "import \"@shared/tool\"\nfun result() = right()");
        CompilationConfig config = loadConfig(
                "  \"@\": \"shared/default\"\n  \"@shared\": \"shared/special\"\n");
        Path entryFile = tempDirectory.resolve("main.nova");

        ModuleGraph graph = resolver.resolve(entryFile, config);
        SourceModule entry = graph.requireModule(graph.getEntryModuleId());

        assertTrue(entry.getTransformedSource().contains("shared/special/tool.nova"));
    }

    @Test
    @DisplayName("相对 import 以当前模块文件为基准")
    void shouldResolveRelativeImportFromImporter() throws Exception {
        CompilationTestSupport.write(tempDirectory, "feature/helper.nova", "fun helper() = 2");
        CompilationTestSupport.write(tempDirectory, "feature/main.nova",
                "import \"./helper\"\nfun result() = helper()");
        CompilationConfig config = loadConfig("  \"@\": \".\"\n");
        Path entryFile = tempDirectory.resolve("feature/main.nova");

        ModuleGraph graph = resolver.resolve(entryFile, config);

        assertEquals(2, graph.getModules().size());
        assertEquals(tempDirectory.resolve("feature/helper.nova").toRealPath().toUri().toString(),
                graph.getTopologicalOrder().get(0));
    }

    @Test
    @DisplayName("允许相对导入入口目录之外的文件")
    void shouldResolveImportsOutsideEntryDirectory() throws Exception {
        CompilationTestSupport.write(tempDirectory, "outside.nova", "fun outside() = 1");
        CompilationTestSupport.write(tempDirectory, "scripts/main.nova",
                "import \"../outside\"\nfun result() = outside()");
        CompilationConfig config = loadConfig("  \"@\": \"scripts\"\n");
        Path entryFile = tempDirectory.resolve("scripts/main.nova");

        ModuleGraph graph = resolver.resolve(entryFile, config);
        assertEquals(2, graph.getModules().size());

    }

    @Test
    @DisplayName("拒绝未声明 Alias")
    void shouldRejectUndeclaredAlias() throws Exception {
        CompilationTestSupport.write(tempDirectory, "main.nova",
                "import \"@missing/tool\"\nfun result() = 1");
        CompilationConfig config = loadConfig("  \"@\": \".\"\n");
        Path entryFile = tempDirectory.resolve("main.nova");

        CompilationException exception = assertThrows(CompilationException.class,
                () -> resolver.resolve(entryFile, config));

        assertEquals("Undeclared Compilation alias: @missing/tool", exception.getMessage());
    }

    @Test
    @DisplayName("拒绝不使用 Alias 或相对路径的字符串 import")
    void shouldRejectUnqualifiedStringImport() throws Exception {
        CompilationTestSupport.write(tempDirectory, "main.nova",
                "import \"shared/tool\"\nfun result() = 1");
        CompilationConfig config = loadConfig("  \"@\": \".\"\n");
        Path entryFile = tempDirectory.resolve("main.nova");

        CompilationException exception = assertThrows(CompilationException.class,
                () -> resolver.resolve(entryFile, config));

        assertTrue(exception.getMessage().startsWith(
                "Compilation string import must use an @ alias or a relative path"));
    }

    @Test
    @DisplayName("加载阶段拒绝循环依赖")
    void shouldRejectCyclicDependency() throws Exception {
        CompilationTestSupport.write(tempDirectory, "a.nova", "import \"./b\"\nfun a() = 1");
        CompilationTestSupport.write(tempDirectory, "b.nova", "import \"./a\"\nfun b() = 2");
        CompilationConfig config = loadConfig("  \"@\": \".\"\n");
        Path entryFile = tempDirectory.resolve("a.nova");

        CompilationException exception = assertThrows(CompilationException.class,
                () -> resolver.resolve(entryFile, config));

        assertTrue(exception.getMessage().startsWith("Cyclic module dependency detected"));
        assertTrue(exception.getMessage().contains("a.nova"));
        assertTrue(exception.getMessage().contains("b.nova"));
    }

    @Test
    @DisplayName("虚拟入口可以导入物理 Alias 模块")
    void shouldResolvePhysicalImportFromVirtualEntry() throws Exception {
        Path origin = CompilationTestSupport.write(tempDirectory, "skills.yml", "skills: {}");
        CompilationTestSupport.write(tempDirectory, "lib/helper.nova", "fun helper() = 3");
        CompilationTestSupport.write(tempDirectory, "placeholder.nova", "fun placeholder() = 0");
        CompilationConfig config = loadConfig("  \"@\": \".\"\n");
        Path entryFile = tempDirectory.resolve("placeholder.nova");
        SourceUnit virtual = new SourceUnit("@generated/fire",
                "import \"@/lib/helper\"\nfun fire() = helper()",
                origin, "skills.fire", 5, 0, null);

        ModuleGraph graph = resolver.resolve(virtual, config);

        assertEquals("@generated/fire", graph.getEntryModuleId());
        assertEquals(tempDirectory.resolve("lib/helper.nova").toRealPath().toUri().toString(),
                graph.requireModule("@generated/fire").getDependencies().get(0));
    }

    @Test
    @DisplayName("没有物理来源的虚拟模块不能使用相对 import")
    void shouldRejectRelativeImportFromSourceWithoutOrigin() throws Exception {
        CompilationTestSupport.write(tempDirectory, "placeholder.nova", "fun placeholder() = 0");
        CompilationTestSupport.write(tempDirectory, "helper.nova", "fun helper() = 1");
        CompilationConfig config = loadConfig("  \"@\": \".\"\n");
        Path entryFile = tempDirectory.resolve("placeholder.nova");
        SourceUnit virtual = new SourceUnit("@generated/no-origin",
                "import \"./helper\"\nfun result() = helper()",
                null, null, 1, 0, null);

        CompilationException exception = assertThrows(CompilationException.class,
                () -> resolver.resolve(virtual, config));

        assertTrue(exception.getMessage().startsWith(
                "A virtual module without a physical origin cannot use relative imports"));
    }

    @Test
    @DisplayName("字符串 import 必须独占一行")
    void shouldRejectTrailingCodeAfterStringImport() throws Exception {
        CompilationTestSupport.write(tempDirectory, "helper.nova", "fun helper() = 1");
        CompilationTestSupport.write(tempDirectory, "main.nova",
                "import \"./helper\"; fun result() = helper()");
        CompilationConfig config = loadConfig("  \"@\": \".\"\n");
        Path entryFile = tempDirectory.resolve("main.nova");

        CompilationException exception = assertThrows(CompilationException.class,
                () -> resolver.resolve(entryFile, config));

        assertTrue(exception.getMessage().startsWith("String import must occupy its own line"));
    }

    @Test
    @DisplayName("模块路径只允许正斜杠")
    void shouldRejectBackslashSpecifier() throws Exception {
        CompilationTestSupport.write(tempDirectory, "main.nova",
                "import \"@\\\\lib\\\\helper\"\nfun result() = 1");
        CompilationConfig config = loadConfig("  \"@\": \".\"\n");
        Path entryFile = tempDirectory.resolve("main.nova");

        CompilationException exception = assertThrows(CompilationException.class,
                () -> resolver.resolve(entryFile, config));

        assertTrue(exception.getMessage().startsWith("Undeclared Compilation alias")
                || exception.getMessage().startsWith("Compilation module paths must use"));
    }

    /** 宿主入口不登记到元数据后，依赖回引入口仍必须明确报告循环。 */
    @Test
    void shouldRejectDependencyImportingHostEntry() throws Exception {
        CompilationTestSupport.write(tempDirectory, "helper.nova", "import \"generated-main\"\nfun helper() = 1");
        SourceUnit entry = SourceUnit.virtual("generated-main", "import \"./helper\"",
                tempDirectory.resolve("business.yml"), "actions.open", 12, 0);
        CompilationException failure = assertThrows(CompilationException.class,
                () -> resolver.resolve(entry, new CompilationConfig.Builder().build()));
        assertTrue(failure.getMessage().startsWith("Cyclic module dependency detected"));
        assertTrue(failure.getMessage().contains("generated-main"));
        assertTrue(failure.getMessage().contains("helper.nova"));
    }

    /** 只写入别名元数据，入口由各测试直接传入解析器。 */
    private CompilationConfig loadConfig(String aliases) throws Exception {
        String content = "version: 1\nname: resolver-test\naliases:\n" + aliases;
        Path file = CompilationTestSupport.write(tempDirectory, "nova.config.yml", content);
        return new CompilationConfigLoader().load(file);
    }
}
