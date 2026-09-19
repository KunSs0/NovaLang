package com.novalang.bukkit.types.server;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.NamespacedKey;
import org.bukkit.UnsafeValues;
import org.bukkit.advancement.Advancement;
import org.bukkit.inventory.ItemStack;

/** Spigot 1.12.2 UnsafeValues 的 Fluxon 可用函数别名。 */
@Requires(classes = {"org.bukkit.UnsafeValues"})
public final class NovaUnsafeValues {

    private NovaUnsafeValues() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(UnsafeValues.class, "modifyItemStack", function -> function
                .param("stack", ItemStack.class)
                .param("arguments", String.class)
                .returns(ItemStack.class)
                .invoke(arguments -> unsafeValues(arguments).modifyItemStack(
                        NovaTypeSupport.argument(arguments, 1, ItemStack.class),
                        NovaTypeSupport.argument(arguments, 2, String.class))));
        builder.extension(UnsafeValues.class, "loadAdvancement", function -> function
                .param("key", NamespacedKey.class)
                .param("advancement", String.class)
                .returns(JavaTypeRef.javaType(Advancement.class).nullable())
                .invoke(arguments -> unsafeValues(arguments).loadAdvancement(
                        NovaTypeSupport.argument(arguments, 1, NamespacedKey.class),
                        NovaTypeSupport.argument(arguments, 2, String.class))));
        builder.extension(UnsafeValues.class, "removeAdvancement", function -> function
                .param("key", NamespacedKey.class)
                .returns(Boolean.class)
                .invoke(arguments -> unsafeValues(arguments).removeAdvancement(
                        NovaTypeSupport.argument(arguments, 1, NamespacedKey.class))));
    }

    private static UnsafeValues unsafeValues(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, UnsafeValues.class);
    }
}
