package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

/** 1.20+ BrushableBlock 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.block.BrushableBlock"}, methods = {
        "org.bukkit.block.BrushableBlock#getItem",
        "org.bukkit.block.BrushableBlock#setItem"})
public final class NovaBlockBrushable {

    private static final String BRUSHABLE_BLOCK = "org.bukkit.block.BrushableBlock";

    private NovaBlockBrushable() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> brushableType = NovaBlockDataReflection.type(NovaBlockBrushable.class, BRUSHABLE_BLOCK);
        Method getItem = NovaBlockDataReflection.method(brushableType, "getItem");
        Method setItem = NovaBlockDataReflection.method(brushableType, "setItem", ItemStack.class);
        JavaTypeRef nullableItemStack = JavaTypeRef.javaType(ItemStack.class).nullable();

        builder.extension(brushableType, "item", function -> function
                .returns(nullableItemStack)
                .invoke(arguments -> NovaBlockDataReflection.invoke(getItem, arguments[0])));
        builder.extension(brushableType, "setItem", function -> function
                .param("item", nullableItemStack)
                .returns(Void.TYPE)
                .invoke(arguments -> NovaBlockDataReflection.invoke(setItem, arguments[0], arguments[1])));
    }
}
