package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

/** 铁砧结果预览事件的可选 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.event.inventory.PrepareAnvilEvent"})
public final class NovaPrepareAnvilEvent {

    private NovaPrepareAnvilEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        JavaTypeRef nullableItem = JavaTypeRef.javaType(ItemStack.class).nullable();
        builder.extension(PrepareAnvilEvent.class, "inventory", function -> function
                .returns(AnvilInventory.class)
                .invoke(arguments -> event(arguments).getInventory()));
        builder.extension(PrepareAnvilEvent.class, "result", function -> function
                .returns(nullableItem)
                .invoke(arguments -> event(arguments).getResult()));
        builder.extension(PrepareAnvilEvent.class, "setResult", function -> function
                .param("result", nullableItem)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setResult(argument(arguments, 1, ItemStack.class));
                    return null;
                }));
    }

    private static PrepareAnvilEvent event(Object[] arguments) {
        return argument(arguments, 0, PrepareAnvilEvent.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
