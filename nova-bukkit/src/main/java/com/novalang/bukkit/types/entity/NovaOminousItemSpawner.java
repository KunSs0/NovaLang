package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

/** 1.21+ OminousItemSpawner 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.entity.OminousItemSpawner"}, methods = {
        "org.bukkit.entity.OminousItemSpawner#getItem",
        "org.bukkit.entity.OminousItemSpawner#setItem",
        "org.bukkit.entity.OminousItemSpawner#getSpawnItemAfterTicks",
        "org.bukkit.entity.OminousItemSpawner#setSpawnItemAfterTicks"})
public final class NovaOminousItemSpawner {
    private static final String TYPE = "org.bukkit.entity.OminousItemSpawner";
    private NovaOminousItemSpawner() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaEntityReflection.type(NovaOminousItemSpawner.class, TYPE);
        Method getItem = NovaEntityReflection.method(type, "getItem");
        Method setItem = NovaEntityReflection.method(type, "setItem", ItemStack.class);
        Method getSpawnItemAfterTicks = NovaEntityReflection.method(type, "getSpawnItemAfterTicks");
        Method setSpawnItemAfterTicks = NovaEntityReflection.method(type, "setSpawnItemAfterTicks", Long.TYPE);
        builder.extension(type, "item", function -> function.returns(ItemStack.class).invoke(arguments -> NovaEntityReflection.invoke(getItem, arguments[0])));
        builder.extension(type, "setItem", function -> function.param("item", ItemStack.class).returns(Void.TYPE).invoke(arguments -> NovaEntityReflection.invoke(setItem, arguments[0], arguments[1])));
        builder.extension(type, "spawnItemAfterTicks", function -> function.returns(Long.class).invoke(arguments -> NovaEntityReflection.invoke(getSpawnItemAfterTicks, arguments[0])));
        builder.extension(type, "setSpawnItemAfterTicks", function -> function.param("ticks", Long.class).returns(Void.TYPE).invoke(arguments -> NovaEntityReflection.invoke(setSpawnItemAfterTicks, arguments[0], arguments[1])));
    }
}
