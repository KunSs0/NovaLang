package com.novalang.runtime.codegen;

import com.novalang.ir.NovaIrCompiler;
import com.novalang.runtime.CompiledNova;
import com.novalang.runtime.Nova;
import com.novalang.runtime.compilation.CompilationConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Java 接口枚举返回值 codegen")
class JavaInterfaceEnumReturnCodegenTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("实现 Java 接口时返回嵌套枚举常量")
    void interfaceMethodShouldReturnNestedJavaEnum() throws Exception {
        try (URLClassLoader loader = compileFixture()) {
            NovaIrCompiler compiler = new NovaIrCompiler();
            String source =
                    "import java fixture.DramaBehaviorSpec\n" +
                    "import java fixture.DramaBehaviorSpec.Kind\n" +
                    "class ActionBehavior : DramaBehaviorSpec {\n" +
                    "    fun getKind() { return Kind.ACTION }\n" +
                    "}\n";

            Map<String, Class<?>> loaded = compiler.compileAndLoad(source, "enum-return.nova", loader);
            Class<?> actionBehavior = loaded.get("ActionBehavior");
            Class<?> spec = loader.loadClass("fixture.DramaBehaviorSpec");
            Class<?> kind = loader.loadClass("fixture.DramaBehaviorSpec$Kind");

            assertNotNull(actionBehavior);
            assertTrue(spec.isAssignableFrom(actionBehavior));
            Object instance = actionBehavior.getDeclaredConstructor().newInstance();
            ClassLoader previousLoader = Thread.currentThread().getContextClassLoader();
            Object result;
            try {
                Thread.currentThread().setContextClassLoader(loader);
                result = spec.getMethod("getKind").invoke(instance);
            } finally {
                Thread.currentThread().setContextClassLoader(previousLoader);
            }

            assertEquals(kind.getField("ACTION").get(null), result);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"DramaBehaviorSpec.Kind", "fixture.DramaBehaviorSpec.Kind", "BehaviorSpec.Kind"})
    @DisplayName("外层类限定的嵌套枚举参数可由 Java 接口回调调用")
    void interfaceCallbackShouldUseNestedEnumParameter(String parameterType) throws Exception {
        try (URLClassLoader loader = compileFixture()) {
            NovaIrCompiler compiler = new NovaIrCompiler();
            String source =
                    "import java fixture.DramaBehaviorSpec\n" +
                    "import java fixture.DramaBehaviorSpec as BehaviorSpec\n" +
                    "fun echo(kind: " + parameterType + "): " + parameterType + " { return kind }\n" +
                    "class ActionBehavior : DramaBehaviorSpec {\n" +
                    "    fun getKind() { return DramaBehaviorSpec.Kind.ACTION }\n" +
                    "    fun describe(kind: " + parameterType + "): String {\n" +
                    "        if (kind is " + parameterType + ") { return echo(kind).toString() }\n" +
                    "        return \"wrong type\"\n" +
                    "    }\n" +
                    "}\n";
            Map<String, Class<?>> loaded = compiler.compileAndLoad(source, "enum-callback.nova", loader);
            Class<?> spec = loader.loadClass("fixture.DramaBehaviorSpec");
            Class<?> kind = loader.loadClass("fixture.DramaBehaviorSpec$Kind");
            Object instance = loaded.get("ActionBehavior").getDeclaredConstructor().newInstance();

            assertEquals("ACTION", spec.getMethod("describe", kind)
                    .invoke(instance, kind.getField("ACTION").get(null)));
            assertEquals("PASSIVE", spec.getMethod("describe", kind)
                    .invoke(instance, kind.getField("PASSIVE").get(null)));
        }
    }

    @Test
    @DisplayName("跨模块导入外层类后可以执行嵌套枚举回调")
    void importedModuleShouldPreserveNestedEnumParameter() throws Exception {
        try (URLClassLoader loader = compileFixture()) {
            Files.write(tempDir.resolve("api.nova"),
                    "import java fixture.DramaBehaviorSpec\n".getBytes(StandardCharsets.UTF_8));
            Path entry = tempDir.resolve("entry.nova");
            Files.write(entry, ("import \"./api\"\n" +
                    "class ActionBehavior : DramaBehaviorSpec {\n" +
                    "    fun getKind() { return DramaBehaviorSpec.Kind.ACTION }\n" +
                    "    fun describe(kind: DramaBehaviorSpec.Kind): String { return kind.toString() }\n" +
                    "}\n" +
                    "fun create(): DramaBehaviorSpec { return ActionBehavior() }\n")
                    .getBytes(StandardCharsets.UTF_8));
            Nova nova = new Nova();
            nova.setScriptClassLoader(loader);
            try (CompiledNova program = nova.compileToBytecode(entry, new CompilationConfig.Builder().build())) {
                program.run();
                Object instance = program.call("create");
                Class<?> spec = loader.loadClass("fixture.DramaBehaviorSpec");
                Class<?> kind = loader.loadClass("fixture.DramaBehaviorSpec$Kind");
                assertEquals("ACTION", spec.getMethod("describe", kind)
                        .invoke(instance, kind.getField("ACTION").get(null)));
            }
        }
    }

    private URLClassLoader compileFixture() throws Exception {
        Path sourceDir = tempDir.resolve("src").resolve("fixture");
        Path classesDir = tempDir.resolve("classes");
        Files.createDirectories(sourceDir);
        Files.createDirectories(classesDir);
        Path sourceFile = sourceDir.resolve("DramaBehaviorSpec.java");
        Files.write(sourceFile,
                ("package fixture;\n" +
                        "public interface DramaBehaviorSpec {\n" +
                        "    enum Kind { ACTION, PASSIVE }\n" +
                        "    Kind getKind();\n" +
                        "    default String describe(Kind kind) { return \"default\"; }\n" +
                        "}\n").getBytes(StandardCharsets.UTF_8));

        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        assertNotNull(javaCompiler, "测试需要 JDK 编译器");
        int exitCode = javaCompiler.run(null, null, null,
                "-encoding", "UTF-8", "-d", classesDir.toString(), sourceFile.toString());
        assertEquals(0, exitCode, "Java 接口 fixture 应编译成功");
        return new URLClassLoader(new URL[]{classesDir.toUri().toURL()}, null);
    }
}
