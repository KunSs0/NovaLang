package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.types.value.NovaTypeSupport;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Fireball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

/** Spigot 1.12.2 中 ArmorStand、Arrow、Fireball 的常用别名。 */
public final class NovaEntityCombat {
    private NovaEntityCombat() { }

    public static void register(JavaTypes.Builder b) {
        JavaTypeRef nullableItem = JavaTypeRef.javaType(ItemStack.class).nullable();
        JavaTypeRef nullableBlock = JavaTypeRef.javaType(Block.class).nullable();
        b.extension(ArmorStand.class, "itemInHand", f -> f.returns(nullableItem).invoke(a -> stand(a).getItemInHand()));
        b.extension(ArmorStand.class, "setItemInHand", f -> f.param("item", nullableItem).returns(Void.TYPE).invoke(a -> { stand(a).setItemInHand(arg(a, 1, ItemStack.class)); return null; }));
        b.extension(ArmorStand.class, "boots", f -> f.returns(nullableItem).invoke(a -> stand(a).getBoots()));
        b.extension(ArmorStand.class, "setBoots", f -> f.param("item", nullableItem).returns(Void.TYPE).invoke(a -> { stand(a).setBoots(arg(a, 1, ItemStack.class)); return null; }));
        b.extension(ArmorStand.class, "leggings", f -> f.returns(nullableItem).invoke(a -> stand(a).getLeggings()));
        b.extension(ArmorStand.class, "setLeggings", f -> f.param("item", nullableItem).returns(Void.TYPE).invoke(a -> { stand(a).setLeggings(arg(a, 1, ItemStack.class)); return null; }));
        b.extension(ArmorStand.class, "chestplate", f -> f.returns(nullableItem).invoke(a -> stand(a).getChestplate()));
        b.extension(ArmorStand.class, "setChestplate", f -> f.param("item", nullableItem).returns(Void.TYPE).invoke(a -> { stand(a).setChestplate(arg(a, 1, ItemStack.class)); return null; }));
        b.extension(ArmorStand.class, "helmet", f -> f.returns(nullableItem).invoke(a -> stand(a).getHelmet()));
        b.extension(ArmorStand.class, "setHelmet", f -> f.param("item", nullableItem).returns(Void.TYPE).invoke(a -> { stand(a).setHelmet(arg(a, 1, ItemStack.class)); return null; }));
        b.extension(ArmorStand.class, "bodyPose", f -> f.returns(EulerAngle.class).invoke(a -> stand(a).getBodyPose()));
        b.extension(ArmorStand.class, "setBodyPose", f -> f.param("pose", EulerAngle.class).returns(Void.TYPE).invoke(a -> { stand(a).setBodyPose(arg(a, 1, EulerAngle.class)); return null; }));
        b.extension(ArmorStand.class, "hasBasePlate", f -> f.returns(Boolean.class).invoke(a -> stand(a).hasBasePlate()));
        b.extension(ArmorStand.class, "setBasePlate", f -> f.param("enabled", Boolean.class).returns(Void.TYPE).invoke(a -> { stand(a).setBasePlate(arg(a, 1, Boolean.class)); return null; }));
        b.extension(ArmorStand.class, "isVisible", f -> f.returns(Boolean.class).invoke(a -> stand(a).isVisible()));
        b.extension(ArmorStand.class, "setVisible", f -> f.param("visible", Boolean.class).returns(Void.TYPE).invoke(a -> { stand(a).setVisible(arg(a, 1, Boolean.class)); return null; }));
        b.extension(ArmorStand.class, "hasArms", f -> f.returns(Boolean.class).invoke(a -> stand(a).hasArms()));
        b.extension(ArmorStand.class, "setArms", f -> f.param("enabled", Boolean.class).returns(Void.TYPE).invoke(a -> { stand(a).setArms(arg(a, 1, Boolean.class)); return null; }));
        b.extension(ArmorStand.class, "isSmall", f -> f.returns(Boolean.class).invoke(a -> stand(a).isSmall()));
        b.extension(ArmorStand.class, "setSmall", f -> f.param("small", Boolean.class).returns(Void.TYPE).invoke(a -> { stand(a).setSmall(arg(a, 1, Boolean.class)); return null; }));
        b.extension(Arrow.class, "knockbackStrength", f -> f.returns(Integer.class).invoke(a -> arrow(a).getKnockbackStrength()));
        b.extension(Arrow.class, "setKnockbackStrength", f -> f.param("strength", Integer.class).returns(Void.TYPE).invoke(a -> { arrow(a).setKnockbackStrength(arg(a, 1, Integer.class)); return null; }));
        b.extension(Arrow.class, "isCritical", f -> f.returns(Boolean.class).invoke(a -> arrow(a).isCritical()));
        b.extension(Arrow.class, "setCritical", f -> f.param("critical", Boolean.class).returns(Void.TYPE).invoke(a -> { arrow(a).setCritical(arg(a, 1, Boolean.class)); return null; }));
        b.extension(Arrow.class, "isInBlock", f -> f.returns(Boolean.class).invoke(a -> arrow(a).isInBlock()));
        b.extension(Arrow.class, "attachedBlock", f -> f.returns(nullableBlock).invoke(a -> arrow(a).getAttachedBlock()));
        b.extension(Fireball.class, "direction", f -> f.returns(Vector.class).invoke(a -> fireball(a).getDirection()));
        b.extension(Fireball.class, "setDirection", f -> f.param("direction", Vector.class).returns(Void.TYPE).invoke(a -> { fireball(a).setDirection(arg(a, 1, Vector.class)); return null; }));
    }

    private static ArmorStand stand(Object[] a) { return NovaTypeSupport.argument(a, 0, ArmorStand.class); }
    private static Arrow arrow(Object[] a) { return NovaTypeSupport.argument(a, 0, Arrow.class); }
    private static Fireball fireball(Object[] a) { return NovaTypeSupport.argument(a, 0, Fireball.class); }
    private static <T> T arg(Object[] a, int i, Class<T> type) { return NovaTypeSupport.argument(a, i, type); }
}
