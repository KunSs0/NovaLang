package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.lang.reflect.Method;

/** 1.20.5+ EntitySnapshot 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.entity.EntitySnapshot"}, methods = {
        "org.bukkit.entity.EntitySnapshot#createEntity",
        "org.bukkit.entity.EntitySnapshot#getEntityType"})
public final class NovaEntitySnapshot {
    private static final String TYPE = "org.bukkit.entity.EntitySnapshot";
    private NovaEntitySnapshot() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaEntityReflection.type(NovaEntitySnapshot.class, TYPE);
        Method createInWorld = NovaEntityReflection.method(type, "createEntity", World.class);
        Method createAtLocation = NovaEntityReflection.method(type, "createEntity", Location.class);
        Method getEntityType = NovaEntityReflection.method(type, "getEntityType");
        builder.extension(type, "createEntity", function -> function.param("world", World.class).returns(JavaTypeRef.javaType(Entity.class).nullable()).invoke(arguments -> NovaEntityReflection.invoke(createInWorld, arguments[0], arguments[1])));
        builder.extension(type, "createEntity", function -> function.param("location", Location.class).returns(JavaTypeRef.javaType(Entity.class).nullable()).invoke(arguments -> NovaEntityReflection.invoke(createAtLocation, arguments[0], arguments[1])));
        builder.extension(type, "entityType", function -> function.returns(EntityType.class).invoke(arguments -> NovaEntityReflection.invoke(getEntityType, arguments[0])));
    }
}
