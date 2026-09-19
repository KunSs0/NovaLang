package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/** Spigot 1.12.2 ItemFactory 的 Fluxon 函数别名。 */
final class NovaItemFactory {

    private NovaItemFactory() {
    }

    static void register(JavaTypes.Builder builder) {
        JavaTypeRef nullableItemMeta = JavaTypeRef.javaType(ItemMeta.class).nullable();
        builder.extension(ItemFactory.class, "getItemMeta", function -> function.param("material", Material.class)
                .returns(nullableItemMeta).invoke(arguments -> factory(arguments).getItemMeta(argument(arguments, 1, Material.class))));
        builder.extension(ItemFactory.class, "isApplicable", function -> function.param("meta", ItemMeta.class)
                .param("stack", ItemStack.class).returns(Boolean.class)
                .invoke(arguments -> factory(arguments).isApplicable(argument(arguments, 1, ItemMeta.class), argument(arguments, 2, ItemStack.class))));
        builder.extension(ItemFactory.class, "isApplicable", function -> function.param("meta", ItemMeta.class)
                .param("material", Material.class).returns(Boolean.class)
                .invoke(arguments -> factory(arguments).isApplicable(argument(arguments, 1, ItemMeta.class), argument(arguments, 2, Material.class))));
        builder.extension(ItemFactory.class, "asMetaFor", function -> function.param("meta", ItemMeta.class)
                .param("stack", ItemStack.class).returns(nullableItemMeta)
                .invoke(arguments -> factory(arguments).asMetaFor(argument(arguments, 1, ItemMeta.class), argument(arguments, 2, ItemStack.class))));
        builder.extension(ItemFactory.class, "asMetaFor", function -> function.param("meta", ItemMeta.class)
                .param("material", Material.class).returns(nullableItemMeta)
                .invoke(arguments -> factory(arguments).asMetaFor(argument(arguments, 1, ItemMeta.class), argument(arguments, 2, Material.class))));
        builder.extension(ItemFactory.class, "defaultLeatherColor", function -> function.returns(Color.class)
                .invoke(arguments -> factory(arguments).getDefaultLeatherColor()));
    }

    private static ItemFactory factory(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, ItemFactory.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
