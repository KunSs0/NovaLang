package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.paper.entity.PaperEntityReflection;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;

/** 1.19+ Tadpole 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.entity.Tadpole"}, methods = {"org.bukkit.entity.Tadpole#getAge", "org.bukkit.entity.Tadpole#setAge"})
public final class NovaTadpole {
    private static final String TYPE = "org.bukkit.entity.Tadpole";
    private NovaTadpole() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = PaperEntityReflection.type(NovaTadpole.class, TYPE);
        Method age = PaperEntityReflection.method(type, "getAge");
        Method setAge = PaperEntityReflection.method(type, "setAge", Integer.TYPE);
        builder.extension(type, "age", function -> function.returns(Integer.class).invoke(arguments -> PaperEntityReflection.invoke(age, arguments[0])));
        builder.extension(type, "setAge", function -> function.param("age", Integer.class).returns(Void.TYPE).invoke(arguments -> PaperEntityReflection.invoke(setAge, arguments[0], arguments[1])));
    }
}
