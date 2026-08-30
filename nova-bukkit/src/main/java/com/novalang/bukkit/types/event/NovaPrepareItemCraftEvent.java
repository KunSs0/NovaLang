package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Recipe;

/** 合成结果预览事件的可选 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.event.inventory.PrepareItemCraftEvent"})
public final class NovaPrepareItemCraftEvent {

    private NovaPrepareItemCraftEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PrepareItemCraftEvent.class, "recipe", function -> function
                .returns(JavaTypeRef.javaType(Recipe.class).nullable())
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, PrepareItemCraftEvent.class).getRecipe()));
        builder.extension(PrepareItemCraftEvent.class, "inventory", function -> function
                .returns(CraftingInventory.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, PrepareItemCraftEvent.class).getInventory()));
        builder.extension(PrepareItemCraftEvent.class, "isRepair", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, PrepareItemCraftEvent.class).isRepair()));
    }
}
