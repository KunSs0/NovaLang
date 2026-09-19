package com.novalang.bukkit.types.world;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.data.FaceAttachable;

/** Paper 1.21.x FaceAttachable BlockData 的函数契约。 */
public final class NovaBlockFaceAttachable {
    private NovaBlockFaceAttachable() {
    }

    /** 注册附着面读取、写入及字符串转换函数。 */
    public static void register(JavaTypes.Builder builder) {
        builder.extension(FaceAttachable.class, "attachedFace", f -> f
                .returns(JavaTypeRef.javaType(FaceAttachable.AttachedFace.class))
                .invoke(a -> faceAttachable(a).getAttachedFace()));
        builder.extension(FaceAttachable.class, "setAttachedFace", f -> f
                .param("attachedFace", FaceAttachable.AttachedFace.class).returns(Void.TYPE)
                .invoke(a -> { faceAttachable(a).setAttachedFace(argument(a, 1, FaceAttachable.AttachedFace.class)); return null; }));
        builder.extension(FaceAttachable.class, "setAttachedFace", f -> f.param("attachedFace", String.class)
                .returns(Void.TYPE).invoke(a -> {
                    FaceAttachable.AttachedFace value = NovaTypeSupport.findEnum(
                            FaceAttachable.AttachedFace.class, argument(a, 1, String.class));
                    if (value != null) {
                        faceAttachable(a).setAttachedFace(value);
                    }
                    return null;
                }));
    }

    /** 取得扩展调用中的 FaceAttachable 参数。 */
    private static FaceAttachable faceAttachable(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, FaceAttachable.class);
    }

    /** 取得扩展调用中的指定类型参数。 */
    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
