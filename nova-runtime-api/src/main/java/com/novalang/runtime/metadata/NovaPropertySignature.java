package com.novalang.runtime.metadata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 保留 Nova 属性在 JVM 字段描述符之外的声明类型信息。 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NovaPropertySignature {

    /** @return 属性类型的 JVM 描述符 */
    String type();

    /** @return 属性是否可空 */
    boolean nullable() default false;
}
