package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.types.value.NovaTypeSupport;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import java.util.UUID;

/** Bukkit Entity 的 Fluxon 函数别名。 */
public final class NovaEntity {

    private NovaEntity() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Entity.class, "location", function -> function
                .returns(Location.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Entity.class).getLocation()));
        builder.extension(Entity.class, "velocity", function -> function
                .returns(Vector.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Entity.class).getVelocity()));
        builder.extension(Entity.class, "world", function -> function
                .returns(World.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Entity.class).getWorld()));
        builder.extension(Entity.class, "server", function -> function
                .returns(Server.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Entity.class).getServer()));
        builder.extension(Entity.class, "passenger", function -> function
                .returns(JavaTypeRef.javaType(Entity.class).nullable())
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Entity.class).getPassenger()));
        builder.extension(Entity.class, "vehicle", function -> function
                .returns(JavaTypeRef.javaType(Entity.class).nullable())
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Entity.class).getVehicle()));
        builder.extension(Entity.class, "uniqueId", function -> function
                .returns(UUID.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Entity.class).getUniqueId()));
        builder.extension(Entity.class, "entityId", function -> function
                .returns(Integer.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Entity.class).getEntityId()));
        builder.extension(Entity.class, "type", function -> function
                .returns(EntityType.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Entity.class).getType()));
        builder.extension(Entity.class, "isDead", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Entity.class).isDead()));
        builder.extension(Entity.class, "isValid", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Entity.class).isValid()));
    }
}
