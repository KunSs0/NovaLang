package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Recipe;

/** 物品合成事件的可选 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.event.inventory.CraftItemEvent"})
public final class NovaCraftItemEvent {

    private NovaCraftItemEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(CraftItemEvent.class, "recipe", function -> function
                .returns(Recipe.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, CraftItemEvent.class).getRecipe()));
        builder.extension(CraftItemEvent.class, "inventory", function -> function
                .returns(CraftingInventory.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, CraftItemEvent.class).getInventory()));
    }
}
