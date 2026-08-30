package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.entity.CreatureSpawnEvent;

/** 生物生成事件的可选编译期别名。 */
@Requires(classes = {"org.bukkit.event.entity.CreatureSpawnEvent"})
public final class NovaCreatureSpawnEvent {

    private NovaCreatureSpawnEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(CreatureSpawnEvent.class, "spawnReason", function -> function
                .returns(CreatureSpawnEvent.SpawnReason.class)
                .invoke(arguments -> event(arguments).getSpawnReason()));
    }

    private static CreatureSpawnEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, CreatureSpawnEvent.class);
    }
}
