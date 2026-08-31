package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.LlamaInventory;

/** Spigot 1.12.2 LlamaInventory 扩展。 */
@Requires(classes = {"org.bukkit.inventory.LlamaInventory"})
public final class NovaLlamaInventory {

    private NovaLlamaInventory() {
    }

    public static void register(JavaTypes.Builder builder) {
        JavaTypeRef item = JavaTypeRef.javaType(ItemStack.class).nullable();
        builder.extension(LlamaInventory.class, "decor", function -> function.returns(item).invoke(arguments -> inventory(arguments).getDecor()));
        builder.extension(LlamaInventory.class, "setDecor", function -> function.param("item", item).returns(Void.TYPE).invoke(arguments -> {
            inventory(arguments).setDecor(NovaTypeSupport.argument(arguments, 1, ItemStack.class));
            return null;
        }));
    }

    private static LlamaInventory inventory(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, LlamaInventory.class);
    }
}
