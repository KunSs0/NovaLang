package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.List;

@Requires(classes = {"org.bukkit.inventory.MerchantRecipe"})
public final class NovaMerchantRecipe {
    private NovaMerchantRecipe() { }
    public static void register(JavaTypes.Builder builder) {
        builder.extension(MerchantRecipe.class, "result", function -> function.returns(ItemStack.class).invoke(arguments -> event(arguments).getResult()));
        builder.extension(MerchantRecipe.class, "ingredients", function -> function.returns(JavaTypeRef.listOf(JavaTypeRef.javaType(ItemStack.class))).invoke(arguments -> event(arguments).getIngredients()));
        builder.extension(MerchantRecipe.class, "addIngredient", function -> function.param("item", ItemStack.class).returns(Void.TYPE).invoke(arguments -> { event(arguments).addIngredient(NovaTypeSupport.argument(arguments, 1, ItemStack.class)); return null; }));
        builder.extension(MerchantRecipe.class, "removeIngredient", function -> function.param("index", Integer.class).returns(Void.TYPE).invoke(arguments -> { event(arguments).removeIngredient(NovaTypeSupport.argument(arguments, 1, Integer.class)); return null; }));
        builder.extension(MerchantRecipe.class, "setIngredients", function -> function.param("ingredients", JavaTypeRef.listOf(JavaTypeRef.javaType(ItemStack.class))).returns(Void.TYPE).invoke(arguments -> { event(arguments).setIngredients(ingredients(arguments, 1)); return null; }));
        builder.extension(MerchantRecipe.class, "uses", function -> function.returns(Integer.class).invoke(arguments -> event(arguments).getUses()));
        builder.extension(MerchantRecipe.class, "setUses", function -> function.param("uses", Integer.class).returns(Void.TYPE).invoke(arguments -> { event(arguments).setUses(NovaTypeSupport.argument(arguments, 1, Integer.class)); return null; }));
        builder.extension(MerchantRecipe.class, "maxUses", function -> function.returns(Integer.class).invoke(arguments -> event(arguments).getMaxUses()));
        builder.extension(MerchantRecipe.class, "setMaxUses", function -> function.param("maxUses", Integer.class).returns(Void.TYPE).invoke(arguments -> { event(arguments).setMaxUses(NovaTypeSupport.argument(arguments, 1, Integer.class)); return null; }));
        builder.extension(MerchantRecipe.class, "hasExperienceReward", function -> function.returns(Boolean.class).invoke(arguments -> event(arguments).hasExperienceReward()));
        builder.extension(MerchantRecipe.class, "setExperienceReward", function -> function.param("experienceReward", Boolean.class).returns(Void.TYPE).invoke(arguments -> { event(arguments).setExperienceReward(NovaTypeSupport.argument(arguments, 1, Boolean.class)); return null; }));
    }
    private static MerchantRecipe event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, MerchantRecipe.class);
    }

    @SuppressWarnings("unchecked")
    private static List<ItemStack> ingredients(Object[] arguments, int index) {
        return (List<ItemStack>) NovaTypeSupport.argument(arguments, index, List.class);
    }
}
