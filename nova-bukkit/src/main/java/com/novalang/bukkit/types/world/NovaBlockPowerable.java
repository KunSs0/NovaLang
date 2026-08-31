package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.13+ Powerable BlockData 的 Fluxon 函数契约。 */
@Requires(
        classes = {"org.bukkit.block.data.Powerable"},
        methods = {"org.bukkit.block.data.Powerable#isPowered", "org.bukkit.block.data.Powerable#setPowered"})
public final class NovaBlockPowerable {

    private static final String POWERABLE = "org.bukkit.block.data.Powerable";

    private NovaBlockPowerable() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> powerableType = NovaBlockDataReflection.type(NovaBlockPowerable.class, POWERABLE);
        Method isPowered = NovaBlockDataReflection.method(powerableType, "isPowered");
        Method setPowered = NovaBlockDataReflection.method(powerableType, "setPowered", Boolean.TYPE);
        builder.extension(powerableType, "isPowered", function -> function.returns(Boolean.class)
                .invoke(arguments -> NovaBlockDataReflection.invoke(isPowered, arguments[0])));
        builder.extension(powerableType, "setPowered", function -> function
                .param("powered", Boolean.class)
                .returns(Void.TYPE)
                .invoke(arguments -> NovaBlockDataReflection.invoke(setPowered, arguments[0], arguments[1])));
    }
}
