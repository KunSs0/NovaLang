package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.14+ Bell BlockData 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.block.data.type.Bell", "org.bukkit.block.data.type.Bell$Attachment"}, methods = {
        "org.bukkit.block.data.type.Bell#getAttachment", "org.bukkit.block.data.type.Bell#setAttachment"})
public final class NovaBlockBell {
    private static final String BELL = "org.bukkit.block.data.type.Bell";
    private static final String ATTACHMENT = "org.bukkit.block.data.type.Bell$Attachment";
    private NovaBlockBell() {
    }
    public static void register(JavaTypes.Builder builder) {
        Class<?> bellType = NovaBlockDataReflection.type(NovaBlockBell.class, BELL);
        Class<?> attachmentType = NovaBlockDataReflection.type(NovaBlockBell.class, ATTACHMENT);
        Method getAttachment = NovaBlockDataReflection.method(bellType, "getAttachment");
        Method setAttachment = NovaBlockDataReflection.method(bellType, "setAttachment", attachmentType);
        builder.extension(bellType, "attachment", function -> function.returns(JavaTypeRef.javaType(attachmentType)).invoke(arguments -> NovaBlockDataReflection.invoke(getAttachment, arguments[0])));
        builder.extension(bellType, "setAttachment", function -> function.param("attachment", attachmentType).returns(Void.TYPE).invoke(arguments -> NovaBlockDataReflection.invoke(setAttachment, arguments[0], arguments[1])));
        builder.extension(bellType, "setAttachment", function -> function.param("attachment", String.class).returns(Void.TYPE).invoke(arguments -> {
            Object attachment = NovaBlockDataReflection.enumValue(attachmentType, (String) arguments[1]);
            if (attachment != null) {
                NovaBlockDataReflection.invoke(setAttachment, arguments[0], attachment);
            }
            return null;
        }));
    }
}
