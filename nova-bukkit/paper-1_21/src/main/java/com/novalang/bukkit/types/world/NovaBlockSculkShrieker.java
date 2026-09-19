package com.novalang.bukkit.types.world;
import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;
/** 1.19+ SculkShrieker BlockData 的 Fluxon 函数契约。 */
@Requires(classes={"org.bukkit.block.data.type.SculkShrieker"},methods={"org.bukkit.block.data.type.SculkShrieker#isCanSummon","org.bukkit.block.data.type.SculkShrieker#setCanSummon","org.bukkit.block.data.type.SculkShrieker#isShrieking","org.bukkit.block.data.type.SculkShrieker#setShrieking"})
public final class NovaBlockSculkShrieker {
    private NovaBlockSculkShrieker() { }
    public static void register(JavaTypes.Builder b) {
        Class<?> t=NovaBlockDataReflection.type(NovaBlockSculkShrieker.class,"org.bukkit.block.data.type.SculkShrieker"); Method c=NovaBlockDataReflection.method(t,"isCanSummon"),sc=NovaBlockDataReflection.method(t,"setCanSummon",Boolean.TYPE),s=NovaBlockDataReflection.method(t,"isShrieking"),ss=NovaBlockDataReflection.method(t,"setShrieking",Boolean.TYPE);
        b.extension(t,"isCanSummon",f->f.returns(Boolean.class).invoke(a->NovaBlockDataReflection.invoke(c,a[0]))); b.extension(t,"setCanSummon",f->f.param("canSummon",Boolean.class).returns(Void.TYPE).invoke(a->NovaBlockDataReflection.invoke(sc,a[0],a[1]))); b.extension(t,"isShrieking",f->f.returns(Boolean.class).invoke(a->NovaBlockDataReflection.invoke(s,a[0]))); b.extension(t,"setShrieking",f->f.param("shrieking",Boolean.class).returns(Void.TYPE).invoke(a->NovaBlockDataReflection.invoke(ss,a[0],a[1])));
    }
}
