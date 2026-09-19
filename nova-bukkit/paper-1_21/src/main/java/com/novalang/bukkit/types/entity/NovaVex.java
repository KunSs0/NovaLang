package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.paper.entity.PaperEntityReflection;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;
import org.bukkit.Location;

/** 1.13+ Vex 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.entity.Vex"}, methods = {"org.bukkit.entity.Vex#isCharging", "org.bukkit.entity.Vex#setCharging", "org.bukkit.entity.Vex#getBound", "org.bukkit.entity.Vex#setBound", "org.bukkit.entity.Vex#getLifeTicks", "org.bukkit.entity.Vex#setLifeTicks", "org.bukkit.entity.Vex#hasLimitedLife"})
public final class NovaVex {
    private static final String TYPE = "org.bukkit.entity.Vex";
    private NovaVex() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = PaperEntityReflection.type(NovaVex.class, TYPE); Method charging = PaperEntityReflection.method(type, "isCharging"); Method setCharging = PaperEntityReflection.method(type, "setCharging", Boolean.TYPE); Method bound = PaperEntityReflection.method(type, "getBound"); Method setBound = PaperEntityReflection.method(type, "setBound", Location.class); Method lifeTicks = PaperEntityReflection.method(type, "getLifeTicks"); Method setLifeTicks = PaperEntityReflection.method(type, "setLifeTicks", Integer.TYPE); Method limitedLife = PaperEntityReflection.method(type, "hasLimitedLife");
        JavaTypeRef nullableLocation = JavaTypeRef.javaType(Location.class).nullable();
        builder.extension(type, "isCharging", function -> function.returns(Boolean.class).invoke(arguments -> PaperEntityReflection.invoke(charging, arguments[0])));
        builder.extension(type, "setCharging", function -> function.param("value", Boolean.class).returns(Void.TYPE).invoke(arguments -> PaperEntityReflection.invoke(setCharging, arguments[0], arguments[1])));
        builder.extension(type, "bound", function -> function.returns(nullableLocation).invoke(arguments -> PaperEntityReflection.invoke(bound, arguments[0])));
        builder.extension(type, "setBound", function -> function.param("location", nullableLocation).returns(Void.TYPE).invoke(arguments -> PaperEntityReflection.invoke(setBound, arguments[0], arguments[1])));
        builder.extension(type, "lifeTicks", function -> function.returns(Integer.class).invoke(arguments -> PaperEntityReflection.invoke(lifeTicks, arguments[0])));
        builder.extension(type, "setLifeTicks", function -> function.param("ticks", Integer.class).returns(Void.TYPE).invoke(arguments -> PaperEntityReflection.invoke(setLifeTicks, arguments[0], arguments[1])));
        builder.extension(type, "hasLimitedLife", function -> function.returns(Boolean.class).invoke(arguments -> PaperEntityReflection.invoke(limitedLife, arguments[0])));
    }
}
