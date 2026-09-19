package com.novalang.bukkit.types.world;
import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;
import java.lang.reflect.Method;
import java.util.Set;
/** 1.17+ PointedDripstone BlockData 的 Fluxon 函数契约。 */
@Requires(classes={"org.bukkit.block.data.type.PointedDripstone","org.bukkit.block.data.type.PointedDripstone$Thickness"},methods={"org.bukkit.block.data.type.PointedDripstone#getVerticalDirection","org.bukkit.block.data.type.PointedDripstone#setVerticalDirection","org.bukkit.block.data.type.PointedDripstone#getVerticalDirections","org.bukkit.block.data.type.PointedDripstone#getThickness","org.bukkit.block.data.type.PointedDripstone#setThickness"})
public final class NovaBlockPointedDripstone {
    private NovaBlockPointedDripstone() { }
    public static void register(JavaTypes.Builder b) {
        Class<?> t=NovaBlockDataReflection.type(NovaBlockPointedDripstone.class,"org.bukkit.block.data.type.PointedDripstone"), x=NovaBlockDataReflection.type(NovaBlockPointedDripstone.class,"org.bukkit.block.data.type.PointedDripstone$Thickness");
        Method d=NovaBlockDataReflection.method(t,"getVerticalDirection"), sd=NovaBlockDataReflection.method(t,"setVerticalDirection",BlockFace.class), ds=NovaBlockDataReflection.method(t,"getVerticalDirections"), th=NovaBlockDataReflection.method(t,"getThickness"), sth=NovaBlockDataReflection.method(t,"setThickness",x);
        b.extension(t,"verticalDirection",f->f.returns(BlockFace.class).invoke(a->NovaBlockDataReflection.invoke(d,a[0]))); b.extension(t,"setVerticalDirection",f->f.param("direction",BlockFace.class).returns(Void.TYPE).invoke(a->NovaBlockDataReflection.invoke(sd,a[0],a[1]))); b.extension(t,"verticalDirections",f->f.returns(Set.class).invoke(a->NovaBlockDataReflection.invoke(ds,a[0])));
        b.extension(t,"thickness",f->f.returns(JavaTypeRef.javaType(x)).invoke(a->NovaBlockDataReflection.invoke(th,a[0]))); b.extension(t,"setThickness",f->f.param("thickness",x).returns(Void.TYPE).invoke(a->NovaBlockDataReflection.invoke(sth,a[0],a[1]))); b.extension(t,"setThickness",f->f.param("thickness",String.class).returns(Void.TYPE).invoke(a->{Object y=NovaBlockDataReflection.enumValue(x,(String)a[1]);if(y!=null)NovaBlockDataReflection.invoke(sth,a[0],y);return null;}));
    }
}
