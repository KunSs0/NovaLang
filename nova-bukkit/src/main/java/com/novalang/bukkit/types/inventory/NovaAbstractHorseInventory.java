package com.novalang.bukkit.types.inventory;
import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.inventory.AbstractHorseInventory;
import org.bukkit.inventory.ItemStack;
@Requires(classes = {"org.bukkit.inventory.AbstractHorseInventory"})
public final class NovaAbstractHorseInventory {
    private NovaAbstractHorseInventory() { }
    public static void register(JavaTypes.Builder builder) {
        JavaTypeRef item = JavaTypeRef.javaType(ItemStack.class).nullable();
        builder.extension(AbstractHorseInventory.class, "saddle", function -> function.returns(item).invoke(arguments -> event(arguments).getSaddle()));
        builder.extension(AbstractHorseInventory.class, "setSaddle", function -> function.param("saddle", item).returns(Void.TYPE).invoke(arguments -> { event(arguments).setSaddle(NovaTypeSupport.argument(arguments, 1, ItemStack.class)); return null; }));
    }
    private static AbstractHorseInventory event(Object[] arguments) { return NovaTypeSupport.argument(arguments, 0, AbstractHorseInventory.class); }
}
