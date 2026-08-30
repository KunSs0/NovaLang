package com.novalang.bukkit.types.value;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;

/** 所有 Bukkit Keyed 对象共用的 Fluxon key 函数别名。 */
@Requires(classes = {"org.bukkit.Keyed"})
public final class NovaKeyed {
    private NovaKeyed() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Keyed.class, "key", function -> function.returns(NamespacedKey.class)
                .invoke(arguments -> keyed(arguments).getKey()));
    }

    private static Keyed keyed(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Keyed.class);
    }
}
