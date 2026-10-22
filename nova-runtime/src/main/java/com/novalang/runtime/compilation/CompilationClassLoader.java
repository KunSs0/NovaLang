package com.novalang.runtime.compilation;

import com.novalang.ir.JavaClassLookup;
import com.novalang.runtime.host.JavaTypeLookupContext;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 一个 Nova 编译程序独占的可增量字节码类加载器。
 */
final class CompilationClassLoader extends ClassLoader implements JavaClassLookup, JavaTypeLookupContext {

    private final Map<String, byte[]> pendingBytecode = new LinkedHashMap<String, byte[]>();

    /** 创建当前编译结果专属的类加载环境，并通过父加载器解析宿主 API。 */
    CompilationClassLoader(ClassLoader parent) {
        super(parent);
    }

    /**
     * 编译期宿主 Java 类型统一交给父加载器解析。
     *
     * <p>本加载器只负责定义本次编译生成的脚本类；若把自身作为
     * JavaTypeOracle 的上下文加载器，会让 Java 类型缓存按编译代际反复失效。</p>
     */
    @Override
    public ClassLoader getJavaTypeClassLoader() {
        return getParent();
    }

    /** 判断名称是否属于本程序已加载或待加载的脚本类。 */
    @Override
    public synchronized boolean hasLocalClass(String name) {
        return findLoadedClass(name) != null || pendingBytecode.containsKey(name);
    }

    /** 复制并装载本编译组字节码，重复定义立即报错。 */
    synchronized Map<String, Class<?>> install(Map<String, byte[]> bytecode) {
        for (Map.Entry<String, byte[]> entry : bytecode.entrySet()) {
            String className = entry.getKey();
            if (findLoadedClass(className) != null || pendingBytecode.containsKey(className)) {
                throw new CompilationException(
                        "Compiled script class is already defined: " + className);
            }
            pendingBytecode.put(className, Arrays.copyOf(
                    entry.getValue(), entry.getValue().length));
        }

        Map<String, Class<?>> loaded = new LinkedHashMap<String, Class<?>>();
        try {
            for (String className : bytecode.keySet()) {
                loaded.put(className, loadClass(className));
            }
        } catch (ClassNotFoundException exception) {
            throw new CompilationException("Failed to load Compiled script class", exception);
        }
        return loaded;
    }

    /** 优先解析本程序脚本类，宿主类型遵循父加载器身份。 */
    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve)
            throws ClassNotFoundException {
        Class<?> loaded = findLoadedClass(name);
        if (loaded == null && pendingBytecode.containsKey(name)) {
            loaded = findClass(name);
        }
        if (loaded == null) {
            loaded = super.loadClass(name, false);
        }
        if (resolve) {
            resolveClass(loaded);
        }
        return loaded;
    }

    /** 从待加载表移除字节码并定义脚本类，缺失时明确报告。 */
    @Override
    protected synchronized Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] bytecode = pendingBytecode.remove(name);
        if (bytecode == null) {
            throw new ClassNotFoundException(name);
        }
        return defineClass(name, bytecode, 0, bytecode.length);
    }
}
