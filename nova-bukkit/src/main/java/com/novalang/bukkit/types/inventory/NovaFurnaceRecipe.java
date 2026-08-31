package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.material.MaterialData;

/** Spigot 1.12.2 FurnaceRecipe 的 Fluxon 输入设置别名。 */
public final class NovaFurnaceRecipe {

    private NovaFurnaceRecipe() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(FurnaceRecipe.class, "setInput", function -> function.param("input", MaterialData.class).returns(Void.TYPE).invoke(arguments -> {
            recipe(arguments).setInput(NovaTypeSupport.argument(arguments, 1, MaterialData.class));
            return null;
        }));
        builder.extension(FurnaceRecipe.class, "setInput", function -> function.param("input", Material.class).param("data", Integer.class).returns(Void.TYPE).invoke(arguments -> {
            recipe(arguments).setInput(NovaTypeSupport.argument(arguments, 1, Material.class), NovaTypeSupport.argument(arguments, 2, Integer.class));
            return null;
        }));
    }

    private static FurnaceRecipe recipe(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, FurnaceRecipe.class);
    }
}
