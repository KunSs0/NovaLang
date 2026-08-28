package com.novalang.runtime.codegen;

import com.novalang.ir.NovaIrCompiler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

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
                        "}\n").getBytes(StandardCharsets.UTF_8));

        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        assertNotNull(javaCompiler, "测试需要 JDK 编译器");
        int exitCode = javaCompiler.run(null, null, null,
                "-encoding", "UTF-8", "-d", classesDir.toString(), sourceFile.toString());
        assertEquals(0, exitCode, "Java 接口 fixture 应编译成功");
        return new URLClassLoader(new URL[]{classesDir.toUri().toURL()}, null);
    }
}
