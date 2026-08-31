package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;

/** 1.16+ Hoglin 的 Fluxon 函数契约。 */
@Requires(
        classes = {"org.bukkit.entity.Hoglin"},
        methods = {
                "org.bukkit.entity.Hoglin#isImmuneToZombification",
                "org.bukkit.entity.Hoglin#setImmuneToZombification",
                "org.bukkit.entity.Hoglin#isAbleToBeHunted",
                "org.bukkit.entity.Hoglin#setIsAbleToBeHunted",
                "org.bukkit.entity.Hoglin#getConversionTime",
                "org.bukkit.entity.Hoglin#setConversionTime",
                "org.bukkit.entity.Hoglin#isConverting"
        })
public final class NovaHoglin {

    private static final String TYPE = "org.bukkit.entity.Hoglin";

    private NovaHoglin() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaEntityReflection.type(NovaHoglin.class, TYPE);
        flag(builder, type, "isImmuneToZombification", "setImmuneToZombification");
        flag(builder, type, "isAbleToBeHunted", "setIsAbleToBeHunted");
        Method conversionTime = NovaEntityReflection.method(type, "getConversionTime");
        Method setConversionTime = NovaEntityReflection.method(type, "setConversionTime", Integer.TYPE);
        Method converting = NovaEntityReflection.method(type, "isConverting");
        builder.extension(type, "conversionTime", function -> function.returns(Integer.class)
                .invoke(arguments -> NovaEntityReflection.invoke(conversionTime, arguments[0])));
        builder.extension(type, "setConversionTime", function -> function.param("ticks", Integer.class).returns(Void.TYPE)
                .invoke(arguments -> NovaEntityReflection.invoke(setConversionTime, arguments[0], arguments[1])));
        builder.extension(type, "isConverting", function -> function.returns(Boolean.class)
                .invoke(arguments -> NovaEntityReflection.invoke(converting, arguments[0])));
    }

    private static void flag(JavaTypes.Builder builder, Class<?> type, String getterName, String setterName) {
        Method getter = NovaEntityReflection.method(type, getterName);
        Method setter = NovaEntityReflection.method(type, setterName, Boolean.TYPE);
        builder.extension(type, getterName, function -> function.returns(Boolean.class)
                .invoke(arguments -> NovaEntityReflection.invoke(getter, arguments[0])));
        builder.extension(type, setterName, function -> function.param("value", Boolean.class).returns(Void.TYPE)
                .invoke(arguments -> NovaEntityReflection.invoke(setter, arguments[0], arguments[1])));
    }
}
