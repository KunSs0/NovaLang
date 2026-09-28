package com.novalang.ir;

/**
 * 为编译器提供不触发父加载器探测的本地类索引。
 *
 * <p>隔离型脚本 ClassLoader 可实现此接口，声明自身已经定义或准备定义的类。
 * 编译器据此优先解析本地生成类，再决定是否查询父加载器。</p>
 */
public interface JavaClassLookup {

    /**
     * 判断类名是否由当前加载器本地持有或已排队等待定义。
     *
     * @param className 二进制类名，例如 {@code a.b.Outer$Inner}
     * @return 当前加载器可以直接尝试定义或返回该类时为 {@code true}
     */
    boolean hasLocalClass(String className);
}
