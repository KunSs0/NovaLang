package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.NamespacedKey;

import java.lang.reflect.Method;

/** 已废弃 ItemTag API 中 CustomItemTagContainer 的 Fluxon 函数契约。 */
@Requires(classes = {
        "org.bukkit.inventory.meta.tags.CustomItemTagContainer",
        "org.bukkit.inventory.meta.tags.ItemTagAdapterContext"}, methods = {
        "org.bukkit.inventory.meta.tags.CustomItemTagContainer#removeCustomTag",
        "org.bukkit.inventory.meta.tags.CustomItemTagContainer#isEmpty",
        "org.bukkit.inventory.meta.tags.CustomItemTagContainer#getAdapterContext"})
public final class NovaCustomItemTagContainer {

    private static final String CONTAINER = "org.bukkit.inventory.meta.tags.CustomItemTagContainer";
    private static final String CONTEXT = "org.bukkit.inventory.meta.tags.ItemTagAdapterContext";

    private NovaCustomItemTagContainer() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> container = NovaInventoryReflection.type(NovaCustomItemTagContainer.class, CONTAINER);
        Class<?> context = NovaInventoryReflection.type(NovaCustomItemTagContainer.class, CONTEXT);
        Method removeCustomTag = NovaInventoryReflection.method(container, "removeCustomTag", NamespacedKey.class);
        Method isEmpty = NovaInventoryReflection.method(container, "isEmpty");
        Method getAdapterContext = NovaInventoryReflection.method(container, "getAdapterContext");

        builder.extension(container, "removeCustomTag", function -> function.param("key", NamespacedKey.class).returns(Void.TYPE)
                .invoke(arguments -> NovaInventoryReflection.invoke(removeCustomTag, arguments[0], arguments[1])));
        builder.extension(container, "isEmpty", function -> function.returns(Boolean.class)
                .invoke(arguments -> NovaInventoryReflection.invoke(isEmpty, arguments[0])));
        builder.extension(container, "adapterContext", function -> function.returns(JavaTypeRef.javaType(context))
                .invoke(arguments -> NovaInventoryReflection.invoke(getAdapterContext, arguments[0])));
    }
}
