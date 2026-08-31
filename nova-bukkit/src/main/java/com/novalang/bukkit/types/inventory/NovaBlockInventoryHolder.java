package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.Block;


/** BlockInventoryHolder 的 Fluxon getter 别名。 */
@Requires(classes = {"org.bukkit.inventory.BlockInventoryHolder"}, methods = {
        "org.bukkit.inventory.BlockInventoryHolder#getBlock"})
public final class NovaBlockInventoryHolder {

    private static final String BLOCK_INVENTORY_HOLDER = "org.bukkit.inventory.BlockInventoryHolder";

    private NovaBlockInventoryHolder() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> holderType = NovaInventoryReflection.type(NovaBlockInventoryHolder.class, BLOCK_INVENTORY_HOLDER);
        java.lang.reflect.Method getBlock = NovaInventoryReflection.method(holderType, "getBlock");
        builder.extension(holderType, "block", function -> function
                .returns(JavaTypeRef.javaType(Block.class).nullable())
                .invoke(arguments -> NovaInventoryReflection.invoke(getBlock, arguments[0])));
    }
}
