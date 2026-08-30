package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

/** 实体射箭事件的可选 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.event.entity.EntityShootBowEvent"})
public final class NovaEntityShootBowEvent {

    private NovaEntityShootBowEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        JavaTypeRef nullableItem = JavaTypeRef.javaType(ItemStack.class).nullable();
        builder.extension(EntityShootBowEvent.class, "entity", function -> function
                .returns(LivingEntity.class)
                .invoke(arguments -> event(arguments).getEntity()));
        builder.extension(EntityShootBowEvent.class, "bow", function -> function
                .returns(nullableItem)
                .invoke(arguments -> event(arguments).getBow()));
        builder.extension(EntityShootBowEvent.class, "projectile", function -> function
                .returns(Entity.class)
                .invoke(arguments -> event(arguments).getProjectile()));
        builder.extension(EntityShootBowEvent.class, "setProjectile", function -> function
                .param("projectile", Entity.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setProjectile(argument(arguments, 1, Entity.class));
                    return null;
                }));
        builder.extension(EntityShootBowEvent.class, "force", function -> function
                .returns(Float.class)
                .invoke(arguments -> event(arguments).getForce()));
    }

    private static EntityShootBowEvent event(Object[] arguments) {
        return argument(arguments, 0, EntityShootBowEvent.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
