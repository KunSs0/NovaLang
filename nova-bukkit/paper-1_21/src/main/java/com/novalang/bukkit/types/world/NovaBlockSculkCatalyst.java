package com.novalang.bukkit.types.world;
import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;
/** 1.19+ SculkCatalyst BlockData 的 Fluxon 函数契约。 */
@Requires(classes={"org.bukkit.block.data.type.SculkCatalyst"},methods={"org.bukkit.block.data.type.SculkCatalyst#isBloom","org.bukkit.block.data.type.SculkCatalyst#setBloom"})
public final class NovaBlockSculkCatalyst {
    private NovaBlockSculkCatalyst() { }
    public static void register(JavaTypes.Builder b) {
        Class<?> t=NovaBlockDataReflection.type(NovaBlockSculkCatalyst.class,"org.bukkit.block.data.type.SculkCatalyst"); Method g=NovaBlockDataReflection.method(t,"isBloom"),s=NovaBlockDataReflection.method(t,"setBloom",Boolean.TYPE);
        b.extension(t,"isBloom",f->f.returns(Boolean.class).invoke(a->NovaBlockDataReflection.invoke(g,a[0]))); b.extension(t,"setBloom",f->f.param("bloom",Boolean.class).returns(Void.TYPE).invoke(a->NovaBlockDataReflection.invoke(s,a[0],a[1])));
    }
}
