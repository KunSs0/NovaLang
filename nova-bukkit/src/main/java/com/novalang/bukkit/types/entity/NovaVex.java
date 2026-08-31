package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;
import org.bukkit.Location;

/** 1.13+ Vex 的 Fluxon 函数契约。 */
@Requires(
        classes = {"org.bukkit.entity.Vex"},
        methods = {
                "org.bukkit.entity.Vex#isCharging", "org.bukkit.entity.Vex#setCharging",
                "org.bukkit.entity.Vex#getBound", "org.bukkit.entity.Vex#setBound",
                "org.bukkit.entity.Vex#getLifeTicks", "org.bukkit.entity.Vex#setLifeTicks",
                "org.bukkit.entity.Vex#hasLimitedLife"
        })
public final class NovaVex {
    private static final String TYPE = "org.bukkit.entity.Vex";
    private NovaVex() {
    }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaEntityReflection.type(NovaVex.class, TYPE);
        Method charging = NovaEntityReflection.method(type, "isCharging");
        Method setCharging = NovaEntityReflection.method(type, "setCharging", Boolean.TYPE);
        Method bound = NovaEntityReflection.method(type, "getBound");
        Method setBound = NovaEntityReflection.method(type, "setBound", Location.class);
        Method lifeTicks = NovaEntityReflection.method(type, "getLifeTicks");
        Method setLifeTicks = NovaEntityReflection.method(type, "setLifeTicks", Integer.TYPE);
        Method limitedLife = NovaEntityReflection.method(type, "hasLimitedLife");
        JavaTypeRef nullableLocation = JavaTypeRef.javaType(Location.class).nullable();
        builder.extension(type, "isCharging", function -> function.returns(Boolean.class)
                .invoke(arguments -> NovaEntityReflection.invoke(charging, arguments[0])));
        builder.extension(type, "setCharging", function -> function.param("value", Boolean.class).returns(Void.TYPE)
                .invoke(arguments -> NovaEntityReflection.invoke(setCharging, arguments[0], arguments[1])));
        builder.extension(type, "bound", function -> function.returns(nullableLocation)
                .invoke(arguments -> NovaEntityReflection.invoke(bound, arguments[0])));
        builder.extension(type, "setBound", function -> function.param("location", nullableLocation).returns(Void.TYPE)
                .invoke(arguments -> NovaEntityReflection.invoke(setBound, arguments[0], arguments[1])));
        builder.extension(type, "lifeTicks", function -> function.returns(Integer.class)
                .invoke(arguments -> NovaEntityReflection.invoke(lifeTicks, arguments[0])));
        builder.extension(type, "setLifeTicks", function -> function.param("ticks", Integer.class).returns(Void.TYPE)
                .invoke(arguments -> NovaEntityReflection.invoke(setLifeTicks, arguments[0], arguments[1])));
        builder.extension(type, "hasLimitedLife", function -> function.returns(Boolean.class)
                .invoke(arguments -> NovaEntityReflection.invoke(limitedLife, arguments[0])));
    }
}
