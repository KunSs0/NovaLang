package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Llama;
import org.bukkit.inventory.LlamaInventory;

/** Spigot 1.12.2 羊驼的 Fluxon 函数别名。 */
public final class NovaLlama {

    private NovaLlama() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Llama.class, "color", function -> function.returns(Llama.Color.class).invoke(arguments -> llama(arguments).getColor()));
        builder.extension(Llama.class, "setColor", function -> function.param("color", Llama.Color.class).returns(Void.TYPE).invoke(arguments -> {
            llama(arguments).setColor(argument(arguments, 1, Llama.Color.class));
            return null;
        }));
        builder.extension(Llama.class, "setColor", function -> function.param("color", String.class).returns(Void.TYPE).invoke(arguments -> {
            Llama.Color color = NovaTypeSupport.findEnum(Llama.Color.class, argument(arguments, 1, String.class));
            if (color != null) {
                llama(arguments).setColor(color);
            }
            return null;
        }));
        builder.extension(Llama.class, "strength", function -> function.returns(Integer.class).invoke(arguments -> llama(arguments).getStrength()));
        builder.extension(Llama.class, "setStrength", function -> function.param("strength", Integer.class).returns(Void.TYPE).invoke(arguments -> {
            llama(arguments).setStrength(argument(arguments, 1, Integer.class));
            return null;
        }));
        builder.extension(Llama.class, "inventory", function -> function.returns(LlamaInventory.class).invoke(arguments -> llama(arguments).getInventory()));
    }

    private static Llama llama(Object[] arguments) {
        return argument(arguments, 0, Llama.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
