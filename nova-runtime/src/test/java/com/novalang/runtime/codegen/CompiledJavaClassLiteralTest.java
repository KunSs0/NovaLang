package com.novalang.runtime.codegen;

import com.novalang.runtime.CompiledNova;
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

@DisplayName("Java 类字面量互操作")
class CompiledJavaClassLiteralTest {

    @TempDir
    Path tempDirectory;

    @Test
    @DisplayName("显式导入的 Java 类型应匹配 Class<T> 重载并传递 JVM 类字面量")
    void importedJavaTypeShouldUseClassLiteralForGenericOverload() throws Exception {
        try (URLClassLoader fixtureLoader = compileFixture()) {
            Nova nova = new Nova().setScriptClassLoader(fixtureLoader);
            CompiledNova compiled = nova.compileToBytecode(
                    "import java dynamic.ClassLiteralFixture\n"
                            + "ClassLiteralFixture.choose(ClassLiteralFixture)",
                    "java-class-literal.nova");
            assertEquals("class:dynamic.ClassLiteralFixture", compiled.run());
        }
    }

    @Test
    @DisplayName("局部值应优先于同名 Java 类型导入")
    void localValueShouldShadowImportedJavaTypeInExpression() throws Exception {
        Nova nova = new Nova();
        CompiledNova compiled = nova.compileToBytecode(
                "import java java.lang.String\n"
                        + "fun read(): String {\n"
                        + "    var String = \"x\"\n"
                        + "    String = \"y\"\n"
                        + "    return String\n"
                        + "}\n"
                        + "read()",
                "java-class-literal-shadow.nova");

        assertEquals("y", compiled.run());
    }

    @Test
    @DisplayName("参数应优先于同名 Java 类型导入")
    void parameterShouldShadowImportedJavaTypeInExpression() throws Exception {
        Nova nova = new Nova();
        CompiledNova compiled = nova.compileToBytecode(
                "import java java.util.ArrayList\n"
                        + "fun read(ArrayList: String): String {\n"
                        + "    return ArrayList\n"
                        + "}\n"
                        + "read(\"parameter\")",
                "java-class-literal-parameter-shadow.nova");

        assertEquals("parameter", compiled.run());
    }

    private URLClassLoader compileFixture() throws Exception {
        Path sourceDirectory = tempDirectory.resolve("src").resolve("dynamic");
        Path classesDirectory = tempDirectory.resolve("classes");
        Files.createDirectories(sourceDirectory);
        Files.createDirectories(classesDirectory);
        Path sourceFile = sourceDirectory.resolve("ClassLiteralFixture.java");
        String source = "package dynamic;\n"
                + "public final class ClassLiteralFixture {\n"
                + "    public static <T> String choose(Class<T> type) {\n"
                + "        return \"class:\" + type.getName();\n"
                + "    }\n"
                + "    public static String choose(Object value) {\n"
                + "        return \"object\";\n"
                + "    }\n"
                + "}\n";
        Files.write(sourceFile, source.getBytes(StandardCharsets.UTF_8));

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("JDK compiler is required for the Java fixture");
        }
        int result = compiler.run(null, null, null,
                "-d", classesDirectory.toString(), sourceFile.toString());
        if (result != 0) {
            throw new IllegalStateException("Failed to compile Java class literal fixture: " + result);
        }
        return new URLClassLoader(
                new URL[]{classesDirectory.toUri().toURL()},
                getClass().getClassLoader());
    }
}
