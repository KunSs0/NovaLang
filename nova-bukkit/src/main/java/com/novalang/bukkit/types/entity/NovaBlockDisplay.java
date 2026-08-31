package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;

/** 1.19.4+ BlockDisplay 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.entity.BlockDisplay", "org.bukkit.block.data.BlockData"}, methods = {"org.bukkit.entity.BlockDisplay#getBlock", "org.bukkit.entity.BlockDisplay#setBlock"})
public final class NovaBlockDisplay {
    private static final String TYPE="org.bukkit.entity.BlockDisplay", DATA="org.bukkit.block.data.BlockData";
    private NovaBlockDisplay() { }
    public static void register(JavaTypes.Builder b) {
        Class<?> t=NovaEntityReflection.type(NovaBlockDisplay.class,TYPE),d=NovaEntityReflection.type(NovaBlockDisplay.class,DATA);
        Method get=NovaEntityReflection.method(t,"getBlock"),set=NovaEntityReflection.method(t,"setBlock",d);
        b.extension(t,"block",f->f.returns(JavaTypeRef.javaType(d)).invoke(a->NovaEntityReflection.invoke(get,a[0])));
        b.extension(t,"setBlock",f->f.param("block",JavaTypeRef.javaType(d)).returns(Void.TYPE).invoke(a->NovaEntityReflection.invoke(set,a[0],a[1])));
    }
}
