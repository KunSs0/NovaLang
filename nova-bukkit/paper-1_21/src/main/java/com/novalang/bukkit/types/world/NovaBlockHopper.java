package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.13+ Hopper BlockData 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.block.data.type.Hopper"}, methods = {
        "org.bukkit.block.data.type.Hopper#isEnabled",
        "org.bukkit.block.data.type.Hopper#setEnabled"})
public final class NovaBlockHopper {

    private static final String HOPPER = "org.bukkit.block.data.type.Hopper";

    private NovaBlockHopper() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> hopperType = NovaBlockDataReflection.type(NovaBlockHopper.class, HOPPER);
        Method isEnabled = NovaBlockDataReflection.method(hopperType, "isEnabled");
        Method setEnabled = NovaBlockDataReflection.method(hopperType, "setEnabled", Boolean.TYPE);

        builder.extension(hopperType, "isEnabled", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> NovaBlockDataReflection.invoke(isEnabled, arguments[0])));
        builder.extension(hopperType, "setEnabled", function -> function
                .param("enabled", Boolean.class)
                .returns(Void.TYPE)
                .invoke(arguments -> NovaBlockDataReflection.invoke(setEnabled, arguments[0], arguments[1])));
    }
}
