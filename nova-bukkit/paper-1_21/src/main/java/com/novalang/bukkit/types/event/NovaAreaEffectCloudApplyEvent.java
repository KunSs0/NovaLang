package com.novalang.bukkit.types.event;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;

/** Spigot 1.12.2 区域效果云作用事件别名。 */
public final class NovaAreaEffectCloudApplyEvent {

    private NovaAreaEffectCloudApplyEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(AreaEffectCloudApplyEvent.class, "affectedEntities", function -> function
                .returns(JavaTypeRef.listOf(JavaTypeRef.javaType(LivingEntity.class)))
                .invoke(arguments -> event(arguments).getAffectedEntities()));
    }

    private static AreaEffectCloudApplyEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, AreaEffectCloudApplyEvent.class);
    }
}
