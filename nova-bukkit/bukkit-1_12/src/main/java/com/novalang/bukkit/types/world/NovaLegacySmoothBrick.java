package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Material;
import org.bukkit.material.SmoothBrick;

/** 旧版 SmoothBrick 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.SmoothBrick"})
final class NovaLegacySmoothBrick {

    private NovaLegacySmoothBrick() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(SmoothBrick.class, "textures", function -> function.returns(JavaTypeRef.listOf(JavaTypeRef.javaType(Material.class)))
                .invoke(arguments -> brick(arguments).getTextures()));
        builder.extension(SmoothBrick.class, "clone", function -> function.returns(SmoothBrick.class).invoke(arguments -> brick(arguments).clone()));
    }

    private static SmoothBrick brick(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, SmoothBrick.class);
    }
}
