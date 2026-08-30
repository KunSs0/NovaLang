package com.novalang.bukkit;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.TreeSpecies;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;

/** Spigot 1.12.2 中常用掉落物、载具和特殊实体别名。 */
final class NovaEntityObjects {
    private NovaEntityObjects() { }

    static void register(JavaTypes.Builder b) {
        JavaTypeRef nullableItem = JavaTypeRef.javaType(ItemStack.class).nullable();
        JavaTypeRef nullableLocation = JavaTypeRef.javaType(Location.class).nullable();
        b.extension(Boat.class, "woodType", f -> f.returns(TreeSpecies.class).invoke(a -> boat(a).getWoodType()));
        b.extension(Boat.class, "setWoodType", f -> f.param("type", TreeSpecies.class).returns(Void.TYPE).invoke(a -> { boat(a).setWoodType(arg(a, 1, TreeSpecies.class)); return null; }));
        b.extension(Boat.class, "maxSpeed", f -> f.returns(Double.class).invoke(a -> boat(a).getMaxSpeed()));
        b.extension(Boat.class, "setMaxSpeed", f -> f.param("speed", Double.class).returns(Void.TYPE).invoke(a -> { boat(a).setMaxSpeed(arg(a, 1, Double.class)); return null; }));
        b.extension(Boat.class, "occupiedDeceleration", f -> f.returns(Double.class).invoke(a -> boat(a).getOccupiedDeceleration()));
        b.extension(Boat.class, "setOccupiedDeceleration", f -> f.param("deceleration", Double.class).returns(Void.TYPE).invoke(a -> { boat(a).setOccupiedDeceleration(arg(a, 1, Double.class)); return null; }));
        b.extension(Boat.class, "unoccupiedDeceleration", f -> f.returns(Double.class).invoke(a -> boat(a).getUnoccupiedDeceleration()));
        b.extension(Boat.class, "setUnoccupiedDeceleration", f -> f.param("deceleration", Double.class).returns(Void.TYPE).invoke(a -> { boat(a).setUnoccupiedDeceleration(arg(a, 1, Double.class)); return null; }));
        b.extension(Boat.class, "workOnLand", f -> f.returns(Boolean.class).invoke(a -> boat(a).getWorkOnLand()));
        b.extension(Boat.class, "setWorkOnLand", f -> f.param("enabled", Boolean.class).returns(Void.TYPE).invoke(a -> { boat(a).setWorkOnLand(arg(a, 1, Boolean.class)); return null; }));
        b.extension(ExperienceOrb.class, "experience", f -> f.returns(Integer.class).invoke(a -> orb(a).getExperience()));
        b.extension(ExperienceOrb.class, "setExperience", f -> f.param("experience", Integer.class).returns(Void.TYPE).invoke(a -> { orb(a).setExperience(arg(a, 1, Integer.class)); return null; }));
        b.extension(FallingBlock.class, "material", f -> f.returns(Material.class).invoke(a -> falling(a).getMaterial()));
        b.extension(FallingBlock.class, "blockId", f -> f.returns(Integer.class).invoke(a -> falling(a).getBlockId()));
        b.extension(FallingBlock.class, "blockData", f -> f.returns(Byte.class).invoke(a -> falling(a).getBlockData()));
        b.extension(FallingBlock.class, "dropItem", f -> f.returns(Boolean.class).invoke(a -> falling(a).getDropItem()));
        b.extension(FallingBlock.class, "setDropItem", f -> f.param("drop", Boolean.class).returns(Void.TYPE).invoke(a -> { falling(a).setDropItem(arg(a, 1, Boolean.class)); return null; }));
        b.extension(FallingBlock.class, "canHurtEntities", f -> f.returns(Boolean.class).invoke(a -> falling(a).canHurtEntities()));
        b.extension(FallingBlock.class, "setHurtEntities", f -> f.param("hurt", Boolean.class).returns(Void.TYPE).invoke(a -> { falling(a).setHurtEntities(arg(a, 1, Boolean.class)); return null; }));
        b.extension(Item.class, "itemStack", f -> f.returns(ItemStack.class).invoke(a -> item(a).getItemStack()));
        b.extension(Item.class, "setItemStack", f -> f.param("item", ItemStack.class).returns(Void.TYPE).invoke(a -> { item(a).setItemStack(arg(a, 1, ItemStack.class)); return null; }));
        b.extension(Item.class, "pickupDelay", f -> f.returns(Integer.class).invoke(a -> item(a).getPickupDelay()));
        b.extension(Item.class, "setPickupDelay", f -> f.param("delay", Integer.class).returns(Void.TYPE).invoke(a -> { item(a).setPickupDelay(arg(a, 1, Integer.class)); return null; }));
        b.extension(ItemFrame.class, "item", f -> f.returns(nullableItem).invoke(a -> frame(a).getItem()));
        b.extension(ItemFrame.class, "setItem", f -> f.param("item", nullableItem).returns(Void.TYPE).invoke(a -> { frame(a).setItem(arg(a, 1, ItemStack.class)); return null; }));
        b.extension(ItemFrame.class, "rotation", f -> f.returns(Rotation.class).invoke(a -> frame(a).getRotation()));
        b.extension(ItemFrame.class, "setRotation", f -> f.param("rotation", Rotation.class).returns(Void.TYPE).invoke(a -> { frame(a).setRotation(arg(a, 1, Rotation.class)); return null; }));
        b.extension(EnderCrystal.class, "isShowingBottom", f -> f.returns(Boolean.class).invoke(a -> crystal(a).isShowingBottom()));
        b.extension(EnderCrystal.class, "setShowingBottom", f -> f.param("show", Boolean.class).returns(Void.TYPE).invoke(a -> { crystal(a).setShowingBottom(arg(a, 1, Boolean.class)); return null; }));
        b.extension(EnderCrystal.class, "beamTarget", f -> f.returns(nullableLocation).invoke(a -> crystal(a).getBeamTarget()));
        b.extension(EnderCrystal.class, "setBeamTarget", f -> f.param("location", nullableLocation).returns(Void.TYPE).invoke(a -> { crystal(a).setBeamTarget(arg(a, 1, Location.class)); return null; }));
    }

    private static Boat boat(Object[] a) { return NovaTypeSupport.argument(a, 0, Boat.class); }
    private static ExperienceOrb orb(Object[] a) { return NovaTypeSupport.argument(a, 0, ExperienceOrb.class); }
    private static FallingBlock falling(Object[] a) { return NovaTypeSupport.argument(a, 0, FallingBlock.class); }
    private static Item item(Object[] a) { return NovaTypeSupport.argument(a, 0, Item.class); }
    private static ItemFrame frame(Object[] a) { return NovaTypeSupport.argument(a, 0, ItemFrame.class); }
    private static EnderCrystal crystal(Object[] a) { return NovaTypeSupport.argument(a, 0, EnderCrystal.class); }
    private static <T> T arg(Object[] a, int i, Class<T> type) { return NovaTypeSupport.argument(a, i, type); }
}
