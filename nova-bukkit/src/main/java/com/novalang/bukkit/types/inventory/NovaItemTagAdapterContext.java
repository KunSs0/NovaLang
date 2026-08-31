package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 已废弃 ItemTag API 中 ItemTagAdapterContext 的 Fluxon 函数契约。 */
@Requires(classes = {
        "org.bukkit.inventory.meta.tags.ItemTagAdapterContext",
        "org.bukkit.inventory.meta.tags.CustomItemTagContainer"}, methods = {
        "org.bukkit.inventory.meta.tags.ItemTagAdapterContext#newTagContainer"})
public final class NovaItemTagAdapterContext {

    private static final String CONTEXT = "org.bukkit.inventory.meta.tags.ItemTagAdapterContext";
    private static final String CONTAINER = "org.bukkit.inventory.meta.tags.CustomItemTagContainer";

    private NovaItemTagAdapterContext() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> context = NovaInventoryReflection.type(NovaItemTagAdapterContext.class, CONTEXT);
        Class<?> container = NovaInventoryReflection.type(NovaItemTagAdapterContext.class, CONTAINER);
        Method newTagContainer = NovaInventoryReflection.method(context, "newTagContainer");

        builder.extension(context, "newTagContainer", function -> function.returns(JavaTypeRef.javaType(container))
                .invoke(arguments -> NovaInventoryReflection.invoke(newTagContainer, arguments[0])));
    }
}
