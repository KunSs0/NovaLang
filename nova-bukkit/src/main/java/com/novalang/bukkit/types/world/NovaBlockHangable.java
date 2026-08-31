package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.13+ Hangable BlockData 的 Fluxon 函数契约。 */
@Requires(
        classes = {"org.bukkit.block.data.Hangable"},
        methods = {"org.bukkit.block.data.Hangable#isHanging", "org.bukkit.block.data.Hangable#setHanging"})
public final class NovaBlockHangable {

    private static final String HANGABLE = "org.bukkit.block.data.Hangable";

    private NovaBlockHangable() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaBlockDataReflection.type(NovaBlockHangable.class, HANGABLE);
        Method isHanging = NovaBlockDataReflection.method(type, "isHanging");
        Method setHanging = NovaBlockDataReflection.method(type, "setHanging", Boolean.TYPE);
        builder.extension(type, "isHanging", function -> function.returns(Boolean.class)
                .invoke(arguments -> NovaBlockDataReflection.invoke(isHanging, arguments[0])));
        builder.extension(type, "setHanging", function -> function.param("hanging", Boolean.class).returns(Void.TYPE)
                .invoke(arguments -> NovaBlockDataReflection.invoke(setHanging, arguments[0], arguments[1])));
    }
}
