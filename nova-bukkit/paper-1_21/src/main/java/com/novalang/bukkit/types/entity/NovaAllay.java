package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.paper.entity.PaperEntityReflection;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;
import org.bukkit.Location;

/** 1.19+ Allay 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.entity.Allay"}, methods = {"org.bukkit.entity.Allay#canDuplicate", "org.bukkit.entity.Allay#setCanDuplicate", "org.bukkit.entity.Allay#getDuplicationCooldown", "org.bukkit.entity.Allay#setDuplicationCooldown", "org.bukkit.entity.Allay#resetDuplicationCooldown", "org.bukkit.entity.Allay#isDancing", "org.bukkit.entity.Allay#startDancing", "org.bukkit.entity.Allay#stopDancing", "org.bukkit.entity.Allay#duplicateAllay", "org.bukkit.entity.Allay#getJukebox"})
public final class NovaAllay {
    private static final String TYPE = "org.bukkit.entity.Allay";
    private NovaAllay() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = PaperEntityReflection.type(NovaAllay.class, TYPE); Method canDuplicate = PaperEntityReflection.method(type, "canDuplicate"); Method setCanDuplicate = PaperEntityReflection.method(type, "setCanDuplicate", Boolean.TYPE); Method getCooldown = PaperEntityReflection.method(type, "getDuplicationCooldown"); Method setCooldown = PaperEntityReflection.method(type, "setDuplicationCooldown", Long.TYPE); Method resetCooldown = PaperEntityReflection.method(type, "resetDuplicationCooldown"); Method isDancing = PaperEntityReflection.method(type, "isDancing"); Method startDancing = PaperEntityReflection.method(type, "startDancing"); Method startDancingAt = PaperEntityReflection.method(type, "startDancing", Location.class); Method stopDancing = PaperEntityReflection.method(type, "stopDancing"); Method duplicateAllay = PaperEntityReflection.method(type, "duplicateAllay"); Method getJukebox = PaperEntityReflection.method(type, "getJukebox");
        builder.extension(type, "canDuplicate", f -> f.returns(Boolean.class).invoke(a -> PaperEntityReflection.invoke(canDuplicate, a[0]))); builder.extension(type, "setCanDuplicate", f -> f.param("value", Boolean.class).returns(Void.TYPE).invoke(a -> PaperEntityReflection.invoke(setCanDuplicate, a[0], a[1]))); builder.extension(type, "duplicationCooldown", f -> f.returns(Long.class).invoke(a -> PaperEntityReflection.invoke(getCooldown, a[0]))); builder.extension(type, "setDuplicationCooldown", f -> f.param("ticks", Long.class).returns(Void.TYPE).invoke(a -> PaperEntityReflection.invoke(setCooldown, a[0], a[1]))); builder.extension(type, "resetDuplicationCooldown", f -> f.returns(Void.TYPE).invoke(a -> PaperEntityReflection.invoke(resetCooldown, a[0]))); builder.extension(type, "isDancing", f -> f.returns(Boolean.class).invoke(a -> PaperEntityReflection.invoke(isDancing, a[0]))); builder.extension(type, "startDancing", f -> f.returns(Void.TYPE).invoke(a -> PaperEntityReflection.invoke(startDancing, a[0]))); builder.extension(type, "startDancing", f -> f.param("jukebox", Location.class).returns(Void.TYPE).invoke(a -> PaperEntityReflection.invoke(startDancingAt, a[0], a[1]))); builder.extension(type, "stopDancing", f -> f.returns(Void.TYPE).invoke(a -> PaperEntityReflection.invoke(stopDancing, a[0]))); builder.extension(type, "duplicateAllay", f -> f.returns(JavaTypeRef.javaType(type).nullable()).invoke(a -> PaperEntityReflection.invoke(duplicateAllay, a[0]))); builder.extension(type, "jukebox", f -> f.returns(JavaTypeRef.javaType(Location.class).nullable()).invoke(a -> PaperEntityReflection.invoke(getJukebox, a[0])));
    }
}
