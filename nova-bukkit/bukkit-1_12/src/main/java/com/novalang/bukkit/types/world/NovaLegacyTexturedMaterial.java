package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Material;
import org.bukkit.material.TexturedMaterial;

/** 旧版 TexturedMaterial 的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.TexturedMaterial"})
final class NovaLegacyTexturedMaterial {

    private NovaLegacyTexturedMaterial() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(TexturedMaterial.class, "textures", function -> function
                .returns(JavaTypeRef.listOf(JavaTypeRef.javaType(Material.class)))
                .invoke(arguments -> textured(arguments).getTextures()));
        builder.extension(TexturedMaterial.class, "material", function -> function
                .returns(Material.class).invoke(arguments -> textured(arguments).getMaterial()));
        builder.extension(TexturedMaterial.class, "setMaterial", function -> function
                .param("material", Material.class)
                .invoke(arguments -> {
                    textured(arguments).setMaterial(NovaTypeSupport.argument(arguments, 1, Material.class));
                    return null;
                }));
        builder.extension(TexturedMaterial.class, "toString", function -> function
                .returns(String.class).invoke(arguments -> textured(arguments).toString()));
        builder.extension(TexturedMaterial.class, "clone", function -> function
                .returns(TexturedMaterial.class).invoke(arguments -> textured(arguments).clone()));
    }

    private static TexturedMaterial textured(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, TexturedMaterial.class);
    }
}
