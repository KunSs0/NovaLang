package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;

import java.util.List;

@Requires(classes = {"org.bukkit.inventory.Merchant"})
public final class NovaMerchant {
    private NovaMerchant() { }
    public static void register(JavaTypes.Builder builder) {
        builder.extension(Merchant.class, "recipes", function -> function.returns(JavaTypeRef.listOf(JavaTypeRef.javaType(MerchantRecipe.class))).invoke(arguments -> event(arguments).getRecipes()));
        builder.extension(Merchant.class, "setRecipes", function -> function.param("recipes", JavaTypeRef.listOf(JavaTypeRef.javaType(MerchantRecipe.class))).returns(Void.TYPE).invoke(arguments -> { event(arguments).setRecipes(recipes(arguments, 1)); return null; }));
        builder.extension(Merchant.class, "getRecipe", function -> function.param("index", Integer.class).returns(MerchantRecipe.class).invoke(arguments -> event(arguments).getRecipe(NovaTypeSupport.argument(arguments, 1, Integer.class))));
        builder.extension(Merchant.class, "setRecipe", function -> function.param("index", Integer.class).param("recipe", MerchantRecipe.class).returns(Void.TYPE).invoke(arguments -> { event(arguments).setRecipe(NovaTypeSupport.argument(arguments, 1, Integer.class), NovaTypeSupport.argument(arguments, 2, MerchantRecipe.class)); return null; }));
        builder.extension(Merchant.class, "recipeCount", function -> function.returns(Integer.class).invoke(arguments -> event(arguments).getRecipeCount()));
        builder.extension(Merchant.class, "isTrading", function -> function.returns(Boolean.class).invoke(arguments -> event(arguments).isTrading()));
        builder.extension(Merchant.class, "trader", function -> function.returns(JavaTypeRef.javaType(HumanEntity.class).nullable()).invoke(arguments -> event(arguments).getTrader()));
    }
    private static Merchant event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Merchant.class);
    }

    @SuppressWarnings("unchecked")
    private static List<MerchantRecipe> recipes(Object[] arguments, int index) {
        return (List<MerchantRecipe>) NovaTypeSupport.argument(arguments, index, List.class);
    }
}
