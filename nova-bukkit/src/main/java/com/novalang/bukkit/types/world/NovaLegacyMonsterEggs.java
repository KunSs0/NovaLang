package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Material;
import org.bukkit.material.MonsterEggs;

/** 旧版 MonsterEggs 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.MonsterEggs"})
final class NovaLegacyMonsterEggs {

    private NovaLegacyMonsterEggs() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(MonsterEggs.class, "textures", function -> function.returns(JavaTypeRef.listOf(JavaTypeRef.javaType(Material.class)))
                .invoke(arguments -> eggs(arguments).getTextures()));
        builder.extension(MonsterEggs.class, "clone", function -> function.returns(MonsterEggs.class).invoke(arguments -> eggs(arguments).clone()));
    }

    private static MonsterEggs eggs(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, MonsterEggs.class);
    }
}
