package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;

/** 1.13+ EndPortalFrame BlockData 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.block.data.type.EndPortalFrame"}, methods = {"org.bukkit.block.data.type.EndPortalFrame#hasEye", "org.bukkit.block.data.type.EndPortalFrame#setEye"})
public final class NovaBlockEndPortalFrame {
    private NovaBlockEndPortalFrame() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaBlockDataReflection.type(NovaBlockEndPortalFrame.class, "org.bukkit.block.data.type.EndPortalFrame");
        Method hasEye = NovaBlockDataReflection.method(type, "hasEye"); Method setEye = NovaBlockDataReflection.method(type, "setEye", Boolean.TYPE);
        builder.extension(type, "hasEye", f -> f.returns(Boolean.class).invoke(a -> NovaBlockDataReflection.invoke(hasEye, a[0])));
        builder.extension(type, "setEye", f -> f.param("eye", Boolean.class).returns(Void.TYPE).invoke(a -> NovaBlockDataReflection.invoke(setEye, a[0], a[1])));
    }
}
