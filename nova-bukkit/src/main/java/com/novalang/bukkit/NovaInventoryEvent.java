package com.novalang.bukkit;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.DragType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Set;

/** 常用库存事件的 Spigot 1.12.2 别名。 */
final class NovaInventoryEvent {
    private NovaInventoryEvent() { }

    static void register(JavaTypes.Builder b) {
        JavaTypeRef nullableItem = JavaTypeRef.javaType(ItemStack.class).nullable();
        b.extension(InventoryEvent.class, "inventory", f -> f.returns(Inventory.class).invoke(a -> inventoryEvent(a).getInventory()));
        b.extension(InventoryEvent.class, "viewers", f -> f.returns(JavaTypeRef.listOf(JavaTypeRef.javaType(HumanEntity.class))).invoke(a -> inventoryEvent(a).getViewers()));
        b.extension(InventoryEvent.class, "view", f -> f.returns(InventoryView.class).invoke(a -> inventoryEvent(a).getView()));
        b.extension(InventoryClickEvent.class, "clickedInventory", f -> f.returns(JavaTypeRef.javaType(Inventory.class).nullable()).invoke(a -> click(a).getClickedInventory()));
        b.extension(InventoryClickEvent.class, "slotType", f -> f.returns(InventoryType.SlotType.class).invoke(a -> click(a).getSlotType()));
        b.extension(InventoryClickEvent.class, "cursor", f -> f.returns(nullableItem).invoke(a -> click(a).getCursor()));
        b.extension(InventoryClickEvent.class, "currentItem", f -> f.returns(nullableItem).invoke(a -> click(a).getCurrentItem()));
        b.extension(InventoryClickEvent.class, "slot", f -> f.returns(Integer.class).invoke(a -> click(a).getSlot()));
        b.extension(InventoryClickEvent.class, "rawSlot", f -> f.returns(Integer.class).invoke(a -> click(a).getRawSlot()));
        b.extension(InventoryClickEvent.class, "hotbarButton", f -> f.returns(Integer.class).invoke(a -> click(a).getHotbarButton()));
        b.extension(InventoryClickEvent.class, "action", f -> f.returns(InventoryAction.class).invoke(a -> click(a).getAction()));
        b.extension(InventoryClickEvent.class, "click", f -> f.returns(ClickType.class).invoke(a -> click(a).getClick()));
        b.extension(InventoryClickEvent.class, "isRightClick", f -> f.returns(Boolean.class).invoke(a -> click(a).isRightClick()));
        b.extension(InventoryClickEvent.class, "isLeftClick", f -> f.returns(Boolean.class).invoke(a -> click(a).isLeftClick()));
        b.extension(InventoryClickEvent.class, "isShiftClick", f -> f.returns(Boolean.class).invoke(a -> click(a).isShiftClick()));
        b.extension(InventoryDragEvent.class, "newItems", f -> f.returns(Map.class).invoke(a -> drag(a).getNewItems()));
        b.extension(InventoryDragEvent.class, "rawSlots", f -> f.returns(Set.class).invoke(a -> drag(a).getRawSlots()));
        b.extension(InventoryDragEvent.class, "inventorySlots", f -> f.returns(Set.class).invoke(a -> drag(a).getInventorySlots()));
        b.extension(InventoryDragEvent.class, "oldCursor", f -> f.returns(nullableItem).invoke(a -> drag(a).getOldCursor()));
        b.extension(InventoryDragEvent.class, "dragType", f -> f.returns(DragType.class).invoke(a -> drag(a).getType()));
        b.extension(InventoryOpenEvent.class, "player", f -> f.returns(HumanEntity.class).invoke(a -> open(a).getPlayer()));
        b.extension(InventoryCloseEvent.class, "player", f -> f.returns(HumanEntity.class).invoke(a -> close(a).getPlayer()));
    }

    private static InventoryEvent inventoryEvent(Object[] a) { return NovaTypeSupport.argument(a, 0, InventoryEvent.class); }
    private static InventoryClickEvent click(Object[] a) { return NovaTypeSupport.argument(a, 0, InventoryClickEvent.class); }
    private static InventoryDragEvent drag(Object[] a) { return NovaTypeSupport.argument(a, 0, InventoryDragEvent.class); }
    private static InventoryOpenEvent open(Object[] a) { return NovaTypeSupport.argument(a, 0, InventoryOpenEvent.class); }
    private static InventoryCloseEvent close(Object[] a) { return NovaTypeSupport.argument(a, 0, InventoryCloseEvent.class); }
}
