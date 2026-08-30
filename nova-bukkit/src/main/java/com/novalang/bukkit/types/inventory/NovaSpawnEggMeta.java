package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.meta.SpawnEggMeta;

/** 刷怪蛋物品元数据的可选编译期别名。 */
@Requires(classes = {"org.bukkit.inventory.meta.SpawnEggMeta"})
public final class NovaSpawnEggMeta {

    private NovaSpawnEggMeta() {
    }

    public static void register(JavaTypes.Builder builder) {
        JavaTypeRef nullableEntityType = JavaTypeRef.javaType(EntityType.class).nullable();
        builder.extension(SpawnEggMeta.class, "spawnedType", function -> function
                .returns(nullableEntityType)
                .invoke(arguments -> meta(arguments).getSpawnedType()));
        builder.extension(SpawnEggMeta.class, "setSpawnedType", function -> function
                .param("type", nullableEntityType)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    meta(arguments).setSpawnedType(argument(arguments, 1, EntityType.class));
                    return null;
                }));
        builder.extension(SpawnEggMeta.class, "clone", function -> function
                .returns(SpawnEggMeta.class)
                .invoke(arguments -> meta(arguments).clone()));
    }

    private static SpawnEggMeta meta(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, SpawnEggMeta.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
