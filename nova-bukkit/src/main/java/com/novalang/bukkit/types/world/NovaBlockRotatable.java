package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;

import java.lang.reflect.Method;

/** 1.13+ Rotatable BlockData 的 Fluxon 函数契约。 */
@Requires(
        classes = {"org.bukkit.block.data.Rotatable"},
        methods = {"org.bukkit.block.data.Rotatable#getRotation", "org.bukkit.block.data.Rotatable#setRotation"})
public final class NovaBlockRotatable {

    private static final String ROTATABLE = "org.bukkit.block.data.Rotatable";

    private NovaBlockRotatable() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> rotatableType = NovaBlockDataReflection.type(NovaBlockRotatable.class, ROTATABLE);
        Method getRotation = NovaBlockDataReflection.method(rotatableType, "getRotation");
        Method setRotation = NovaBlockDataReflection.method(rotatableType, "setRotation", BlockFace.class);
        builder.extension(rotatableType, "rotation", function -> function.returns(BlockFace.class)
                .invoke(arguments -> NovaBlockDataReflection.invoke(getRotation, arguments[0])));
        builder.extension(rotatableType, "setRotation", function -> function
                .param("rotation", BlockFace.class).returns(Void.TYPE)
                .invoke(arguments -> NovaBlockDataReflection.invoke(setRotation, arguments[0], arguments[1])));
        builder.extension(rotatableType, "setRotation", function -> function
                .param("rotation", String.class).returns(Void.TYPE)
                .invoke(arguments -> {
                    BlockFace rotation = NovaTypeSupport.findEnum(BlockFace.class, (String) arguments[1]);
                    if (rotation != null) {
                        NovaBlockDataReflection.invoke(setRotation, arguments[0], rotation);
                    }
                    return null;
                }));
    }
}
