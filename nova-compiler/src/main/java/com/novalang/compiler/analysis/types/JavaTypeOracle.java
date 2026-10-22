package com.novalang.compiler.analysis.types;

import com.novalang.compiler.profile.SemanticProfile;
import com.novalang.runtime.host.JavaTypeLookupContext;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Compiler-side Java type metadata oracle.
 * Uses cached class metadata lookups only during semantic analysis.
 */
public final class JavaTypeOracle {

    private static final JavaTypeOracle INSTANCE = new JavaTypeOracle();

    /**
     * JDK 标准库加载器。
     *
     * <p>简单类型名会依次尝试 java.lang/java.util 等标准包；这些包不依赖业务插件。
     * 使用平台/扩展加载器可以直接命中或快速失败，避免每次失败都让 Paper 的插件组
     * 遍历全部插件依赖。</p>
     */
    private static final ClassLoader STANDARD_LIBRARY_LOADER = standardLibraryLoader();

    private static ClassLoader standardLibraryLoader() {
        ClassLoader systemLoader = ClassLoader.getSystemClassLoader();
        ClassLoader parent = systemLoader != null ? systemLoader.getParent() : null;
        return parent != null ? parent : systemLoader;
    }

    private final Map<CacheKey, JavaTypeDescriptor> descriptorCache =
            new ConcurrentHashMap<CacheKey, JavaTypeDescriptor>();
    private final Set<CacheKey> negativeCache = ConcurrentHashMap.newKeySet();

    private JavaTypeOracle() {}

    public static JavaTypeOracle get() {
        return INSTANCE;
    }

    public JavaTypeDescriptor resolve(String typeName) {
        if (typeName == null || typeName.isEmpty()) return null;
        long profileStart = SemanticProfile.start();
        try {
            return resolveInternal(typeName);
        } finally {
            SemanticProfile.add("semantic.java.resolve", profileStart);
        }
    }

    private JavaTypeDescriptor resolveInternal(String typeName) {
        ClassLoader contextLoader = rawContextClassLoader();
        // 本次编译生成的依赖类必须走 CompilationClassLoader；宿主 Java 类型走稳定的
        // 父加载器，避免每个入口都重复执行 Paper 插件类加载器查询。
        ClassLoader lookupLoader = lookupLoader(contextLoader, typeName);
        CacheKey requestedKey = new CacheKey(lookupLoader, typeName);
        JavaTypeDescriptor cached = descriptorCache.get(requestedKey);
        if (cached != null) {
            SemanticProfile.increment("semantic.java.resolve.cache-hit");
            return cached;
        }
        if (negativeCache.contains(requestedKey)) {
            SemanticProfile.increment("semantic.java.resolve.negative-hit");
            return null;
        }
        SemanticProfile.increment("semantic.java.resolve.miss");

        JavaTypeDescriptor descriptor = resolveUncached(typeName, contextLoader, lookupLoader);
        if (descriptor != null) {
            CacheKey actualKey = new CacheKey(descriptor.getJavaClass().getClassLoader(),
                    descriptor.getQualifiedName());
            descriptorCache.put(actualKey, descriptor);
        } else {
            negativeCache.add(requestedKey);
        }
        return descriptor;
    }

    /**
     * 按已经解析出的 Java 类获取描述符，避免再次依赖当前线程上下文类加载器。
     *
     * @param javaClass 已加载的 Java 类
     * @return 与该类所属类加载器绑定的描述符
     */
    public JavaTypeDescriptor resolve(Class<?> javaClass) {
        if (javaClass == null) return null;
        CacheKey key = new CacheKey(javaClass.getClassLoader(), javaClass.getName());
        JavaTypeDescriptor cached = descriptorCache.get(key);
        if (cached != null) return cached;
        JavaTypeDescriptor descriptor = new JavaTypeDescriptor(
                javaClass, JavaTypeDescriptor.findSamMethod(javaClass));
        descriptorCache.put(key, descriptor);
        return descriptor;
    }

