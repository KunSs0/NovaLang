package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Material;
import java.lang.reflect.Method;

/** 1.13+ BlockDataMeta 的 Fluxon 函数契约。 */
@Requires(classes={"org.bukkit.inventory.meta.BlockDataMeta","org.bukkit.block.data.BlockData"},methods={"org.bukkit.inventory.meta.BlockDataMeta#hasBlockData","org.bukkit.inventory.meta.BlockDataMeta#getBlockData","org.bukkit.inventory.meta.BlockDataMeta#setBlockData"})
public final class NovaBlockDataMeta {
    private static final String TYPE="org.bukkit.inventory.meta.BlockDataMeta", DATA="org.bukkit.block.data.BlockData";
    private NovaBlockDataMeta() { }
    public static void register(JavaTypes.Builder b) { Class<?>t=NovaInventoryReflection.type(NovaBlockDataMeta.class,TYPE),d=NovaInventoryReflection.type(NovaBlockDataMeta.class,DATA); Method h=NovaInventoryReflection.method(t,"hasBlockData"),g=NovaInventoryReflection.method(t,"getBlockData",Material.class),s=NovaInventoryReflection.method(t,"setBlockData",d); b.extension(t,"hasBlockData",f->f.returns(Boolean.class).invoke(a->NovaInventoryReflection.invoke(h,a[0]))); b.extension(t,"getBlockData",f->f.param("material",Material.class).returns(JavaTypeRef.javaType(d).nullable()).invoke(a->NovaInventoryReflection.invoke(g,a[0],a[1]))); b.extension(t,"setBlockData",f->f.param("data",d).returns(Void.TYPE).invoke(a->NovaInventoryReflection.invoke(s,a[0],a[1]))); }
}
