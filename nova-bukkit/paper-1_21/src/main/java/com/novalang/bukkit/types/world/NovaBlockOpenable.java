package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.13+ Openable BlockData 的 Fluxon 函数契约。 */
@Requires(
        classes = {"org.bukkit.block.data.Openable"},
        methods = {"org.bukkit.block.data.Openable#isOpen", "org.bukkit.block.data.Openable#setOpen"})
public final class NovaBlockOpenable {

    private static final String OPENABLE = "org.bukkit.block.data.Openable";

    private NovaBlockOpenable() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> openableType = NovaBlockDataReflection.type(NovaBlockOpenable.class, OPENABLE);
        Method isOpen = NovaBlockDataReflection.method(openableType, "isOpen");
        Method setOpen = NovaBlockDataReflection.method(openableType, "setOpen", Boolean.TYPE);
        builder.extension(openableType, "isOpen", function -> function.returns(Boolean.class)
                .invoke(arguments -> NovaBlockDataReflection.invoke(isOpen, arguments[0])));
        builder.extension(openableType, "setOpen", function -> function
                .param("open", Boolean.class)
                .returns(Void.TYPE)
                .invoke(arguments -> NovaBlockDataReflection.invoke(setOpen, arguments[0], arguments[1])));
    }
}
