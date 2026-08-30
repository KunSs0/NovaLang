package com.novalang.bukkit.types.event;

import com.novalang.bukkit.types.value.NovaTypeSupport;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** 常用实体事件的 Spigot 1.12.2 别名。 */
public final class NovaEntityEvent {
    private NovaEntityEvent() { }

    public static void register(JavaTypes.Builder b) {
        JavaTypeRef nullableEntity = JavaTypeRef.javaType(Entity.class).nullable();
        b.extension(EntityEvent.class, "entity", f -> f.returns(Entity.class).invoke(a -> entityEvent(a).getEntity()));
        b.extension(EntityEvent.class, "entityType", f -> f.returns(org.bukkit.entity.EntityType.class).invoke(a -> entityEvent(a).getEntityType()));
        b.extension(EntityDamageEvent.class, "cause", f -> f.returns(EntityDamageEvent.DamageCause.class).invoke(a -> damage(a).getCause()));
        b.extension(EntityDamageEvent.class, "damage", f -> f.returns(Double.class).invoke(a -> damage(a).getDamage()));
        b.extension(EntityDamageEvent.class, "setDamage", f -> f.param("damage", Double.class).returns(Void.TYPE).invoke(a -> { damage(a).setDamage(arg(a, 1, Double.class)); return null; }));
        b.extension(EntityDamageEvent.class, "finalDamage", f -> f.returns(Double.class).invoke(a -> damage(a).getFinalDamage()));
        b.extension(EntityDamageByEntityEvent.class, "damager", f -> f.returns(Entity.class).invoke(a -> byEntity(a).getDamager()));
        b.extension(EntityDamageByBlockEvent.class, "damager", f -> f.returns(JavaTypeRef.javaType(Block.class).nullable()).invoke(a -> byBlock(a).getDamager()));
        b.extension(EntityDeathEvent.class, "entity", f -> f.returns(LivingEntity.class).invoke(a -> death(a).getEntity()));
        b.extension(EntityDeathEvent.class, "drops", f -> f.returns(JavaTypeRef.listOf(JavaTypeRef.javaType(ItemStack.class))).invoke(a -> death(a).getDrops()));
        b.extension(EntityDeathEvent.class, "droppedExp", f -> f.returns(Integer.class).invoke(a -> death(a).getDroppedExp()));
        b.extension(EntityDeathEvent.class, "setDroppedExp", f -> f.param("experience", Integer.class).returns(Void.TYPE).invoke(a -> { death(a).setDroppedExp(arg(a, 1, Integer.class)); return null; }));
        b.extension(EntityExplodeEvent.class, "blockList", f -> f.returns(JavaTypeRef.listOf(JavaTypeRef.javaType(Block.class))).invoke(a -> explode(a).blockList()));
        b.extension(EntityExplodeEvent.class, "location", f -> f.returns(Location.class).invoke(a -> explode(a).getLocation()));
        b.extension(EntityExplodeEvent.class, "yield", f -> f.returns(Float.class).invoke(a -> explode(a).getYield()));
        b.extension(EntityExplodeEvent.class, "setYield", f -> f.param("yield", Float.class).returns(Void.TYPE).invoke(a -> { explode(a).setYield(arg(a, 1, Float.class)); return null; }));
        b.extension(EntityTargetEvent.class, "target", f -> f.returns(nullableEntity).invoke(a -> target(a).getTarget()));
        b.extension(EntityTargetEvent.class, "setTarget", f -> f.param("target", nullableEntity).returns(Void.TYPE).invoke(a -> { target(a).setTarget(arg(a, 1, Entity.class)); return null; }));
        b.extension(EntityTargetEvent.class, "reason", f -> f.returns(EntityTargetEvent.TargetReason.class).invoke(a -> target(a).getReason()));
        b.extension(ProjectileHitEvent.class, "projectile", f -> f.returns(Projectile.class).invoke(a -> hit(a).getEntity()));
        b.extension(ProjectileHitEvent.class, "hitBlock", f -> f.returns(JavaTypeRef.javaType(Block.class).nullable()).invoke(a -> hit(a).getHitBlock()));
        b.extension(ProjectileHitEvent.class, "hitEntity", f -> f.returns(nullableEntity).invoke(a -> hit(a).getHitEntity()));
    }

    private static EntityEvent entityEvent(Object[] a) { return NovaTypeSupport.argument(a, 0, EntityEvent.class); }
    private static EntityDamageEvent damage(Object[] a) { return NovaTypeSupport.argument(a, 0, EntityDamageEvent.class); }
    private static EntityDamageByEntityEvent byEntity(Object[] a) { return NovaTypeSupport.argument(a, 0, EntityDamageByEntityEvent.class); }
    private static EntityDamageByBlockEvent byBlock(Object[] a) { return NovaTypeSupport.argument(a, 0, EntityDamageByBlockEvent.class); }
    private static EntityDeathEvent death(Object[] a) { return NovaTypeSupport.argument(a, 0, EntityDeathEvent.class); }
    private static EntityExplodeEvent explode(Object[] a) { return NovaTypeSupport.argument(a, 0, EntityExplodeEvent.class); }
    private static EntityTargetEvent target(Object[] a) { return NovaTypeSupport.argument(a, 0, EntityTargetEvent.class); }
    private static ProjectileHitEvent hit(Object[] a) { return NovaTypeSupport.argument(a, 0, ProjectileHitEvent.class); }
    private static <T> T arg(Object[] a, int i, Class<T> type) { return NovaTypeSupport.argument(a, i, type); }
}
