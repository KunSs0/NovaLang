package com.novalang.workspace;

import com.novalang.runtime.Nova;
import com.novalang.runtime.CompiledNova;
import com.novalang.runtime.SchedulerHolder;
import com.novalang.compiler.parser.Parser;
import com.novalang.compiler.lexer.Lexer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class InlineScriptTest {
    @TempDir Path directory;

    @BeforeEach void installScheduler() {
        SchedulerHolder.set(WorkspaceTestSupport.directScheduler());
    }

    @AfterEach void clearScheduler() {
        com.novalang.runtime.interpreter.Interpreter.resetGlobalSchedulerState();
    }

    @Test void parserKeepsImportsAndLocalDeclarations() {
        String code = "import java java.util.UUID\nval player = UUID.randomUUID()\nreturn player != null";
        com.novalang.compiler.ast.decl.Program program = new Parser(new Lexer(code, "action"), "action")
                .parseInline("execute", "Boolean");
        assertEquals(1, program.getImports().size());
        assertEquals(1, program.getDeclarations().size());
        String nested = "fun execute(): Boolean {\nimport java java.util.UUID\nreturn true\n}";
        assertEquals(1, new Parser(new Lexer(nested, "action"), "action")
                .parseTolerant().getProgram().getImports().size());
    }

    @Test void compilesPrimitiveBooleanWithoutStateFields() throws Exception {
        Nova nova = new Nova();
        Map<String, byte[]> artifact = nova.compileInlineToBytecodeArtifact(
                "import java java.util.UUID\nval player = UUID.fromString(\"00000000-0000-0000-0000-000000000001\")\n"
                        + "var calls = 0\ncalls++\nif (player == null) { return false }\nreturn calls == 1",
                "longsword.nova", "execute", "Boolean");
        WorkspaceGenerationClassLoader loader = new WorkspaceGenerationClassLoader(getClass().getClassLoader());
        WorkspaceBytecodeArtifactCache cache = new WorkspaceBytecodeArtifactCache();
        WorkspaceBytecodeArtifactCache.CacheKey key = new WorkspaceBytecodeArtifactCache.CacheKey(
                getClass().getClassLoader(), "inline", "test", "boolean");
        Map<String, Class<?>> classes = cache.getOrCompile(key, () -> artifact).loadInto(loader);
        Class<?> module = classes.values().iterator().next();
        assertEquals(boolean.class, module.getMethod("execute").getReturnType());
        assertEquals(0, module.getDeclaredFields().length);
        CompiledNova compiled = nova.createCompiledNova(classes);
        assertEquals(true, compiled.call("execute"));
        assertEquals(true, compiled.call("execute"));
    }

    @Test void workspaceDefersActionsAndSharesImports() throws Exception {
        Path config = WorkspaceTestSupport.writeConfig(directory, "caller", "  - \"base\"\n");
        WorkspaceTestSupport.write(directory, "base.nova", "fun base(): Int = 1");
        WorkspaceTestSupport.write(directory, "api.nova",
                "import java java.util.UUID\nfun valid(): Boolean = true");
        RuntimeWorkspace workspace = new RuntimeWorkspace(config, nova -> {});
        String code = "import \"@/api\"\nvar calls = 0\ncalls++\n"
                + "val player = UUID.randomUUID()\nreturn valid() && player != null && calls == 1";
        workspace.registerVirtualSource(SourceUnit.inline("action/a", code, directory.resolve("test.yml"),
                "actions.a", 20, "execute", "Boolean"), true);
        workspace.registerVirtualSource(SourceUnit.inline("action/b", code, directory.resolve("test.yml"),
                "actions.b", 40, "execute", "Boolean"), true);
        try {
            workspace.load();
            assertEquals(true, workspace.invoke("action/a", "execute", Collections.emptyMap(), null));
            assertEquals(true, workspace.invoke("action/a", "execute", Collections.emptyMap(), null));
            assertEquals(true, workspace.invoke("action/b", "execute", Collections.emptyMap(), null));
        } finally {
            workspace.dispose();
        }
    }

    @Test void runtimeFailureMapsToOriginalYamlLine() throws Exception {
        Path config = WorkspaceTestSupport.writeConfig(directory, "caller", "  - \"base\"\n");
        WorkspaceTestSupport.write(directory, "base.nova", "fun base(): Int = 1");
        RuntimeWorkspace workspace = new RuntimeWorkspace(config, nova -> {});
        workspace.registerVirtualSource(SourceUnit.inline("action", "val n = 1\nerror(\"failed\")\nreturn true",
                directory.resolve("test.yml"), "actions.longsword", 30, "execute", "Boolean"), true);
        try {
            workspace.load();
            WorkspaceException failure = assertThrows(WorkspaceException.class,
                    () -> workspace.invoke("action", "execute", Collections.emptyMap(), null));
            assertTrue(failure.getMessage().contains("[actions.longsword]:31"), failure.toString());
        } finally {
            workspace.dispose();
        }
    }

    @Test void rejectsWrongReturnType() {
        assertThrows(RuntimeException.class, () -> new Nova().compileInlineToBytecodeArtifact(
                "return 123", "action", "execute", "Boolean"));
    }

    @Test void rejectsMissingReturn() {
        assertThrows(RuntimeException.class, () -> new Nova().compileInlineToBytecodeArtifact(
                "val player = 1\nif (player == 2) { return false }", "action", "execute", "Boolean"));
    }
    @Test void invocationBindingsAndFinallyRemainIsolated() throws Exception {
        Path config = WorkspaceTestSupport.writeConfig(directory, "caller", "  - \"base\"\n");
        WorkspaceTestSupport.write(directory, "base.nova", "fun base(): Int = 1");
        java.util.concurrent.atomic.AtomicInteger completed = new java.util.concurrent.atomic.AtomicInteger();
        RuntimeWorkspace workspace = new RuntimeWorkspace(config, nova -> nova.defineFunction(
                "recordCompletion", (com.novalang.runtime.Function0<Object>) completed::incrementAndGet));
        String code = "import java com.novalang.runtime.NovaScriptContext\n"
                + "val player = NovaScriptContext.get(\"player\")\n"
                + "if (player == null) { return false }\n"
                + "var granted = false\ntry {\n"
                + "if (player == \"broken\") { throw \"failed\" }\ngranted = true\n"
                + "} catch (exception: Exception) { return false } finally { recordCompletion() }\n"
                + "return granted";
        workspace.registerVirtualSource(SourceUnit.inline("action", code, directory.resolve("test.yml"),
                "actions.a", 1, "execute", "Boolean"), true);
        try {
            workspace.load();
            assertEquals(0, completed.get());
            assertEquals(true, workspace.invoke("action", "execute", Collections.singletonMap("player", "alice"), null));
            assertEquals(false, workspace.invoke("action", "execute", Collections.singletonMap("player", "broken"), null));
            assertEquals(false, workspace.invoke("action", "execute", Collections.emptyMap(), null));
            assertEquals(true, workspace.invoke("action", "execute", Collections.singletonMap("player", "bob"), null));
            assertEquals(3, completed.get());
        } finally {
            workspace.dispose();
        }
    }

    @Test void cacheIncludesReturnContract() throws Exception {
        Path config = WorkspaceTestSupport.writeConfig(directory, "caller", "  - \"base\"\n");
        WorkspaceTestSupport.write(directory, "base.nova", "fun base(): Int = 1");
        WorkspaceBytecodeArtifactCache cache = new WorkspaceBytecodeArtifactCache();
        RuntimeWorkspace first = new RuntimeWorkspace(config, nova -> {}, cache);
        first.registerVirtualSource(SourceUnit.inline("action", "return true", null,
                "actions.a", 1, "execute", "Boolean"), true);
        try {
            first.load();
            assertEquals(true, first.invoke("action", "execute", Collections.emptyMap(), null));
        } finally {
            first.dispose();
        }
        RuntimeWorkspace second = new RuntimeWorkspace(config, nova -> {}, cache);
        second.registerVirtualSource(SourceUnit.inline("action", "return true", null,
                "actions.a", 1, "execute", "Int"), true);
        try {
            assertThrows(WorkspaceException.class, second::load);
        } finally {
            second.dispose();
        }
    }

    @Test void rejectsNullableBooleanAndMainEntry() {
        assertThrows(RuntimeException.class, () -> new Nova().compileInlineToBytecodeArtifact(
                "return null", "action", "execute", "Boolean"));
        assertThrows(IllegalArgumentException.class, () -> SourceUnit.inline(
                "action", "return true", null, "actions.a", 1, "main", "Boolean"));
    }

}
