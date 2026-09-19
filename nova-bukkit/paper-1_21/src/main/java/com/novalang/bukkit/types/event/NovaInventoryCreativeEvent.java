package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.inventory.ItemStack;

@Requires(classes = {"org.bukkit.event.inventory.InventoryCreativeEvent"})
public final class NovaInventoryCreativeEvent {
    private NovaInventoryCreativeEvent() { }
    public static void register(JavaTypes.Builder b) {
        JavaTypeRef nullable = JavaTypeRef.javaType(ItemStack.class).nullable();
        b.extension(InventoryCreativeEvent.class, "cursor", f -> f.returns(nullable).invoke(a -> e(a).getCursor()));
        b.extension(InventoryCreativeEvent.class, "setCursor", f -> f.param("cursor", nullable).returns(Void.TYPE).invoke(a -> { e(a).setCursor(NovaTypeSupport.argument(a, 1, ItemStack.class)); return null; }));
    }
    private static InventoryCreativeEvent e(Object[] a) { return NovaTypeSupport.argument(a, 0, InventoryCreativeEvent.class); }
}
