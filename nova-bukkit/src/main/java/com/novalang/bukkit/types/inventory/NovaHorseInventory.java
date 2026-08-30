package com.novalang.bukkit.types.inventory;
import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.ItemStack;
@Requires(classes = {"org.bukkit.inventory.HorseInventory"})
public final class NovaHorseInventory {
    private NovaHorseInventory() { }
    public static void register(JavaTypes.Builder builder) {
        JavaTypeRef item = JavaTypeRef.javaType(ItemStack.class).nullable();
        builder.extension(HorseInventory.class, "armor", function -> function.returns(item).invoke(arguments -> event(arguments).getArmor()));
        builder.extension(HorseInventory.class, "setArmor", function -> function.param("armor", item).returns(Void.TYPE).invoke(arguments -> { event(arguments).setArmor(NovaTypeSupport.argument(arguments, 1, ItemStack.class)); return null; }));
    }
    private static HorseInventory event(Object[] arguments) { return NovaTypeSupport.argument(arguments, 0, HorseInventory.class); }
}
