package com.novalang.workspace;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

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
            Map<String, byte[]> resources = new LinkedHashMap<String, byte[]>();
            for (Map.Entry<String, byte[]> entry : bytecode.entrySet()) {
                String resourceName = entry.getKey().replace('.', '/') + ".class";
                resources.put(resourceName, entry.getValue());
            }
            URL classPath = createClassPath(resources);
            ArtifactParentClassLoader parent = new ArtifactParentClassLoader(scriptClassLoader);
            ArtifactUrlClassLoader loader = new ArtifactUrlClassLoader(
                    new URL[]{classPath}, parent, bytecode.keySet());
            Map<String, Class<?>> loaded = new LinkedHashMap<String, Class<?>>();
            try {
                for (String className : bytecode.keySet()) {
                    loaded.put(className, loader.loadClass(className));
                }
            } catch (ClassNotFoundException exception) {
                throw new WorkspaceException("Failed to load cached Workspace class", exception);
            } finally {
                try {
                    loader.close();
                } catch (IOException exception) {
                    throw new WorkspaceException("Failed to close cached Workspace class loader", exception);
                }
            }
            return loaded;
        }

        /**
         * 将当前产物装入一个 Generation 统一 ClassLoader。
         */
        Map<String, Class<?>> loadInto(WorkspaceGenerationClassLoader generationClassLoader) {
            if (generationClassLoader == null) {
                throw new IllegalArgumentException("generationClassLoader must not be null");
            }
            return generationClassLoader.install(bytecode);
        }

        private URL createClassPath(Map<String, byte[]> resources) {
            try {
                return new URL(null, "novalang-cache://workspace/", new ArtifactUrlStreamHandler(resources));
            } catch (MalformedURLException exception) {
                throw new WorkspaceException("Failed to create cached Workspace class path", exception);
            }
        }
    }

    /** 优先从 NovaLang 与宿主脚本 ClassLoader 解析依赖，不持有副本脚本类。 */
    private static final class ArtifactParentClassLoader extends ClassLoader {

        private final ClassLoader scriptClassLoader;

        ArtifactParentClassLoader(ClassLoader scriptClassLoader) {
            super(ArtifactParentClassLoader.class.getClassLoader());
            this.scriptClassLoader = scriptClassLoader;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            if (scriptClassLoader != null) {
                return scriptClassLoader.loadClass(name);
            }
            throw new ClassNotFoundException(name);
        }
    }

    /** 对副本脚本类使用子优先解析，实际字节码定义仍交由 JDK URLClassLoader 执行。 */
    private static final class ArtifactUrlClassLoader extends URLClassLoader {

        private final Set<String> artifactClassNames;

        ArtifactUrlClassLoader(URL[] classPath, ClassLoader parent, Set<String> artifactClassNames) {
            super(classPath, parent);
            this.artifactClassNames = new LinkedHashSet<String>(artifactClassNames);
        }

        @Override
        protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            if (!artifactClassNames.contains(name)) {
                return super.loadClass(name, resolve);
            }
            Class<?> existing = findLoadedClass(name);
            if (existing != null) {
                return existing;
            }
            Class<?> loaded = findClass(name);
            if (resolve) {
                resolveClass(loaded);
            }
            return loaded;
        }
    }

    /** 向 JDK URLClassLoader 提供一次性内存类路径，避免插件字节码直接调用 defineClass。 */
    private static final class ArtifactUrlStreamHandler extends URLStreamHandler {

        private final Map<String, byte[]> resources;

        ArtifactUrlStreamHandler(Map<String, byte[]> resources) {
            this.resources = resources;
        }

        @Override
        protected URLConnection openConnection(URL url) {
            return new ArtifactUrlConnection(url, resources);
        }
    }

    /** 内存类路径的连接实现。 */
    private static final class ArtifactUrlConnection extends URLConnection {

        private final Map<String, byte[]> resources;

        ArtifactUrlConnection(URL url, Map<String, byte[]> resources) {
            super(url);
            this.resources = resources;
        }

        @Override
        public void connect() {
            connected = true;
        }

        @Override
        public ByteArrayInputStream getInputStream() throws IOException {
            String path = url.getPath();
            String resourceName = path.substring(1);
            byte[] value = resources.get(resourceName);
            if (value == null) {
                throw new FileNotFoundException(resourceName);
            }
            connect();
            return new ByteArrayInputStream(value);
        }

        @Override
        public long getContentLengthLong() {
            String path = url.getPath();
            String resourceName = path.substring(1);
            byte[] value = resources.get(resourceName);
            if (value == null) {
                return -1;
            }
            return value.length;
        }
    }
}
