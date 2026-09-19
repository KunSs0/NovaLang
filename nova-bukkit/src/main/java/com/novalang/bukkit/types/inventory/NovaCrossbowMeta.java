package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

/** 1.14+ CrossbowMeta 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.inventory.meta.CrossbowMeta"}, methods = {
        "org.bukkit.inventory.meta.CrossbowMeta#hasChargedProjectiles",
        "org.bukkit.inventory.meta.CrossbowMeta#getChargedProjectiles",
        "org.bukkit.inventory.meta.CrossbowMeta#setChargedProjectiles",
        "org.bukkit.inventory.meta.CrossbowMeta#addChargedProjectile"})
public final class NovaCrossbowMeta {
    private static final String TYPE = "org.bukkit.inventory.meta.CrossbowMeta";
    private NovaCrossbowMeta() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaInventoryReflection.type(NovaCrossbowMeta.class, TYPE);
        Method has = NovaInventoryReflection.method(type, "hasChargedProjectiles");
        Method get = NovaInventoryReflection.method(type, "getChargedProjectiles");
        Method set = NovaInventoryReflection.method(type, "setChargedProjectiles", java.util.List.class);
        Method add = NovaInventoryReflection.method(type, "addChargedProjectile", ItemStack.class);
        JavaTypeRef items = JavaTypeRef.listOf(JavaTypeRef.javaType(ItemStack.class));
        builder.extension(type, "hasChargedProjectiles", f -> f.returns(Boolean.class).invoke(a -> NovaInventoryReflection.invoke(has, a[0])));
        builder.extension(type, "chargedProjectiles", f -> f.returns(items).invoke(a -> NovaInventoryReflection.invoke(get, a[0])));
        builder.extension(type, "setChargedProjectiles", f -> f.param("projectiles", items).returns(Void.TYPE).invoke(a -> NovaInventoryReflection.invoke(set, a[0], a[1])));
        builder.extension(type, "addChargedProjectile", f -> f.param("projectile", ItemStack.class).returns(Void.TYPE).invoke(a -> NovaInventoryReflection.invoke(add, a[0], a[1])));
    }
}
