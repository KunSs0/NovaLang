package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.SpawnerSpawnEvent;

@Requires(classes = {"org.bukkit.event.entity.SpawnerSpawnEvent"})
public final class NovaSpawnerSpawnEvent {
    private NovaSpawnerSpawnEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(SpawnerSpawnEvent.class, "entity", function -> function.returns(Entity.class).invoke(arguments -> event(arguments).getEntity()));
        builder.extension(SpawnerSpawnEvent.class, "spawner", function -> function.returns(JavaTypeRef.javaType(CreatureSpawner.class).nullable()).invoke(arguments -> event(arguments).getSpawner()));
    }

    private static SpawnerSpawnEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, SpawnerSpawnEvent.class);
    }
}
