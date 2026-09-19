package com.novalang.bukkit.types.value;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Nameable;

/** Bukkit Nameable 的 Fluxon 函数别名。 */
@Requires(classes = {"org.bukkit.Nameable"})
public final class NovaNameable {

    private NovaNameable() {
    }

    public static void register(JavaTypes.Builder builder) {
        JavaTypeRef nullableString = JavaTypeRef.javaType(String.class).nullable();
        builder.extension(Nameable.class, "customName", function -> function
                .returns(nullableString)
                .invoke(arguments -> nameable(arguments).getCustomName()));
        builder.extension(Nameable.class, "setCustomName", function -> function
                .param("name", nullableString)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    nameable(arguments).setCustomName(NovaTypeSupport.argument(arguments, 1, String.class));
                    return null;
                }));
    }

    private static Nameable nameable(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Nameable.class);
    }
}
