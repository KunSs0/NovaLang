package com.novalang.bukkit.types.world;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.BlockChangeDelegate;
import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Collection;

/** Spigot 1.12.2 World 的实体、掉落物与树木操作别名。 */
final class NovaWorldEntityOperations {

    private NovaWorldEntityOperations() {
    }

    static void register(JavaTypes.Builder builder) {
        JavaTypeRef entities = JavaTypeRef.listOf(JavaTypeRef.javaType(Entity.class));
        JavaTypeRef livingEntities = JavaTypeRef.listOf(JavaTypeRef.javaType(LivingEntity.class));
        JavaTypeRef players = JavaTypeRef.listOf(JavaTypeRef.javaType(Player.class));
        builder.extension(World.class, "dropItem", function -> function
                .param("location", Location.class)
                .param("item", ItemStack.class)
                .returns(Item.class)
                .invoke(arguments -> world(arguments).dropItem(
                        argument(arguments, 1, Location.class),
                        argument(arguments, 2, ItemStack.class))));
        builder.extension(World.class, "dropItemNaturally", function -> function
                .param("location", Location.class)
                .param("item", ItemStack.class)
                .returns(Item.class)
                .invoke(arguments -> world(arguments).dropItemNaturally(
                        argument(arguments, 1, Location.class),
                        argument(arguments, 2, ItemStack.class))));
        builder.extension(World.class, "spawnArrow", function -> function
                .param("location", Location.class)
                .param("direction", Vector.class)
                .param("speed", Float.class)
                .param("spread", Float.class)
                .returns(Arrow.class)
                .invoke(arguments -> world(arguments).spawnArrow(
                        argument(arguments, 1, Location.class),
                        argument(arguments, 2, Vector.class),
                        argument(arguments, 3, Float.class),
                        argument(arguments, 4, Float.class))));
        registerGenerateTree(builder, TreeType.class);
        registerGenerateTree(builder, String.class);
        builder.extension(World.class, "strikeLightning", function -> function
                .param("location", Location.class)
                .returns(LightningStrike.class)
                .invoke(arguments -> world(arguments).strikeLightning(
                        argument(arguments, 1, Location.class))));
        builder.extension(World.class, "strikeLightningEffect", function -> function
                .param("location", Location.class)
                .returns(LightningStrike.class)
                .invoke(arguments -> world(arguments).strikeLightningEffect(
                        argument(arguments, 1, Location.class))));
        builder.extension(World.class, "entities", function -> function
                .returns(entities)
                .invoke(arguments -> world(arguments).getEntities()));
        builder.extension(World.class, "livingEntities", function -> function
                .returns(livingEntities)
                .invoke(arguments -> world(arguments).getLivingEntities()));
        builder.extension(World.class, "entitiesByClasses", function -> function
                .returns(Collection.class)
                .invoke(arguments -> world(arguments).getEntitiesByClasses()));
        builder.extension(World.class, "players", function -> function
                .returns(players)
                .invoke(arguments -> world(arguments).getPlayers()));
        builder.extension(World.class, "getNearbyEntities", function -> function
                .param("location", Location.class)
                .param("x", Double.class)
                .param("y", Double.class)
                .param("z", Double.class)
                .returns(Collection.class)
                .invoke(arguments -> world(arguments).getNearbyEntities(
                        argument(arguments, 1, Location.class),
                        argument(arguments, 2, Double.class),
                        argument(arguments, 3, Double.class),
                        argument(arguments, 4, Double.class))));
    }

    private static void registerGenerateTree(JavaTypes.Builder builder, Class<?> type) {
        builder.extension(World.class, "generateTree", function -> function
                .param("location", Location.class)
                .param("type", type)
                .returns(Boolean.class)
                .invoke(arguments -> world(arguments).generateTree(
                        argument(arguments, 1, Location.class),
                        treeType(arguments, 2, type))));
        builder.extension(World.class, "generateTree", function -> function
                .param("location", Location.class)
                .param("type", type)
                .param("delegate", BlockChangeDelegate.class)
                .returns(Boolean.class)
                .invoke(arguments -> world(arguments).generateTree(
                        argument(arguments, 1, Location.class),
                        treeType(arguments, 2, type),
                        argument(arguments, 3, BlockChangeDelegate.class))));
    }

    private static TreeType treeType(Object[] arguments, int index, Class<?> type) {
        if (type == String.class) {
            return NovaTypeSupport.findEnum(TreeType.class, argument(arguments, index, String.class));
        }
        return argument(arguments, index, TreeType.class);
    }

    private static World world(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, World.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
