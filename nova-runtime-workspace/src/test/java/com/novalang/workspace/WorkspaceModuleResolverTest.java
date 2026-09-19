package com.novalang.workspace;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link WorkspaceModuleResolver} Alias、边界和依赖图测试。
 */
@DisplayName("Workspace 模块解析")
class WorkspaceModuleResolverTest {

    @TempDir
    Path tempDirectory;

    private WorkspaceModuleResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new WorkspaceModuleResolver();
    }

    @Test
    @DisplayName("解析 @ Alias 并补充 .nova 扩展名")
    void shouldResolveAliasAndAppendExtension() throws Exception {
        WorkspaceTestSupport.write(tempDirectory, "lib/core.api.nova", "fun value() = 7");
        WorkspaceTestSupport.write(tempDirectory, "main.nova",
                "import \"@/lib/core.api\"\nfun result() = value()");
        WorkspaceConfig config = loadConfig("  \"@\": \".\"\n",
                "  - \".\"\n", "  - \"main\"\n");

        WorkspaceModuleGraph graph = resolver.resolve(config);

        assertEquals(2, graph.getModules().size());
        WorkspaceModule entry = graph.requireModule(graph.getEntries().get("main"));
        assertTrue(entry.getTransformedSource().contains(
                "import \"@workspace/source-0/lib/core.api.nova\""));
        assertEquals("@workspace/source-0/lib/core.api.nova", entry.getDependencies().get(0));
    }

    @Test
    @DisplayName("多个 Alias 匹配时使用最长名称")
    void shouldUseLongestAlias() throws Exception {
        WorkspaceTestSupport.write(tempDirectory, "shared/default/tool.nova", "fun wrong() = 0");
        WorkspaceTestSupport.write(tempDirectory, "shared/special/tool.nova", "fun right() = 1");
        WorkspaceTestSupport.write(tempDirectory, "main.nova",
                "import \"@shared/tool\"\nfun result() = right()");
        WorkspaceConfig config = loadConfig(
                "  \"@\": \"shared/default\"\n  \"@shared\": \"shared/special\"\n",
                "  - \"shared/default\"\n  - \"shared/special\"\n  - \".\"\n",
                "  - \"main\"\n");

        WorkspaceModuleGraph graph = resolver.resolve(config);
        WorkspaceModule entry = graph.requireModule(graph.getEntries().get("main"));

        assertTrue(entry.getTransformedSource().contains("source-1/tool.nova"));
    }

    @Test
    @DisplayName("相对 import 以当前模块文件为基准")
    void shouldResolveRelativeImportFromImporter() throws Exception {
        WorkspaceTestSupport.write(tempDirectory, "feature/helper.nova", "fun helper() = 2");
        WorkspaceTestSupport.write(tempDirectory, "feature/main.nova",
                "import \"./helper\"\nfun result() = helper()");
        WorkspaceConfig config = loadConfig("  \"@\": \".\"\n",
                "  - \".\"\n", "  - \"feature/main\"\n");

        WorkspaceModuleGraph graph = resolver.resolve(config);

        assertEquals(2, graph.getModules().size());
        assertEquals("@workspace/source-0/feature/helper.nova",
                graph.getTopologicalOrder().get(0));
    }

    @Test
    @DisplayName("拒绝逃逸 source root 的相对路径")
    void shouldRejectPathOutsideSourceRoots() throws Exception {
        WorkspaceTestSupport.write(tempDirectory, "outside.nova", "fun outside() = 1");
        WorkspaceTestSupport.write(tempDirectory, "scripts/main.nova",
                "import \"../outside\"\nfun result() = outside()");
        WorkspaceConfig config = loadConfig("  \"@\": \"scripts\"\n",
                "  - \"scripts\"\n", "  - \"scripts/main\"\n");

        WorkspaceException exception = assertThrows(WorkspaceException.class,
                () -> resolver.resolve(config));

        assertTrue(exception.getMessage().startsWith("Path escapes the declared source roots"));
    }

    @Test
    @DisplayName("拒绝未声明 Alias")
    void shouldRejectUndeclaredAlias() throws Exception {
        WorkspaceTestSupport.write(tempDirectory, "main.nova",
                "import \"@missing/tool\"\nfun result() = 1");
        WorkspaceConfig config = loadConfig("  \"@\": \".\"\n",
                "  - \".\"\n", "  - \"main\"\n");

        WorkspaceException exception = assertThrows(WorkspaceException.class,
                () -> resolver.resolve(config));

        assertEquals("Undeclared Workspace alias: @missing/tool", exception.getMessage());
    }

    @Test
    @DisplayName("拒绝不使用 Alias 或相对路径的字符串 import")
    void shouldRejectUnqualifiedStringImport() throws Exception {
        WorkspaceTestSupport.write(tempDirectory, "main.nova",
                "import \"shared/tool\"\nfun result() = 1");
        WorkspaceConfig config = loadConfig("  \"@\": \".\"\n",
                "  - \".\"\n", "  - \"main\"\n");

        WorkspaceException exception = assertThrows(WorkspaceException.class,
                () -> resolver.resolve(config));

        assertTrue(exception.getMessage().startsWith(
                "Workspace string import must use an @ alias or a relative path"));
    }

    @Test
    @DisplayName("加载阶段拒绝循环依赖")
    void shouldRejectCyclicDependency() throws Exception {
        WorkspaceTestSupport.write(tempDirectory, "a.nova", "import \"./b\"\nfun a() = 1");
        WorkspaceTestSupport.write(tempDirectory, "b.nova", "import \"./a\"\nfun b() = 2");
        WorkspaceConfig config = loadConfig("  \"@\": \".\"\n",
                "  - \".\"\n", "  - \"a\"\n");

        WorkspaceException exception = assertThrows(WorkspaceException.class,
                () -> resolver.resolve(config));

        assertTrue(exception.getMessage().startsWith("Cyclic module dependency detected"));
        assertTrue(exception.getMessage().contains("a.nova"));
        assertTrue(exception.getMessage().contains("b.nova"));
    }

    @Test
    @DisplayName("虚拟入口可以导入物理 Alias 模块")
    void shouldResolvePhysicalImportFromVirtualEntry() throws Exception {
        Path origin = WorkspaceTestSupport.write(tempDirectory, "skills.yml", "skills: {}");
        WorkspaceTestSupport.write(tempDirectory, "lib/helper.nova", "fun helper() = 3");
        WorkspaceTestSupport.write(tempDirectory, "placeholder.nova", "fun placeholder() = 0");
        WorkspaceConfig config = loadConfig("  \"@\": \".\"\n",
                "  - \".\"\n", "  - \"placeholder\"\n");
        SourceUnit virtual = new SourceUnit("@generated/fire",
                "import \"@/lib/helper\"\nfun fire() = helper()",
                origin, "skills.fire", 5, 0, null);

        WorkspaceModuleGraph graph = resolver.resolve(config,
                Collections.singletonList(virtual), Collections.singletonList("@generated/fire"));

        assertEquals("@generated/fire", graph.getEntries().get("@generated/fire"));
        assertEquals("@workspace/source-0/lib/helper.nova",
                graph.requireModule("@generated/fire").getDependencies().get(0));
    }

    @Test
    @DisplayName("没有物理来源的虚拟模块不能使用相对 import")
    void shouldRejectRelativeImportFromSourceWithoutOrigin() throws Exception {
        WorkspaceTestSupport.write(tempDirectory, "placeholder.nova", "fun placeholder() = 0");
        WorkspaceTestSupport.write(tempDirectory, "helper.nova", "fun helper() = 1");
        WorkspaceConfig config = loadConfig("  \"@\": \".\"\n",
                "  - \".\"\n", "  - \"placeholder\"\n");
        SourceUnit virtual = new SourceUnit("@generated/no-origin",
                "import \"./helper\"\nfun result() = helper()",
                null, null, 1, 0, null);

        WorkspaceException exception = assertThrows(WorkspaceException.class,
                () -> resolver.resolve(config, Arrays.asList(virtual),
                        Arrays.asList("@generated/no-origin")));

        assertTrue(exception.getMessage().startsWith(
                "A virtual module without a physical origin cannot use relative imports"));
    }

    @Test
    @DisplayName("字符串 import 必须独占一行")
    void shouldRejectTrailingCodeAfterStringImport() throws Exception {
        WorkspaceTestSupport.write(tempDirectory, "helper.nova", "fun helper() = 1");
        WorkspaceTestSupport.write(tempDirectory, "main.nova",
                "import \"./helper\"; fun result() = helper()");
        WorkspaceConfig config = loadConfig("  \"@\": \".\"\n",
                "  - \".\"\n", "  - \"main\"\n");

        WorkspaceException exception = assertThrows(WorkspaceException.class,
                () -> resolver.resolve(config));

        assertTrue(exception.getMessage().startsWith("String import must occupy its own line"));
    }

    @Test
    @DisplayName("模块路径只允许正斜杠")
    void shouldRejectBackslashSpecifier() throws Exception {
        WorkspaceTestSupport.write(tempDirectory, "main.nova",
                "import \"@\\\\lib\\\\helper\"\nfun result() = 1");
        WorkspaceConfig config = loadConfig("  \"@\": \".\"\n",
                "  - \".\"\n", "  - \"main\"\n");

        WorkspaceException exception = assertThrows(WorkspaceException.class,
                () -> resolver.resolve(config));

        assertTrue(exception.getMessage().startsWith("Undeclared Workspace alias")
                || exception.getMessage().startsWith("Workspace module paths must use"));
    }

    private WorkspaceConfig loadConfig(String aliases, String sources, String entries) throws Exception {
        String content = "version: 1\nname: resolver-test\naliases:\n" + aliases
                + "sources:\n" + sources + "entries:\n" + entries
                + "runtime:\n  security: trusted-server\n  thread: caller\n";
        Path file = WorkspaceTestSupport.write(tempDirectory, "nova.config.yml", content);
        return new WorkspaceConfigLoader().load(file);
    }
}
