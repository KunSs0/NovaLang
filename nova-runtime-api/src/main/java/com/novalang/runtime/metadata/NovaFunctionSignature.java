package com.novalang.runtime.metadata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 保留 Nova 函数的声明类型，供分组编译后的消费单元继续执行静态语义检查。
 *
 * <p>Nova 的 JVM 调用约定统一使用 {@link Object} 参数和返回值，因此不能从 Java
 * 反射签名还原源码类型。该元数据只服务编译期链接，不参与运行时调用分派。</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NovaFunctionSignature {

    /** @return 声明返回类型的 JVM 描述符 */
    String returnType();

    /** @return 返回类型是否可空 */
    boolean returnNullable() default false;

    /** @return 声明参数类型的 JVM 描述符 */
    String[] parameterTypes();

    /** @return 各参数类型是否可空 */
    boolean[] parameterNullable();

    /** @return 最后一个参数是否为 vararg */
    boolean vararg() default false;
}
