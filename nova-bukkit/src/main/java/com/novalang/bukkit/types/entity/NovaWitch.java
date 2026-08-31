package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;

/** Spigot 1.12.2 Witch 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.entity.Witch"}, methods = {"org.bukkit.entity.Witch#isDrinkingPotion"})
public final class NovaWitch {
    private NovaWitch() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaEntityReflection.type(NovaWitch.class, "org.bukkit.entity.Witch");
        Method drinkingPotion = NovaEntityReflection.method(type, "isDrinkingPotion");
        builder.extension(type, "isDrinkingPotion", function -> function.returns(Boolean.class)
                .invoke(arguments -> NovaEntityReflection.invoke(drinkingPotion, arguments[0])));
    }
}
