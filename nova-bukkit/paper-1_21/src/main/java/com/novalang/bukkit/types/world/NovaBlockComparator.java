package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.13+ Comparator BlockData 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.block.data.type.Comparator", "org.bukkit.block.data.type.Comparator$Mode"}, methods = {
        "org.bukkit.block.data.type.Comparator#getMode", "org.bukkit.block.data.type.Comparator#setMode"})
public final class NovaBlockComparator {
    private static final String COMPARATOR = "org.bukkit.block.data.type.Comparator";
    private static final String MODE = "org.bukkit.block.data.type.Comparator$Mode";
    private NovaBlockComparator() {
    }
    public static void register(JavaTypes.Builder builder) {
        Class<?> comparatorType = NovaBlockDataReflection.type(NovaBlockComparator.class, COMPARATOR);
        Class<?> modeType = NovaBlockDataReflection.type(NovaBlockComparator.class, MODE);
        Method getMode = NovaBlockDataReflection.method(comparatorType, "getMode");
        Method setMode = NovaBlockDataReflection.method(comparatorType, "setMode", modeType);
        builder.extension(comparatorType, "mode", function -> function.returns(JavaTypeRef.javaType(modeType)).invoke(arguments -> NovaBlockDataReflection.invoke(getMode, arguments[0])));
        builder.extension(comparatorType, "setMode", function -> function.param("mode", modeType).returns(Void.TYPE).invoke(arguments -> NovaBlockDataReflection.invoke(setMode, arguments[0], arguments[1])));
        builder.extension(comparatorType, "setMode", function -> function.param("mode", String.class).returns(Void.TYPE).invoke(arguments -> {
            Object mode = NovaBlockDataReflection.enumValue(modeType, (String) arguments[1]);
            if (mode != null) {
                NovaBlockDataReflection.invoke(setMode, arguments[0], mode);
            }
            return null;
        }));
    }
}
