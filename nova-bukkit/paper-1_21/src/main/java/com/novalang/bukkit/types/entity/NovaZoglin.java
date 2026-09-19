package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.paper.entity.PaperEntityReflection;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;

/** 1.16+ Zoglin 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.entity.Zoglin"}, methods = {"org.bukkit.entity.Zoglin#isBaby", "org.bukkit.entity.Zoglin#setBaby"})
public final class NovaZoglin {
    private static final String TYPE = "org.bukkit.entity.Zoglin";
    private NovaZoglin() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = PaperEntityReflection.type(NovaZoglin.class, TYPE); Method baby = PaperEntityReflection.method(type, "isBaby"); Method setBaby = PaperEntityReflection.method(type, "setBaby", Boolean.TYPE);
        builder.extension(type, "isBaby", function -> function.returns(Boolean.class).invoke(arguments -> PaperEntityReflection.invoke(baby, arguments[0])));
        builder.extension(type, "setBaby", function -> function.param("baby", Boolean.class).returns(Void.TYPE).invoke(arguments -> PaperEntityReflection.invoke(setBaby, arguments[0], arguments[1])));
    }
}
