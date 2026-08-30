package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.types.value.NovaTypeSupport;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.Set;

/** LivingEntity 在 Spigot 1.12.2 中的 Fluxon 别名。 */
final class NovaLivingEntity {

    private NovaLivingEntity() {
    }

    static void register(JavaTypes.Builder builder) {
        JavaTypeRef blocks = JavaTypeRef.setOf(JavaTypeRef.javaType(Material.class));
        JavaTypeRef entity = JavaTypeRef.javaType(Entity.class);
        JavaTypeRef effect = JavaTypeRef.javaType(PotionEffect.class);
        JavaTypeRef effectType = JavaTypeRef.javaType(PotionEffectType.class);
        builder.extension(LivingEntity.class, "hasPotion", f -> f.returns(Boolean.class).invoke(a -> !NovaTypeSupport.argument(a, 0, LivingEntity.class).getActivePotionEffects().isEmpty()));
        builder.extension(LivingEntity.class, "eyeHeight", f -> f.returns(Double.class).invoke(a -> NovaTypeSupport.argument(a, 0, LivingEntity.class).getEyeHeight()));
        builder.extension(LivingEntity.class, "getEyeHeight", f -> f.param("ignorePose", Boolean.class).returns(Double.class).invoke(a -> NovaTypeSupport.argument(a, 0, LivingEntity.class).getEyeHeight(NovaTypeSupport.argument(a, 1, Boolean.class))));
        builder.extension(LivingEntity.class, "eyeLocation", f -> f.returns(Location.class).invoke(a -> NovaTypeSupport.argument(a, 0, LivingEntity.class).getEyeLocation()));
        builder.extension(LivingEntity.class, "getLineOfSight", f -> f.param("transparent", blocks).param("maxDistance", Integer.class).returns(JavaTypeRef.listOf(JavaTypeRef.javaType(Block.class))).invoke(a -> NovaTypeSupport.argument(a, 0, LivingEntity.class).getLineOfSight(NovaTypeSupport.argument(a, 1, Set.class), NovaTypeSupport.argument(a, 2, Integer.class))));
        builder.extension(LivingEntity.class, "getTargetBlock", f -> f.param("transparent", blocks).param("maxDistance", Integer.class).returns(Block.class).invoke(a -> NovaTypeSupport.argument(a, 0, LivingEntity.class).getTargetBlock(NovaTypeSupport.argument(a, 1, Set.class), NovaTypeSupport.argument(a, 2, Integer.class))));
        builder.extension(LivingEntity.class, "getLastTwoTargetBlocks", f -> f.param("transparent", blocks).param("maxDistance", Integer.class).returns(JavaTypeRef.listOf(JavaTypeRef.javaType(Block.class))).invoke(a -> NovaTypeSupport.argument(a, 0, LivingEntity.class).getLastTwoTargetBlocks(NovaTypeSupport.argument(a, 1, Set.class), NovaTypeSupport.argument(a, 2, Integer.class))));
        builder.extension(LivingEntity.class, "remainingAir", f -> f.returns(Integer.class).invoke(a -> NovaTypeSupport.argument(a, 0, LivingEntity.class).getRemainingAir()));
        builder.extension(LivingEntity.class, "setRemainingAir", f -> f.param("air", Integer.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, LivingEntity.class).setRemainingAir(NovaTypeSupport.argument(a, 1, Integer.class)); return null; }));
        builder.extension(LivingEntity.class, "maximumAir", f -> f.returns(Integer.class).invoke(a -> NovaTypeSupport.argument(a, 0, LivingEntity.class).getMaximumAir()));
        builder.extension(LivingEntity.class, "setMaximumAir", f -> f.param("air", Integer.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, LivingEntity.class).setMaximumAir(NovaTypeSupport.argument(a, 1, Integer.class)); return null; }));
        builder.extension(LivingEntity.class, "maximumNoDamageTicks", f -> f.returns(Integer.class).invoke(a -> NovaTypeSupport.argument(a, 0, LivingEntity.class).getMaximumNoDamageTicks()));
        builder.extension(LivingEntity.class, "setMaximumNoDamageTicks", f -> f.param("ticks", Integer.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, LivingEntity.class).setMaximumNoDamageTicks(NovaTypeSupport.argument(a, 1, Integer.class)); return null; }));
        builder.extension(LivingEntity.class, "lastDamage", f -> f.returns(Double.class).invoke(a -> NovaTypeSupport.argument(a, 0, LivingEntity.class).getLastDamage()));
        builder.extension(LivingEntity.class, "setLastDamage", f -> f.param("damage", Double.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, LivingEntity.class).setLastDamage(NovaTypeSupport.argument(a, 1, Double.class)); return null; }));
        builder.extension(LivingEntity.class, "noDamageTicks", f -> f.returns(Integer.class).invoke(a -> NovaTypeSupport.argument(a, 0, LivingEntity.class).getNoDamageTicks()));
        builder.extension(LivingEntity.class, "setNoDamageTicks", f -> f.param("ticks", Integer.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, LivingEntity.class).setNoDamageTicks(NovaTypeSupport.argument(a, 1, Integer.class)); return null; }));
        builder.extension(LivingEntity.class, "killer", f -> f.returns(JavaTypeRef.javaType(Player.class).nullable()).invoke(a -> NovaTypeSupport.argument(a, 0, LivingEntity.class).getKiller()));
        builder.extension(LivingEntity.class, "addPotionEffect", f -> f.param("effect", effect).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, LivingEntity.class).addPotionEffect(NovaTypeSupport.argument(a, 1, PotionEffect.class))));
        builder.extension(LivingEntity.class, "addPotionEffect", f -> f.param("effect", effect).param("force", Boolean.class).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, LivingEntity.class).addPotionEffect(NovaTypeSupport.argument(a, 1, PotionEffect.class), NovaTypeSupport.argument(a, 2, Boolean.class))));
        builder.extension(LivingEntity.class, "addPotionEffects", f -> f.param("effects", Collection.class).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, LivingEntity.class).addPotionEffects(NovaTypeSupport.argument(a, 1, Collection.class))));
        builder.extension(LivingEntity.class, "hasPotionEffect", f -> f.param("type", effectType).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, LivingEntity.class).hasPotionEffect(NovaTypeSupport.argument(a, 1, PotionEffectType.class))));
        builder.extension(LivingEntity.class, "getPotionEffect", f -> f.param("type", effectType).returns(JavaTypeRef.javaType(PotionEffect.class).nullable()).invoke(a -> NovaTypeSupport.argument(a, 0, LivingEntity.class).getPotionEffect(NovaTypeSupport.argument(a, 1, PotionEffectType.class))));
        builder.extension(LivingEntity.class, "removePotionEffect", f -> f.param("type", effectType).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, LivingEntity.class).removePotionEffect(NovaTypeSupport.argument(a, 1, PotionEffectType.class)); return null; }));
        builder.extension(LivingEntity.class, "activePotionEffects", f -> f.returns(Collection.class).invoke(a -> NovaTypeSupport.argument(a, 0, LivingEntity.class).getActivePotionEffects()));
        builder.extension(LivingEntity.class, "hasLineOfSight", f -> f.param("entity", entity).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, LivingEntity.class).hasLineOfSight(NovaTypeSupport.argument(a, 1, Entity.class))));
        builder.extension(LivingEntity.class, "removeWhenFarAway", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, LivingEntity.class).getRemoveWhenFarAway()));
        builder.extension(LivingEntity.class, "setRemoveWhenFarAway", f -> f.param("remove", Boolean.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, LivingEntity.class).setRemoveWhenFarAway(NovaTypeSupport.argument(a, 1, Boolean.class)); return null; }));
        builder.extension(LivingEntity.class, "equipment", f -> f.returns(EntityEquipment.class).invoke(a -> NovaTypeSupport.argument(a, 0, LivingEntity.class).getEquipment()));
        builder.extension(LivingEntity.class, "setCanPickupItems", f -> f.param("canPickup", Boolean.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, LivingEntity.class).setCanPickupItems(NovaTypeSupport.argument(a, 1, Boolean.class)); return null; }));
        builder.extension(LivingEntity.class, "canPickupItems", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, LivingEntity.class).getCanPickupItems()));
        builder.extension(LivingEntity.class, "isLeashed", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, LivingEntity.class).isLeashed()));
        builder.extension(LivingEntity.class, "leashHolder", f -> f.returns(JavaTypeRef.javaType(Entity.class).nullable()).invoke(a -> NovaTypeSupport.argument(a, 0, LivingEntity.class).getLeashHolder()));
        builder.extension(LivingEntity.class, "setLeashHolder", f -> f.param("holder", entity).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, LivingEntity.class).setLeashHolder(NovaTypeSupport.argument(a, 1, Entity.class))));
        builder.extension(LivingEntity.class, "isGliding", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, LivingEntity.class).isGliding()));
        builder.extension(LivingEntity.class, "setGliding", f -> f.param("gliding", Boolean.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, LivingEntity.class).setGliding(NovaTypeSupport.argument(a, 1, Boolean.class)); return null; }));
        builder.extension(LivingEntity.class, "setAI", f -> f.param("ai", Boolean.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, LivingEntity.class).setAI(NovaTypeSupport.argument(a, 1, Boolean.class)); return null; }));
        builder.extension(LivingEntity.class, "hasAI", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, LivingEntity.class).hasAI()));
        builder.extension(LivingEntity.class, "setCollidable", f -> f.param("collidable", Boolean.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, LivingEntity.class).setCollidable(NovaTypeSupport.argument(a, 1, Boolean.class)); return null; }));
        builder.extension(LivingEntity.class, "isCollidable", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, LivingEntity.class).isCollidable()));
    }
}
