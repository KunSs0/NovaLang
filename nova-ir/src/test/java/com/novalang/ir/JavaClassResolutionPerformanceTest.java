package com.novalang.ir;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

    private static final class CountingClassLoader extends ClassLoader {

        private final AtomicInteger probeCount = new AtomicInteger();

        private CountingClassLoader(ClassLoader parent) {
            super(parent);
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            probeCount.incrementAndGet();
            return super.loadClass(name, resolve);
        }

        private int getProbeCount() {
            return probeCount.get();
        }
    }
}
