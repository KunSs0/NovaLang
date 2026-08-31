package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.20.5+ EntityFactory 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.entity.EntityFactory", "org.bukkit.entity.EntitySnapshot"}, methods = {
        "org.bukkit.entity.EntityFactory#createEntitySnapshot"})
public final class NovaEntityFactory {
    private static final String FACTORY = "org.bukkit.entity.EntityFactory";
    private static final String SNAPSHOT = "org.bukkit.entity.EntitySnapshot";
    private NovaEntityFactory() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> factory = NovaEntityReflection.type(NovaEntityFactory.class, FACTORY);
        Class<?> snapshot = NovaEntityReflection.type(NovaEntityFactory.class, SNAPSHOT);
        Method createEntitySnapshot = NovaEntityReflection.method(factory, "createEntitySnapshot", String.class);
        builder.extension(factory, "createEntitySnapshot", function -> function.param("serialized", String.class).returns(JavaTypeRef.javaType(snapshot).nullable()).invoke(arguments -> NovaEntityReflection.invoke(createEntitySnapshot, arguments[0], arguments[1])));
    }
}
