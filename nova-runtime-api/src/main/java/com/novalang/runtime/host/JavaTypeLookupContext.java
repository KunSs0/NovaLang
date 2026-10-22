package com.novalang.runtime.host;

/**
 * 为编译期 Java 类型解析提供稳定的宿主类加载器。
 *
 * <p>Nova 每次编译都会创建独立的 {@code CompilationClassLoader} 来装载生成的
 * 脚本类；如果直接把它作为 {@code JavaTypeOracle} 的上下文类加载器，Java 类型
 * 缓存会随每次编译失效，导致同一个宿主类型在 Paper 插件类加载器上被反复查找。
 * 实现该接口的类可以返回其宿主父加载器，使编译期 Java 类型缓存跨编译复用。</p>
 */
public interface JavaTypeLookupContext {

    /**
     * 返回用于解析宿主 Java 类型的稳定类加载器。
     *
     * @return 宿主类加载器；返回 null 时由调用方回退到当前上下文类加载器
     */
    ClassLoader getJavaTypeClassLoader();

    /**
     * 判断名称是否属于当前编译生成的脚本类。
     *
     * <p>生成的依赖类仍必须由本次编译的类加载器解析；只有宿主 Java 类型
     * 才交给稳定的父加载器缓存。</p>
     *
     * @param name 二进制类名
     * @return 是否为本次编译的本地脚本类
     */
    boolean hasLocalClass(String name);
}
