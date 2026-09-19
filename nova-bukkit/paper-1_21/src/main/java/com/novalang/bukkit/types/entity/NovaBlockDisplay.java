package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.paper.entity.PaperEntityReflection;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;

/** 1.19.4+ BlockDisplay 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.entity.BlockDisplay", "org.bukkit.block.data.BlockData"}, methods = {"org.bukkit.entity.BlockDisplay#getBlock", "org.bukkit.entity.BlockDisplay#setBlock"})
public final class NovaBlockDisplay {
    private static final String TYPE = "org.bukkit.entity.BlockDisplay";
    private static final String DATA = "org.bukkit.block.data.BlockData";
    private NovaBlockDisplay() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = PaperEntityReflection.type(NovaBlockDisplay.class, TYPE); Class<?> data = PaperEntityReflection.type(NovaBlockDisplay.class, DATA); Method get = PaperEntityReflection.method(type, "getBlock"); Method set = PaperEntityReflection.method(type, "setBlock", data);
        builder.extension(type, "block", function -> function.returns(JavaTypeRef.javaType(data)).invoke(arguments -> PaperEntityReflection.invoke(get, arguments[0]))); builder.extension(type, "setBlock", function -> function.param("block", JavaTypeRef.javaType(data)).returns(Void.TYPE).invoke(arguments -> PaperEntityReflection.invoke(set, arguments[0], arguments[1])));
    }
}