    private JavaTypeDescriptor resolveUncached(String typeName, ClassLoader contextLoader,
                                               ClassLoader lookupLoader) {
        Class<?> javaClass = null;
        if (typeName.indexOf('.') < 0) {
            // 先查 JDK 标准包，失败也不会进入 Paper 插件组扫描；最后再查宿主
            // 类加载器，兼容插件内简单类型和默认包类型。
            String[] prefixes = { "java.lang.", "java.util.", "java.util.concurrent.", "java.util.function." };
            for (String prefix : prefixes) {
                javaClass = loadClass(prefix + typeName, contextLoader, STANDARD_LIBRARY_LOADER);
                if (javaClass != null) break;
            }
            if (javaClass == null) {
                javaClass = loadClass(typeName, contextLoader, lookupLoader);
            }
        } else {
            javaClass = loadClass(typeName, contextLoader, lookupLoader);
        }
        if (javaClass == null) return null;
        Method samMethod = JavaTypeDescriptor.findSamMethod(javaClass);
        return new JavaTypeDescriptor(javaClass, samMethod);
    }

    private Class<?> loadClass(String qualifiedName, ClassLoader contextLoader, ClassLoader lookupLoader) {
        long profileStart = SemanticProfile.start();
        boolean local = isLocalClass(contextLoader, qualifiedName);
        String phase = local
                ? "semantic.java.class-load.local"
                : "semantic.java.class-load.host";
        try {
            String candidate = qualifiedName;
            while (candidate != null) {
                ClassLoader candidateLoader = isLocalClass(contextLoader, candidate)
                        ? contextLoader
                        : lookupLoader;
                try {
                    Class<?> loaded = Class.forName(candidate, false, candidateLoader);
                    SemanticProfile.increment(phase + ".success");
                    return loaded;
                } catch (ClassNotFoundException ignored) {
                    SemanticProfile.increment(phase + ".miss");
                    int separator = candidate.lastIndexOf('.');
                    if (separator < 0) {
                        candidate = null;
                    } else {
                        candidate = candidate.substring(0, separator)
                                + '$' + candidate.substring(separator + 1);
                    }
                }
            }
            return null;
        } finally {
            SemanticProfile.add(phase, profileStart);
        }
    }

    private ClassLoader lookupLoader(ClassLoader contextLoader, String typeName) {
        if (isLocalClass(contextLoader, typeName)) {
            return contextLoader;
        }
        if (contextLoader instanceof JavaTypeLookupContext) {
            ClassLoader hostLoader = ((JavaTypeLookupContext) contextLoader).getJavaTypeClassLoader();
            if (hostLoader != null) {
                return hostLoader;
            }
        }
        return contextLoader != null ? contextLoader : JavaTypeOracle.class.getClassLoader();
    }

    private boolean isLocalClass(ClassLoader contextLoader, String className) {
        return contextLoader instanceof JavaTypeLookupContext
                && ((JavaTypeLookupContext) contextLoader).hasLocalClass(className);
    }

    private ClassLoader rawContextClassLoader() {
        ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        return contextLoader != null ? contextLoader : JavaTypeOracle.class.getClassLoader();
    }

