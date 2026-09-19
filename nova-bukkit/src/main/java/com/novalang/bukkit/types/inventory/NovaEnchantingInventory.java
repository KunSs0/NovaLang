package com.novalang.bukkit.types.inventory;
import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;
@Requires(classes = {"org.bukkit.inventory.EnchantingInventory"})
public final class NovaEnchantingInventory {
    private NovaEnchantingInventory() { }
    public static void register(JavaTypes.Builder builder) {
        JavaTypeRef item = JavaTypeRef.javaType(ItemStack.class).nullable();
        builder.extension(EnchantingInventory.class, "item", function -> function.returns(item).invoke(arguments -> event(arguments).getItem()));
        builder.extension(EnchantingInventory.class, "setItem", function -> function.param("item", item).returns(Void.TYPE).invoke(arguments -> { event(arguments).setItem(NovaTypeSupport.argument(arguments, 1, ItemStack.class)); return null; }));
        builder.extension(EnchantingInventory.class, "secondary", function -> function.returns(item).invoke(arguments -> event(arguments).getSecondary()));
        builder.extension(EnchantingInventory.class, "setSecondary", function -> function.param("item", item).returns(Void.TYPE).invoke(arguments -> { event(arguments).setSecondary(NovaTypeSupport.argument(arguments, 1, ItemStack.class)); return null; }));
    }
    private static EnchantingInventory event(Object[] arguments) { return NovaTypeSupport.argument(arguments, 0, EnchantingInventory.class); }
}
