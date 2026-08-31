package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;
import org.bukkit.DyeColor;
import org.bukkit.NamespacedKey;

/** 1.14+ Cat 及 Type 的 Fluxon 函数契约。 */
@Requires(
        classes = {"org.bukkit.entity.Cat", "org.bukkit.entity.Cat$Type"},
        methods = {
                "org.bukkit.entity.Cat#getCatType", "org.bukkit.entity.Cat#setCatType",
                "org.bukkit.entity.Cat#getCollarColor", "org.bukkit.entity.Cat#setCollarColor",
                "org.bukkit.entity.Cat$Type#getKey"
        })
public final class NovaCat {
    private static final String TYPE = "org.bukkit.entity.Cat";
    private static final String CAT_TYPE = "org.bukkit.entity.Cat$Type";
    private NovaCat() {
    }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaEntityReflection.type(NovaCat.class, TYPE);
        Class<?> catType = NovaEntityReflection.type(NovaCat.class, CAT_TYPE);
        Method getType = NovaEntityReflection.method(type, "getCatType");
        Method setType = NovaEntityReflection.method(type, "setCatType", catType);
        Method collarColor = NovaEntityReflection.method(type, "getCollarColor");
        Method setCollarColor = NovaEntityReflection.method(type, "setCollarColor", DyeColor.class);
        Method key = NovaEntityReflection.method(catType, "getKey");
        JavaTypeRef typeRef = JavaTypeRef.javaType(catType);
        builder.extension(type, "catType", function -> function.returns(typeRef)
                .invoke(arguments -> NovaEntityReflection.invoke(getType, arguments[0])));
        builder.extension(type, "setCatType", function -> function.param("type", typeRef).returns(Void.TYPE)
                .invoke(arguments -> NovaEntityReflection.invoke(setType, arguments[0], arguments[1])));
        builder.extension(type, "collarColor", function -> function.returns(DyeColor.class)
                .invoke(arguments -> NovaEntityReflection.invoke(collarColor, arguments[0])));
        builder.extension(type, "setCollarColor", function -> function.param("color", DyeColor.class).returns(Void.TYPE)
                .invoke(arguments -> NovaEntityReflection.invoke(setCollarColor, arguments[0], arguments[1])));
        builder.extension(catType, "key", function -> function.returns(NamespacedKey.class)
                .invoke(arguments -> NovaEntityReflection.invoke(key, arguments[0])));
    }
}
