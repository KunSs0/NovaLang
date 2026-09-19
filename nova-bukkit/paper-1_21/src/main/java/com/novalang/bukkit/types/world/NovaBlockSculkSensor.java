package com.novalang.bukkit.types.world;
import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;
/** 1.19+ SculkSensor BlockData 的 Fluxon 函数契约。 */
@Requires(classes={"org.bukkit.block.data.type.SculkSensor","org.bukkit.block.data.type.SculkSensor$Phase"},methods={"org.bukkit.block.data.type.SculkSensor#getPhase","org.bukkit.block.data.type.SculkSensor#setPhase"})
public final class NovaBlockSculkSensor {
    private NovaBlockSculkSensor() { }
    public static void register(JavaTypes.Builder b) {
        Class<?> t=NovaBlockDataReflection.type(NovaBlockSculkSensor.class,"org.bukkit.block.data.type.SculkSensor"),x=NovaBlockDataReflection.type(NovaBlockSculkSensor.class,"org.bukkit.block.data.type.SculkSensor$Phase");Method g=NovaBlockDataReflection.method(t,"getPhase"),s=NovaBlockDataReflection.method(t,"setPhase",x);
        b.extension(t,"phase",f->f.returns(JavaTypeRef.javaType(x)).invoke(a->NovaBlockDataReflection.invoke(g,a[0])));b.extension(t,"setPhase",f->f.param("phase",x).returns(Void.TYPE).invoke(a->NovaBlockDataReflection.invoke(s,a[0],a[1])));b.extension(t,"setPhase",f->f.param("phase",String.class).returns(Void.TYPE).invoke(a->{Object y=NovaBlockDataReflection.enumValue(x,(String)a[1]);if(y!=null)NovaBlockDataReflection.invoke(s,a[0],y);return null;}));
    }
}
