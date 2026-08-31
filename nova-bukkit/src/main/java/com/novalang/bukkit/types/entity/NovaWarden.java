package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

/** 1.19+ Warden 的 Fluxon 函数契约。 */
@Requires(
        classes = {"org.bukkit.entity.Warden", "org.bukkit.entity.Warden$AngerLevel"},
        methods = {
                "org.bukkit.entity.Warden#getAnger",
                "org.bukkit.entity.Warden#increaseAnger",
                "org.bukkit.entity.Warden#setAnger",
                "org.bukkit.entity.Warden#clearAnger",
                "org.bukkit.entity.Warden#getEntityAngryAt",
                "org.bukkit.entity.Warden#setDisturbanceLocation",
                "org.bukkit.entity.Warden#getAngerLevel"
        })
public final class NovaWarden {

    private static final String TYPE = "org.bukkit.entity.Warden";
    private static final String ANGER_LEVEL = "org.bukkit.entity.Warden$AngerLevel";

    private NovaWarden() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaEntityReflection.type(NovaWarden.class, TYPE);
        Class<?> angerLevel = NovaEntityReflection.type(NovaWarden.class, ANGER_LEVEL);
        Method anger = NovaEntityReflection.method(type, "getAnger");
        Method angerFor = NovaEntityReflection.method(type, "getAnger", Entity.class);
        Method increase = NovaEntityReflection.method(type, "increaseAnger", Entity.class, Integer.TYPE);
        Method setAnger = NovaEntityReflection.method(type, "setAnger", Entity.class, Integer.TYPE);
        Method clear = NovaEntityReflection.method(type, "clearAnger", Entity.class);
        Method angryAt = NovaEntityReflection.method(type, "getEntityAngryAt");
        Method disturbance = NovaEntityReflection.method(type, "setDisturbanceLocation", Location.class);
        Method level = NovaEntityReflection.method(type, "getAngerLevel");
        builder.extension(type, "anger", function -> function.returns(Integer.class)
                .invoke(arguments -> NovaEntityReflection.invoke(anger, arguments[0])));
        builder.extension(type, "getAnger", function -> function.param("entity", Entity.class).returns(Integer.class)
                .invoke(arguments -> NovaEntityReflection.invoke(angerFor, arguments[0], arguments[1])));
        builder.extension(type, "increaseAnger", function -> function.param("entity", Entity.class).param("amount", Integer.class).returns(Void.TYPE)
                .invoke(arguments -> NovaEntityReflection.invoke(increase, arguments[0], arguments[1], arguments[2])));
        builder.extension(type, "setAnger", function -> function.param("entity", Entity.class).param("amount", Integer.class).returns(Void.TYPE)
                .invoke(arguments -> NovaEntityReflection.invoke(setAnger, arguments[0], arguments[1], arguments[2])));
        builder.extension(type, "clearAnger", function -> function.param("entity", Entity.class).returns(Void.TYPE)
                .invoke(arguments -> NovaEntityReflection.invoke(clear, arguments[0], arguments[1])));
        builder.extension(type, "entityAngryAt", function -> function.returns(JavaTypeRef.javaType(LivingEntity.class).nullable())
                .invoke(arguments -> NovaEntityReflection.invoke(angryAt, arguments[0])));
        builder.extension(type, "setDisturbanceLocation", function -> function.param("location", Location.class).returns(Void.TYPE)
                .invoke(arguments -> NovaEntityReflection.invoke(disturbance, arguments[0], arguments[1])));
        builder.extension(type, "angerLevel", function -> function.returns(JavaTypeRef.javaType(angerLevel))
                .invoke(arguments -> NovaEntityReflection.invoke(level, arguments[0])));
    }
}
