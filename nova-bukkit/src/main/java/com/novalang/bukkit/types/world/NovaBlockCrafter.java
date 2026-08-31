package com.novalang.bukkit.types.world;
import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;
/** 1.21+ Crafter BlockData 的 Fluxon 函数契约。 */
@Requires(classes={"org.bukkit.block.data.type.Crafter","org.bukkit.block.data.type.Crafter$Orientation"},methods={"org.bukkit.block.data.type.Crafter#isCrafting","org.bukkit.block.data.type.Crafter#setCrafting","org.bukkit.block.data.type.Crafter#isTriggered","org.bukkit.block.data.type.Crafter#setTriggered","org.bukkit.block.data.type.Crafter#getOrientation","org.bukkit.block.data.type.Crafter#setOrientation"})
public final class NovaBlockCrafter {
    private NovaBlockCrafter() { }
    public static void register(JavaTypes.Builder b) {
        Class<?> t=NovaBlockDataReflection.type(NovaBlockCrafter.class,"org.bukkit.block.data.type.Crafter"),x=NovaBlockDataReflection.type(NovaBlockCrafter.class,"org.bukkit.block.data.type.Crafter$Orientation"); Method c=NovaBlockDataReflection.method(t,"isCrafting"),sc=NovaBlockDataReflection.method(t,"setCrafting",Boolean.TYPE),g=NovaBlockDataReflection.method(t,"isTriggered"),sg=NovaBlockDataReflection.method(t,"setTriggered",Boolean.TYPE),o=NovaBlockDataReflection.method(t,"getOrientation"),so=NovaBlockDataReflection.method(t,"setOrientation",x);
        b.extension(t,"isCrafting",f->f.returns(Boolean.class).invoke(a->NovaBlockDataReflection.invoke(c,a[0])));b.extension(t,"setCrafting",f->f.param("crafting",Boolean.class).returns(Void.TYPE).invoke(a->NovaBlockDataReflection.invoke(sc,a[0],a[1])));b.extension(t,"isTriggered",f->f.returns(Boolean.class).invoke(a->NovaBlockDataReflection.invoke(g,a[0])));b.extension(t,"setTriggered",f->f.param("triggered",Boolean.class).returns(Void.TYPE).invoke(a->NovaBlockDataReflection.invoke(sg,a[0],a[1])));b.extension(t,"orientation",f->f.returns(JavaTypeRef.javaType(x)).invoke(a->NovaBlockDataReflection.invoke(o,a[0])));b.extension(t,"setOrientation",f->f.param("orientation",x).returns(Void.TYPE).invoke(a->NovaBlockDataReflection.invoke(so,a[0],a[1])));b.extension(t,"setOrientation",f->f.param("orientation",String.class).returns(Void.TYPE).invoke(a->{Object y=NovaBlockDataReflection.enumValue(x,(String)a[1]);if(y!=null)NovaBlockDataReflection.invoke(so,a[0],y);return null;}));
    }
}
