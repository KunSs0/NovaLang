package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.14+ Campfire BlockData 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.block.data.type.Campfire"}, methods = {
        "org.bukkit.block.data.type.Campfire#isSignalFire", "org.bukkit.block.data.type.Campfire#setSignalFire"})
public final class NovaBlockCampfire {
    private static final String CAMPFIRE = "org.bukkit.block.data.type.Campfire";
    private NovaBlockCampfire() {
    }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaBlockDataReflection.type(NovaBlockCampfire.class, CAMPFIRE);
        Method isSignalFire = NovaBlockDataReflection.method(type, "isSignalFire");
        Method setSignalFire = NovaBlockDataReflection.method(type, "setSignalFire", Boolean.TYPE);
        builder.extension(type, "isSignalFire", function -> function.returns(Boolean.class).invoke(arguments -> NovaBlockDataReflection.invoke(isSignalFire, arguments[0])));
        builder.extension(type, "setSignalFire", function -> function.param("signalFire", Boolean.class).returns(Void.TYPE).invoke(arguments -> NovaBlockDataReflection.invoke(setSignalFire, arguments[0], arguments[1])));
    }
}
