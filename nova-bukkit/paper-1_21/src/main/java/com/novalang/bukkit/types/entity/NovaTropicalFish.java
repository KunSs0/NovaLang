package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.paper.entity.PaperEntityReflection;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;
import org.bukkit.DyeColor;

/** 1.13+ TropicalFish 及 Pattern 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.entity.TropicalFish", "org.bukkit.entity.TropicalFish$Pattern"}, methods = {"org.bukkit.entity.TropicalFish#getPatternColor", "org.bukkit.entity.TropicalFish#setPatternColor", "org.bukkit.entity.TropicalFish#getBodyColor", "org.bukkit.entity.TropicalFish#setBodyColor", "org.bukkit.entity.TropicalFish#getPattern", "org.bukkit.entity.TropicalFish#setPattern"})
public final class NovaTropicalFish {
    private static final String TYPE = "org.bukkit.entity.TropicalFish";
    private static final String PATTERN = "org.bukkit.entity.TropicalFish$Pattern";
    private NovaTropicalFish() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = PaperEntityReflection.type(NovaTropicalFish.class, TYPE); Class<?> pattern = PaperEntityReflection.type(NovaTropicalFish.class, PATTERN); Method patternColor = PaperEntityReflection.method(type, "getPatternColor"); Method setPatternColor = PaperEntityReflection.method(type, "setPatternColor", DyeColor.class); Method bodyColor = PaperEntityReflection.method(type, "getBodyColor"); Method setBodyColor = PaperEntityReflection.method(type, "setBodyColor", DyeColor.class); Method getPattern = PaperEntityReflection.method(type, "getPattern"); Method setPattern = PaperEntityReflection.method(type, "setPattern", pattern); JavaTypeRef patternRef = JavaTypeRef.javaType(pattern);
        pair(builder, type, "patternColor", "setPatternColor", patternColor, setPatternColor, DyeColor.class); pair(builder, type, "bodyColor", "setBodyColor", bodyColor, setBodyColor, DyeColor.class); builder.extension(type, "pattern", function -> function.returns(patternRef).invoke(arguments -> PaperEntityReflection.invoke(getPattern, arguments[0]))); builder.extension(type, "setPattern", function -> function.param("pattern", patternRef).returns(Void.TYPE).invoke(arguments -> PaperEntityReflection.invoke(setPattern, arguments[0], arguments[1]))); builder.extension(type, "setPattern", function -> function.param("pattern", String.class).returns(Void.TYPE).invoke(arguments -> setPattern(setPattern, pattern, arguments[0], (String) arguments[1])));
    }
    private static void pair(JavaTypes.Builder builder, Class<?> type, String getterName, String setterName, Method getter, Method setter, Class<?> valueType) { builder.extension(type, getterName, function -> function.returns(valueType).invoke(arguments -> PaperEntityReflection.invoke(getter, arguments[0]))); builder.extension(type, setterName, function -> function.param("color", valueType).returns(Void.TYPE).invoke(arguments -> PaperEntityReflection.invoke(setter, arguments[0], arguments[1]))); }
    private static Object setPattern(Method setter, Class<?> pattern, Object target, String name) { Object value = PaperEntityReflection.enumValue(pattern, name); if (value == null) { return null; } return PaperEntityReflection.invoke(setter, target, value); }
}
