package com.novalang.bukkit;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

/** Entity 在 1.12.2 中、且不与基础表重复的 Fluxon 别名。 */
final class NovaEntityExtra {

    private NovaEntityExtra() {
    }

    static void register(JavaTypes.Builder builder) {
        JavaTypeRef entity = JavaTypeRef.javaType(Entity.class);
        JavaTypeRef location = JavaTypeRef.javaType(Location.class);
        builder.extension(Entity.class, "isMoving", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Entity.class).getVelocity().lengthSquared() > 0.0));
        builder.extension(Entity.class, "getLocation", f -> f.param("location", location).returns(location).invoke(a -> NovaTypeSupport.argument(a, 0, Entity.class).getLocation(NovaTypeSupport.argument(a, 1, Location.class))));
        builder.extension(Entity.class, "setVelocity", f -> f.param("velocity", Vector.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Entity.class).setVelocity(NovaTypeSupport.argument(a, 1, Vector.class)); return null; }));
        builder.extension(Entity.class, "height", f -> f.returns(Double.class).invoke(a -> NovaTypeSupport.argument(a, 0, Entity.class).getHeight()));
        builder.extension(Entity.class, "width", f -> f.returns(Double.class).invoke(a -> NovaTypeSupport.argument(a, 0, Entity.class).getWidth()));
        builder.extension(Entity.class, "isOnGround", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Entity.class).isOnGround()));
        builder.extension(Entity.class, "teleport", f -> f.param("location", location).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Entity.class).teleport(NovaTypeSupport.argument(a, 1, Location.class))));
        builder.extension(Entity.class, "teleport", f -> f.param("entity", entity).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Entity.class).teleport(NovaTypeSupport.argument(a, 1, Entity.class))));
        builder.extension(Entity.class, "teleport", f -> f.param("location", location).param("cause", PlayerTeleportEvent.TeleportCause.class).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Entity.class).teleport(NovaTypeSupport.argument(a, 1, Location.class), NovaTypeSupport.argument(a, 2, PlayerTeleportEvent.TeleportCause.class))));
        builder.extension(Entity.class, "teleport", f -> f.param("entity", entity).param("cause", PlayerTeleportEvent.TeleportCause.class).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Entity.class).teleport(NovaTypeSupport.argument(a, 1, Entity.class), NovaTypeSupport.argument(a, 2, PlayerTeleportEvent.TeleportCause.class))));
        builder.extension(Entity.class, "teleport", f -> f.param("location", location).param("cause", String.class).returns(Boolean.class).invoke(a -> {
            PlayerTeleportEvent.TeleportCause causeValue = NovaTypeSupport.findEnum(PlayerTeleportEvent.TeleportCause.class, NovaTypeSupport.argument(a, 2, String.class));
            if (causeValue == null) {
                return false;
            }
            return NovaTypeSupport.argument(a, 0, Entity.class).teleport(NovaTypeSupport.argument(a, 1, Location.class), causeValue);
        }));
        builder.extension(Entity.class, "teleport", f -> f.param("entity", entity).param("cause", String.class).returns(Boolean.class).invoke(a -> {
            PlayerTeleportEvent.TeleportCause causeValue = NovaTypeSupport.findEnum(PlayerTeleportEvent.TeleportCause.class, NovaTypeSupport.argument(a, 2, String.class));
            if (causeValue == null) {
                return false;
            }
            return NovaTypeSupport.argument(a, 0, Entity.class).teleport(NovaTypeSupport.argument(a, 1, Entity.class), causeValue);
        }));
        builder.extension(Entity.class, "getNearbyEntities", f -> f.param("x", Double.class).param("y", Double.class).param("z", Double.class).returns(JavaTypeRef.listOf(entity)).invoke(a -> NovaTypeSupport.argument(a, 0, Entity.class).getNearbyEntities(NovaTypeSupport.argument(a, 1, Double.class), NovaTypeSupport.argument(a, 2, Double.class), NovaTypeSupport.argument(a, 3, Double.class))));
        builder.extension(Entity.class, "fireTicks", f -> f.returns(Integer.class).invoke(a -> NovaTypeSupport.argument(a, 0, Entity.class).getFireTicks()));
        builder.extension(Entity.class, "maxFireTicks", f -> f.returns(Integer.class).invoke(a -> NovaTypeSupport.argument(a, 0, Entity.class).getMaxFireTicks()));
        builder.extension(Entity.class, "setFireTicks", f -> f.param("ticks", Integer.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Entity.class).setFireTicks(NovaTypeSupport.argument(a, 1, Integer.class)); return null; }));
        builder.extension(Entity.class, "remove", f -> f.returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Entity.class).remove(); return null; }));
        builder.extension(Entity.class, "passengers", f -> f.returns(JavaTypeRef.listOf(entity)).invoke(a -> NovaTypeSupport.argument(a, 0, Entity.class).getPassengers()));
        builder.extension(Entity.class, "setPassenger", f -> f.param("passenger", entity).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Entity.class).setPassenger(NovaTypeSupport.argument(a, 1, Entity.class))));
        builder.extension(Entity.class, "addPassenger", f -> f.param("passenger", entity).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Entity.class).addPassenger(NovaTypeSupport.argument(a, 1, Entity.class))));
        builder.extension(Entity.class, "removePassenger", f -> f.param("passenger", entity).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Entity.class).removePassenger(NovaTypeSupport.argument(a, 1, Entity.class))));
        builder.extension(Entity.class, "isEmpty", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Entity.class).isEmpty()));
        builder.extension(Entity.class, "eject", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Entity.class).eject()));
        builder.extension(Entity.class, "fallDistance", f -> f.returns(Float.class).invoke(a -> NovaTypeSupport.argument(a, 0, Entity.class).getFallDistance()));
        builder.extension(Entity.class, "setFallDistance", f -> f.param("distance", Float.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Entity.class).setFallDistance(NovaTypeSupport.argument(a, 1, Float.class)); return null; }));
        builder.extension(Entity.class, "setLastDamageCause", f -> f.param("cause", EntityDamageEvent.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Entity.class).setLastDamageCause(NovaTypeSupport.argument(a, 1, EntityDamageEvent.class)); return null; }));
        builder.extension(Entity.class, "lastDamageCause", f -> f.returns(JavaTypeRef.javaType(EntityDamageEvent.class).nullable()).invoke(a -> NovaTypeSupport.argument(a, 0, Entity.class).getLastDamageCause()));
        builder.extension(Entity.class, "ticksLived", f -> f.returns(Integer.class).invoke(a -> NovaTypeSupport.argument(a, 0, Entity.class).getTicksLived()));
        builder.extension(Entity.class, "setTicksLived", f -> f.param("ticks", Integer.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Entity.class).setTicksLived(NovaTypeSupport.argument(a, 1, Integer.class)); return null; }));
        builder.extension(Entity.class, "playEffect", f -> f.param("effect", EntityEffect.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Entity.class).playEffect(NovaTypeSupport.argument(a, 1, EntityEffect.class)); return null; }));
        builder.extension(Entity.class, "isInsideVehicle", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Entity.class).isInsideVehicle()));
        builder.extension(Entity.class, "leaveVehicle", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Entity.class).leaveVehicle()));
        builder.extension(Entity.class, "setCustomNameVisible", f -> f.param("visible", Boolean.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Entity.class).setCustomNameVisible(NovaTypeSupport.argument(a, 1, Boolean.class)); return null; }));
        builder.extension(Entity.class, "isCustomNameVisible", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Entity.class).isCustomNameVisible()));
        builder.extension(Entity.class, "setGlowing", f -> f.param("glowing", Boolean.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Entity.class).setGlowing(NovaTypeSupport.argument(a, 1, Boolean.class)); return null; }));
        builder.extension(Entity.class, "isGlowing", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Entity.class).isGlowing()));
        builder.extension(Entity.class, "setInvulnerable", f -> f.param("invulnerable", Boolean.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Entity.class).setInvulnerable(NovaTypeSupport.argument(a, 1, Boolean.class)); return null; }));
        builder.extension(Entity.class, "isInvulnerable", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Entity.class).isInvulnerable()));
        builder.extension(Entity.class, "isSilent", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Entity.class).isSilent()));
        builder.extension(Entity.class, "setSilent", f -> f.param("silent", Boolean.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Entity.class).setSilent(NovaTypeSupport.argument(a, 1, Boolean.class)); return null; }));
        builder.extension(Entity.class, "hasGravity", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Entity.class).hasGravity()));
        builder.extension(Entity.class, "setGravity", f -> f.param("gravity", Boolean.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Entity.class).setGravity(NovaTypeSupport.argument(a, 1, Boolean.class)); return null; }));
        builder.extension(Entity.class, "portalCooldown", f -> f.returns(Integer.class).invoke(a -> NovaTypeSupport.argument(a, 0, Entity.class).getPortalCooldown()));
        builder.extension(Entity.class, "setPortalCooldown", f -> f.param("ticks", Integer.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Entity.class).setPortalCooldown(NovaTypeSupport.argument(a, 1, Integer.class)); return null; }));
        builder.extension(Entity.class, "scoreboardTags", f -> f.returns(JavaTypeRef.setOf(JavaTypeRef.javaType(String.class))).invoke(a -> NovaTypeSupport.argument(a, 0, Entity.class).getScoreboardTags()));
        builder.extension(Entity.class, "addScoreboardTag", f -> f.param("tag", String.class).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Entity.class).addScoreboardTag(NovaTypeSupport.argument(a, 1, String.class))));
        builder.extension(Entity.class, "removeScoreboardTag", f -> f.param("tag", String.class).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Entity.class).removeScoreboardTag(NovaTypeSupport.argument(a, 1, String.class))));
        builder.extension(Entity.class, "pistonMoveReaction", f -> f.returns(PistonMoveReaction.class).invoke(a -> NovaTypeSupport.argument(a, 0, Entity.class).getPistonMoveReaction()));
    }
}
