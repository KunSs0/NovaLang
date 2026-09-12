package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.paper.entity.PaperEntityReflection;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;

/** 1.17+ Goat 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.entity.Goat"}, methods = {"org.bukkit.entity.Goat#hasLeftHorn", "org.bukkit.entity.Goat#setLeftHorn", "org.bukkit.entity.Goat#hasRightHorn", "org.bukkit.entity.Goat#setRightHorn", "org.bukkit.entity.Goat#isScreaming", "org.bukkit.entity.Goat#setScreaming"})
public final class NovaGoat {
    private static final String TYPE = "org.bukkit.entity.Goat";
    private NovaGoat() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = PaperEntityReflection.type(NovaGoat.class, TYPE);
        Method left = PaperEntityReflection.method(type, "hasLeftHorn"); Method setLeft = PaperEntityReflection.method(type, "setLeftHorn", Boolean.TYPE);
        Method right = PaperEntityReflection.method(type, "hasRightHorn"); Method setRight = PaperEntityReflection.method(type, "setRightHorn", Boolean.TYPE);
        Method screaming = PaperEntityReflection.method(type, "isScreaming"); Method setScreaming = PaperEntityReflection.method(type, "setScreaming", Boolean.TYPE);
        builder.extension(type, "hasLeftHorn", f -> f.returns(Boolean.class).invoke(a -> PaperEntityReflection.invoke(left, a[0])));
        builder.extension(type, "setLeftHorn", f -> f.param("present", Boolean.class).returns(Void.TYPE).invoke(a -> PaperEntityReflection.invoke(setLeft, a[0], a[1])));
        builder.extension(type, "hasRightHorn", f -> f.returns(Boolean.class).invoke(a -> PaperEntityReflection.invoke(right, a[0])));
        builder.extension(type, "setRightHorn", f -> f.param("present", Boolean.class).returns(Void.TYPE).invoke(a -> PaperEntityReflection.invoke(setRight, a[0], a[1])));
        builder.extension(type, "isScreaming", f -> f.returns(Boolean.class).invoke(a -> PaperEntityReflection.invoke(screaming, a[0])));
        builder.extension(type, "setScreaming", f -> f.param("screaming", Boolean.class).returns(Void.TYPE).invoke(a -> PaperEntityReflection.invoke(setScreaming, a[0], a[1])));
    }
}
