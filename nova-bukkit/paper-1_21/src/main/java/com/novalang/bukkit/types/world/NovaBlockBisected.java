package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.13+ Bisected BlockData 的 Fluxon 函数契约。 */
@Requires(
        classes = {"org.bukkit.block.data.Bisected", "org.bukkit.block.data.Bisected$Half"},
        methods = {"org.bukkit.block.data.Bisected#getHalf", "org.bukkit.block.data.Bisected#setHalf"})
public final class NovaBlockBisected {

    private static final String BISECTED = "org.bukkit.block.data.Bisected";
    private static final String HALF = "org.bukkit.block.data.Bisected$Half";

    private NovaBlockBisected() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> bisectedType = NovaBlockDataReflection.type(NovaBlockBisected.class, BISECTED);
        Class<?> halfType = NovaBlockDataReflection.type(NovaBlockBisected.class, HALF);
        Method getHalf = NovaBlockDataReflection.method(bisectedType, "getHalf");
        Method setHalf = NovaBlockDataReflection.method(bisectedType, "setHalf", halfType);
        builder.extension(bisectedType, "half", function -> function.returns(JavaTypeRef.javaType(halfType))
                .invoke(arguments -> NovaBlockDataReflection.invoke(getHalf, arguments[0])));
        builder.extension(bisectedType, "setHalf", function -> function
                .param("half", halfType).returns(Void.TYPE)
                .invoke(arguments -> NovaBlockDataReflection.invoke(setHalf, arguments[0], arguments[1])));
        builder.extension(bisectedType, "setHalf", function -> function
                .param("half", String.class).returns(Void.TYPE)
                .invoke(arguments -> {
                    Object half = NovaBlockDataReflection.enumValue(halfType, (String) arguments[1]);
                    if (half != null) {
                        NovaBlockDataReflection.invoke(setHalf, arguments[0], half);
                    }
                    return null;
                }));
    }
}
