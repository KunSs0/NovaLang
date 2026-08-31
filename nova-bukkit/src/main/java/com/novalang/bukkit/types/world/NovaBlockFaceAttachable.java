package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.13+ FaceAttachable BlockData 的 Fluxon 函数契约。 */
@Requires(
        classes = {"org.bukkit.block.data.FaceAttachable", "org.bukkit.block.data.FaceAttachable$AttachedFace"},
        methods = {
                "org.bukkit.block.data.FaceAttachable#getAttachedFace",
                "org.bukkit.block.data.FaceAttachable#setAttachedFace"
        })
public final class NovaBlockFaceAttachable {

    private static final String FACE_ATTACHABLE = "org.bukkit.block.data.FaceAttachable";
    private static final String ATTACHED_FACE = "org.bukkit.block.data.FaceAttachable$AttachedFace";

    private NovaBlockFaceAttachable() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> faceAttachableType = NovaBlockDataReflection.type(NovaBlockFaceAttachable.class, FACE_ATTACHABLE);
        Class<?> attachedFaceType = NovaBlockDataReflection.type(NovaBlockFaceAttachable.class, ATTACHED_FACE);
        Method getAttachedFace = NovaBlockDataReflection.method(faceAttachableType, "getAttachedFace");
        Method setAttachedFace = NovaBlockDataReflection.method(faceAttachableType, "setAttachedFace", attachedFaceType);
        builder.extension(faceAttachableType, "attachedFace", function -> function.returns(JavaTypeRef.javaType(attachedFaceType))
                .invoke(arguments -> NovaBlockDataReflection.invoke(getAttachedFace, arguments[0])));
        builder.extension(faceAttachableType, "setAttachedFace", function -> function
                .param("attachedFace", attachedFaceType).returns(Void.TYPE)
                .invoke(arguments -> NovaBlockDataReflection.invoke(setAttachedFace, arguments[0], arguments[1])));
        builder.extension(faceAttachableType, "setAttachedFace", function -> function
                .param("attachedFace", String.class).returns(Void.TYPE)
                .invoke(arguments -> {
                    Object attachedFace = NovaBlockDataReflection.enumValue(attachedFaceType, (String) arguments[1]);
                    if (attachedFace != null) {
                        NovaBlockDataReflection.invoke(setAttachedFace, arguments[0], attachedFace);
                    }
                    return null;
                }));
    }
}
