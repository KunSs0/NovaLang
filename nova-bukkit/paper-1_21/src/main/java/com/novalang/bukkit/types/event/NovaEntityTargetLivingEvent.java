package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

/** 实体选择活体目标事件的可选编译期别名。 */
@Requires(classes = {"org.bukkit.event.entity.EntityTargetLivingEntityEvent"})
public final class NovaEntityTargetLivingEvent {

    private NovaEntityTargetLivingEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        JavaTypeRef nullableLivingEntity = JavaTypeRef.javaType(LivingEntity.class).nullable();
        builder.extension(EntityTargetLivingEntityEvent.class, "target", function -> function
                .returns(nullableLivingEntity)
                .invoke(arguments -> event(arguments).getTarget()));
        builder.extension(EntityTargetLivingEntityEvent.class, "setTarget", function -> function
                .param("target", nullableLivingEntity)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setTarget(argument(arguments, 1, LivingEntity.class));
                    return null;
                }));
    }

    private static EntityTargetLivingEntityEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, EntityTargetLivingEntityEvent.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
