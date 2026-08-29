package com.novalang.workspace;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 可在多个 Workspace 间复用的 Nova 字节码产物缓存。
 *
 * <p>缓存只保存不可变字节码；每次 {@link BytecodeArtifact#load(ClassLoader)} 都创建独立
 * ClassLoader，确保脚本类静态字段和模块状态仍严格属于单个 Workspace。</p>
 */
public final class WorkspaceBytecodeArtifactCache {

    private final Map<CacheKey, BytecodeArtifact> artifacts =
            new LinkedHashMap<CacheKey, BytecodeArtifact>();

    /**
     * 返回缓存产物，缓存未命中时只编译一次。
     *
     * @param key 由脚本类加载器、入口 ID 与完整源码组成的缓存键
     * @param compiler 编译字节码的回调
     * @return 不可变字节码产物
     */
    public synchronized BytecodeArtifact getOrCompile(CacheKey key, BytecodeCompiler compiler) {
        if (key == null) {
            throw new IllegalArgumentException("Cache key must not be null");
        }
        if (compiler == null) {
            throw new IllegalArgumentException("Bytecode compiler must not be null");
        }
        BytecodeArtifact existing = artifacts.get(key);
        if (existing != null) {
            return existing;
        }
        Map<String, byte[]> compiled = compiler.compile();
        BytecodeArtifact artifact = new BytecodeArtifact(compiled);
        artifacts.put(key, artifact);
        return artifact;
    }

    /** 清空当前宿主代际的全部编译产物。 */
    public synchronized void clear() {
        artifacts.clear();
    }

    /** 返回缓存的产物数，主要用于测试与诊断。 */
    public synchronized int size() {
        return artifacts.size();
    }

    /** 字节码生成回调。 */
    public interface BytecodeCompiler {

        Map<String, byte[]> compile();
    }

    /**
     * 一个不可变的缓存键。
     *
     * <p>类加载器按对象身份比较，避免不同插件 ClassLoader 之间错误共享字节码。</p>
     */
    public static final class CacheKey {

        private final ClassLoader scriptClassLoader;
        private final String workspaceName;
        private final String entryModuleId;
        private final String source;

        public CacheKey(String workspaceName, String entryModuleId, String source) {
            this(null, workspaceName, entryModuleId, source);
        }

        public CacheKey(ClassLoader scriptClassLoader,
                        String workspaceName,
                        String entryModuleId,
                        String source) {
            if (workspaceName == null || entryModuleId == null || source == null) {
                throw new IllegalArgumentException("Cache key fields must not be null");
            }
            this.scriptClassLoader = scriptClassLoader;
            this.workspaceName = workspaceName;
            this.entryModuleId = entryModuleId;
            this.source = source;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof CacheKey)) {
                return false;
            }
            CacheKey value = (CacheKey) other;
            return scriptClassLoader == value.scriptClassLoader
                    && workspaceName.equals(value.workspaceName)
                    && entryModuleId.equals(value.entryModuleId)
                    && source.equals(value.source);
        }

        @Override
        public int hashCode() {
            int result = System.identityHashCode(scriptClassLoader);
            result = 31 * result + workspaceName.hashCode();
            result = 31 * result + entryModuleId.hashCode();
            result = 31 * result + source.hashCode();
            return result;
        }
    }

    /** 不可变字节码产物；每次加载都使用新的类加载器。 */
    public static final class BytecodeArtifact {

        private final Map<String, byte[]> bytecode;

        BytecodeArtifact(Map<String, byte[]> source) {
            if (source == null || source.isEmpty()) {
                throw new IllegalArgumentException("Compiled bytecode must not be empty");
            }
            Map<String, byte[]> copied = new LinkedHashMap<String, byte[]>();
            for (Map.Entry<String, byte[]> entry : source.entrySet()) {
                String className = entry.getKey();
                byte[] value = entry.getValue();
                if (className == null || value == null || value.length == 0) {
                    throw new IllegalArgumentException("Compiled bytecode contains an invalid class");
                }
                copied.put(className, Arrays.copyOf(value, value.length));
            }
            bytecode = Collections.unmodifiableMap(copied);
        }

        /**
         * 将字节码装载到新的隔离 ClassLoader。
         *
         * @param scriptClassLoader 宿主脚本 ClassLoader
         * @return 本次独立加载的类
         */
        public Map<String, Class<?>> load(ClassLoader scriptClassLoader) {
            Map<String, byte[]> loadingBytes = new LinkedHashMap<String, byte[]>();
            for (Map.Entry<String, byte[]> entry : bytecode.entrySet()) {
                loadingBytes.put(entry.getKey(), Arrays.copyOf(entry.getValue(), entry.getValue().length));
            }
            ArtifactClassLoader loader = new ArtifactClassLoader(loadingBytes, scriptClassLoader);
            Map<String, Class<?>> loaded = new LinkedHashMap<String, Class<?>>();
            for (String className : bytecode.keySet()) {
                try {
                    loaded.put(className, loader.loadClass(className));
                } catch (ClassNotFoundException exception) {
                    throw new WorkspaceException("Failed to load cached Workspace class: " + className, exception);
                }
            }
            return loaded;
        }
    }

    /** 优先装载缓存产物，再回退到宿主脚本 ClassLoader。 */
    private static final class ArtifactClassLoader extends ClassLoader {

        private final Map<String, byte[]> classes;
        private final ClassLoader scriptClassLoader;

        ArtifactClassLoader(Map<String, byte[]> classes, ClassLoader scriptClassLoader) {
            super(ArtifactClassLoader.class.getClassLoader());
            this.classes = classes;
            this.scriptClassLoader = scriptClassLoader;
        }

        @Override
        protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            Class<?> existing = findLoadedClass(name);
            if (existing != null) {
                return existing;
            }
            if (classes.containsKey(name)) {
                Class<?> loaded = findClass(name);
                if (resolve) {
                    resolveClass(loaded);
                }
                return loaded;
            }
            return super.loadClass(name, resolve);
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            byte[] value = classes.remove(name);
            if (value != null) {
                return defineClass(name, value, 0, value.length);
            }
            if (scriptClassLoader != null) {
                return scriptClassLoader.loadClass(name);
            }
            throw new ClassNotFoundException(name);
        }
    }
}
