package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.20+ SignSide 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.block.sign.SignSide"}, methods = {
        "org.bukkit.block.sign.SignSide#getLines",
        "org.bukkit.block.sign.SignSide#getLine",
        "org.bukkit.block.sign.SignSide#setLine",
        "org.bukkit.block.sign.SignSide#isGlowingText",
        "org.bukkit.block.sign.SignSide#setGlowingText"})
public final class NovaBlockSignSide {

    private static final String SIGN_SIDE = "org.bukkit.block.sign.SignSide";

    private NovaBlockSignSide() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> signSideType = NovaBlockDataReflection.type(NovaBlockSignSide.class, SIGN_SIDE);
        Method getLines = NovaBlockDataReflection.method(signSideType, "getLines");
        Method getLine = NovaBlockDataReflection.method(signSideType, "getLine", Integer.TYPE);
        Method setLine = NovaBlockDataReflection.method(signSideType, "setLine", Integer.TYPE, String.class);
        Method isGlowingText = NovaBlockDataReflection.method(signSideType, "isGlowingText");
        Method setGlowingText = NovaBlockDataReflection.method(signSideType, "setGlowingText", Boolean.TYPE);

        builder.extension(signSideType, "lines", function -> function
                .returns(String[].class)
                .invoke(arguments -> NovaBlockDataReflection.invoke(getLines, arguments[0])));
        builder.extension(signSideType, "getLine", function -> function
                .param("index", Integer.class)
                .returns(String.class)
                .invoke(arguments -> NovaBlockDataReflection.invoke(getLine, arguments[0], arguments[1])));
        builder.extension(signSideType, "setLine", function -> function
                .param("index", Integer.class)
                .param("line", String.class)
                .returns(Void.TYPE)
                .invoke(arguments -> NovaBlockDataReflection.invoke(setLine, arguments[0], arguments[1], arguments[2])));
        builder.extension(signSideType, "isGlowingText", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> NovaBlockDataReflection.invoke(isGlowingText, arguments[0])));
        builder.extension(signSideType, "setGlowingText", function -> function
                .param("glowing", Boolean.class)
                .returns(Void.TYPE)
                .invoke(arguments -> NovaBlockDataReflection.invoke(setGlowingText, arguments[0], arguments[1])));
    }
}
