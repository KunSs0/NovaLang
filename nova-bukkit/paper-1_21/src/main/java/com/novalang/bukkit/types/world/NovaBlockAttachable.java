package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.13+ Attachable BlockData 的 Fluxon 函数契约。 */
@Requires(
        classes = {"org.bukkit.block.data.Attachable"},
        methods = {"org.bukkit.block.data.Attachable#isAttached", "org.bukkit.block.data.Attachable#setAttached"})
public final class NovaBlockAttachable {

    private static final String ATTACHABLE = "org.bukkit.block.data.Attachable";

    private NovaBlockAttachable() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaBlockDataReflection.type(NovaBlockAttachable.class, ATTACHABLE);
        Method isAttached = NovaBlockDataReflection.method(type, "isAttached");
        Method setAttached = NovaBlockDataReflection.method(type, "setAttached", Boolean.TYPE);
        builder.extension(type, "isAttached", function -> function.returns(Boolean.class)
                .invoke(arguments -> NovaBlockDataReflection.invoke(isAttached, arguments[0])));
        builder.extension(type, "setAttached", function -> function.param("attached", Boolean.class).returns(Void.TYPE)
                .invoke(arguments -> NovaBlockDataReflection.invoke(setAttached, arguments[0], arguments[1])));
    }
}
