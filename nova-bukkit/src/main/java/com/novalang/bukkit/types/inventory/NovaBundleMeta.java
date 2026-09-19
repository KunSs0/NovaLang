package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

/** 1.17+ BundleMeta 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.inventory.meta.BundleMeta"}, methods = {
        "org.bukkit.inventory.meta.BundleMeta#hasItems",
        "org.bukkit.inventory.meta.BundleMeta#getItems",
        "org.bukkit.inventory.meta.BundleMeta#setItems",
        "org.bukkit.inventory.meta.BundleMeta#addItem"})
public final class NovaBundleMeta {
    private static final String TYPE = "org.bukkit.inventory.meta.BundleMeta";
    private NovaBundleMeta() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaInventoryReflection.type(NovaBundleMeta.class, TYPE);
        Method has = NovaInventoryReflection.method(type, "hasItems");
        Method get = NovaInventoryReflection.method(type, "getItems");
        Method set = NovaInventoryReflection.method(type, "setItems", java.util.List.class);
        Method add = NovaInventoryReflection.method(type, "addItem", ItemStack.class);
        JavaTypeRef items = JavaTypeRef.listOf(JavaTypeRef.javaType(ItemStack.class));
        builder.extension(type, "hasItems", f -> f.returns(Boolean.class).invoke(a -> NovaInventoryReflection.invoke(has, a[0])));
        builder.extension(type, "items", f -> f.returns(items).invoke(a -> NovaInventoryReflection.invoke(get, a[0])));
        builder.extension(type, "setItems", f -> f.param("items", items).returns(Void.TYPE).invoke(a -> NovaInventoryReflection.invoke(set, a[0], a[1])));
        builder.extension(type, "addItem", f -> f.param("item", ItemStack.class).returns(Void.TYPE).invoke(a -> NovaInventoryReflection.invoke(add, a[0], a[1])));
    }
}
