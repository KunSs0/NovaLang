package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

/** 抛掷药水的可选 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.entity.ThrownPotion"})
final class NovaThrownPotion {

    private NovaThrownPotion() {
    }

    static void register(JavaTypes.Builder builder) {
        JavaTypeRef nullableItem = JavaTypeRef.javaType(ItemStack.class).nullable();
        builder.extension(ThrownPotion.class, "effects", function -> function
                .returns(JavaTypeRef.javaType(Collection.class))
                .invoke(arguments -> potion(arguments).getEffects()));
        builder.extension(ThrownPotion.class, "item", function -> function
                .returns(nullableItem)
                .invoke(arguments -> potion(arguments).getItem()));
        builder.extension(ThrownPotion.class, "setItem", function -> function
                .param("item", nullableItem)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    potion(arguments).setItem(argument(arguments, 1, ItemStack.class));
                    return null;
                }));
    }

    private static ThrownPotion potion(Object[] arguments) {
        return argument(arguments, 0, ThrownPotion.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
