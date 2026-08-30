package com.novalang.bukkit.types.enums;

import com.novalang.bukkit.types.value.NovaTypeSupport;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Art;
import org.bukkit.ChatColor;
import org.bukkit.CoalType;
import org.bukkit.CropState;
import org.bukkit.Difficulty;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.GrassSpecies;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.NetherWartsState;
import org.bukkit.Particle;
import org.bukkit.PortalType;
import org.bukkit.Rotation;
import org.bukkit.SandstoneType;
import org.bukkit.SkullType;
import org.bukkit.SoundCategory;
import org.bukkit.TreeSpecies;
import org.bukkit.TreeType;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.DragType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.MainHand;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginLoadOrder;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.potion.PotionType;
import org.bukkit.scoreboard.DisplaySlot;

/** Spigot 1.12.2 基础枚举全局查询入口。 */
public final class NovaEnum {

    private NovaEnum() {
    }

    public static void register(JavaTypes.Builder builder) {
        registerEnum(builder, "action", Action.class);
        registerEnum(builder, "art", Art.class);
        registerEnum(builder, "biome", Biome.class);
        registerEnum(builder, "blockFace", BlockFace.class);
        registerEnum(builder, "chatColor", ChatColor.class);
        registerEnum(builder, "clickType", ClickType.class);
        registerEnum(builder, "coalType", CoalType.class);
        registerEnum(builder, "creatureSpawnEventSpawnReason", CreatureSpawnEvent.SpawnReason.class);
        registerEnum(builder, "cropState", CropState.class);
        registerEnum(builder, "difficulty", Difficulty.class);
        registerEnum(builder, "displaySlot", DisplaySlot.class);
        registerEnum(builder, "dragType", DragType.class);
        registerEnum(builder, "dyeColor", DyeColor.class);
        registerEnum(builder, "effect", Effect.class);
        registerEnum(builder, "entityDamageEventDamageCause", EntityDamageEvent.DamageCause.class);
        registerEnum(builder, "entityEffect", EntityEffect.class);
        registerEnum(builder, "entityTargetEventTargetReason", EntityTargetEvent.TargetReason.class);
        registerEnum(builder, "entityType", EntityType.class);
        registerEnum(builder, "equipmentSlot", EquipmentSlot.class);
        registerEnum(builder, "eventPriority", EventPriority.class);
        registerEnum(builder, "gameMode", GameMode.class);
        registerEnum(builder, "grassSpecies", GrassSpecies.class);
        registerEnum(builder, "instrument", Instrument.class);
        registerEnum(builder, "inventoryAction", InventoryAction.class);
        registerEnum(builder, "inventoryType", InventoryType.class);
        registerEnum(builder, "inventoryTypeSlotType", InventoryType.SlotType.class);
        registerEnum(builder, "itemFlag", ItemFlag.class);
        registerEnum(builder, "mainHand", MainHand.class);
        registerEnum(builder, "material", Material.class);
        registerEnum(builder, "netherWartsState", NetherWartsState.class);
        registerEnum(builder, "particle", Particle.class);
        registerEnum(builder, "permissionDefault", PermissionDefault.class);
        registerEnum(builder, "pistonMoveReaction", PistonMoveReaction.class);
        registerEnum(builder, "playerAnimationType", PlayerAnimationType.class);
        registerEnum(builder, "playerFishEventState", PlayerFishEvent.State.class);
        registerEnum(builder, "playerLoginEventResult", PlayerLoginEvent.Result.class);
        registerEnum(builder, "playerTeleportEventTeleportCause", PlayerTeleportEvent.TeleportCause.class);
        registerEnum(builder, "pluginLoadOrder", PluginLoadOrder.class);
        registerEnum(builder, "portalType", PortalType.class);
        registerEnum(builder, "potionType", PotionType.class);
        registerEnum(builder, "rotation", Rotation.class);
        registerEnum(builder, "sandstoneType", SandstoneType.class);
        registerEnum(builder, "servicePriority", ServicePriority.class);
        registerEnum(builder, "skullType", SkullType.class);
        registerEnum(builder, "soundCategory", SoundCategory.class);
        registerEnum(builder, "treeSpecies", TreeSpecies.class);
        registerEnum(builder, "treeType", TreeType.class);
        registerEnum(builder, "weatherType", WeatherType.class);
        registerEnum(builder, "worldEnvironment", World.Environment.class);
        registerEnum(builder, "worldType", WorldType.class);
    }

    private static <E extends Enum<E>> void registerEnum(JavaTypes.Builder builder,
                                                          String functionName,
                                                          Class<E> enumClass) {
        builder.globalFunction(functionName, function -> function
                .param("name", String.class)
                .returns(JavaTypeRef.javaType(enumClass).nullable())
                .invoke1(String.class, value -> NovaTypeSupport.findEnum(enumClass, value)));
    }
}
