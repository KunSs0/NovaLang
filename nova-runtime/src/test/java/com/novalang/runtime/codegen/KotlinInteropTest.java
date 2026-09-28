package com.novalang.runtime.codegen;

import com.novalang.runtime.Nova;
import org.jetbrains.kotlin.cli.common.ExitCode;
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Kotlin 互操作回归测试：用真实 kotlinc 产物（而非手写 Java 模拟）验证 NovaLang 对
 * Kotlin 类的解析。
 *
 * fixture 源码位于 src/test/resources/kotlin-fixtures/dynamic/*.kt，测试期用
 * kotlin-compiler-embeddable 编译成 .class 后再加载，避免提交二进制产物、也不依赖
 * 绝对路径。
 *
 * companion object 相关用例是 docs/文档/Kotlin Companion Java 互操作问题.md 的回归：
 * `X.Companion.method()` 必须解析为「读外层静态字段 Companion → 实例方法调用」，
 * 而不是把 Companion 当成嵌套类做静态调用。
 */
@DisplayName("Kotlin interop with real kotlinc fixtures")
class KotlinInteropTest {

    private static final String[] FIXTURES = {"StaticFixture.kt", "KotlinShapes.kt"};

    private static final String IMPORT_STATIC_FIXTURE =
            "import java dynamic.StaticFixture\n";

    private static final String IMPORTS_SHAPES =
            "import java dynamic.Singleton\n" +
                    "import java dynamic.Holder\n" +
                    "import java dynamic.Point\n" +
                    "import java dynamic.KotlinShapesKt\n";

    @TempDir
    static Path tempDir;

    private static URLClassLoader fixtureLoader;

