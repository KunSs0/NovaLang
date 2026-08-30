package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;

@Requires(classes = {"org.bukkit.event.player.PlayerStatisticIncrementEvent"})
public final class NovaPlayerStatisticIncrementEvent {

    private NovaPlayerStatisticIncrementEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PlayerStatisticIncrementEvent.class, "statistic", function -> function
                .returns(Statistic.class)
                .invoke(arguments -> event(arguments).getStatistic()));
        builder.extension(PlayerStatisticIncrementEvent.class, "previousValue", function -> function
                .returns(Integer.class)
                .invoke(arguments -> event(arguments).getPreviousValue()));
        builder.extension(PlayerStatisticIncrementEvent.class, "newValue", function -> function
                .returns(Integer.class)
                .invoke(arguments -> event(arguments).getNewValue()));
        builder.extension(PlayerStatisticIncrementEvent.class, "entityType", function -> function
                .returns(JavaTypeRef.javaType(EntityType.class).nullable())
                .invoke(arguments -> event(arguments).getEntityType()));
        builder.extension(PlayerStatisticIncrementEvent.class, "material", function -> function
                .returns(JavaTypeRef.javaType(Material.class).nullable())
                .invoke(arguments -> event(arguments).getMaterial()));
    }

    private static PlayerStatisticIncrementEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, PlayerStatisticIncrementEvent.class);
    }
}
