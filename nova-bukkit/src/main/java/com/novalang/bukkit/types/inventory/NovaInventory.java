package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.types.value.NovaTypeSupport;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/** Spigot 1.12.2 Inventory、PlayerInventory、InventoryView 扩展。 */
final class NovaInventory {

    private NovaInventory() {
    }

    static void register(JavaTypes.Builder builder) {
        JavaTypeRef item = JavaTypeRef.javaType(ItemStack.class).nullable();
        JavaTypeRef items = JavaTypeRef.javaType(ItemStack[].class);
        builder.extension(Inventory.class, "size", f -> f.returns(Integer.class).invoke(a -> inv(a).getSize()));
        builder.extension(Inventory.class, "maxStackSize", f -> f.returns(Integer.class).invoke(a -> inv(a).getMaxStackSize()));
        builder.extension(Inventory.class, "setMaxStackSize", f -> f.param("size", Integer.class).invoke(a -> { inv(a).setMaxStackSize(arg(a, 1, Integer.class)); return null; }));
        builder.extension(Inventory.class, "getItem", f -> f.param("slot", Integer.class).returns(item).invoke(a -> inv(a).getItem(arg(a, 1, Integer.class))));
        builder.extension(Inventory.class, "setItem", f -> f.param("slot", Integer.class).param("item", item).invoke(a -> { inv(a).setItem(arg(a, 1, Integer.class), arg(a, 2, ItemStack.class)); return null; }));
        builder.extension(Inventory.class, "contents", f -> f.returns(items).invoke(a -> inv(a).getContents()));
        builder.extension(Inventory.class, "setContents", f -> f.param("contents", items).invoke(a -> { inv(a).setContents(arg(a, 1, ItemStack[].class)); return null; }));
        builder.extension(Inventory.class, "storageContents", f -> f.returns(items).invoke(a -> inv(a).getStorageContents()));
        builder.extension(Inventory.class, "setStorageContents", f -> f.param("contents", items).invoke(a -> { inv(a).setStorageContents(arg(a, 1, ItemStack[].class)); return null; }));
        builder.extension(Inventory.class, "contains", f -> f.param("material", Material.class).returns(Boolean.class).invoke(a -> inv(a).contains(arg(a, 1, Material.class))));
        builder.extension(Inventory.class, "contains", f -> f.param("item", ItemStack.class).returns(Boolean.class).invoke(a -> inv(a).contains(arg(a, 1, ItemStack.class))));
        builder.extension(Inventory.class, "contains", f -> f.param("material", Material.class).param("amount", Integer.class).returns(Boolean.class).invoke(a -> inv(a).contains(arg(a, 1, Material.class), arg(a, 2, Integer.class))));
        builder.extension(Inventory.class, "contains", f -> f.param("item", ItemStack.class).param("amount", Integer.class).returns(Boolean.class).invoke(a -> inv(a).contains(arg(a, 1, ItemStack.class), arg(a, 2, Integer.class))));
        builder.extension(Inventory.class, "containsAtLeast", f -> f.param("item", ItemStack.class).param("amount", Integer.class).returns(Boolean.class).invoke(a -> inv(a).containsAtLeast(arg(a, 1, ItemStack.class), arg(a, 2, Integer.class))));
        builder.extension(Inventory.class, "first", f -> f.param("material", Material.class).returns(Integer.class).invoke(a -> inv(a).first(arg(a, 1, Material.class))));
        builder.extension(Inventory.class, "first", f -> f.param("item", ItemStack.class).returns(Integer.class).invoke(a -> inv(a).first(arg(a, 1, ItemStack.class))));
        builder.extension(Inventory.class, "firstEmpty", f -> f.returns(Integer.class).invoke(a -> inv(a).firstEmpty()));
        builder.extension(Inventory.class, "isEmpty", f -> f.returns(Boolean.class).invoke(a -> isEmpty(inv(a))));
        builder.extension(Inventory.class, "remove", f -> f.param("material", Material.class).invoke(a -> { inv(a).remove(arg(a, 1, Material.class)); return null; }));
        builder.extension(Inventory.class, "remove", f -> f.param("item", ItemStack.class).invoke(a -> { inv(a).remove(arg(a, 1, ItemStack.class)); return null; }));
        builder.extension(Inventory.class, "clear", f -> f.invoke(a -> { inv(a).clear(); return null; }));
        builder.extension(Inventory.class, "clear", f -> f.param("slot", Integer.class).invoke(a -> { inv(a).clear(arg(a, 1, Integer.class)); return null; }));
        builder.extension(Inventory.class, "viewers", f -> f.returns(JavaTypeRef.listOf(JavaTypeRef.javaType(HumanEntity.class))).invoke(a -> inv(a).getViewers()));
        builder.extension(Inventory.class, "type", f -> f.returns(org.bukkit.event.inventory.InventoryType.class).invoke(a -> inv(a).getType()));
        builder.extension(Inventory.class, "holder", f -> f.returns(JavaTypeRef.javaType(InventoryHolder.class).nullable()).invoke(a -> inv(a).getHolder()));
        builder.extension(Inventory.class, "location", f -> f.returns(JavaTypeRef.javaType(org.bukkit.Location.class).nullable()).invoke(a -> inv(a).getLocation()));
        builder.extension(Inventory.class, "iterator", f -> f.returns(java.util.ListIterator.class).invoke(a -> inv(a).iterator()));
        builder.extension(Inventory.class, "iterator", f -> f.param("index", Integer.class).returns(java.util.ListIterator.class).invoke(a -> inv(a).iterator(arg(a, 1, Integer.class))));

        builder.extension(InventoryHolder.class, "inventory", f -> f.returns(Inventory.class).invoke(a -> NovaTypeSupport.argument(a, 0, InventoryHolder.class).getInventory()));
        builder.extension(InventoryView.class, "topInventory", f -> f.returns(Inventory.class).invoke(a -> view(a).getTopInventory()));
        builder.extension(InventoryView.class, "bottomInventory", f -> f.returns(Inventory.class).invoke(a -> view(a).getBottomInventory()));
        builder.extension(InventoryView.class, "player", f -> f.returns(HumanEntity.class).invoke(a -> view(a).getPlayer()));
        builder.extension(InventoryView.class, "type", f -> f.returns(org.bukkit.event.inventory.InventoryType.class).invoke(a -> view(a).getType()));
        builder.extension(InventoryView.class, "getItem", f -> f.param("slot", Integer.class).returns(item).invoke(a -> view(a).getItem(arg(a, 1, Integer.class))));
        builder.extension(InventoryView.class, "setItem", f -> f.param("slot", Integer.class).param("item", item).invoke(a -> { view(a).setItem(arg(a, 1, Integer.class), arg(a, 2, ItemStack.class)); return null; }));
        builder.extension(InventoryView.class, "cursor", f -> f.returns(item).invoke(a -> view(a).getCursor()));
        builder.extension(InventoryView.class, "setCursor", f -> f.param("item", item).invoke(a -> { view(a).setCursor(arg(a, 1, ItemStack.class)); return null; }));
        builder.extension(InventoryView.class, "countSlots", f -> f.returns(Integer.class).invoke(a -> view(a).countSlots()));
        builder.extension(InventoryView.class, "title", f -> f.returns(String.class).invoke(a -> view(a).getTitle()));
        builder.extension(InventoryView.class, "close", f -> f.invoke(a -> { view(a).close(); return null; }));
    }

    private static Inventory inv(Object[] a) {
        return NovaTypeSupport.argument(a, 0, Inventory.class);
    }

    private static InventoryView view(Object[] a) {
        return NovaTypeSupport.argument(a, 0, InventoryView.class);
    }

    private static boolean isEmpty(Inventory inventory) {
        for (ItemStack stack : inventory.getContents()) {
            if (stack != null && !stack.getType().equals(Material.AIR) && stack.getAmount() > 0) {
                return false;
            }
        }
        return true;
    }

    private static <T> T arg(Object[] a, int index, Class<T> type) {
        return NovaTypeSupport.argument(a, index, type);
    }
}
