package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.types.value.NovaTypeSupport;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;

/** 1.12.2 Creature（Mob 接口自 1.14 才出现）的 Fluxon 别名。 */
final class NovaCreature {

    private NovaCreature() {
    }

    static void register(JavaTypes.Builder builder) {
        JavaTypeRef target = JavaTypeRef.javaType(LivingEntity.class);
        builder.extension(Creature.class, "setTarget", f -> f.param("target", target).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Creature.class).setTarget(NovaTypeSupport.argument(a, 1, LivingEntity.class)); return null; }));
        builder.extension(Creature.class, "target", f -> f.returns(JavaTypeRef.javaType(LivingEntity.class).nullable()).invoke(a -> NovaTypeSupport.argument(a, 0, Creature.class).getTarget()));
    }
}