    public NovaType toNovaType(Class<?> javaClass, boolean nullable) {
        if (javaClass == null || javaClass == Void.TYPE || javaClass == Void.class) return NovaTypes.UNIT;
        if (javaClass == Integer.TYPE || javaClass == Integer.class) {
            return nullable ? NovaTypes.INT.withNullable(true) : NovaTypes.INT;
        }
        if (javaClass == Long.TYPE || javaClass == Long.class) {
            return nullable ? NovaTypes.LONG.withNullable(true) : NovaTypes.LONG;
        }
        if (javaClass == Float.TYPE || javaClass == Float.class) {
            return nullable ? NovaTypes.FLOAT.withNullable(true) : NovaTypes.FLOAT;
        }
        if (javaClass == Double.TYPE || javaClass == Double.class) {
            return nullable ? NovaTypes.DOUBLE.withNullable(true) : NovaTypes.DOUBLE;
        }
        if (javaClass == Boolean.TYPE || javaClass == Boolean.class) {
            return nullable ? NovaTypes.BOOLEAN.withNullable(true) : NovaTypes.BOOLEAN;
        }
        if (javaClass == Character.TYPE || javaClass == Character.class) {
            return nullable ? NovaTypes.CHAR.withNullable(true) : NovaTypes.CHAR;
        }
        if (javaClass == String.class) return nullable ? NovaTypes.STRING.withNullable(true) : NovaTypes.STRING;
        if (javaClass == Object.class) return nullable ? NovaTypes.ANY.withNullable(true) : NovaTypes.ANY;

        JavaTypeDescriptor descriptor = resolve(javaClass);
        if (descriptor == null) {
            return nullable ? NovaTypes.ANY.withNullable(true) : NovaTypes.ANY;
        }
        return new JavaClassNovaType(descriptor, nullable);
    }

    public Class<?>[] toJavaArgumentTypes(List<NovaType> argTypes) {
        if (argTypes == null || argTypes.isEmpty()) return new Class<?>[0];
        List<Class<?>> resolved = new ArrayList<Class<?>>(argTypes.size());
        for (NovaType argType : argTypes) {
            resolved.add(toJavaArgumentType(argType));
        }
        return resolved.toArray(new Class<?>[0]);
    }

    public Class<?> toJavaArgumentType(NovaType type) {
        // A missing inferred type is an unknown expression, not a null literal.
        // Keep the call available for runtime overload selection; an explicit
        // nullable Nothing type below continues to represent a null literal.
        if (type == null) return Object.class;
        if (type instanceof NothingType && type.isNullable()) return null;
        if (NovaTypes.isDynamicType(type)) return Object.class;
        if (type instanceof JavaClassLiteralNovaType) return Class.class;
        String typeName = type.getTypeName();
        if ("Int".equals(typeName)) return Integer.class;
        if ("Long".equals(typeName)) return Long.class;
        if ("Float".equals(typeName)) return Float.class;
        if ("Double".equals(typeName)) return Double.class;
        if ("Boolean".equals(typeName)) return Boolean.class;
        if ("Char".equals(typeName)) return Character.class;
        if ("String".equals(typeName)) return String.class;
        if ("Any".equals(typeName) || "Unit".equals(typeName)) return Object.class;
        if ("List".equals(typeName)) return java.util.List.class;
        if ("Set".equals(typeName)) return java.util.Set.class;
        if ("Map".equals(typeName)) return java.util.Map.class;
        if ("Array".equals(typeName)) return Object[].class;
        if (type instanceof JavaClassNovaType) {
            JavaTypeDescriptor descriptor = ((JavaClassNovaType) type).getDescriptor();
            if (descriptor != null) return descriptor.getJavaClass();
        }
        if (typeName != null) {
            ClassLoader contextLoader = rawContextClassLoader();
            Class<?> javaClass = loadClass(typeName, contextLoader,
                    lookupLoader(contextLoader, typeName));
            if (javaClass != null) return javaClass;
            JavaTypeDescriptor descriptor = resolve(typeName);
            if (descriptor != null) {
                return descriptor.getJavaClass();
            }
        }
        return Object.class;
    }

    /** 按类加载器身份和二进制类名区分 Java 类型缓存。 */
    private static final class CacheKey {
        private final ClassLoader loader;
        private final String name;

        private CacheKey(ClassLoader loader, String name) {
            this.loader = loader;
            this.name = name;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (!(other instanceof CacheKey)) return false;
            CacheKey that = (CacheKey) other;
            return loader == that.loader && name.equals(that.name);
        }

        @Override
        public int hashCode() {
            return 31 * System.identityHashCode(loader) + name.hashCode();
        }
    }
}
