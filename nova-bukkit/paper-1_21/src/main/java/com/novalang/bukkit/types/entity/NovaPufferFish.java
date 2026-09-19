package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.paper.entity.PaperEntityReflection;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;

/** 1.13+ PufferFish 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.entity.PufferFish"}, methods = {"org.bukkit.entity.PufferFish#getPuffState", "org.bukkit.entity.PufferFish#setPuffState"})
public final class NovaPufferFish {
    private static final String TYPE = "org.bukkit.entity.PufferFish";
    private NovaPufferFish() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = PaperEntityReflection.type(NovaPufferFish.class, TYPE); Method puffState = PaperEntityReflection.method(type, "getPuffState"); Method setPuffState = PaperEntityReflection.method(type, "setPuffState", Integer.TYPE);
        builder.extension(type, "puffState", function -> function.returns(Integer.class).invoke(arguments -> PaperEntityReflection.invoke(puffState, arguments[0])));
        builder.extension(type, "setPuffState", function -> function.param("state", Integer.class).returns(Void.TYPE).invoke(arguments -> PaperEntityReflection.invoke(setPuffState, arguments[0], arguments[1])));
    }
}
