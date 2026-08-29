package com.novalang.workspace;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * 副本 Workspace 编译产物缓存的最小复现。
 *
 * <p>两个副本使用相同入口源码时，编译管线只能执行一次；但每个副本必须通过自己的
 * ClassLoader 装载产物，因此脚本模块中的静态状态不能互相泄漏。</p>
 */
class WorkspaceBytecodeArtifactCacheMockTest {

    @Test
    void shouldCompileOnceAndLoadIndependentModuleStateForTwoDungeons() throws Exception {
        AtomicInteger compilationCount = new AtomicInteger();
        WorkspaceBytecodeArtifactCache cache = new WorkspaceBytecodeArtifactCache();
        String source = "mock-counter-module";

        WorkspaceBytecodeArtifactCache.CacheKey key =
                new WorkspaceBytecodeArtifactCache.CacheKey("creator-test", "stage.start", source);
        WorkspaceBytecodeArtifactCache.BytecodeArtifact artifact = cache.getOrCompile(key, () -> {
            compilationCount.incrementAndGet();
            Map<String, byte[]> classes = new LinkedHashMap<String, byte[]>();
            String className = CounterModule.class.getName();
            try {
                classes.put(className, readClassBytes(CounterModule.class));
            } catch (IOException exception) {
                throw new IllegalStateException("Unable to prepare mock bytecode", exception);
            }
            return classes;
        });

        Map<String, Class<?>> firstDungeonClasses = artifact.load(getClass().getClassLoader());
        Map<String, Class<?>> secondDungeonClasses = artifact.load(getClass().getClassLoader());
        String className = CounterModule.class.getName();
        Class<?> firstModule = firstDungeonClasses.get(className);
        Class<?> secondModule = secondDungeonClasses.get(className);
        Method firstNext = firstModule.getMethod("next");
        Method secondNext = secondModule.getMethod("next");

        assertEquals(1, compilationCount.get());
        assertNotSame(firstModule, secondModule);
        assertEquals(1, ((Number) firstNext.invoke(null)).intValue());
        assertEquals(1, ((Number) secondNext.invoke(null)).intValue());
        assertEquals(2, ((Number) firstNext.invoke(null)).intValue());
    }

    private static byte[] readClassBytes(Class<?> type) throws IOException {
        String resourceName = type.getName().replace('.', '/') + ".class";
        InputStream stream = type.getClassLoader().getResourceAsStream(resourceName);
        if (stream == null) {
            throw new IOException("Missing class resource: " + resourceName);
        }
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int count = stream.read(buffer);
            while (count >= 0) {
                output.write(buffer, 0, count);
                count = stream.read(buffer);
            }
            return output.toByteArray();
        } finally {
            stream.close();
        }
    }

    public static class CounterModule {

        private static int counter;

        public static int next() {
            counter++;
            return counter;
        }
    }
}
