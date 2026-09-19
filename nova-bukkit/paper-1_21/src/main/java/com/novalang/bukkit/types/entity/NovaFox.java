package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.paper.entity.PaperEntityReflection;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;
import org.bukkit.entity.AnimalTamer;

/** 1.14+ Fox 及 Type 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.entity.Fox", "org.bukkit.entity.Fox$Type"}, methods = {"org.bukkit.entity.Fox#getFoxType", "org.bukkit.entity.Fox#setFoxType", "org.bukkit.entity.Fox#isCrouching", "org.bukkit.entity.Fox#setCrouching", "org.bukkit.entity.Fox#setSleeping", "org.bukkit.entity.Fox#getFirstTrustedPlayer", "org.bukkit.entity.Fox#setFirstTrustedPlayer", "org.bukkit.entity.Fox#getSecondTrustedPlayer", "org.bukkit.entity.Fox#setSecondTrustedPlayer", "org.bukkit.entity.Fox#isFaceplanted"})
public final class NovaFox {
    private static final String TYPE = "org.bukkit.entity.Fox";
    private static final String FOX_TYPE = "org.bukkit.entity.Fox$Type";
    private NovaFox() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = PaperEntityReflection.type(NovaFox.class, TYPE); Class<?> foxType = PaperEntityReflection.type(NovaFox.class, FOX_TYPE); Method getType = PaperEntityReflection.method(type, "getFoxType"); Method setType = PaperEntityReflection.method(type, "setFoxType", foxType); Method crouching = PaperEntityReflection.method(type, "isCrouching"); Method setCrouching = PaperEntityReflection.method(type, "setCrouching", Boolean.TYPE); Method setSleeping = PaperEntityReflection.method(type, "setSleeping", Boolean.TYPE); Method first = PaperEntityReflection.method(type, "getFirstTrustedPlayer"); Method setFirst = PaperEntityReflection.method(type, "setFirstTrustedPlayer", AnimalTamer.class); Method second = PaperEntityReflection.method(type, "getSecondTrustedPlayer"); Method setSecond = PaperEntityReflection.method(type, "setSecondTrustedPlayer", AnimalTamer.class); Method faceplanted = PaperEntityReflection.method(type, "isFaceplanted"); JavaTypeRef nullableTamer = JavaTypeRef.javaType(AnimalTamer.class).nullable(); JavaTypeRef foxTypeRef = JavaTypeRef.javaType(foxType);
        builder.extension(type, "foxType", f -> f.returns(foxTypeRef).invoke(a -> PaperEntityReflection.invoke(getType, a[0]))); builder.extension(type, "setFoxType", f -> f.param("type", foxTypeRef).returns(Void.TYPE).invoke(a -> PaperEntityReflection.invoke(setType, a[0], a[1]))); builder.extension(type, "setFoxType", f -> f.param("type", String.class).returns(Void.TYPE).invoke(a -> setType(setType, foxType, a[0], (String) a[1]))); builder.extension(type, "isCrouching", f -> f.returns(Boolean.class).invoke(a -> PaperEntityReflection.invoke(crouching, a[0]))); builder.extension(type, "setCrouching", f -> f.param("value", Boolean.class).returns(Void.TYPE).invoke(a -> PaperEntityReflection.invoke(setCrouching, a[0], a[1]))); builder.extension(type, "setSleeping", f -> f.param("value", Boolean.class).returns(Void.TYPE).invoke(a -> PaperEntityReflection.invoke(setSleeping, a[0], a[1]))); builder.extension(type, "firstTrustedPlayer", f -> f.returns(nullableTamer).invoke(a -> PaperEntityReflection.invoke(first, a[0]))); builder.extension(type, "setFirstTrustedPlayer", f -> f.param("player", nullableTamer).returns(Void.TYPE).invoke(a -> PaperEntityReflection.invoke(setFirst, a[0], a[1]))); builder.extension(type, "secondTrustedPlayer", f -> f.returns(nullableTamer).invoke(a -> PaperEntityReflection.invoke(second, a[0]))); builder.extension(type, "setSecondTrustedPlayer", f -> f.param("player", nullableTamer).returns(Void.TYPE).invoke(a -> PaperEntityReflection.invoke(setSecond, a[0], a[1]))); builder.extension(type, "isFaceplanted", f -> f.returns(Boolean.class).invoke(a -> PaperEntityReflection.invoke(faceplanted, a[0])));
    }
    private static Object setType(Method method, Class<?> type, Object target, String name) { Object value = PaperEntityReflection.enumValue(type, name); if (value != null) { return PaperEntityReflection.invoke(method, target, value); } return null; }
}
