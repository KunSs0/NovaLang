package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** EntityBlockStorage 的 Fluxon getter 别名。 */
@Requires(classes = {"org.bukkit.block.EntityBlockStorage"}, methods = {
        "org.bukkit.block.EntityBlockStorage#getEntityCount",
        "org.bukkit.block.EntityBlockStorage#getMaxEntities"})
public final class NovaBlockEntityStorage {

    private static final String ENTITY_BLOCK_STORAGE = "org.bukkit.block.EntityBlockStorage";

    private NovaBlockEntityStorage() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> storageType = NovaBlockDataReflection.type(NovaBlockEntityStorage.class, ENTITY_BLOCK_STORAGE);
        Method getEntityCount = NovaBlockDataReflection.method(storageType, "getEntityCount");
        Method getMaxEntities = NovaBlockDataReflection.method(storageType, "getMaxEntities");

        builder.extension(storageType, "entityCount", function -> function
                .returns(Integer.class)
                .invoke(arguments -> NovaBlockDataReflection.invoke(getEntityCount, arguments[0])));
        builder.extension(storageType, "maxEntities", function -> function
                .returns(Integer.class)
                .invoke(arguments -> NovaBlockDataReflection.invoke(getMaxEntities, arguments[0])));
    }
}
