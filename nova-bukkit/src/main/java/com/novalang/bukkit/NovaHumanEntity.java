package com.novalang.bukkit;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.inventory.PlayerInventory;

/** HumanEntity 在 Spigot 1.12.2 中的 Fluxon 别名。 */
final class NovaHumanEntity {

    private NovaHumanEntity() {
    }

    static void register(JavaTypes.Builder builder) {
        JavaTypeRef view = JavaTypeRef.javaType(InventoryView.class);
        JavaTypeRef nullableView = view.nullable();
        JavaTypeRef location = JavaTypeRef.javaType(Location.class);
        JavaTypeRef item = JavaTypeRef.javaType(ItemStack.class);
        JavaTypeRef entity = JavaTypeRef.javaType(Entity.class);
        builder.extension(HumanEntity.class, "inventory", f -> f.returns(PlayerInventory.class).invoke(a -> NovaTypeSupport.argument(a, 0, HumanEntity.class).getInventory()));
        builder.extension(HumanEntity.class, "enderChest", f -> f.returns(Inventory.class).invoke(a -> NovaTypeSupport.argument(a, 0, HumanEntity.class).getEnderChest()));
        builder.extension(HumanEntity.class, "mainHand", f -> f.returns(MainHand.class).invoke(a -> NovaTypeSupport.argument(a, 0, HumanEntity.class).getMainHand()));
        builder.extension(HumanEntity.class, "setWindowProperty", f -> f.param("property", InventoryView.Property.class).param("value", Integer.class).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, HumanEntity.class).setWindowProperty(NovaTypeSupport.argument(a, 1, InventoryView.Property.class), NovaTypeSupport.argument(a, 2, Integer.class))));
        builder.extension(HumanEntity.class, "openInventory", f -> f.returns(nullableView).invoke(a -> NovaTypeSupport.argument(a, 0, HumanEntity.class).getOpenInventory()));
        builder.extension(HumanEntity.class, "openInventory", f -> f.param("inventory", Inventory.class).returns(nullableView).invoke(a -> NovaTypeSupport.argument(a, 0, HumanEntity.class).openInventory(NovaTypeSupport.argument(a, 1, Inventory.class))));
        builder.extension(HumanEntity.class, "openWorkbench", f -> f.param("location", location).param("force", Boolean.class).returns(nullableView).invoke(a -> NovaTypeSupport.argument(a, 0, HumanEntity.class).openWorkbench(NovaTypeSupport.argument(a, 1, Location.class), NovaTypeSupport.argument(a, 2, Boolean.class))));
        builder.extension(HumanEntity.class, "openEnchanting", f -> f.param("location", location).param("force", Boolean.class).returns(nullableView).invoke(a -> NovaTypeSupport.argument(a, 0, HumanEntity.class).openEnchanting(NovaTypeSupport.argument(a, 1, Location.class), NovaTypeSupport.argument(a, 2, Boolean.class))));
        builder.extension(HumanEntity.class, "openInventory", f -> f.param("view", view).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, HumanEntity.class).openInventory(NovaTypeSupport.argument(a, 1, InventoryView.class)); return null; }));
        builder.extension(HumanEntity.class, "closeInventory", f -> f.returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, HumanEntity.class).closeInventory(); return null; }));
        builder.extension(HumanEntity.class, "itemInHand", f -> f.returns(item).invoke(a -> NovaTypeSupport.argument(a, 0, HumanEntity.class).getItemInHand()));
        builder.extension(HumanEntity.class, "setItemInHand", f -> f.param("item", item).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, HumanEntity.class).setItemInHand(NovaTypeSupport.argument(a, 1, ItemStack.class)); return null; }));
        builder.extension(HumanEntity.class, "itemOnCursor", f -> f.returns(item).invoke(a -> NovaTypeSupport.argument(a, 0, HumanEntity.class).getItemOnCursor()));
        builder.extension(HumanEntity.class, "setItemOnCursor", f -> f.param("item", item).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, HumanEntity.class).setItemOnCursor(NovaTypeSupport.argument(a, 1, ItemStack.class)); return null; }));
        builder.extension(HumanEntity.class, "hasCooldown", f -> f.param("material", Material.class).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, HumanEntity.class).hasCooldown(NovaTypeSupport.argument(a, 1, Material.class))));
        builder.extension(HumanEntity.class, "getCooldown", f -> f.param("material", Material.class).returns(Integer.class).invoke(a -> NovaTypeSupport.argument(a, 0, HumanEntity.class).getCooldown(NovaTypeSupport.argument(a, 1, Material.class))));
        builder.extension(HumanEntity.class, "setCooldown", f -> f.param("material", Material.class).param("ticks", Integer.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, HumanEntity.class).setCooldown(NovaTypeSupport.argument(a, 1, Material.class), NovaTypeSupport.argument(a, 2, Integer.class)); return null; }));
        builder.extension(HumanEntity.class, "isSleeping", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, HumanEntity.class).isSleeping()));
        builder.extension(HumanEntity.class, "sleepTicks", f -> f.returns(Integer.class).invoke(a -> NovaTypeSupport.argument(a, 0, HumanEntity.class).getSleepTicks()));
        builder.extension(HumanEntity.class, "gameMode", f -> f.returns(GameMode.class).invoke(a -> NovaTypeSupport.argument(a, 0, HumanEntity.class).getGameMode()));
        builder.extension(HumanEntity.class, "setGameMode", f -> f.param("gameMode", GameMode.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, HumanEntity.class).setGameMode(NovaTypeSupport.argument(a, 1, GameMode.class)); return null; }));
        builder.extension(HumanEntity.class, "isBlocking", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, HumanEntity.class).isBlocking()));
        builder.extension(HumanEntity.class, "isHandRaised", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, HumanEntity.class).isHandRaised()));
        builder.extension(HumanEntity.class, "expToLevel", f -> f.returns(Integer.class).invoke(a -> NovaTypeSupport.argument(a, 0, HumanEntity.class).getExpToLevel()));
        builder.extension(HumanEntity.class, "shoulderEntityLeft", f -> f.returns(JavaTypeRef.javaType(Entity.class).nullable()).invoke(a -> NovaTypeSupport.argument(a, 0, HumanEntity.class).getShoulderEntityLeft()));
        builder.extension(HumanEntity.class, "shoulderEntityRight", f -> f.returns(JavaTypeRef.javaType(Entity.class).nullable()).invoke(a -> NovaTypeSupport.argument(a, 0, HumanEntity.class).getShoulderEntityRight()));
    }
}
