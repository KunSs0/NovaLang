package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;

import java.lang.reflect.Method;

/** 1.13+ MultipleFacing BlockData 的 Fluxon 函数契约。 */
@Requires(
        classes = {"org.bukkit.block.data.MultipleFacing"},
        methods = {
                "org.bukkit.block.data.MultipleFacing#hasFace",
                "org.bukkit.block.data.MultipleFacing#setFace",
                "org.bukkit.block.data.MultipleFacing#getFaces",
                "org.bukkit.block.data.MultipleFacing#getAllowedFaces"
        })
public final class NovaBlockMultipleFacing {

    private static final String MULTIPLE_FACING = "org.bukkit.block.data.MultipleFacing";

    private NovaBlockMultipleFacing() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> multipleFacingType = NovaBlockDataReflection.type(NovaBlockMultipleFacing.class, MULTIPLE_FACING);
        Method hasFace = NovaBlockDataReflection.method(multipleFacingType, "hasFace", BlockFace.class);
        Method setFace = NovaBlockDataReflection.method(multipleFacingType, "setFace", BlockFace.class, Boolean.TYPE);
        Method getFaces = NovaBlockDataReflection.method(multipleFacingType, "getFaces");
        Method getAllowedFaces = NovaBlockDataReflection.method(multipleFacingType, "getAllowedFaces");
        JavaTypeRef faces = JavaTypeRef.setOf(JavaTypeRef.javaType(BlockFace.class));

        builder.extension(multipleFacingType, "hasFace", function -> function
                .param("face", BlockFace.class).returns(Boolean.class)
                .invoke(arguments -> NovaBlockDataReflection.invoke(hasFace, arguments[0], arguments[1])));
        builder.extension(multipleFacingType, "hasFace", function -> function
                .param("face", String.class).returns(Boolean.class)
                .invoke(arguments -> {
                    BlockFace face = NovaTypeSupport.findEnum(BlockFace.class, (String) arguments[1]);
                    if (face == null) {
                        return false;
                    }
                    return NovaBlockDataReflection.invoke(hasFace, arguments[0], face);
                }));
        builder.extension(multipleFacingType, "setFace", function -> function
                .param("face", BlockFace.class).param("hasFace", Boolean.class).returns(Void.TYPE)
                .invoke(arguments -> NovaBlockDataReflection.invoke(setFace, arguments[0], arguments[1], arguments[2])));
        builder.extension(multipleFacingType, "setFace", function -> function
                .param("face", String.class).param("hasFace", Boolean.class).returns(Void.TYPE)
                .invoke(arguments -> {
                    BlockFace face = NovaTypeSupport.findEnum(BlockFace.class, (String) arguments[1]);
                    if (face != null) {
                        NovaBlockDataReflection.invoke(setFace, arguments[0], face, arguments[2]);
                    }
                    return null;
                }));
        builder.extension(multipleFacingType, "faces", function -> function.returns(faces)
                .invoke(arguments -> NovaBlockDataReflection.invoke(getFaces, arguments[0])));
        builder.extension(multipleFacingType, "allowedFaces", function -> function.returns(faces)
                .invoke(arguments -> NovaBlockDataReflection.invoke(getAllowedFaces, arguments[0])));
    }
}
