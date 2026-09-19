package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.paper.entity.PaperEntityReflection;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;
import org.bukkit.Color;

/** 1.19.4+ Display 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.entity.Display", "org.bukkit.entity.Display$Brightness", "org.bukkit.entity.Display$Billboard", "org.bukkit.util.Transformation"}, methods = {"org.bukkit.entity.Display#getTransformation", "org.bukkit.entity.Display#setTransformation", "org.bukkit.entity.Display#getInterpolationDuration", "org.bukkit.entity.Display#setInterpolationDuration", "org.bukkit.entity.Display#getTeleportDuration", "org.bukkit.entity.Display#setTeleportDuration", "org.bukkit.entity.Display#getViewRange", "org.bukkit.entity.Display#setViewRange", "org.bukkit.entity.Display#getShadowRadius", "org.bukkit.entity.Display#setShadowRadius", "org.bukkit.entity.Display#getShadowStrength", "org.bukkit.entity.Display#setShadowStrength", "org.bukkit.entity.Display#getDisplayWidth", "org.bukkit.entity.Display#setDisplayWidth", "org.bukkit.entity.Display#getDisplayHeight", "org.bukkit.entity.Display#setDisplayHeight", "org.bukkit.entity.Display#getInterpolationDelay", "org.bukkit.entity.Display#setInterpolationDelay", "org.bukkit.entity.Display#getBillboard", "org.bukkit.entity.Display#setBillboard", "org.bukkit.entity.Display#getGlowColorOverride", "org.bukkit.entity.Display#setGlowColorOverride", "org.bukkit.entity.Display#getBrightness", "org.bukkit.entity.Display#setBrightness"})
public final class NovaDisplay {
    private static final String TYPE = "org.bukkit.entity.Display";
    private static final String BRIGHTNESS = "org.bukkit.entity.Display$Brightness";
    private static final String BILLBOARD = "org.bukkit.entity.Display$Billboard";
    private static final String TRANSFORMATION = "org.bukkit.util.Transformation";
    private NovaDisplay() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = PaperEntityReflection.type(NovaDisplay.class, TYPE); Class<?> brightness = PaperEntityReflection.type(NovaDisplay.class, BRIGHTNESS); Class<?> billboard = PaperEntityReflection.type(NovaDisplay.class, BILLBOARD); Class<?> transformation = PaperEntityReflection.type(NovaDisplay.class, TRANSFORMATION);
        pair(builder, type, "transformation", "setTransformation", transformation); pair(builder, type, "interpolationDuration", "setInterpolationDuration", Integer.class); pair(builder, type, "teleportDuration", "setTeleportDuration", Integer.class); pair(builder, type, "viewRange", "setViewRange", Float.class); pair(builder, type, "shadowRadius", "setShadowRadius", Float.class); pair(builder, type, "shadowStrength", "setShadowStrength", Float.class); pair(builder, type, "displayWidth", "setDisplayWidth", Float.class); pair(builder, type, "displayHeight", "setDisplayHeight", Float.class); pair(builder, type, "interpolationDelay", "setInterpolationDelay", Integer.class); pair(builder, type, "billboard", "setBillboard", billboard); enumSetter(builder, type, "setBillboard", billboard); nullablePair(builder, type, "glowColorOverride", "setGlowColorOverride", Color.class); nullablePair(builder, type, "brightness", "setBrightness", brightness);
    }
    private static void pair(JavaTypes.Builder builder, Class<?> owner, String getterName, String setterName, Class<?> valueType) {
        Method getter = PaperEntityReflection.method(owner, "get" + capitalize(getterName)); Method setter = PaperEntityReflection.method(owner, setterName, primitive(valueType)); JavaTypeRef value = JavaTypeRef.javaType(valueType);
        builder.extension(owner, getterName, function -> function.returns(value).invoke(arguments -> PaperEntityReflection.invoke(getter, arguments[0]))); builder.extension(owner, setterName, function -> function.param("value", value).returns(Void.TYPE).invoke(arguments -> PaperEntityReflection.invoke(setter, arguments[0], arguments[1])));
    }
    private static void nullablePair(JavaTypes.Builder builder, Class<?> owner, String getterName, String setterName, Class<?> valueType) {
        Method getter = PaperEntityReflection.method(owner, "get" + capitalize(getterName)); Method setter = PaperEntityReflection.method(owner, setterName, valueType); JavaTypeRef value = JavaTypeRef.javaType(valueType).nullable();
        builder.extension(owner, getterName, function -> function.returns(value).invoke(arguments -> PaperEntityReflection.invoke(getter, arguments[0]))); builder.extension(owner, setterName, function -> function.param("value", value).returns(Void.TYPE).invoke(arguments -> PaperEntityReflection.invoke(setter, arguments[0], arguments[1])));
    }
    private static void enumSetter(JavaTypes.Builder builder, Class<?> owner, String name, Class<?> enumType) {
        Method setter = PaperEntityReflection.method(owner, name, enumType); builder.extension(owner, name, function -> function.param("value", String.class).returns(Void.TYPE).invoke(arguments -> setEnum(setter, enumType, arguments[0], (String) arguments[1])));
    }
    private static Object setEnum(Method setter, Class<?> enumType, Object target, String name) { Object value = PaperEntityReflection.enumValue(enumType, name); if (value == null) { return null; } return PaperEntityReflection.invoke(setter, target, value); }
    private static Class<?> primitive(Class<?> type) { if (type == Integer.class) { return Integer.TYPE; } if (type == Float.class) { return Float.TYPE; } return type; }
    private static String capitalize(String name) { return Character.toUpperCase(name.charAt(0)) + name.substring(1); }
}
