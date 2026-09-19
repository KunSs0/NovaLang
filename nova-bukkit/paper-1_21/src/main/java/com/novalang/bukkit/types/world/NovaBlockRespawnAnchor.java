package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.16+ RespawnAnchor BlockData 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.block.data.type.RespawnAnchor"}, methods = {
        "org.bukkit.block.data.type.RespawnAnchor#getCharges", "org.bukkit.block.data.type.RespawnAnchor#setCharges", "org.bukkit.block.data.type.RespawnAnchor#getMaximumCharges"})
public final class NovaBlockRespawnAnchor {
    private static final String RESPAWN_ANCHOR = "org.bukkit.block.data.type.RespawnAnchor";
    private NovaBlockRespawnAnchor() {
    }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaBlockDataReflection.type(NovaBlockRespawnAnchor.class, RESPAWN_ANCHOR);
        Method getCharges = NovaBlockDataReflection.method(type, "getCharges");
        Method setCharges = NovaBlockDataReflection.method(type, "setCharges", Integer.TYPE);
        Method getMaximumCharges = NovaBlockDataReflection.method(type, "getMaximumCharges");
        builder.extension(type, "charges", function -> function.returns(Integer.class).invoke(arguments -> NovaBlockDataReflection.invoke(getCharges, arguments[0])));
        builder.extension(type, "setCharges", function -> function.param("charges", Integer.class).returns(Void.TYPE).invoke(arguments -> NovaBlockDataReflection.invoke(setCharges, arguments[0], arguments[1])));
        builder.extension(type, "maximumCharges", function -> function.returns(Integer.class).invoke(arguments -> NovaBlockDataReflection.invoke(getMaximumCharges, arguments[0])));
    }
}
