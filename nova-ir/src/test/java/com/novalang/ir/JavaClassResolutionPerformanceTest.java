package com.novalang.ir;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Java 类型解析性能回归测试。
 */
@DisplayName("Java 类型解析性能")
class JavaClassResolutionPerformanceTest {

    @Test
    @DisplayName("Nova 命名空间调用不应探测 Java 类")
    void novaNamespaceCallsShouldNotProbeJavaClasses() {
        CountingClassLoader loader = new CountingClassLoader(getClass().getClassLoader());
        ClassLoader previousLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(loader);
        try {
            NovaIrCompiler compiler = new NovaIrCompiler();
            compiler.compile(novaNamespaceCalls(40), "namespace-calls.nova");
        } finally {
            Thread.currentThread().setContextClassLoader(previousLoader);
        }

        assertEquals(0, loader.getProbeCount(),
                "CreatorApi.xxx() 是已声明的 Nova 对象调用，不应通过 Class.forName 探测 Java 类型");
    }

    @Test
    @DisplayName("Nova 实例方法调用不应探测 Java 类")
    void novaInstanceCallsShouldNotProbeJavaClasses() {
        CountingClassLoader loader = new CountingClassLoader(getClass().getClassLoader());
        ClassLoader previousLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(loader);
        try {
            NovaIrCompiler compiler = new NovaIrCompiler();
            compiler.compile(novaInstanceCalls(40), "instance-calls.nova");
        } finally {
            Thread.currentThread().setContextClassLoader(previousLoader);
        }

        assertEquals(0, loader.getProbeCount(),
                "已声明 Nova 类型的方法调用不应通过 Class.forName 反向探测 Java 类型");
    }

    @Test
    @DisplayName("Java 方法的 Nova 实参不应探测尚未输出的类")
    void novaArgumentsOfJavaMethodsShouldNotProbeUnemittedClasses() {
        CountingClassLoader loader = compileWithCountingLoader(
                novaArgumentsOfJavaMethods(40), "java-method-nova-arguments.nova");

        assertEquals(0, loader.countContaining("Service"),
                "Nova 实参类型尚未输出字节码，Java 重载解析不应尝试通过 Class.forName 加载");
    }

    @Test
    @DisplayName("Java 方法的 Lambda 实参不应探测编译器生成类")
    void lambdaArgumentsOfJavaMethodsShouldNotProbeGeneratedClasses() {
        CountingClassLoader loader = compileWithCountingLoader(
                lambdaArgumentsOfJavaMethods(40), "java-method-lambda-arguments.nova");

        assertEquals(0, loader.countContaining("$Lambda$"),
                "编译器生成的 Lambda 类尚未输出字节码，Java 重载解析不应尝试通过 Class.forName 加载");
    }

    @Test
    @DisplayName("Nova 前向类型引用不应探测 Java 类")
    void forwardNovaTypeReferencesShouldNotProbeJavaClasses() {
        CountingClassLoader loader = compileWithCountingLoader(
                forwardNovaTypeReferences(40), "forward-nova-types.nova");

        assertEquals(0, loader.countContaining("ForwardBase"),
                "后声明的 Nova 父类应在 HIR 降级前完成类型预声明，不应探测 Java 类");
    }

    @Test
    @DisplayName("多个编译单元的相同 Java 导入只应探测一次")
    void sharedJavaImportsShouldOnlyBeProbedOnceAcrossCompilationUnits() {
        CountingClassLoader loader = new CountingClassLoader(getClass().getClassLoader());
        ClassLoader previousLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(loader);
        try {
            for (int index = 0; index < 20; index++) {
                NovaIrCompiler compiler = new NovaIrCompiler();
                compiler.compile(sharedJavaImportSource(), "shared-java-import-" + index + ".nova");
            }
        } finally {
            Thread.currentThread().setContextClassLoader(previousLoader);
        }

        assertEquals(1, loader.countContaining("SharedJavaImportFixture"),
                "语义阶段已解析的 Java 导入应由 HIR 复用，不应随入口文件数量重复 Class.forName");
    }

    @Test
    @DisplayName("HIR 不应重复探测语义阶段已判定缺失的 Java 导入")
    void missingJavaImportsShouldNotBeProbedAgainDuringHirLowering() {
        CountingClassLoader loader = compileWithCountingLoader(
                missingJavaImportSource(), "missing-java-import.nova");

        assertEquals(4, loader.countContaining("MissingJavaImportFixture"),
                "缺失导入只允许语义类型解析遍历一次候选名，HIR 不应再按 Java 前缀扩散探测");
    }

