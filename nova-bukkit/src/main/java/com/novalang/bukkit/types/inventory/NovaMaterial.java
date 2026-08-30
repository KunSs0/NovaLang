package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.types.value.NovaTypeSupport;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

/** Spigot 1.12.2 Material 扩展和旧版名称解析。 */
final class NovaMaterial {

    private NovaMaterial() {
    }

    static void register(JavaTypes.Builder builder) {
        JavaTypeRef nullableMaterial = JavaTypeRef.javaType(Material.class).nullable();
        builder.extension(Material.class, "data", f -> f.returns(JavaTypeRef.javaType(Class.class)).invoke(a -> material(a).getData()));
        builder.extension(Material.class, "id", f -> f.returns(Integer.class).invoke(a -> material(a).getId()));
        builder.extension(Material.class, "isBlock", f -> f.returns(Boolean.class).invoke(a -> material(a).isBlock()));
        builder.extension(Material.class, "isEdible", f -> f.returns(Boolean.class).invoke(a -> material(a).isEdible()));
        builder.extension(Material.class, "isRecord", f -> f.returns(Boolean.class).invoke(a -> material(a).isRecord()));
        builder.extension(Material.class, "isSolid", f -> f.returns(Boolean.class).invoke(a -> material(a).isSolid()));
        builder.extension(Material.class, "isTransparent", f -> f.returns(Boolean.class).invoke(a -> material(a).isTransparent()));
        builder.extension(Material.class, "isFlammable", f -> f.returns(Boolean.class).invoke(a -> material(a).isFlammable()));
        builder.extension(Material.class, "isBurnable", f -> f.returns(Boolean.class).invoke(a -> material(a).isBurnable()));
        builder.extension(Material.class, "isFuel", f -> f.returns(Boolean.class).invoke(a -> material(a).isFuel()));
        builder.extension(Material.class, "isOccluding", f -> f.returns(Boolean.class).invoke(a -> material(a).isOccluding()));
        builder.extension(Material.class, "hasGravity", f -> f.returns(Boolean.class).invoke(a -> material(a).hasGravity()));
        builder.extension(Material.class, "isItem", f -> f.returns(Boolean.class).invoke(a -> material(a).isItem()));
        builder.extension(Material.class, "maxStackSize", f -> f.returns(Integer.class).invoke(a -> material(a).getMaxStackSize()));
        builder.extension(Material.class, "maxDurability", f -> f.returns(Integer.class).invoke(a -> (int) material(a).getMaxDurability()));
        builder.extension(Material.class, "getNewData", f -> f.param("data", Integer.class).returns(MaterialData.class).invoke(a -> material(a).getNewData(arg(a, 1, Integer.class).byteValue())));
        builder.extension(Material.class, "getMaterial", f -> f.param("name", String.class).returns(nullableMaterial).invoke(a -> Material.getMaterial(arg(a, 1, String.class))));
        builder.extension(Material.class, "matchMaterial", f -> f.param("name", String.class).returns(nullableMaterial).invoke(a -> Material.matchMaterial(arg(a, 1, String.class))));
    }

    private static Material material(Object[] a) {
        return NovaTypeSupport.argument(a, 0, Material.class);
    }

    private static <T> T arg(Object[] a, int index, Class<T> type) {
        return NovaTypeSupport.argument(a, index, type);
    }
}
