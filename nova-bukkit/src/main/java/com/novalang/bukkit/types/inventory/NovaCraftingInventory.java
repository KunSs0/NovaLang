package com.novalang.bukkit.types.inventory;
import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
@Requires(classes = {"org.bukkit.inventory.CraftingInventory"})
public final class NovaCraftingInventory {
    private NovaCraftingInventory() { }
    public static void register(JavaTypes.Builder builder) {
        JavaTypeRef item = JavaTypeRef.javaType(ItemStack.class).nullable();
        builder.extension(CraftingInventory.class, "result", function -> function.returns(item).invoke(arguments -> event(arguments).getResult()));
        builder.extension(CraftingInventory.class, "setResult", function -> function.param("result", item).returns(Void.TYPE).invoke(arguments -> { event(arguments).setResult(NovaTypeSupport.argument(arguments, 1, ItemStack.class)); return null; }));
        builder.extension(CraftingInventory.class, "setMatrix", function -> function.param("matrix", ItemStack[].class).returns(Void.TYPE).invoke(arguments -> { event(arguments).setMatrix(NovaTypeSupport.argument(arguments, 1, ItemStack[].class)); return null; }));
        builder.extension(CraftingInventory.class, "recipe", function -> function.returns(JavaTypeRef.javaType(Recipe.class).nullable()).invoke(arguments -> event(arguments).getRecipe()));
    }
    private static CraftingInventory event(Object[] arguments) { return NovaTypeSupport.argument(arguments, 0, CraftingInventory.class); }
}
