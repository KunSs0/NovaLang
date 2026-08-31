package com.novalang.bukkit.types.world;
import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;
import java.lang.reflect.Method;
/** 1.13+ Wall BlockData 的 Fluxon 函数契约。 */
@Requires(classes={"org.bukkit.block.data.type.Wall","org.bukkit.block.data.type.Wall$Height"},methods={"org.bukkit.block.data.type.Wall#isUp","org.bukkit.block.data.type.Wall#setUp","org.bukkit.block.data.type.Wall#getHeight","org.bukkit.block.data.type.Wall#setHeight"})
public final class NovaBlockWall {
    private NovaBlockWall() { }
    public static void register(JavaTypes.Builder b) {
        Class<?> w=NovaBlockDataReflection.type(NovaBlockWall.class,"org.bukkit.block.data.type.Wall"), h=NovaBlockDataReflection.type(NovaBlockWall.class,"org.bukkit.block.data.type.Wall$Height");
        Method up=NovaBlockDataReflection.method(w,"isUp"), su=NovaBlockDataReflection.method(w,"setUp",Boolean.TYPE), gh=NovaBlockDataReflection.method(w,"getHeight",BlockFace.class), sh=NovaBlockDataReflection.method(w,"setHeight",BlockFace.class,h);
        b.extension(w,"isUp",f->f.returns(Boolean.class).invoke(a->NovaBlockDataReflection.invoke(up,a[0]))); b.extension(w,"setUp",f->f.param("up",Boolean.class).returns(Void.TYPE).invoke(a->NovaBlockDataReflection.invoke(su,a[0],a[1])));
        b.extension(w,"getHeight",f->f.param("face",BlockFace.class).returns(JavaTypeRef.javaType(h)).invoke(a->NovaBlockDataReflection.invoke(gh,a[0],a[1])));
        b.extension(w,"getHeight",f->f.param("face",String.class).returns(JavaTypeRef.javaType(h)).invoke(a->{BlockFace x=NovaTypeSupport.findEnum(BlockFace.class,(String)a[1]);return x==null?null:NovaBlockDataReflection.invoke(gh,a[0],x);}));
        b.extension(w,"setHeight",f->f.param("face",BlockFace.class).param("height",h).returns(Void.TYPE).invoke(a->NovaBlockDataReflection.invoke(sh,a[0],a[1],a[2])));
        b.extension(w,"setHeight",f->f.param("face",String.class).param("height",h).returns(Void.TYPE).invoke(a->{BlockFace x=NovaTypeSupport.findEnum(BlockFace.class,(String)a[1]);if(x!=null)NovaBlockDataReflection.invoke(sh,a[0],x,a[2]);return null;}));
        b.extension(w,"setHeight",f->f.param("face",BlockFace.class).param("height",String.class).returns(Void.TYPE).invoke(a->{Object x=NovaBlockDataReflection.enumValue(h,(String)a[2]);if(x!=null)NovaBlockDataReflection.invoke(sh,a[0],a[1],x);return null;}));
        b.extension(w,"setHeight",f->f.param("face",String.class).param("height",String.class).returns(Void.TYPE).invoke(a->{BlockFace x=NovaTypeSupport.findEnum(BlockFace.class,(String)a[1]);Object y=NovaBlockDataReflection.enumValue(h,(String)a[2]);if(x!=null&&y!=null)NovaBlockDataReflection.invoke(sh,a[0],x,y);return null;}));
    }
}
