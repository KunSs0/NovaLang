package com.novalang.bukkit.types.value;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.NamespacedKey;

/** Spigot 1.12.2 NamespacedKey 的 Fluxon 函数别名。 */
@Requires(classes = {"org.bukkit.NamespacedKey"})
public final class NovaNamespacedKey {
    private NovaNamespacedKey() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(NamespacedKey.class, "namespace", function -> function.returns(String.class)
                .invoke(arguments -> key(arguments).getNamespace()));
        builder.extension(NamespacedKey.class, "key", function -> function.returns(String.class)
                .invoke(arguments -> key(arguments).getKey()));
    }

    private static NamespacedKey key(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, NamespacedKey.class);
    }
}
