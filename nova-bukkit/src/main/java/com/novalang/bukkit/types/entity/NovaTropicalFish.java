package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;
import org.bukkit.DyeColor;

/** 1.13+ TropicalFish 及 Pattern 的 Fluxon 函数契约。 */
@Requires(
        classes = {"org.bukkit.entity.TropicalFish", "org.bukkit.entity.TropicalFish$Pattern"},
        methods = {
                "org.bukkit.entity.TropicalFish#getPatternColor", "org.bukkit.entity.TropicalFish#setPatternColor",
                "org.bukkit.entity.TropicalFish#getBodyColor", "org.bukkit.entity.TropicalFish#setBodyColor",
                "org.bukkit.entity.TropicalFish#getPattern", "org.bukkit.entity.TropicalFish#setPattern"
        })
public final class NovaTropicalFish {
    private static final String TYPE = "org.bukkit.entity.TropicalFish";
    private static final String PATTERN = "org.bukkit.entity.TropicalFish$Pattern";
    private NovaTropicalFish() {
    }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaEntityReflection.type(NovaTropicalFish.class, TYPE);
        Class<?> pattern = NovaEntityReflection.type(NovaTropicalFish.class, PATTERN);
        Method patternColor = NovaEntityReflection.method(type, "getPatternColor");
        Method setPatternColor = NovaEntityReflection.method(type, "setPatternColor", DyeColor.class);
        Method bodyColor = NovaEntityReflection.method(type, "getBodyColor");
        Method setBodyColor = NovaEntityReflection.method(type, "setBodyColor", DyeColor.class);
        Method getPattern = NovaEntityReflection.method(type, "getPattern");
        Method setPattern = NovaEntityReflection.method(type, "setPattern", pattern);
        JavaTypeRef patternRef = JavaTypeRef.javaType(pattern);
        pair(builder, type, "patternColor", "setPatternColor", patternColor, setPatternColor, DyeColor.class);
        pair(builder, type, "bodyColor", "setBodyColor", bodyColor, setBodyColor, DyeColor.class);
        builder.extension(type, "pattern", function -> function.returns(patternRef)
                .invoke(arguments -> NovaEntityReflection.invoke(getPattern, arguments[0])));
        builder.extension(type, "setPattern", function -> function.param("pattern", patternRef).returns(Void.TYPE)
                .invoke(arguments -> NovaEntityReflection.invoke(setPattern, arguments[0], arguments[1])));
        builder.extension(type, "setPattern", function -> function.param("pattern", String.class).returns(Void.TYPE)
                .invoke(arguments -> setPattern(setPattern, pattern, arguments[0], (String) arguments[1])));
    }
    private static void pair(JavaTypes.Builder builder, Class<?> type, String getterName, String setterName, Method getter, Method setter, Class<?> valueType) {
        builder.extension(type, getterName, function -> function.returns(valueType)
                .invoke(arguments -> NovaEntityReflection.invoke(getter, arguments[0])));
        builder.extension(type, setterName, function -> function.param("color", valueType).returns(Void.TYPE)
                .invoke(arguments -> NovaEntityReflection.invoke(setter, arguments[0], arguments[1])));
    }
    private static Object setPattern(Method setter, Class<?> pattern, Object target, String name) {
        Object value = NovaEntityReflection.enumValue(pattern, name);
        if (value == null) {
            return null;
        }
        return NovaEntityReflection.invoke(setter, target, value);
    }
}
