package com.novalang.bukkit.types.world;
import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;
/** 1.17+ BigDripleaf BlockData 的 Fluxon 函数契约。 */
@Requires(classes={"org.bukkit.block.data.type.BigDripleaf","org.bukkit.block.data.type.BigDripleaf$Tilt"},methods={"org.bukkit.block.data.type.BigDripleaf#getTilt","org.bukkit.block.data.type.BigDripleaf#setTilt"})
public final class NovaBlockBigDripleaf {
    private NovaBlockBigDripleaf() { }
    public static void register(JavaTypes.Builder b) {
        Class<?> t=NovaBlockDataReflection.type(NovaBlockBigDripleaf.class,"org.bukkit.block.data.type.BigDripleaf"), x=NovaBlockDataReflection.type(NovaBlockBigDripleaf.class,"org.bukkit.block.data.type.BigDripleaf$Tilt");
        Method g=NovaBlockDataReflection.method(t,"getTilt"), s=NovaBlockDataReflection.method(t,"setTilt",x);
        b.extension(t,"tilt",f->f.returns(JavaTypeRef.javaType(x)).invoke(a->NovaBlockDataReflection.invoke(g,a[0])));
        b.extension(t,"setTilt",f->f.param("tilt",x).returns(Void.TYPE).invoke(a->NovaBlockDataReflection.invoke(s,a[0],a[1])));
        b.extension(t,"setTilt",f->f.param("tilt",String.class).returns(Void.TYPE).invoke(a->{Object y=NovaBlockDataReflection.enumValue(x,(String)a[1]);if(y!=null)NovaBlockDataReflection.invoke(s,a[0],y);return null;}));
    }
}
