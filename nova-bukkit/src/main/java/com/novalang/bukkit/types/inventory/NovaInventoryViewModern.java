package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.inventory.InventoryView;


/** 1.14+ InventoryView 标题别名。 */
@Requires(classes = {"org.bukkit.inventory.InventoryView"}, methods = {
        "org.bukkit.inventory.InventoryView#getOriginalTitle",
        "org.bukkit.inventory.InventoryView#setTitle"})
public final class NovaInventoryViewModern {

    private NovaInventoryViewModern() {
    }

    public static void register(JavaTypes.Builder builder) {
        java.lang.reflect.Method getOriginalTitle = NovaInventoryReflection.method(InventoryView.class, "getOriginalTitle");
        builder.extension(InventoryView.class, "originalTitle", function -> function
                .returns(JavaTypeRef.javaType(String.class).nullable())
                .invoke(arguments -> NovaInventoryReflection.invoke(getOriginalTitle, arguments[0])));
    }
}
