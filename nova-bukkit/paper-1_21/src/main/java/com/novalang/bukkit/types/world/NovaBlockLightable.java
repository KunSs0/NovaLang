package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.13+ Lightable BlockData 的 Fluxon 函数契约。 */
@Requires(
        classes = {"org.bukkit.block.data.Lightable"},
        methods = {"org.bukkit.block.data.Lightable#isLit", "org.bukkit.block.data.Lightable#setLit"})
public final class NovaBlockLightable {

    private static final String LIGHTABLE = "org.bukkit.block.data.Lightable";

    private NovaBlockLightable() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> lightableType = NovaBlockDataReflection.type(NovaBlockLightable.class, LIGHTABLE);
        Method isLit = NovaBlockDataReflection.method(lightableType, "isLit");
        Method setLit = NovaBlockDataReflection.method(lightableType, "setLit", Boolean.TYPE);
        builder.extension(lightableType, "isLit", function -> function.returns(Boolean.class)
                .invoke(arguments -> NovaBlockDataReflection.invoke(isLit, arguments[0])));
        builder.extension(lightableType, "setLit", function -> function
                .param("lit", Boolean.class).returns(Void.TYPE)
                .invoke(arguments -> NovaBlockDataReflection.invoke(setLit, arguments[0], arguments[1])));
    }
}
