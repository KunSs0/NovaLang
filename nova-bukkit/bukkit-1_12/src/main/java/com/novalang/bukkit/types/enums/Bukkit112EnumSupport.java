package com.novalang.bukkit.types.enums;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;

/** Bukkit 1.12 枚举全局函数的本地注册辅助工具。 */
final class Bukkit112EnumSupport {

    private Bukkit112EnumSupport() {
    }

    /** 为 Bukkit 1.12 的 Java 枚举注册按名称查询函数。 */
    static <E extends Enum<E>> void registerEnum(JavaTypes.Builder builder,
                                                  String functionName,
                                                  Class<E> enumClass) {
        builder.globalFunction(functionName, function -> function
                .param("name", String.class)
                .returns(JavaTypeRef.javaType(enumClass).nullable())
                .invoke1(String.class, value -> NovaTypeSupport.findEnum(enumClass, value)));
    }
}
