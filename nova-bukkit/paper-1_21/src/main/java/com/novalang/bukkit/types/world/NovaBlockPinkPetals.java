package com.novalang.bukkit.types.world;
import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;
/** 1.20+ PinkPetals BlockData 的 Fluxon 函数契约。 */
@Requires(classes={"org.bukkit.block.data.type.PinkPetals"},methods={"org.bukkit.block.data.type.PinkPetals#getFlowerAmount","org.bukkit.block.data.type.PinkPetals#setFlowerAmount","org.bukkit.block.data.type.PinkPetals#getMaximumFlowerAmount"})
public final class NovaBlockPinkPetals {
    private NovaBlockPinkPetals() { }
    public static void register(JavaTypes.Builder b) {
        Class<?> t=NovaBlockDataReflection.type(NovaBlockPinkPetals.class,"org.bukkit.block.data.type.PinkPetals"); Method g=NovaBlockDataReflection.method(t,"getFlowerAmount"),s=NovaBlockDataReflection.method(t,"setFlowerAmount",Integer.TYPE),m=NovaBlockDataReflection.method(t,"getMaximumFlowerAmount");
        b.extension(t,"flowerAmount",f->f.returns(Integer.class).invoke(a->NovaBlockDataReflection.invoke(g,a[0]))); b.extension(t,"setFlowerAmount",f->f.param("flowerAmount",Integer.class).returns(Void.TYPE).invoke(a->NovaBlockDataReflection.invoke(s,a[0],a[1]))); b.extension(t,"maximumFlowerAmount",f->f.returns(Integer.class).invoke(a->NovaBlockDataReflection.invoke(m,a[0])));
    }
}
