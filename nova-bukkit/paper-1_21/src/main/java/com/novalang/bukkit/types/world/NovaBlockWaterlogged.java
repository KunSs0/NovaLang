package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.13+ Waterlogged BlockData 的 Fluxon 函数契约。 */
@Requires(
        classes = {"org.bukkit.block.data.Waterlogged"},
        methods = {"org.bukkit.block.data.Waterlogged#isWaterlogged", "org.bukkit.block.data.Waterlogged#setWaterlogged"})
public final class NovaBlockWaterlogged {

    private static final String WATERLOGGED = "org.bukkit.block.data.Waterlogged";

    private NovaBlockWaterlogged() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> waterloggedType = NovaBlockDataReflection.type(NovaBlockWaterlogged.class, WATERLOGGED);
        Method isWaterlogged = NovaBlockDataReflection.method(waterloggedType, "isWaterlogged");
        Method setWaterlogged = NovaBlockDataReflection.method(waterloggedType, "setWaterlogged", Boolean.TYPE);
        builder.extension(waterloggedType, "isWaterlogged", function -> function.returns(Boolean.class)
                .invoke(arguments -> NovaBlockDataReflection.invoke(isWaterlogged, arguments[0])));
        builder.extension(waterloggedType, "setWaterlogged", function -> function
                .param("waterlogged", Boolean.class)
                .returns(Void.TYPE)
                .invoke(arguments -> NovaBlockDataReflection.invoke(setWaterlogged, arguments[0], arguments[1])));
    }
}
