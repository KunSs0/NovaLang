package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;

/** 1.14+ WanderingTrader 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.entity.WanderingTrader"}, methods = {
        "org.bukkit.entity.WanderingTrader#getDespawnDelay", "org.bukkit.entity.WanderingTrader#setDespawnDelay"})
public final class NovaWanderingTrader {

    private static final String TYPE = "org.bukkit.entity.WanderingTrader";

    private NovaWanderingTrader() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaEntityReflection.type(NovaWanderingTrader.class, TYPE);
        Method despawnDelay = NovaEntityReflection.method(type, "getDespawnDelay");
        Method setDespawnDelay = NovaEntityReflection.method(type, "setDespawnDelay", Integer.TYPE);
        builder.extension(type, "despawnDelay", function -> function.returns(Integer.class)
                .invoke(arguments -> NovaEntityReflection.invoke(despawnDelay, arguments[0])));
        builder.extension(type, "setDespawnDelay", function -> function.param("ticks", Integer.class).returns(Void.TYPE)
                .invoke(arguments -> NovaEntityReflection.invoke(setDespawnDelay, arguments[0], arguments[1])));
    }
}
