package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;

/** 1.17+ GlowSquid 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.entity.GlowSquid"}, methods = {
        "org.bukkit.entity.GlowSquid#getDarkTicksRemaining", "org.bukkit.entity.GlowSquid#setDarkTicksRemaining"})
public final class NovaGlowSquid {

    private static final String TYPE = "org.bukkit.entity.GlowSquid";

    private NovaGlowSquid() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaEntityReflection.type(NovaGlowSquid.class, TYPE);
        Method darkTicks = NovaEntityReflection.method(type, "getDarkTicksRemaining");
        Method setDarkTicks = NovaEntityReflection.method(type, "setDarkTicksRemaining", Integer.TYPE);
        builder.extension(type, "darkTicksRemaining", function -> function.returns(Integer.class)
                .invoke(arguments -> NovaEntityReflection.invoke(darkTicks, arguments[0])));
        builder.extension(type, "setDarkTicksRemaining", function -> function.param("ticks", Integer.class).returns(Void.TYPE)
                .invoke(arguments -> NovaEntityReflection.invoke(setDarkTicks, arguments[0], arguments[1])));
    }
}
