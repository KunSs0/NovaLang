package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;

/** 1.17+ Goat 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.entity.Goat"}, methods = {"org.bukkit.entity.Goat#hasLeftHorn", "org.bukkit.entity.Goat#setLeftHorn", "org.bukkit.entity.Goat#hasRightHorn", "org.bukkit.entity.Goat#setRightHorn", "org.bukkit.entity.Goat#isScreaming", "org.bukkit.entity.Goat#setScreaming"})
public final class NovaGoat {
    private static final String TYPE = "org.bukkit.entity.Goat";
    private NovaGoat() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaEntityReflection.type(NovaGoat.class, TYPE);
        Method left = NovaEntityReflection.method(type, "hasLeftHorn"); Method setLeft = NovaEntityReflection.method(type, "setLeftHorn", Boolean.TYPE);
        Method right = NovaEntityReflection.method(type, "hasRightHorn"); Method setRight = NovaEntityReflection.method(type, "setRightHorn", Boolean.TYPE);
        Method screaming = NovaEntityReflection.method(type, "isScreaming"); Method setScreaming = NovaEntityReflection.method(type, "setScreaming", Boolean.TYPE);
        builder.extension(type, "hasLeftHorn", f -> f.returns(Boolean.class).invoke(a -> NovaEntityReflection.invoke(left, a[0])));
        builder.extension(type, "setLeftHorn", f -> f.param("present", Boolean.class).returns(Void.TYPE).invoke(a -> NovaEntityReflection.invoke(setLeft, a[0], a[1])));
        builder.extension(type, "hasRightHorn", f -> f.returns(Boolean.class).invoke(a -> NovaEntityReflection.invoke(right, a[0])));
        builder.extension(type, "setRightHorn", f -> f.param("present", Boolean.class).returns(Void.TYPE).invoke(a -> NovaEntityReflection.invoke(setRight, a[0], a[1])));
        builder.extension(type, "isScreaming", f -> f.returns(Boolean.class).invoke(a -> NovaEntityReflection.invoke(screaming, a[0])));
        builder.extension(type, "setScreaming", f -> f.param("screaming", Boolean.class).returns(Void.TYPE).invoke(a -> NovaEntityReflection.invoke(setScreaming, a[0], a[1])));
    }
}
