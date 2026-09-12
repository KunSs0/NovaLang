package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.14+ TileState 的持久化数据容器别名。 */
@Requires(classes = {
        "org.bukkit.block.TileState",
        "org.bukkit.persistence.PersistentDataContainer"}, methods = {
        "org.bukkit.block.TileState#getPersistentDataContainer"})
public final class NovaBlockTileState {

    private static final String TILE_STATE = "org.bukkit.block.TileState";
    private static final String PERSISTENT_DATA_CONTAINER = "org.bukkit.persistence.PersistentDataContainer";

    private NovaBlockTileState() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> tileStateType = NovaBlockDataReflection.type(NovaBlockTileState.class, TILE_STATE);
        Class<?> containerType = NovaBlockDataReflection.type(NovaBlockTileState.class, PERSISTENT_DATA_CONTAINER);
        Method getPersistentDataContainer = NovaBlockDataReflection.method(tileStateType, "getPersistentDataContainer");

        builder.extension(tileStateType, "persistentDataContainer", function -> function
                .returns(JavaTypeRef.javaType(containerType))
                .invoke(arguments -> NovaBlockDataReflection.invoke(getPersistentDataContainer, arguments[0])));
    }
}
