package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 已废弃 ItemTag API 中 ItemTagType 与 PrimitiveTagType 的 Fluxon 函数契约。 */
@Requires(classes = {
        "org.bukkit.inventory.meta.tags.ItemTagType",
        "org.bukkit.inventory.meta.tags.ItemTagType$PrimitiveTagType",
        "org.bukkit.inventory.meta.tags.ItemTagAdapterContext"}, methods = {
        "org.bukkit.inventory.meta.tags.ItemTagType#getPrimitiveType",
        "org.bukkit.inventory.meta.tags.ItemTagType#getComplexType",
        "org.bukkit.inventory.meta.tags.ItemTagType$PrimitiveTagType#toPrimitive",
        "org.bukkit.inventory.meta.tags.ItemTagType$PrimitiveTagType#fromPrimitive"})
public final class NovaItemTagType {

    private static final String TYPE = "org.bukkit.inventory.meta.tags.ItemTagType";
    private static final String PRIMITIVE_TYPE = "org.bukkit.inventory.meta.tags.ItemTagType$PrimitiveTagType";
    private static final String CONTEXT = "org.bukkit.inventory.meta.tags.ItemTagAdapterContext";

    private NovaItemTagType() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaInventoryReflection.type(NovaItemTagType.class, TYPE);
        Class<?> primitiveType = NovaInventoryReflection.type(NovaItemTagType.class, PRIMITIVE_TYPE);
        Class<?> context = NovaInventoryReflection.type(NovaItemTagType.class, CONTEXT);
        registerType(builder, type, primitiveType, context);
        registerType(builder, primitiveType, primitiveType, context);
    }

    private static void registerType(JavaTypes.Builder builder, Class<?> target, Class<?> primitiveType, Class<?> context) {
        Method getPrimitiveType = NovaInventoryReflection.method(target, "getPrimitiveType");
        Method getComplexType = NovaInventoryReflection.method(target, "getComplexType");
        Method toPrimitive = NovaInventoryReflection.method(primitiveType, "toPrimitive", Object.class, context);
        Method fromPrimitive = NovaInventoryReflection.method(primitiveType, "fromPrimitive", Object.class, context);

        builder.extension(target, "primitiveType", function -> function.returns(Class.class)
                .invoke(arguments -> NovaInventoryReflection.invoke(getPrimitiveType, arguments[0])));
        builder.extension(target, "complexType", function -> function.returns(Class.class)
                .invoke(arguments -> NovaInventoryReflection.invoke(getComplexType, arguments[0])));
        builder.extension(target, "toPrimitive", function -> function.param("value", Object.class).param("context", JavaTypeRef.javaType(context)).returns(Object.class)
                .invoke(arguments -> invokePrimitive(primitiveType, toPrimitive, arguments)));
        builder.extension(target, "fromPrimitive", function -> function.param("value", Object.class).param("context", JavaTypeRef.javaType(context)).returns(Object.class)
                .invoke(arguments -> invokePrimitive(primitiveType, fromPrimitive, arguments)));
    }

    private static Object invokePrimitive(Class<?> primitiveType, Method method, Object[] arguments) {
        if (!primitiveType.isInstance(arguments[0])) {
            return null;
        }
        return NovaInventoryReflection.invoke(method, arguments[0], arguments[1], arguments[2]);
    }
}
