package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.13+ BubbleColumn BlockData 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.block.data.type.BubbleColumn"}, methods = {
        "org.bukkit.block.data.type.BubbleColumn#isDrag", "org.bukkit.block.data.type.BubbleColumn#setDrag"})
public final class NovaBlockBubbleColumn {
    private static final String BUBBLE_COLUMN = "org.bukkit.block.data.type.BubbleColumn";
    private NovaBlockBubbleColumn() {
    }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaBlockDataReflection.type(NovaBlockBubbleColumn.class, BUBBLE_COLUMN);
        Method isDrag = NovaBlockDataReflection.method(type, "isDrag");
        Method setDrag = NovaBlockDataReflection.method(type, "setDrag", Boolean.TYPE);
        builder.extension(type, "isDrag", function -> function.returns(Boolean.class).invoke(arguments -> NovaBlockDataReflection.invoke(isDrag, arguments[0])));
        builder.extension(type, "setDrag", function -> function.param("drag", Boolean.class).returns(Void.TYPE).invoke(arguments -> NovaBlockDataReflection.invoke(setDrag, arguments[0], arguments[1])));
    }
}
