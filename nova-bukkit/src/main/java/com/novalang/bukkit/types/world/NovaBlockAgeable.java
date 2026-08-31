package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.13+ Ageable BlockData 的 Fluxon 函数契约。 */
@Requires(
        classes = {"org.bukkit.block.data.Ageable"},
        methods = {
                "org.bukkit.block.data.Ageable#getAge",
                "org.bukkit.block.data.Ageable#setAge",
                "org.bukkit.block.data.Ageable#getMaximumAge"
        })
public final class NovaBlockAgeable {

    private static final String AGEABLE = "org.bukkit.block.data.Ageable";

    private NovaBlockAgeable() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> ageableType = NovaBlockDataReflection.type(NovaBlockAgeable.class, AGEABLE);
        Method getAge = NovaBlockDataReflection.method(ageableType, "getAge");
        Method setAge = NovaBlockDataReflection.method(ageableType, "setAge", Integer.TYPE);
        Method getMaximumAge = NovaBlockDataReflection.method(ageableType, "getMaximumAge");
        builder.extension(ageableType, "age", function -> function.returns(Integer.class)
                .invoke(arguments -> NovaBlockDataReflection.invoke(getAge, arguments[0])));
        builder.extension(ageableType, "setAge", function -> function
                .param("age", Integer.class).returns(Void.TYPE)
                .invoke(arguments -> NovaBlockDataReflection.invoke(setAge, arguments[0], arguments[1])));
        builder.extension(ageableType, "maximumAge", function -> function.returns(Integer.class)
                .invoke(arguments -> NovaBlockDataReflection.invoke(getMaximumAge, arguments[0])));
    }
}
