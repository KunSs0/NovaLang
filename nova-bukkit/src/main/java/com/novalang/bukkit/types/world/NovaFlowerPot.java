package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.FlowerPot;
import org.bukkit.material.MaterialData;

@Requires(classes = {"org.bukkit.block.FlowerPot"})
public final class NovaFlowerPot {
    private NovaFlowerPot() { }
    public static void register(JavaTypes.Builder b) {
        JavaTypeRef nullable = JavaTypeRef.javaType(MaterialData.class).nullable();
        b.extension(FlowerPot.class, "contents", f -> f.returns(nullable).invoke(a -> e(a).getContents()));
        b.extension(FlowerPot.class, "setContents", f -> f.param("contents", nullable).returns(Void.TYPE).invoke(a -> { e(a).setContents(NovaTypeSupport.argument(a, 1, MaterialData.class)); return null; }));
    }
    private static FlowerPot e(Object[] a) { return NovaTypeSupport.argument(a, 0, FlowerPot.class); }
}
