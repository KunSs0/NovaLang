package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.paper.entity.PaperEntityReflection;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;

/** 1.19.3+ Camel 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.entity.Camel"}, methods = {"org.bukkit.entity.Camel#isDashing", "org.bukkit.entity.Camel#setDashing"})
public final class NovaCamel {
    private static final String TYPE = "org.bukkit.entity.Camel";
    private NovaCamel() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = PaperEntityReflection.type(NovaCamel.class, TYPE); Method isDashing = PaperEntityReflection.method(type, "isDashing"); Method setDashing = PaperEntityReflection.method(type, "setDashing", Boolean.TYPE);
        builder.extension(type, "isDashing", f -> f.returns(Boolean.class).invoke(a -> PaperEntityReflection.invoke(isDashing, a[0]))); builder.extension(type, "setDashing", f -> f.param("dashing", Boolean.class).returns(Void.TYPE).invoke(a -> PaperEntityReflection.invoke(setDashing, a[0], a[1])));
    }
}
