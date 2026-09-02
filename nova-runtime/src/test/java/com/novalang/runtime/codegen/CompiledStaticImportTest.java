package com.novalang.runtime.codegen;

import com.novalang.ir.NovaIrCompiler;
import com.novalang.runtime.Nova;
import com.novalang.runtime.CompiledNova;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.net.URL;
import java.net.URLClassLoader;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Java static import compiled/interpreted paths")
class CompiledStaticImportTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("compiled explicit alias resolves static overload and field")
    void compiledExplicitAliasShouldResolveStaticMembers() {
        Nova nova = new Nova();
        assertEquals(7, nova.compileToBytecode(
                "import static java.lang.Math.max as maxInt\n" +
                        "import static java.lang.Integer.MAX_VALUE as maxValue\n" +
                        "maxInt(7, 3) + maxValue * 0",
                "static-import.nova").run());
    }

    @Test
    @DisplayName("compiled imported Java class resolves static fields")
    void compiledImportedJavaClassShouldResolveStaticField() {
        Nova nova = new Nova();
        assertEquals(Integer.MAX_VALUE, nova.compileToBytecode(
                "import java java.lang.Integer\nInteger.MAX_VALUE",
                "java-class-static-field.nova").run());
    }

    @Test
    @DisplayName("compiled Java overload prefers String over Supplier for string literals")
    void compiledJavaOverloadShouldPreferExactStringType() {
        Nova nova = new Nova();
        assertEquals("done", nova.compileToBytecode(
                "import java java.util.logging.Logger\n" +
                        "Logger.getGlobal().info(\"message\")\n" +
                        "\"done\"",
                "java-string-overload.nova").run());
    }

    @Test
    @DisplayName("compiled wildcard resolves static methods and fields")
    void compiledWildcardShouldResolveStaticMembers() {
        Nova nova = new Nova();
        assertEquals(Math.PI, nova.compileToBytecode(
                "import static java.lang.Math.*\nPI",
                "static-wildcard.nova").run());
        assertEquals(9, nova.compileToBytecode(
                "import static java.lang.Math.*\nmax(4, 9)",
                "static-wildcard-method.nova").run());
    }

    @Test
    @DisplayName("compiled static import accepts generated dollar-prefixed module class")
    void compiledStaticImportShouldAcceptGeneratedModuleClass() throws Exception {
        try (URLClassLoader loader = compileFixture()) {
            Nova nova = new Nova().setScriptClassLoader(loader);
            assertEquals(42, nova.compileToBytecode(
                    "import static dynamic.$Module.answer\n"
                            + "answer(40, 2)",
                    "generated-module-static-import.nova").run());
        }
    }

    @Test
    @DisplayName("compiled entry links functions from a packaged Nova module")
    void compiledEntryShouldLinkPackagedNovaModule() {
        NovaIrCompiler compiler = new NovaIrCompiler();
        Map<String, Class<?>> sharedClasses = compiler.compileAndLoad(
                "package dynamic.shared\n"
                        + "fun answer(left: Int, right: Int): Int { return left + right }\n",
                "shared-module.nova", getClass().getClassLoader());
        Class<?> sharedModule = sharedClasses.get("dynamic.shared.$Module");
        assertNotNull(sharedModule);

        Nova nova = new Nova().setScriptClassLoader(sharedModule.getClassLoader());
        assertEquals(42, nova.compileToBytecode(
                "package dynamic.entry\n"
                        + "import static dynamic.shared.$Module.answer\n"
                        + "fun execute(): Int { return answer(40, 2) }\n"
                        + "execute()\n",
                "linked-entry.nova").run());
    }

    @Test
    @DisplayName("interpreter static import keeps alias semantics")
    void interpreterShouldResolveStaticAlias() {
        Nova nova = new Nova();
        assertEquals(7, nova.eval(
                "import static java.lang.Math.max as maxInt\nmaxInt(7, 3)",
                "static-interpreter.nova"));
    }

    @Test
    @DisplayName("compiled static import loads classes from configured script ClassLoader")
    void compiledStaticImportShouldUseScriptClassLoader() throws Exception {
        try (URLClassLoader loader = compileFixture()) {
            Nova nova = new Nova().setScriptClassLoader(loader);
            assertEquals(42, nova.compileToBytecode(
                    "import java dynamic.StaticFixture\nStaticFixture.answer(40, 2)",
                    "external-java-static.nova").run());
            assertEquals(42, nova.compileToBytecode(
                    "import static dynamic.StaticFixture.answer as result\nresult(40, 2)",
                    "external-static-import.nova").run());
            assertEquals(99, nova.compileToBytecode(
                    "import static dynamic.StaticFixture.VALUE as result\nresult",
                    "external-static-field.nova").run());
            assertEquals(99, nova.compileToBytecode(
                    "import java dynamic.StaticFixture\nStaticFixture.VALUE",
                    "external-java-class-field.nova").run());
            assertEquals(42L, nova.compileToBytecode(
                    "import static dynamic.StaticFixture.INSTANCE\nINSTANCE.currentTick()",
                    "external-static-instance-field.nova").run());
            assertEquals(42L, nova.compileToBytecode(
                    "import static dynamic.StaticFixture.INSTANCE as STATIC_FIXTURE_INSTANCE\n" +
                            "val fixture = STATIC_FIXTURE_INSTANCE\n" +
                            "fixture.currentTick()",
                    "external-aliased-static-instance-field.nova").run());
            assertEquals("abc", nova.compileToBytecode(
                    "import java dynamic.StaticFixture\n" +
                            "StaticFixture.handle(\"a\", \"b\", \"c\")",
                    "external-java-static-handle.nova").run());
        }
    }

    @Test
    @DisplayName("compiled Java interface static method uses InterfaceMethodref")
    void compiledJavaInterfaceStaticMethodShouldUseInterfaceMethodRef() throws Exception {
        try (URLClassLoader loader = compileFixture()) {
            Nova nova = new Nova().setScriptClassLoader(loader);
            assertEquals(42, nova.compileToBytecode(
                    "import java dynamic.StaticInterfaceFixture\n" +
                            "StaticInterfaceFixture.answer(40, 2)",
                    "external-java-interface-static.nova").run());
        }
    }

    @Test
    @DisplayName("generated object keeps static imports after execution context ends")
    void generatedObjectShouldKeepStaticImportsOutsideExecutionContext() throws Exception {
        try (URLClassLoader fixtureLoader = compileFixture();
             URLClassLoader unrelatedContextLoader = new URLClassLoader(new URL[0], null)) {
            String source =
                    "import static dynamic.StaticFixture.INSTANCE\n" +
                            "import static dynamic.StaticFixture.answer\n" +
                            "import static dynamic.StaticFixture.Kind.FLOW\n" +
                            "object StaticImportReader {\n" +
                            "    fun readInstance(): Any { return INSTANCE.currentTick() }\n" +
                            "    fun readMethod(): Any { return answer(40, 2) }\n" +
                            "    fun readNestedEnum(): Any { return FLOW }\n" +
                            "}\n";
            NovaIrCompiler compiler = new NovaIrCompiler();
            Map<String, Class<?>> loaded = compiler.compileAndLoad(
                    source, "static-import-lifecycle.nova", fixtureLoader);
            Class<?> readerClass = loaded.get("StaticImportReader");
            assertNotNull(readerClass);
            Object reader = readerClass.getField("INSTANCE").get(null);

            ClassLoader previousContextLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(unrelatedContextLoader);
            try {
                Method readInstance = readerClass.getDeclaredMethod("readInstance");
                Method readMethod = readerClass.getDeclaredMethod("readMethod");
                Method readNestedEnum = readerClass.getDeclaredMethod("readNestedEnum");
                assertEquals(42L, readInstance.invoke(reader));
                assertEquals(42, readMethod.invoke(reader));
                Class<?> kindClass = fixtureLoader.loadClass("dynamic.StaticFixture$Kind");
                Object flow = kindClass.getField("FLOW").get(null);
                assertSame(flow, readNestedEnum.invoke(reader));
            } finally {
                Thread.currentThread().setContextClassLoader(previousContextLoader);
            }
        }
    }

    @Test
    @DisplayName("unresolved static import owner fails during compilation")
    void unresolvedStaticImportOwnerShouldFailDuringCompilation() {
        Nova nova = new Nova();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> nova.compileToBytecode(
                        "import static unavailable.Outer.Inner.VALUE\nVALUE",
                        "unresolved-static-import.nova"));
        assertTrue(exception.getMessage().contains(
                "Cannot resolve Java static import owner: unavailable.Outer.Inner"));
    }

    @Test
    @DisplayName("compiled Java member chain keeps covariant return instead of bridge return")
    void compiledJavaMemberChainShouldSelectCovariantReturn() throws Exception {
        try (URLClassLoader loader = compileFixture()) {
            Nova nova = new Nova().setScriptClassLoader(loader);
            assertEquals("derived", nova.compileToBytecode(
                    "import java dynamic.CovariantFactory\n" +
                            "CovariantFactory.instance().manager().derivedOnly()",
                    "covariant-java-member.nova").run());
        }
    }

    @Test
    @DisplayName("compile-time Java class inspection does not run static initializer")
    void compileShouldNotInitializeImportedJavaClass() throws Exception {
        Path marker = tempDir.resolve("initialization.marker");
        Files.deleteIfExists(marker);
        String previousMarker = System.getProperty("nova.fixture.init.marker");
        System.setProperty("nova.fixture.init.marker", marker.toString());
        try (URLClassLoader loader = compileFixture()) {
            Nova nova = new Nova().setScriptClassLoader(loader);
            CompiledNova compiled = nova.compileToBytecode(
                    "import java dynamic.InitializationFixture\n" +
                            "InitializationFixture.answer()",
                    "java-initialization.nova");
            assertFalse(Files.exists(marker), "编译和 import 不应触发静态初始化");
            assertEquals(7, compiled.run());
            assertTrue(Files.exists(marker), "实际调用应触发静态初始化");
        } finally {
            if (previousMarker == null) {
                System.clearProperty("nova.fixture.init.marker");
            } else {
                System.setProperty("nova.fixture.init.marker", previousMarker);
            }
        }
    }

    private URLClassLoader compileFixture() throws Exception {
        Path classesDir = tempDir.resolve("classes");
        Path srcDir = tempDir.resolve("src").resolve("dynamic");
        Files.createDirectories(srcDir);
        Files.createDirectories(classesDir);
        Path javaFile = srcDir.resolve("StaticFixture.java");
        Files.write(javaFile,
                ("package dynamic;\n" +
                        "public final class StaticFixture {\n" +
                        "    public static final int VALUE = 99;\n" +
                        "    public static final StaticFixture INSTANCE = new StaticFixture();\n" +
                        "    public enum Kind { FLOW, WAIT }\n" +
                        "    public static int answer(int left, int right) { return left + right; }\n" +
                        "    public long currentTick() { return 42L; }\n" +
                        "    public static String handle(String first, String second, String third) {\n" +
                        "        return first + second + third;\n" +
                        "    }\n" +
                        "}\n").getBytes(StandardCharsets.UTF_8));
        Path covariantJavaFile = srcDir.resolve("CovariantFactory.java");
        Files.write(covariantJavaFile,
                ("package dynamic;\n" +
                        "public final class CovariantFactory {\n" +
                        "    public interface BaseManager {}\n" +
                        "    public interface FactoryApi { BaseManager manager(); }\n" +
                        "    public static final class DerivedManager implements BaseManager {\n" +
                        "        public String derivedOnly() { return \"derived\"; }\n" +
                        "    }\n" +
                        "    public static final class FactoryImpl implements FactoryApi {\n" +
                        "        @Override public DerivedManager manager() { return new DerivedManager(); }\n" +
                        "    }\n" +
                        "    public static FactoryImpl instance() { return new FactoryImpl(); }\n" +
                        "}\n").getBytes(StandardCharsets.UTF_8));
        Path initializationJavaFile = srcDir.resolve("InitializationFixture.java");
        Files.write(initializationJavaFile,
                ("package dynamic;\n" +
                        "import java.nio.charset.StandardCharsets;\n" +
                        "import java.nio.file.Files;\n" +
                        "import java.nio.file.Path;\n" +
                        "import java.nio.file.Paths;\n" +
                        "public final class InitializationFixture {\n" +
                        "    static {\n" +
                        "        try {\n" +
                        "            Path marker = Paths.get(System.getProperty(\"nova.fixture.init.marker\"));\n" +
                        "            Files.write(marker, \"initialized\".getBytes(StandardCharsets.UTF_8));\n" +
                        "        } catch (Exception exception) {\n" +
                        "            throw new ExceptionInInitializerError(exception);\n" +
                        "        }\n" +
                        "    }\n" +
                        "    public static int answer() { return 7; }\n" +
                        "}\n").getBytes(StandardCharsets.UTF_8));
        Path staticInterfaceJavaFile = srcDir.resolve("StaticInterfaceFixture.java");
        Files.write(staticInterfaceJavaFile,
                ("package dynamic;\n" +
                        "public interface StaticInterfaceFixture {\n" +
                        "    static int answer(int left, int right) { return left + right; }\n" +
                        "}\n").getBytes(StandardCharsets.UTF_8));
        Path generatedModuleJavaFile = srcDir.resolve("$Module.java");
        Files.write(generatedModuleJavaFile,
                ("package dynamic;\n" +
                        "public final class $Module {\n" +
                        "    public static int answer(int left, int right) { return left + right; }\n" +
                        "}\n").getBytes(StandardCharsets.UTF_8));
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        assertNotNull(compiler, "测试需要 JDK 编译器");
        int exit = compiler.run(null, null, null,
                "-encoding", "UTF-8", "-d", classesDir.toString(),
                javaFile.toString(), covariantJavaFile.toString(), initializationJavaFile.toString(),
                staticInterfaceJavaFile.toString(), generatedModuleJavaFile.toString());
        assertEquals(0, exit, "静态导入测试夹具应编译成功");
        return new URLClassLoader(new URL[]{classesDir.toUri().toURL()}, null);
    }
}
