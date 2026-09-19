package com.novalang.bukkit.types.inventory;
import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.inventory.BeaconInventory;
import org.bukkit.inventory.ItemStack;
@Requires(classes = {"org.bukkit.inventory.BeaconInventory"})
public final class NovaBeaconInventory {
    private NovaBeaconInventory() { }
    public static void register(JavaTypes.Builder builder) {
        JavaTypeRef item = JavaTypeRef.javaType(ItemStack.class).nullable();
        builder.extension(BeaconInventory.class, "item", function -> function.returns(item).invoke(arguments -> event(arguments).getItem()));
        builder.extension(BeaconInventory.class, "setItem", function -> function.param("item", item).returns(Void.TYPE).invoke(arguments -> { event(arguments).setItem(NovaTypeSupport.argument(arguments, 1, ItemStack.class)); return null; }));
    }
    private static BeaconInventory event(Object[] arguments) { return NovaTypeSupport.argument(arguments, 0, BeaconInventory.class); }
}
