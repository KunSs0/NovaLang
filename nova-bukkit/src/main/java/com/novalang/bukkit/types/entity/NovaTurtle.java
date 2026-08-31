package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;

/** 1.13+ Turtle 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.entity.Turtle"}, methods = {
        "org.bukkit.entity.Turtle#hasEgg", "org.bukkit.entity.Turtle#isLayingEgg"})
public final class NovaTurtle {
    private static final String TYPE = "org.bukkit.entity.Turtle";
    private NovaTurtle() {
    }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaEntityReflection.type(NovaTurtle.class, TYPE);
        Method hasEgg = NovaEntityReflection.method(type, "hasEgg");
        Method layingEgg = NovaEntityReflection.method(type, "isLayingEgg");
        builder.extension(type, "hasEgg", function -> function.returns(Boolean.class)
                .invoke(arguments -> NovaEntityReflection.invoke(hasEgg, arguments[0])));
        builder.extension(type, "isLayingEgg", function -> function.returns(Boolean.class)
                .invoke(arguments -> NovaEntityReflection.invoke(layingEgg, arguments[0])));
    }
}
