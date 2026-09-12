package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.paper.entity.PaperEntityReflection;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import java.lang.reflect.Method;

/** 1.20.5+ EntitySnapshot 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.entity.EntitySnapshot"}, methods = {"org.bukkit.entity.EntitySnapshot#createEntity", "org.bukkit.entity.EntitySnapshot#getEntityType"})
public final class NovaEntitySnapshot {
    private static final String TYPE = "org.bukkit.entity.EntitySnapshot";
    private NovaEntitySnapshot() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = PaperEntityReflection.type(NovaEntitySnapshot.class, TYPE);
        Method createInWorld = PaperEntityReflection.method(type, "createEntity", World.class);
        Method createAtLocation = PaperEntityReflection.method(type, "createEntity", Location.class);
        Method getEntityType = PaperEntityReflection.method(type, "getEntityType");
        builder.extension(type, "createEntity", function -> function.param("world", World.class).returns(JavaTypeRef.javaType(Entity.class).nullable()).invoke(arguments -> PaperEntityReflection.invoke(createInWorld, arguments[0], arguments[1])));
        builder.extension(type, "createEntity", function -> function.param("location", Location.class).returns(JavaTypeRef.javaType(Entity.class).nullable()).invoke(arguments -> PaperEntityReflection.invoke(createAtLocation, arguments[0], arguments[1])));
        builder.extension(type, "entityType", function -> function.returns(EntityType.class).invoke(arguments -> PaperEntityReflection.invoke(getEntityType, arguments[0])));
    }
}
