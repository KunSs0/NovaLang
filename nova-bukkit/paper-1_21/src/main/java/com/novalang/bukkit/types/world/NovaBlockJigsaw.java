package com.novalang.bukkit.types.world;
import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;
/** 1.16+ Jigsaw BlockData 的 Fluxon 函数契约。 */
@Requires(classes={"org.bukkit.block.data.type.Jigsaw","org.bukkit.block.data.type.Jigsaw$Orientation"},methods={"org.bukkit.block.data.type.Jigsaw#getOrientation()"})
public final class NovaBlockJigsaw {
    private NovaBlockJigsaw() { }
    public static void register(JavaTypes.Builder b) {
        Class<?> t=NovaBlockDataReflection.type(NovaBlockJigsaw.class,"org.bukkit.block.data.type.Jigsaw"),x=NovaBlockDataReflection.type(NovaBlockJigsaw.class,"org.bukkit.block.data.type.Jigsaw$Orientation"); Method g=NovaBlockDataReflection.method(t,"getOrientation");
        b.extension(t,"orientation",f->f.returns(JavaTypeRef.javaType(x)).invoke(a->NovaBlockDataReflection.invoke(g,a[0])));
    }
}
