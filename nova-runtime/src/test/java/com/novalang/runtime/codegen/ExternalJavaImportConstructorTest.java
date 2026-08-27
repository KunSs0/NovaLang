package com.novalang.runtime.codegen;

import com.novalang.runtime.Nova;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 验证脚本级 ClassLoader 中的第三方 Java 类可通过显式 import 直接构造。
 *
 * <p>测试夹具只包含一个通用 Java 值对象，不依赖 Bukkit 或其他宿主业务类型。</p>
 */
@DisplayName("脚本级 ClassLoader 的 Java 类导入构造")
class ExternalJavaImportConstructorTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("解释器：import java 后可直接构造脚本级 Java 类")
    void interpreterShouldConstructImportedExternalClass() throws Exception {
        try (URLClassLoader loader = compileFixture()) {
            Nova nova = new Nova().setScriptClassLoader(loader);
            Object result = nova.eval(
                    "import java dynamic.ConstructorFixture\n" +
                            "val fixture = ConstructorFixture(42)\n" +
                            "fixture.getValue()",
                    "external-import.nova");

            assertEquals(42, ((Number) result).intValue());
        }
    }

    @Test
    @DisplayName("字节码：import java 后可直接构造脚本级 Java 类")
    void compiledShouldConstructImportedExternalClass() throws Exception {
        try (URLClassLoader loader = compileFixture()) {
            Nova nova = new Nova().setScriptClassLoader(loader);
            Object result = nova.compileToBytecode(
                    "import java dynamic.ConstructorFixture\n" +
                            "val fixture = ConstructorFixture(42)\n" +
                            "fixture.getValue()",
                    "external-import-compiled.nova").run();

            assertEquals(42, ((Number) result).intValue());
        }
    }

    /**
     * 编译并加载测试用第三方 Java 类。
     *
     * @return 只包含测试类目录的独立 ClassLoader
     */
    private URLClassLoader compileFixture() throws Exception {
        Path classesDir = tempDir.resolve("classes");
        Path srcDir = tempDir.resolve("src").resolve("dynamic");
        Files.createDirectories(srcDir);
        Files.createDirectories(classesDir);

        Path javaFile = srcDir.resolve("ConstructorFixture.java");
        Files.write(javaFile,
                ("package dynamic;\n" +
                        "public final class ConstructorFixture {\n" +
                        "    private final int value;\n" +
                        "    public ConstructorFixture(int value) { this.value = value; }\n" +
                        "    public int getValue() { return value; }\n" +
                        "}\n").getBytes(StandardCharsets.UTF_8));

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        assertNotNull(compiler, "测试需要 JDK 编译器");
        int exit = compiler.run(null, null, null,
                "-encoding", "UTF-8",
                "-d", classesDir.toString(),
                javaFile.toString());
        assertEquals(0, exit, "通用 Java 测试夹具应编译成功");

        return new URLClassLoader(new URL[]{classesDir.toUri().toURL()}, null);
    }
}
