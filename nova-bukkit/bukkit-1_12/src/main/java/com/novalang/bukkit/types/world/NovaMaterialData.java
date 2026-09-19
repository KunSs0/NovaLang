package com.novalang.bukkit.types.world;

import com.novalang.bukkit.types.value.NovaTypeSupport;

import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/** Spigot 1.12.2 MaterialData 扩展。 */
final class NovaMaterialData {

    private NovaMaterialData() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(MaterialData.class, "data", f -> f.returns(Integer.class).invoke(a -> (int) data(a).getData()));
        builder.extension(MaterialData.class, "setData", f -> f.param("data", Integer.class).invoke(a -> { data(a).setData(arg(a, 1, Integer.class).byteValue()); return null; }));
        builder.extension(MaterialData.class, "itemType", f -> f.returns(Material.class).invoke(a -> data(a).getItemType()));
        builder.extension(MaterialData.class, "toItemStack", f -> f.returns(ItemStack.class).invoke(a -> data(a).toItemStack()));
        builder.extension(MaterialData.class, "toItemStack", f -> f.param("amount", Integer.class).returns(ItemStack.class).invoke(a -> data(a).toItemStack(arg(a, 1, Integer.class))));
        builder.extension(MaterialData.class, "toString", f -> f.returns(String.class).invoke(a -> data(a).toString()));
        builder.extension(MaterialData.class, "clone", f -> f.returns(MaterialData.class).invoke(a -> data(a).clone()));
    }

    private static MaterialData data(Object[] a) { return NovaTypeSupport.argument(a, 0, MaterialData.class); }
    private static <T> T arg(Object[] a, int index, Class<T> type) { return NovaTypeSupport.argument(a, index, type); }
}
