package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.EntityType;
import org.bukkit.material.SpawnEgg;

/** 旧版 SpawnEgg 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.SpawnEgg"})
final class NovaLegacySpawnEgg {

    private NovaLegacySpawnEgg() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(SpawnEgg.class, "spawnedType", function -> function.returns(EntityType.class).invoke(arguments -> egg(arguments).getSpawnedType()));
        builder.extension(SpawnEgg.class, "setSpawnedType", function -> function.param("type", EntityType.class).returns(Void.TYPE)
                .invoke(arguments -> { egg(arguments).setSpawnedType(NovaTypeSupport.argument(arguments, 1, EntityType.class)); return null; }));
        builder.extension(SpawnEgg.class, "toString", function -> function.returns(String.class).invoke(arguments -> egg(arguments).toString()));
        builder.extension(SpawnEgg.class, "clone", function -> function.returns(SpawnEgg.class).invoke(arguments -> egg(arguments).clone()));
    }

    private static SpawnEgg egg(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, SpawnEgg.class);
    }
}