    private CountingClassLoader compileWithCountingLoader(String source, String sourceName) {
        CountingClassLoader loader = new CountingClassLoader(getClass().getClassLoader());
        ClassLoader previousLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(loader);
        try {
            NovaIrCompiler compiler = new NovaIrCompiler();
            compiler.compile(source, sourceName);
        } finally {
            Thread.currentThread().setContextClassLoader(previousLoader);
        }
        return loader;
    }

    private String novaNamespaceCalls(int count) {
        StringBuilder source = new StringBuilder();
        source.append("object CreatorApi {\n");
        for (int index = 0; index < count; index++) {
            source.append("  fun action")
                    .append(index)
                    .append("(): Any { return null }\n");
        }
        source.append("}\n");
        source.append("object Test {\n");
        source.append("  fun run(): Any {\n");
        for (int index = 0; index < count; index++) {
            source.append("    CreatorApi.action")
                    .append(index)
                    .append("()\n");
        }
        source.append("    return null\n");
        source.append("  }\n");
        source.append("}\n");
        return source.toString();
    }

    private String novaInstanceCalls(int count) {
        StringBuilder source = new StringBuilder();
        for (int index = 0; index < count; index++) {
            source.append("class Service")
                    .append(index)
                    .append(" { fun refresh(): Any { return null } }\n");
        }
        source.append("object Test {\n");
        source.append("  fun run(): Any {\n");
        for (int index = 0; index < count; index++) {
            source.append("    val service")
                    .append(index)
                    .append(" = Service")
                    .append(index)
                    .append("()\n");
            source.append("    service")
                    .append(index)
                    .append(".refresh()\n");
        }
        source.append("    return null\n");
        source.append("  }\n");
        source.append("}\n");
        return source.toString();
    }

    private String novaArgumentsOfJavaMethods(int count) {
        StringBuilder source = new StringBuilder();
        source.append("import java java.util.concurrent.atomic.AtomicReference\n");
        for (int index = 0; index < count; index++) {
            source.append("class Service")
                    .append(index)
                    .append("\n");
        }
        source.append("object Test {\n");
        source.append("  fun run(): Any {\n");
        source.append("    val holder = AtomicReference()\n");
        for (int index = 0; index < count; index++) {
            source.append("    holder.set(Service")
                    .append(index)
                    .append("())\n");
        }
        source.append("    return holder\n");
        source.append("  }\n");
        source.append("}\n");
        return source.toString();
    }

    private String lambdaArgumentsOfJavaMethods(int count) {
        StringBuilder source = new StringBuilder();
        source.append("import java java.util.concurrent.atomic.AtomicReference\n");
        source.append("object Test {\n");
        source.append("  fun run(): Any {\n");
        source.append("    val holder = AtomicReference()\n");
        for (int index = 0; index < count; index++) {
            source.append("    val callback")
                    .append(index)
                    .append(" = { value -> value }\n");
            source.append("    holder.set(callback")
                    .append(index)
                    .append(")\n");
        }
        source.append("    return holder\n");
        source.append("  }\n");
        source.append("}\n");
        return source.toString();
    }

    private String forwardNovaTypeReferences(int count) {
        StringBuilder source = new StringBuilder();
        for (int index = 0; index < count; index++) {
            source.append("class ForwardChild")
                    .append(index)
                    .append(" : ForwardBase")
                    .append(index)
                    .append("\n");
        }
        for (int index = 0; index < count; index++) {
            source.append("open class ForwardBase")
                    .append(index)
                    .append("\n");
        }
        return source.toString();
    }

    private String sharedJavaImportSource() {
        return "import java com.novalang.ir.fixture.SharedJavaImportFixture\n"
                + "fun keep(value: SharedJavaImportFixture): SharedJavaImportFixture { return value }\n";
    }

    private String missingJavaImportSource() {
        return "import java com.novalang.missing.MissingJavaImportFixture\n"
                + "fun main() { }\n";
    }

    private static final class CountingClassLoader extends ClassLoader {

        private final AtomicInteger probeCount = new AtomicInteger();
        private final List<String> probedClassNames = new ArrayList<>();

        private CountingClassLoader(ClassLoader parent) {
            super(parent);
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            probeCount.incrementAndGet();
            probedClassNames.add(name);
            return super.loadClass(name, resolve);
        }

        private int getProbeCount() {
            return probeCount.get();
        }

        private long countContaining(String marker) {
            return probedClassNames.stream()
                    .filter(name -> name.contains(marker))
                    .count();
        }
    }
}
