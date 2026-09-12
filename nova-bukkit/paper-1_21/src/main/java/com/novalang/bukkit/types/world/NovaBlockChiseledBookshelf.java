package com.novalang.bukkit.types.world;
import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;
import java.util.Set;
/** 1.20+ ChiseledBookshelf BlockData 的 Fluxon 函数契约。 */
@Requires(classes={"org.bukkit.block.data.type.ChiseledBookshelf"},methods={"org.bukkit.block.data.type.ChiseledBookshelf#isSlotOccupied","org.bukkit.block.data.type.ChiseledBookshelf#setSlotOccupied","org.bukkit.block.data.type.ChiseledBookshelf#getOccupiedSlots","org.bukkit.block.data.type.ChiseledBookshelf#getMaximumOccupiedSlots"})
public final class NovaBlockChiseledBookshelf {
    private NovaBlockChiseledBookshelf() { }
    public static void register(JavaTypes.Builder b) {
        Class<?> t=NovaBlockDataReflection.type(NovaBlockChiseledBookshelf.class,"org.bukkit.block.data.type.ChiseledBookshelf"); Method g=NovaBlockDataReflection.method(t,"isSlotOccupied",Integer.TYPE),s=NovaBlockDataReflection.method(t,"setSlotOccupied",Integer.TYPE,Boolean.TYPE),o=NovaBlockDataReflection.method(t,"getOccupiedSlots"),m=NovaBlockDataReflection.method(t,"getMaximumOccupiedSlots");
        b.extension(t,"isSlotOccupied",f->f.param("slot",Integer.class).returns(Boolean.class).invoke(a->NovaBlockDataReflection.invoke(g,a[0],a[1]))); b.extension(t,"setSlotOccupied",f->f.param("slot",Integer.class).param("occupied",Boolean.class).returns(Void.TYPE).invoke(a->NovaBlockDataReflection.invoke(s,a[0],a[1],a[2]))); b.extension(t,"occupiedSlots",f->f.returns(Set.class).invoke(a->NovaBlockDataReflection.invoke(o,a[0]))); b.extension(t,"maximumOccupiedSlots",f->f.returns(Integer.class).invoke(a->NovaBlockDataReflection.invoke(m,a[0])));
    }
}
