package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.inventory.ItemStack;

/** 实体繁殖事件的可选编译期别名。 */
@Requires(classes = {"org.bukkit.event.entity.EntityBreedEvent"})
public final class NovaEntityBreedEvent {

    private NovaEntityBreedEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        JavaTypeRef nullableLivingEntity = JavaTypeRef.javaType(LivingEntity.class).nullable();
        builder.extension(EntityBreedEvent.class, "mother", function -> function
                .returns(LivingEntity.class)
                .invoke(arguments -> event(arguments).getMother()));
        builder.extension(EntityBreedEvent.class, "father", function -> function
                .returns(LivingEntity.class)
                .invoke(arguments -> event(arguments).getFather()));
        builder.extension(EntityBreedEvent.class, "breeder", function -> function
                .returns(nullableLivingEntity)
                .invoke(arguments -> event(arguments).getBreeder()));
        builder.extension(EntityBreedEvent.class, "bredWith", function -> function
                .returns(JavaTypeRef.javaType(ItemStack.class).nullable())
                .invoke(arguments -> event(arguments).getBredWith()));
        builder.extension(EntityBreedEvent.class, "experience", function -> function
                .returns(Integer.class)
                .invoke(arguments -> event(arguments).getExperience()));
        builder.extension(EntityBreedEvent.class, "setExperience", function -> function
                .param("experience", Integer.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setExperience(argument(arguments, 1, Integer.class));
                    return null;
                }));
    }

    private static EntityBreedEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, EntityBreedEvent.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
