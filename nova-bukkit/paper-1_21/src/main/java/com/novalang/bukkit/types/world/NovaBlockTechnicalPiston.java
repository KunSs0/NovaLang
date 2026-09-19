package com.novalang.bukkit.types.world;
import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;
/** 1.20+ TechnicalPiston BlockData 的 Fluxon 函数契约。 */
@Requires(classes={"org.bukkit.block.data.type.TechnicalPiston","org.bukkit.block.data.type.TechnicalPiston$Type"},methods={"org.bukkit.block.data.type.TechnicalPiston#getType","org.bukkit.block.data.type.TechnicalPiston#setType"})
public final class NovaBlockTechnicalPiston {
    private NovaBlockTechnicalPiston() { }
    public static void register(JavaTypes.Builder b) {
        Class<?> t=NovaBlockDataReflection.type(NovaBlockTechnicalPiston.class,"org.bukkit.block.data.type.TechnicalPiston"),x=NovaBlockDataReflection.type(NovaBlockTechnicalPiston.class,"org.bukkit.block.data.type.TechnicalPiston$Type"); Method g=NovaBlockDataReflection.method(t,"getType"),s=NovaBlockDataReflection.method(t,"setType",x);
        b.extension(t,"type",f->f.returns(JavaTypeRef.javaType(x)).invoke(a->NovaBlockDataReflection.invoke(g,a[0]))); b.extension(t,"setType",f->f.param("type",x).returns(Void.TYPE).invoke(a->NovaBlockDataReflection.invoke(s,a[0],a[1]))); b.extension(t,"setType",f->f.param("type",String.class).returns(Void.TYPE).invoke(a->{Object y=NovaBlockDataReflection.enumValue(x,(String)a[1]);if(y!=null)NovaBlockDataReflection.invoke(s,a[0],y);return null;}));
    }
}
