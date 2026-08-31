package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.13+ Switch BlockData 的 Fluxon 函数契约。 */
@Requires(
        classes = {"org.bukkit.block.data.type.Switch", "org.bukkit.block.data.type.Switch$Face"},
        methods = {
                "org.bukkit.block.data.type.Switch#getFace",
                "org.bukkit.block.data.type.Switch#setFace"
        })
public final class NovaBlockSwitch {

    private static final String SWITCH = "org.bukkit.block.data.type.Switch";
    private static final String FACE = "org.bukkit.block.data.type.Switch$Face";

    private NovaBlockSwitch() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> switchType = NovaBlockDataReflection.type(NovaBlockSwitch.class, SWITCH);
        Class<?> faceType = NovaBlockDataReflection.type(NovaBlockSwitch.class, FACE);
        Method getFace = NovaBlockDataReflection.method(switchType, "getFace");
        Method setFace = NovaBlockDataReflection.method(switchType, "setFace", faceType);
        builder.extension(switchType, "face", function -> function.returns(JavaTypeRef.javaType(faceType))
                .invoke(arguments -> NovaBlockDataReflection.invoke(getFace, arguments[0])));
        builder.extension(switchType, "setFace", function -> function
                .param("face", faceType).returns(Void.TYPE)
                .invoke(arguments -> NovaBlockDataReflection.invoke(setFace, arguments[0], arguments[1])));
        builder.extension(switchType, "setFace", function -> function
                .param("face", String.class).returns(Void.TYPE)
                .invoke(arguments -> {
                    Object face = NovaBlockDataReflection.enumValue(faceType, (String) arguments[1]);
                    if (face != null) {
                        NovaBlockDataReflection.invoke(setFace, arguments[0], face);
                    }
                    return null;
                }));
    }
}
