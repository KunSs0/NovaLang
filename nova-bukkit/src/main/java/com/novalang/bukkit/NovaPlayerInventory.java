package com.novalang.bukkit;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/** Spigot 1.12.2 PlayerInventory 别名和装备栏扩展。 */
final class NovaPlayerInventory {

    private NovaPlayerInventory() {
    }

    static void register(JavaTypes.Builder builder) {
        JavaTypeRef item = JavaTypeRef.javaType(ItemStack.class).nullable();
        JavaTypeRef items = JavaTypeRef.javaType(ItemStack[].class);
        builder.extension(PlayerInventory.class, "armorContents", f -> f.returns(items).invoke(a -> pi(a).getArmorContents()));
        builder.extension(PlayerInventory.class, "extraContents", f -> f.returns(items).invoke(a -> pi(a).getExtraContents()));
        builder.extension(PlayerInventory.class, "helmet", f -> f.returns(item).invoke(a -> pi(a).getHelmet()));
        builder.extension(PlayerInventory.class, "chestplate", f -> f.returns(item).invoke(a -> pi(a).getChestplate()));
        builder.extension(PlayerInventory.class, "leggings", f -> f.returns(item).invoke(a -> pi(a).getLeggings()));
        builder.extension(PlayerInventory.class, "boots", f -> f.returns(item).invoke(a -> pi(a).getBoots()));
        builder.extension(PlayerInventory.class, "itemInMainHand", f -> f.returns(item).invoke(a -> pi(a).getItemInMainHand()));
        builder.extension(PlayerInventory.class, "itemInOffHand", f -> f.returns(item).invoke(a -> pi(a).getItemInOffHand()));
        builder.extension(PlayerInventory.class, "itemInHand", f -> f.returns(item).invoke(a -> pi(a).getItemInHand()));
        builder.extension(PlayerInventory.class, "heldItemSlot", f -> f.returns(Integer.class).invoke(a -> pi(a).getHeldItemSlot()));
        builder.extension(PlayerInventory.class, "setArmorContents", f -> f.param("contents", items).invoke(a -> { pi(a).setArmorContents(arg(a, 1, ItemStack[].class)); return null; }));
        builder.extension(PlayerInventory.class, "setExtraContents", f -> f.param("contents", items).invoke(a -> { pi(a).setExtraContents(arg(a, 1, ItemStack[].class)); return null; }));
        builder.extension(PlayerInventory.class, "setHelmet", f -> f.param("item", item).invoke(a -> { pi(a).setHelmet(arg(a, 1, ItemStack.class)); return null; }));
        builder.extension(PlayerInventory.class, "setChestplate", f -> f.param("item", item).invoke(a -> { pi(a).setChestplate(arg(a, 1, ItemStack.class)); return null; }));
        builder.extension(PlayerInventory.class, "setLeggings", f -> f.param("item", item).invoke(a -> { pi(a).setLeggings(arg(a, 1, ItemStack.class)); return null; }));
        builder.extension(PlayerInventory.class, "setBoots", f -> f.param("item", item).invoke(a -> { pi(a).setBoots(arg(a, 1, ItemStack.class)); return null; }));
        builder.extension(PlayerInventory.class, "setItemInMainHand", f -> f.param("item", item).invoke(a -> { pi(a).setItemInMainHand(arg(a, 1, ItemStack.class)); return null; }));
        builder.extension(PlayerInventory.class, "setItemInOffHand", f -> f.param("item", item).invoke(a -> { pi(a).setItemInOffHand(arg(a, 1, ItemStack.class)); return null; }));
        builder.extension(PlayerInventory.class, "setItemInHand", f -> f.param("item", item).invoke(a -> { pi(a).setItemInHand(arg(a, 1, ItemStack.class)); return null; }));
        builder.extension(PlayerInventory.class, "setHeldItemSlot", f -> f.param("slot", Integer.class).invoke(a -> { pi(a).setHeldItemSlot(arg(a, 1, Integer.class)); return null; }));
    }

    private static PlayerInventory pi(Object[] a) {
        return NovaTypeSupport.argument(a, 0, PlayerInventory.class);
    }

    private static <T> T arg(Object[] a, int index, Class<T> type) {
        return NovaTypeSupport.argument(a, index, type);
    }
}