    @BeforeAll
    static void compileKotlinFixtures() throws Exception {
        Path srcDir = tempDir.resolve("src").resolve("dynamic");
        Path classesDir = tempDir.resolve("classes");
        Files.createDirectories(srcDir);
        Files.createDirectories(classesDir);

        List<String> sources = new ArrayList<>();
        for (String fixture : FIXTURES) {
            Path target = srcDir.resolve(fixture);
            try (InputStream in = KotlinInteropTest.class
                    .getResourceAsStream("/kotlin-fixtures/dynamic/" + fixture)) {
                assertNotNull(in, "缺少 Kotlin fixture 资源: " + fixture);
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
            sources.add(target.toString());
        }

        // kotlin-stdlib 来自测试运行时 classpath（kotlin-compiler-embeddable 的传递依赖）。
        Class<?> intrinsics = Class.forName("kotlin.jvm.internal.Intrinsics");
        Path stdlibJar = Path.of(
                intrinsics.getProtectionDomain().getCodeSource().getLocation().toURI());

        List<String> args = new ArrayList<>();
        args.add("-no-stdlib");
        args.add("-classpath");
        args.add(stdlibJar.toString());
        args.add("-jvm-target");
        args.add("1.8");
        args.add("-d");
        args.add(classesDir.toString());
        args.addAll(sources);

        ByteArrayOutputStream err = new ByteArrayOutputStream();
        ExitCode code = new K2JVMCompiler().exec(
                new PrintStream(err, true, StandardCharsets.UTF_8.name()),
                args.toArray(new String[0]));
        assertEquals(ExitCode.OK, code,
                "Kotlin fixture 编译失败:\n" + err.toString(StandardCharsets.UTF_8.name()));

        fixtureLoader = new URLClassLoader(new URL[]{
                classesDir.toUri().toURL(),
                stdlibJar.toUri().toURL()
        }, null);
    }

    @AfterAll
    static void closeFixtureLoader() throws Exception {
        if (fixtureLoader != null) {
            fixtureLoader.close();
        }
    }

    private static Nova nova() {
        return new Nova().setScriptClassLoader(fixtureLoader);
    }

    @Test
    @DisplayName("compiled path resolves companion field as instance method call")
    void compiledCompanionFieldResolvesInstanceMethods() {
        assertEquals("mapping:x", nova().compileToBytecode(
                IMPORT_STATIC_FIXTURE +
                        "StaticFixture.Companion.getMapping(\"x\")",
                "kotlin-companion-method.nova").run());
        assertEquals("instance", nova().compileToBytecode(
                IMPORT_STATIC_FIXTURE +
                        "StaticFixture.Companion.getINSTANCE()",
                "kotlin-companion-getter.nova").run());
    }

    @Test
    @DisplayName("interpreter path resolves companion field as instance method call")
    void interpreterCompanionFieldResolvesInstanceMethods() {
        assertEquals("mapping:x", nova().eval(
                IMPORT_STATIC_FIXTURE +
                        "StaticFixture.Companion.getMapping(\"x\")",
                "kotlin-companion-interpreter.nova"));
    }

    @Test
    @DisplayName("nested Kotlin class still resolves as nested type")
    void nestedKotlinClassStillResolves() {
        assertEquals("v", nova().compileToBytecode(
                IMPORT_STATIC_FIXTURE +
                        "StaticFixture.Nested(\"v\").value()",
                "kotlin-nested.nova").run());
        assertEquals(42L, nova().compileToBytecode(
                IMPORT_STATIC_FIXTURE +
                        "StaticFixture().currentTick()",
                "kotlin-instance-method.nova").run());
    }

    @Test
    @DisplayName("Kotlin object singleton resolves via INSTANCE field")
    void kotlinObjectSingletonResolves() {
        assertEquals("singleton-greet", nova().compileToBytecode(
                IMPORTS_SHAPES + "Singleton.INSTANCE.greet()",
                "kotlin-singleton.nova").run());
    }

    @Test
    @DisplayName("@JvmStatic and @JvmField expose real static members")
    void kotlinJvmStaticAndJvmFieldResolve() {
        assertEquals("singleton-static", nova().compileToBytecode(
                IMPORTS_SHAPES + "Singleton.staticGreet()",
                "kotlin-jvmstatic.nova").run());
        assertEquals("singleton-field", nova().compileToBytecode(
                IMPORTS_SHAPES + "Singleton.FIELD",
                "kotlin-jvmfield.nova").run());
    }

    @Test
    @DisplayName("companion @JvmStatic and @JvmField resolve on the outer class")
    void kotlinCompanionJvmStaticAndJvmFieldResolve() {
        assertEquals("holder-jvm-static", nova().compileToBytecode(
                IMPORTS_SHAPES + "Holder.jvmStatic()",
                "kotlin-companion-jvmstatic.nova").run());
        assertEquals("holder-const", nova().compileToBytecode(
                IMPORTS_SHAPES + "Holder.CONST",
                "kotlin-companion-jvmfield.nova").run());
    }

    @Test
    @DisplayName("companion plain method resolves via the Companion field")
    void kotlinCompanionPlainMethodResolves() {
        assertEquals("holder-plain", nova().compileToBytecode(
                IMPORTS_SHAPES + "Holder.Companion.plain()",
                "kotlin-companion-plain.nova").run());
    }

    @Test
    @DisplayName("top-level Kotlin function resolves via the file facade class")
    void kotlinTopLevelFunctionResolves() {
        assertEquals("top-level-fn", nova().compileToBytecode(
                IMPORTS_SHAPES + "KotlinShapesKt.topLevel()",
                "kotlin-toplevel.nova").run());
    }

    @Test
    @DisplayName("Kotlin property maps to its getter")
    void kotlinPropertyGetterResolves() {
        assertEquals("x", nova().compileToBytecode(
                IMPORTS_SHAPES + "Holder(\"x\").getName()",
                "kotlin-property.nova").run());
    }

    @Test
    @DisplayName("Kotlin data class members resolve")
    void kotlinDataClassMembersResolve() {
        assertEquals(1, nova().compileToBytecode(
                IMPORTS_SHAPES + "Point(1, 2).getX()",
                "kotlin-data-getx.nova").run());
        assertEquals("Point(x=1, y=2)", nova().compileToBytecode(
                IMPORTS_SHAPES + "Point(1, 2).toString()",
                "kotlin-data-tostring.nova").run());
    }
}
