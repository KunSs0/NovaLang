package com.novalang.runtime.compilation;

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
        SchedulerHolder.set(CompilationTestSupport.directScheduler());
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
        CompilationClassLoader loader = new CompilationClassLoader(getClass().getClassLoader());
        BytecodeArtifactCache cache = new BytecodeArtifactCache();
        BytecodeArtifactCache.CacheKey key = new BytecodeArtifactCache.CacheKey(
                getClass().getClassLoader(), "inline", "test", "boolean");
        Map<String, Class<?>> classes = cache.getOrCompile(key, () -> artifact).loadInto(loader);
        Class<?> module = classes.values().iterator().next();
        assertEquals(boolean.class, module.getMethod("execute").getReturnType());
        assertEquals(0, module.getDeclaredFields().length);
        CompiledNova compiled = nova.createCompiledNova(classes);
        assertEquals(true, compiled.call("execute"));
        assertEquals(true, compiled.call("execute"));
    }

    /** 两个行内脚本分别作为入口编译，均能导入依赖且动作只在调用时执行。 */
    @Test void compilationDefersActionsAndSharesImports() throws Exception {
        CompilationTestSupport.write(directory, "api.nova",
                "import java java.util.UUID\nfun valid(): Boolean = true");
        Nova nova = new Nova();
        BytecodeArtifactCache cache = new BytecodeArtifactCache();
        CompilationConfig metadata = new CompilationConfig.Builder().alias("@", directory).build();
        String code = "import \"@/api\"\nvar calls = 0\ncalls++\n"
                + "val player = UUID.randomUUID()\nreturn valid() && player != null && calls == 1";
        SourceUnit firstEntry = SourceUnit.inline("action/a", code, directory.resolve("test.yml"),
                "actions.a", 20, "executeA", "Boolean");
        SourceUnit secondEntry = SourceUnit.inline("action/b", code, directory.resolve("test.yml"),
                "actions.b", 40, "executeB", "Boolean");
        try (CompiledNova first = nova.compileToBytecode(firstEntry, metadata, cache);
             CompiledNova second = nova.compileToBytecode(secondEntry, metadata, cache)) {
            // 初始化仅链接动作；局部变量在每次显式调用时重新创建。
            first.run();
            second.run();
            assertEquals(true, first.callIsolated("executeA", Collections.emptyMap()));
            assertEquals(true, first.callIsolated("executeA", Collections.emptyMap()));
            assertEquals(true, second.callIsolated("executeB", Collections.emptyMap()));
        }
    }

    /** 直接编译行内入口后，运行错误仍映射到 YAML 的原始行。 */
    @Test void runtimeFailureMapsToOriginalYamlLine() {
        SourceUnit entry = SourceUnit.inline("action", "val n = 1\nerror(\"failed\")\nreturn true",
                directory.resolve("test.yml"), "actions.longsword", 30, "execute", "Boolean");
        try (CompiledNova program = new Nova().compileToBytecode(entry, new CompilationConfig.Builder().build())) {
            program.run();
            CompilationException failure = assertThrows(CompilationException.class,
                    () -> program.callIsolated("execute", Collections.emptyMap()));
            assertTrue(failure.getMessage().contains("[actions.longsword]:31"), failure.toString());
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
    /** 行内入口每次调用使用独立绑定，且正常和异常路径都执行 finally。 */
    @Test void invocationBindingsAndFinallyRemainIsolated() {
        java.util.concurrent.atomic.AtomicInteger completed = new java.util.concurrent.atomic.AtomicInteger();
        Nova nova = new Nova();
        nova.defineFunction("recordCompletion", (com.novalang.runtime.Function0<Object>) completed::incrementAndGet);
        String code = "import java com.novalang.runtime.NovaScriptContext\n"
                + "val player = NovaScriptContext.get(\"player\")\n"
                + "if (player == null) { return false }\n"
                + "var granted = false\ntry {\n"
                + "if (player == \"broken\") { throw \"failed\" }\ngranted = true\n"
                + "} catch (exception: Exception) { return false } finally { recordCompletion() }\n"
                + "return granted";
        SourceUnit entry = SourceUnit.inline("action", code, directory.resolve("test.yml"),
                "actions.a", 1, "execute", "Boolean");
        try (CompiledNova program = nova.compileToBytecode(entry, new CompilationConfig.Builder().build())) {
            program.run();
            assertEquals(0, completed.get());
            assertEquals(true, program.callIsolated("execute", Collections.singletonMap("player", "alice")));
            assertEquals(false, program.callIsolated("execute", Collections.singletonMap("player", "broken")));
            assertEquals(false, program.callIsolated("execute", Collections.emptyMap()));
            assertEquals(true, program.callIsolated("execute", Collections.singletonMap("player", "bob")));
            assertEquals(3, completed.get());
        }
    }

    /** 相同入口文本采用不同返回契约时，缓存不能绕过返回类型检查。 */
    @Test void cacheIncludesReturnContract() {
        Nova nova = new Nova();
        CompilationConfig metadata = new CompilationConfig.Builder().build();
        BytecodeArtifactCache cache = new BytecodeArtifactCache();
        SourceUnit firstEntry = SourceUnit.inline("action", "return true", null,
                "actions.a", 1, "execute", "Boolean");
        try (CompiledNova first = nova.compileToBytecode(firstEntry, metadata, cache)) {
            first.run();
            assertEquals(true, first.callIsolated("execute", Collections.emptyMap()));
        }
        SourceUnit secondEntry = SourceUnit.inline("action", "return true", null,
                "actions.a", 1, "execute", "Int");
        assertThrows(CompilationException.class, () -> nova.compileToBytecode(secondEntry, metadata, cache));
    }

    @Test void rejectsNullableBooleanAndMainEntry() {
        assertThrows(RuntimeException.class, () -> new Nova().compileInlineToBytecodeArtifact(
                "return null", "action", "execute", "Boolean"));
        assertThrows(IllegalArgumentException.class, () -> SourceUnit.inline(
                "action", "return true", null, "actions.a", 1, "main", "Boolean"));
    }

}
