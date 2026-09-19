package com.novalang.bukkit.types.entity;
import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;
import org.bukkit.block.BlockFace;
/** 1.13+ Shulker 的 Fluxon 函数契约。 */
@Requires(classes={"org.bukkit.entity.Shulker"},methods={"org.bukkit.entity.Shulker#getPeek","org.bukkit.entity.Shulker#setPeek","org.bukkit.entity.Shulker#getAttachedFace","org.bukkit.entity.Shulker#setAttachedFace"})
public final class NovaShulker { private NovaShulker(){} public static void register(JavaTypes.Builder b){Class<?>t=NovaEntityReflection.type(NovaShulker.class,"org.bukkit.entity.Shulker");Method p=NovaEntityReflection.method(t,"getPeek"),sp=NovaEntityReflection.method(t,"setPeek",Float.TYPE),f=NovaEntityReflection.method(t,"getAttachedFace"),sf=NovaEntityReflection.method(t,"setAttachedFace",BlockFace.class);b.extension(t,"peek",x->x.returns(Float.class).invoke(a->NovaEntityReflection.invoke(p,a[0])));b.extension(t,"setPeek",x->x.param("peek",Float.class).returns(Void.TYPE).invoke(a->NovaEntityReflection.invoke(sp,a[0],a[1])));b.extension(t,"attachedFace",x->x.returns(BlockFace.class).invoke(a->NovaEntityReflection.invoke(f,a[0])));b.extension(t,"setAttachedFace",x->x.param("face",BlockFace.class).returns(Void.TYPE).invoke(a->NovaEntityReflection.invoke(sf,a[0],a[1])));}}
