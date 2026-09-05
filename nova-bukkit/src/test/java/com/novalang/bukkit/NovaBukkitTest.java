package com.novalang.bukkit;

import com.novalang.runtime.Nova;
import com.novalang.runtime.host.JavaExtensionDescriptor;
import com.novalang.runtime.host.JavaExtensionPropertyDescriptor;
import com.novalang.runtime.host.JavaFunctionDescriptor;
import com.novalang.runtime.host.JavaNamespaceDescriptor;
import com.novalang.runtime.host.JavaParameterDescriptor;
import com.novalang.runtime.host.JavaSymbolDescriptor;
import com.novalang.runtime.host.JavaTypes;
import com.novalang.runtime.interpreter.ModuleLoader;
import org.bukkit.Color;
import org.bukkit.CoalType;
import org.bukkit.CropState;
import org.bukkit.Difficulty;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.Instrument;
import org.bukkit.GameMode;
import org.bukkit.GrassSpecies;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Nameable;
import org.bukkit.Note;
import org.bukkit.Server;
import org.bukkit.Statistic;
import org.bukkit.Rotation;
import org.bukkit.SandstoneType;
import org.bukkit.TreeSpecies;
import org.bukkit.WorldType;
import org.bukkit.UnsafeValues;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.boss.BossBar;
import org.bukkit.util.EulerAngle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConfigurationOptions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.block.Container;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Beacon;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Furnace;
import org.bukkit.block.Jukebox;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.BlockPistonEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Nova Bukkit 类型资源")
@SuppressWarnings("deprecation")
class NovaBukkitTest {

    @Test
    @DisplayName("Bukkit 门面注册并注销共享逻辑模块")
    void shouldManageSharedModuleThroughBukkitFacade() {
        String moduleId = "test.bukkit.shared";
        try {
            NovaBukkit.registerModule(moduleId,
                    java.util.Collections.<Class<?>>singletonList(String.class));

            assertEquals("import java java.lang.String\n",
                    ModuleLoader.sharedModuleSnapshot().get(moduleId));
            assertTrue(NovaBukkit.unregisterModule(moduleId));
            assertFalse(ModuleLoader.sharedModuleSnapshot().containsKey(moduleId));
        } finally {
            NovaBukkit.unregisterModule(moduleId);
        }
    }

    @Test
    @DisplayName("注册 Fluxon platform-bukkit 对应的核心全局入口")
    void shouldExposeCoreBukkitFunctions() {
        JavaNamespaceDescriptor namespace = NovaBukkit.create().resolveNamespace("default");

        assertEquals(Server.class, returnClass(namespace, "server"));
        assertEquals(Player.class, returnClass(namespace, "player"));
        assertEquals(Villager.Profession.class, returnClass(namespace, "villagerProfession"));
        assertEquals(Location.class, returnClass(namespace, "location"));
        assertEquals(org.bukkit.WorldCreator.class, returnClass(namespace, "worldCreator"));
        assertEquals(4, overloads(namespace, "location").size());
        assertEquals(3, overloads(namespace, "color").size());
    }

    @Test
    @DisplayName("缺失 Requires 类的注册器不会安装契约")
    void shouldSkipRegistrarWithMissingRequiredClass() {
        boolean[] invoked = new boolean[]{false};
        JavaTypes.Builder builder = JavaTypes.builder();

        NovaBukkitRegistrar.register(builder, MissingRequiredRegistrar.class,
                ignored -> invoked[0] = true);

        assertFalse(invoked[0]);
        assertFalse(NovaBukkitRegistrar.isSatisfied(MissingRequiredRegistrar.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(MissingRequiredMethodRegistrar.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(
                com.novalang.bukkit.types.platform.NovaDragonBattle.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(
                com.novalang.bukkit.types.world.NovaBlockDataAccess.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(
                com.novalang.bukkit.types.world.NovaBlockData.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(
                com.novalang.bukkit.types.world.NovaBlockDirectional.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(
                com.novalang.bukkit.types.world.NovaBlockOpenable.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(
                com.novalang.bukkit.types.world.NovaBlockPowerable.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(
                com.novalang.bukkit.types.world.NovaBlockWaterlogged.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(
                com.novalang.bukkit.types.world.NovaBlockAgeable.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(
                com.novalang.bukkit.types.world.NovaBlockBisected.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(
                com.novalang.bukkit.types.world.NovaBlockLevelled.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(
                com.novalang.bukkit.types.world.NovaBlockOrientable.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(
                com.novalang.bukkit.types.world.NovaBlockRotatable.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(
                com.novalang.bukkit.types.world.NovaBlockLightable.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(
                com.novalang.bukkit.types.world.NovaBlockMultipleFacing.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(
                com.novalang.bukkit.types.world.NovaBlockAnaloguePowerable.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(
                com.novalang.bukkit.types.world.NovaBlockHatchable.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(
                com.novalang.bukkit.types.world.NovaBlockBrushable.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(
                com.novalang.bukkit.types.world.NovaBlockAttachable.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(
                com.novalang.bukkit.types.world.NovaBlockHangable.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(
                com.novalang.bukkit.types.world.NovaBlockSnowable.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(
                com.novalang.bukkit.types.world.NovaBlockFaceAttachable.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(
                com.novalang.bukkit.types.world.NovaBlockRail.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(
                com.novalang.bukkit.types.world.NovaBlockDoor.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(
                com.novalang.bukkit.types.world.NovaBlockStairs.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(
                com.novalang.bukkit.types.world.NovaBlockSlab.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(
                com.novalang.bukkit.types.world.NovaBlockChest.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(
                com.novalang.bukkit.types.world.NovaBlockSwitch.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockBed.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockBell.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockBeehive.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockBamboo.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockCake.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockCampfire.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockCandle.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockSeaPickle.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockRespawnAnchor.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockFarmland.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockSnow.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockScaffolding.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockTNT.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockBubbleColumn.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockDaylightDetector.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockComparator.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockRepeater.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockRedstoneWire.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockTurtleEgg.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockPiston.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockGate.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockTripwire.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockLeaves.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockEndPortalFrame.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockPistonHead.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockJukebox.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockLectern.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockDispenser.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockCommandBlock.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockBrewingStand.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockCaveVinesPlant.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockSapling.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockWall.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockPointedDripstone.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockBigDripleaf.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockSculkCatalyst.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockSculkShrieker.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockPinkPetals.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockChiseledBookshelf.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockJigsaw.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockTechnicalPiston.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockCrafter.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockSculkSensor.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockTrialSpawner.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockVault.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockHopper.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockNote.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockStructureBlock.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockLidded.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockTileState.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockBrushable.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockConduit.class));
        assertTrue(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockStructure.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockEntityStorage.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockSignSide.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockType.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockSpawnerEntry.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockSpawnerEquipment.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.world.NovaBlockSpawnRule.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.inventory.NovaBlockInventoryHolder.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.inventory.NovaInventoryViewModern.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.inventory.NovaChiseledBookshelfInventory.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.inventory.NovaJukeboxInventory.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.inventory.NovaLecternInventory.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.inventory.NovaSmithingInventory.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.inventory.NovaItemType.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.enums.NovaCreativeCategory.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.inventory.NovaCompassMeta.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.inventory.NovaCrossbowMeta.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.inventory.NovaBundleMeta.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.inventory.NovaTropicalFishBucketMeta.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.inventory.NovaAxolotlBucketMeta.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.inventory.NovaOminousBottleMeta.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.inventory.NovaSuspiciousStewMeta.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.inventory.NovaMusicInstrument.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.inventory.NovaMusicInstrumentMeta.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.inventory.NovaDamageable.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.inventory.NovaColorableArmorMeta.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.inventory.NovaArmorTrim.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.inventory.NovaTrimMaterial.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.inventory.NovaTrimPattern.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.inventory.NovaBlockDataMeta.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.inventory.NovaArmorMeta.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.inventory.NovaWritableBookMeta.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.inventory.NovaFoodComponent.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.inventory.NovaToolComponent.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.inventory.NovaCustomItemTagContainer.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.inventory.NovaItemTagAdapterContext.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.inventory.NovaItemTagType.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.inventory.NovaDecoratedPotInventory.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.inventory.NovaEquipmentSlotGroup.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.inventory.NovaItemCraftResult.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.inventory.NovaRecipeChoice.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.inventory.NovaSmithingRecipe.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.inventory.NovaSmithingTransformRecipe.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.inventory.NovaSmithingTrimRecipe.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.inventory.NovaStonecuttingRecipe.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaEntitySnapshot.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaEntityFactory.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaInteraction.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaOminousItemSpawner.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaAllay.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaCamel.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaGoat.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaSniffer.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaFrog.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaAbstractWindCharge.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaBee.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaFox.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaPanda.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaBlockDisplay.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaItemDisplay.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaTextDisplay.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaDisplay.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaDisplayBrightness.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaDisplayBillboard.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaPhantom.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaHoglin.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaPiglin.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaWarden.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaWardenAngerLevel.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaZoglin.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaStrider.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaGlowSquid.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaTadpole.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaWanderingTrader.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaPufferFish.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaHusk.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaCat.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaAxolotl.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaTropicalFish.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaVex.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaCatType.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaAxolotlVariant.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaTropicalFishPattern.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaTurtle.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaMushroomCow.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaMushroomCowVariant.class));
        assertFalse(NovaBukkitRegistrar.isSatisfied(com.novalang.bukkit.types.entity.NovaWitch.class));
        assertTrue(NovaBukkitRegistrar.isSatisfied(PresentRequiredRegistrar.class));
    }

    @Test
    @DisplayName("完整领域注册器不会生成重复扩展签名")
    void shouldExposeExpandedBukkitExtensionsWithoutDuplicates() {
        JavaTypes types = NovaBukkit.create();
        assertEquals(3014, types.extensions().size());
        assertTrue(types.extensionProperties().size() > 100);
        assertTrue(hasProperty(types, Location.class, "x", true));
        assertTrue(hasProperty(types, Player.class, "name", false));
        assertTrue(hasExtension(types, Location.class, "block"));
        assertTrue(hasExtension(types, Location.class, "chunk"));
        assertTrue(hasExtension(types, AsyncPlayerChatEvent.class, "setFormat"));
        assertTrue(hasExtension(types, EntityRegainHealthEvent.class, "setAmount"));
        assertTrue(hasExtension(types, PlayerDeathEvent.class, "setKeepInventory"));
        assertTrue(hasExtension(types, BlockDamageEvent.class, "setInstaBreak"));
        assertTrue(hasExtension(types, BlockMultiPlaceEvent.class, "replacedBlockStates"));
        assertTrue(hasExtension(types, PlayerItemDamageEvent.class, "setDamage"));
        assertTrue(hasExtension(types, PlayerItemBreakEvent.class, "brokenItem"));
        assertTrue(hasExtension(types, PlayerInteractEntityEvent.class, "rightClicked"));
        assertTrue(hasExtension(types, PlayerInteractAtEntityEvent.class, "clickedPosition"));
        assertTrue(hasExtension(types, EulerAngle.class, "add"));
        assertTrue(hasExtension(types, EulerAngle.class, "subtract"));
        assertTrue(hasExtension(types, Color.class, "lerp"));
        assertTrue(hasExtension(types, Color.class, "deserialize"));
        assertTrue(hasExtension(types, NamespacedKey.class, "minecraft"));
        assertTrue(hasExtension(types, Plugin.class, "getDefaultWorldGenerator"));
        assertTrue(hasExtension(types, PluginManager.class, "registerEvent"));
        assertTrue(hasExtension(types, PluginManager.class, "subscribeToPermission"));
        assertTrue(hasExtension(types, PluginManager.class, "permissions"));
        assertTrue(hasExtension(types, org.bukkit.plugin.RegisteredServiceProvider.class, "compareTo"));
        assertTrue(hasExtension(types, PermissionDefault.class, "getValue"));
        assertTrue(hasExtension(types, BossBar.class, "setColor"));
        assertTrue(hasExtension(types, Scoreboard.class, "getScores"));
        assertTrue(hasExtension(types, PotionType.class, "maxLevel"));
        assertTrue(hasExtension(types, org.bukkit.ChunkSnapshot.class, "getData"));
        assertTrue(hasExtension(types, PlayerItemConsumeEvent.class, "setItem"));
        assertTrue(hasExtension(types, PlayerItemHeldEvent.class, "previousSlot"));
        assertTrue(hasExtension(types, PlayerDropItemEvent.class, "itemDrop"));
        assertTrue(hasExtension(types, PlayerPickupItemEvent.class, "remaining"));
        assertTrue(hasExtension(types, SignChangeEvent.class, "setLine"));
        assertTrue(hasExtension(types, BlockIgniteEvent.class, "ignitingEntity"));
        assertTrue(hasExtension(types, BlockGrowEvent.class, "newState"));
        assertTrue(hasExtension(types, BlockSpreadEvent.class, "source"));
        assertTrue(hasExtension(types, BlockPistonEvent.class, "isSticky"));
        assertTrue(hasExtension(types, BlockPistonExtendEvent.class, "blocks"));
        assertTrue(hasExtension(types, BlockPistonRetractEvent.class, "blocks"));
        assertTrue(hasExtension(types, EntityCombustEvent.class, "setDuration"));
        assertTrue(hasExtension(types, EntityTeleportEvent.class, "setTo"));
        assertTrue(hasExtension(types, EntityPortalEvent.class, "setPortalTravelAgent"));
        assertTrue(hasExtension(types, EntityTargetLivingEntityEvent.class, "setTarget"));
        assertTrue(hasExtension(types, EntityPickupItemEvent.class, "remaining"));
        assertTrue(hasExtension(types, CreatureSpawnEvent.class, "spawnReason"));
        assertTrue(hasExtension(types, EntityBreedEvent.class, "setExperience"));
        assertTrue(hasExtension(types, SkullMeta.class, "setOwningPlayer"));
        assertTrue(hasExtension(types, BookMeta.class, "setPages"));
        assertTrue(hasExtension(types, PotionMeta.class, "addCustomEffect"));
        assertTrue(hasExtension(types, FireworkMeta.class, "setPower"));
        assertTrue(hasExtension(types, LeatherArmorMeta.class, "setColor"));
        assertTrue(hasExtension(types, EnchantmentStorageMeta.class, "addStoredEnchant"));
        assertTrue(hasExtension(types, MapMeta.class, "setScaling"));
        assertTrue(hasExtension(types, BannerMeta.class, "setPatterns"));
        assertTrue(hasExtension(types, SpawnEggMeta.class, "setSpawnedType"));
        assertTrue(hasExtension(types, ArmorStand.class, "setHeadPose"));
        assertTrue(hasExtension(types, Villager.class, "setCareer"));
        assertTrue(hasExtension(types, Scoreboard.class, "objectivesByCriteria"));
        assertTrue(hasExtension(types, Scoreboard.class, "clearSlot"));
        assertTrue(hasExtension(types, Objective.class, "unregister"));
        assertTrue(hasExtension(types, Objective.class, "setDisplaySlot"));
        assertTrue(hasExtension(types, Team.class, "setOption"));
        assertTrue(hasExtension(types, PlayerRespawnEvent.class, "setRespawnLocation"));
        assertTrue(hasExtension(types, org.bukkit.command.CommandExecutor.class, "onCommand"));
        assertTrue(hasExtension(types, org.bukkit.command.TabCompleter.class, "onTabComplete"));
        assertTrue(hasExtension(types, org.bukkit.command.CommandMap.class, "registerAll"));
        assertTrue(hasExtension(types, org.bukkit.command.PluginCommand.class, "setExecutor"));
        assertTrue(hasExtension(types, org.bukkit.event.server.BroadcastMessageEvent.class, "recipients"));
        assertTrue(hasExtension(types, org.bukkit.event.server.MapInitializeEvent.class, "map"));
        assertTrue(hasExtension(types, org.bukkit.event.server.PluginEvent.class, "plugin"));
        assertTrue(hasExtension(types, org.bukkit.event.server.ServerCommandEvent.class, "setCommand"));
        assertTrue(hasExtension(types, org.bukkit.event.server.ServerListPingEvent.class, "setServerIcon"));
        assertTrue(hasExtension(types, org.bukkit.event.server.ServiceEvent.class, "provider"));
        assertTrue(hasExtension(types, org.bukkit.event.server.TabCompleteEvent.class, "setCompletions"));
        assertTrue(hasExtension(types, org.bukkit.event.inventory.InventoryClickEvent.class, "setCursor"));
        assertTrue(hasExtension(types, org.bukkit.event.inventory.InventoryMoveItemEvent.class, "setItem"));
        assertTrue(hasExtension(types, org.bukkit.event.inventory.CraftItemEvent.class, "recipe"));
        assertTrue(hasExtension(types, org.bukkit.event.inventory.PrepareItemCraftEvent.class, "isRepair"));
        assertTrue(hasExtension(types, org.bukkit.event.inventory.BrewEvent.class, "fuelLevel"));
        assertTrue(hasExtension(types, org.bukkit.event.inventory.PrepareAnvilEvent.class, "setResult"));
        assertTrue(hasExtension(types, org.bukkit.event.entity.ProjectileLaunchEvent.class, "entity"));
        assertTrue(hasExtension(types, org.bukkit.event.entity.EntityShootBowEvent.class, "setProjectile"));
        assertTrue(hasExtension(types, org.bukkit.event.entity.ExplosionPrimeEvent.class, "setRadius"));
        assertTrue(hasExtension(types, org.bukkit.event.entity.PotionSplashEvent.class, "setIntensity"));
        assertTrue(hasExtension(types, org.bukkit.event.entity.LingeringPotionSplashEvent.class, "areaEffectCloud"));
        assertTrue(hasExtension(types, org.bukkit.event.entity.ItemSpawnEvent.class, "entity"));
        assertTrue(hasExtension(types, org.bukkit.event.player.PlayerLoginEvent.class, "realAddress"));
        assertTrue(hasExtension(types, org.bukkit.event.player.AsyncPlayerPreLoginEvent.class, "setLoginResult"));
        assertTrue(hasExtension(types, org.bukkit.event.player.PlayerSwapHandItemsEvent.class, "setOffHandItem"));
        assertTrue(hasExtension(types, org.bukkit.event.block.BlockPhysicsEvent.class, "changedType"));
        assertTrue(hasExtension(types, org.bukkit.event.block.BlockFadeEvent.class, "newState"));
        assertTrue(hasExtension(types, org.bukkit.event.block.BlockExplodeEvent.class, "setYield"));
        assertTrue(hasExtension(types, org.bukkit.event.block.BlockDispenseEvent.class, "setVelocity"));
        assertTrue(hasExtension(types, org.bukkit.event.block.BlockBurnEvent.class, "ignitingBlock"));
        assertTrue(hasExtension(types, org.bukkit.event.block.NotePlayEvent.class, "setNote"));
        assertTrue(hasExtension(types, org.bukkit.entity.ThrownPotion.class, "setItem"));
        assertTrue(hasExtension(types, org.bukkit.map.MapCanvas.class, "cursors"));
        assertTrue(hasExtension(types, org.bukkit.map.MapCursor.class, "setDirection"));
        assertTrue(hasExtension(types, org.bukkit.map.MapCursor.Type.class, "byValue"));
        assertTrue(hasExtension(types, org.bukkit.event.vehicle.VehicleEvent.class, "vehicle"));
        assertTrue(hasExtension(types, org.bukkit.event.vehicle.VehicleDamageEvent.class, "setDamage"));
        assertTrue(hasExtension(types, org.bukkit.event.vehicle.VehicleEnterEvent.class, "entered"));
        assertTrue(hasExtension(types, org.bukkit.event.vehicle.VehicleMoveEvent.class, "from"));
        assertTrue(hasExtension(types, org.bukkit.event.vehicle.VehicleEntityCollisionEvent.class, "setCollisionCancelled"));
        assertTrue(hasExtension(types, org.bukkit.event.hanging.HangingPlaceEvent.class, "blockFace"));
        assertTrue(hasExtension(types, org.bukkit.event.weather.LightningStrikeEvent.class, "lightning"));
        assertTrue(hasExtension(types, org.bukkit.event.world.StructureGrowEvent.class, "blocks"));
        assertTrue(hasExtension(types, org.bukkit.event.world.ChunkUnloadEvent.class, "setSaveChunk"));
        assertTrue(hasExtension(types, org.bukkit.event.player.PlayerFishEvent.class, "setExpToDrop"));
        assertTrue(hasExtension(types, org.bukkit.event.player.PlayerVelocityEvent.class, "setVelocity"));
        assertTrue(hasExtension(types, org.bukkit.event.block.BlockCanBuildEvent.class, "setBuildable"));
        assertTrue(hasExtension(types, org.bukkit.event.entity.FoodLevelChangeEvent.class, "setFoodLevel"));
        assertTrue(hasExtension(types, org.bukkit.event.entity.SlimeSplitEvent.class, "setCount"));
        assertTrue(hasExtension(types, org.bukkit.event.player.PlayerEditBookEvent.class, "setNewBookMeta"));
        assertTrue(hasExtension(types, org.bukkit.event.player.PlayerEggThrowEvent.class, "setNumHatches"));
        assertTrue(hasExtension(types, org.bukkit.event.enchantment.EnchantItemEvent.class, "setExpLevelCost"));
        assertTrue(hasExtension(types, org.bukkit.event.inventory.FurnaceBurnEvent.class, "setBurning"));
        assertTrue(hasExtension(types, org.bukkit.event.entity.ExpBottleEvent.class, "setExperience"));
        assertTrue(hasExtension(types, org.bukkit.event.entity.CreeperPowerEvent.class, "cause"));
        assertTrue(hasExtension(types, org.bukkit.event.world.PortalCreateEvent.class, "reason"));
        assertTrue(hasExtension(types, org.bukkit.event.entity.EntityPortalExitEvent.class, "setAfter"));
        assertTrue(hasExtension(types, org.bukkit.event.player.PlayerAnimationEvent.class, "animationType"));
        assertTrue(hasExtension(types, org.bukkit.inventory.MerchantRecipe.class, "setMaxUses"));
        assertTrue(hasExtension(types, org.bukkit.inventory.FurnaceInventory.class, "setFuel"));
        assertTrue(hasExtension(types, org.bukkit.inventory.FurnaceRecipe.class, "setInput"));
        assertTrue(hasExtension(types, Location.class, "chunk"));
        assertTrue(hasExtension(types, org.bukkit.entity.AbstractHorse.class, "setJumpStrength"));
        assertTrue(hasExtension(types, org.bukkit.block.Dispenser.class, "dispense"));
        assertTrue(hasExtension(types, org.bukkit.block.Skull.class, "setRotation"));
        assertTrue(hasExtension(types, org.bukkit.plugin.Plugin.class, "defaultWorldGenerator"));
        assertTrue(hasExtension(types, org.bukkit.entity.FishHook.class, "setBiteChance"));
        assertTrue(hasExtension(types, org.bukkit.inventory.AnvilInventory.class, "setRepairCost"));
        assertTrue(hasExtension(types, org.bukkit.inventory.meta.BlockStateMeta.class, "setBlockState"));
        assertTrue(hasExtension(types, org.bukkit.block.Banner.class, "addPattern"));
        assertTrue(hasExtension(types, org.bukkit.block.EndGateway.class, "setExactTeleport"));
        assertTrue(hasExtension(types, org.bukkit.inventory.DoubleChestInventory.class, "leftSide"));
        assertTrue(hasExtension(types, org.bukkit.inventory.AbstractHorseInventory.class, "setSaddle"));
        assertTrue(hasExtension(types, org.bukkit.entity.ChestedHorse.class, "setCarryingChest"));
        assertTrue(hasExtension(types, org.bukkit.inventory.meta.BannerMeta.class, "setBaseColor"));
        assertTrue(hasExtension(types, org.bukkit.conversations.Conversable.class, "beginConversation"));
        assertTrue(hasExtension(types, org.bukkit.conversations.Conversation.class, "getContext"));
        assertTrue(hasExtension(types, org.bukkit.conversations.ConversationContext.class, "setSessionData"));
        assertTrue(hasExtension(types, org.bukkit.conversations.ConversationFactory.class, "buildConversation"));
        assertTrue(hasExtension(types, org.bukkit.conversations.ConversationPrefix.class, "getPrefix"));
        assertTrue(hasExtension(types, org.bukkit.conversations.PluginNameConversationPrefix.class, "getPrefix"));
        assertTrue(hasExtension(types, org.bukkit.conversations.NullConversationPrefix.class, "getPrefix"));
        assertTrue(hasExtension(types, org.bukkit.conversations.ConversationAbandonedEvent.class, "gracefulExit"));
        assertTrue(hasExtension(types, org.bukkit.conversations.ConversationCanceller.class, "cancelBasedOnInput"));
        assertTrue(hasExtension(types, org.bukkit.conversations.ConversationAbandonedListener.class,
                "conversationAbandoned"));
        assertTrue(hasExtension(types, org.bukkit.conversations.Prompt.class, "getPromptText"));
        assertTrue(hasExtension(types, org.bukkit.conversations.MessagePrompt.class, "acceptInput"));
        assertTrue(hasExtension(types, org.bukkit.conversations.StringPrompt.class, "blocksForInput"));
        assertTrue(hasExtension(types, org.bukkit.conversations.ValidatingPrompt.class, "acceptInput"));
        assertTrue(hasExtension(types, org.bukkit.metadata.FixedMetadataValue.class, "value"));
        assertTrue(hasExtension(types, org.bukkit.metadata.LazyMetadataValue.class, "value"));
        assertTrue(hasExtension(types, org.bukkit.metadata.MetadataValueAdapter.class, "owningPlugin"));
        assertTrue(hasExtension(types, org.bukkit.help.HelpTopicFactory.class, "createTopic"));
        assertTrue(hasExtension(types, org.bukkit.help.GenericCommandHelpTopic.class, "canSee"));
        assertTrue(hasExtension(types, org.bukkit.help.IndexHelpTopic.class, "getFullText"));
        assertTrue(hasExtension(types, org.bukkit.map.MapCanvas.class, "drawText"));
        assertTrue(hasExtension(types, org.bukkit.map.MapCursor.class, "setVisible"));
        assertTrue(hasExtension(types, org.bukkit.enchantments.EnchantmentTarget.class, "includes"));
        assertTrue(hasExtension(types, org.bukkit.enchantments.EnchantmentWrapper.class, "enchantment"));
        assertTrue(hasExtension(types, org.bukkit.attribute.Attributable.class, "getAttribute"));
        assertTrue(hasExtension(types, org.bukkit.map.MapFont.class, "getWidth"));
        assertTrue(hasExtension(types, org.bukkit.map.MapRenderer.class, "render"));
        assertTrue(hasExtension(types, org.bukkit.material.Colorable.class, "setColor"));
        assertTrue(hasExtension(types, org.bukkit.material.Openable.class, "setOpen"));
        assertTrue(hasExtension(types, org.bukkit.material.Attachable.class, "attachedFace"));
        assertTrue(hasExtension(types, org.bukkit.material.TexturedMaterial.class, "textures"));
        assertTrue(hasExtension(types, org.bukkit.material.Redstone.class, "isPowered"));
        assertTrue(hasExtension(types, org.bukkit.material.PressureSensor.class, "isPressed"));
        assertTrue(hasExtension(types, org.bukkit.material.FurnaceAndDispenser.class, "clone"));
        assertTrue(hasExtension(types, org.bukkit.material.SimpleAttachableMaterialData.class, "facing"));
        assertTrue(hasExtension(types, org.bukkit.material.PoweredRail.class, "setPowered"));
        assertTrue(hasExtension(types, org.bukkit.material.DetectorRail.class, "setPressed"));
        assertTrue(hasExtension(types, org.bukkit.material.Comparator.class, "isBeingPowered"));
        assertTrue(hasExtension(types, org.bukkit.material.Observer.class, "facing"));
        assertTrue(hasExtension(types, org.bukkit.material.Tripwire.class, "setObjectTriggering"));
        assertTrue(hasExtension(types, org.bukkit.material.TripwireHook.class, "setConnected"));
        assertTrue(hasExtension(types, org.bukkit.material.Button.class, "setFacingDirection"));
        assertTrue(hasExtension(types, org.bukkit.material.Gate.class, "setOpen"));
        assertTrue(hasExtension(types, org.bukkit.material.Hopper.class, "isActive"));
        assertTrue(hasExtension(types, org.bukkit.material.DirectionalContainer.class, "facing"));
        assertTrue(hasExtension(types, org.bukkit.material.ExtendedRails.class, "setDirection"));
        assertTrue(hasExtension(types, org.bukkit.material.Furnace.class, "clone"));
        assertTrue(hasExtension(types, org.bukkit.material.Dispenser.class, "setFacingDirection"));
        assertTrue(hasExtension(types, org.bukkit.material.Door.class, "setHinge"));
        assertTrue(hasExtension(types, org.bukkit.material.TrapDoor.class, "isInverted"));
        assertTrue(hasExtension(types, org.bukkit.material.Stairs.class, "ascendingDirection"));
        assertTrue(hasExtension(types, org.bukkit.material.Step.class, "textures"));
        assertTrue(hasExtension(types, org.bukkit.material.WoodenStep.class, "setInverted"));
        assertTrue(hasExtension(types, org.bukkit.material.Torch.class, "attachedFace"));
        assertTrue(hasExtension(types, org.bukkit.material.RedstoneTorch.class, "isPowered"));
        assertTrue(hasExtension(types, org.bukkit.material.RedstoneWire.class, "clone"));
        assertTrue(hasExtension(types, org.bukkit.material.Diode.class, "toString"));
        assertTrue(hasExtension(types, org.bukkit.material.Leaves.class, "setDecaying"));
        assertTrue(hasExtension(types, org.bukkit.material.Sapling.class, "setIsInstantGrowable"));
        assertTrue(hasExtension(types, org.bukkit.material.Wood.class, "species"));
        assertTrue(hasExtension(types, org.bukkit.material.Tree.class, "direction"));
        assertTrue(hasExtension(types, org.bukkit.material.Vine.class, "putOnFace"));
        assertTrue(hasExtension(types, org.bukkit.material.CocoaPlant.class, "setSize"));
        assertTrue(hasExtension(types, org.bukkit.material.Cake.class, "slicesRemaining"));
        assertTrue(hasExtension(types, org.bukkit.material.Chest.class, "clone"));
        assertTrue(hasExtension(types, org.bukkit.material.EnderChest.class, "clone"));
        assertTrue(hasExtension(types, org.bukkit.material.FlowerPot.class, "contents"));
        assertTrue(hasExtension(types, org.bukkit.material.Ladder.class, "attachedFace"));
        assertTrue(hasExtension(types, org.bukkit.material.Sign.class, "isWallSign"));
        assertTrue(hasExtension(types, org.bukkit.material.Skull.class, "facing"));
        assertTrue(hasExtension(types, org.bukkit.material.Pumpkin.class, "isLit"));
        assertTrue(hasExtension(types, org.bukkit.material.Mushroom.class, "paintedFaces"));
        assertTrue(hasExtension(types, org.bukkit.material.LongGrass.class, "species"));
        assertTrue(hasExtension(types, org.bukkit.material.NetherWarts.class, "setState"));
        assertTrue(hasExtension(types, org.bukkit.material.Wool.class, "setColor"));
        assertTrue(hasExtension(types, org.bukkit.material.Dye.class, "color"));
        assertTrue(hasExtension(types, org.bukkit.material.Coal.class, "type"));
        assertTrue(hasExtension(types, org.bukkit.material.MonsterEggs.class, "textures"));
        assertTrue(hasExtension(types, org.bukkit.material.SpawnEgg.class, "spawnedType"));
        assertTrue(hasExtension(types, org.bukkit.material.SmoothBrick.class, "clone"));
        assertTrue(hasExtension(types, org.bukkit.material.Sandstone.class, "setType"));
        assertTrue(hasExtension(types, org.bukkit.material.Command.class, "isPowered"));
        assertTrue(hasExtension(types, org.bukkit.material.PistonExtensionMaterial.class, "setSticky"));
        assertTrue(hasExtension(types, org.bukkit.material.PressurePlate.class, "clone"));
        assertTrue(hasExtension(types, org.bukkit.material.Rails.class, "toString"));
        assertTrue(hasExtension(types, org.bukkit.material.Bed.class, "setHeadOfBed"));
        assertTrue(hasExtension(types, org.bukkit.material.Banner.class, "isWallBanner"));
        assertTrue(hasExtension(types, org.bukkit.material.Crops.class, "clone"));
        assertTrue(hasExtension(types, org.bukkit.material.Cauldron.class, "toString"));
        assertTrue(hasExtension(types, org.bukkit.material.Lever.class, "clone"));
        assertTrue(hasExtension(types, org.bukkit.material.PistonBaseMaterial.class, "clone"));
        assertTrue(hasExtension(types, org.bukkit.generator.BlockPopulator.class, "populate"));
        assertTrue(hasExtension(types, org.bukkit.entity.Explosive.class, "setYield"));
        assertTrue(hasExtension(types, org.bukkit.entity.EnderDragonPart.class, "parent"));
        assertTrue(hasExtension(types, org.bukkit.projectiles.ProjectileSource.class, "launchProjectile"));
        assertTrue(hasExtension(types, org.bukkit.BanEntry.class, "setExpiration"));
        assertTrue(hasExtension(types, org.bukkit.BanList.class, "addBan"));
        assertTrue(hasExtension(types, org.bukkit.inventory.HorseInventory.class, "setArmor"));
        assertTrue(hasExtension(types, org.bukkit.inventory.EnchantingInventory.class, "secondary"));
        assertTrue(hasExtension(types, org.bukkit.inventory.CraftingInventory.class, "setMatrix"));
        assertTrue(hasExtension(types, org.bukkit.block.Bed.class, "setColor"));
        assertTrue(hasExtension(types, org.bukkit.block.ShulkerBox.class, "color"));
        assertTrue(hasExtension(types, org.bukkit.event.entity.EntityDeathEvent.class, "setDroppedExp"));
        assertTrue(hasExtension(types, org.bukkit.event.entity.EntityExplodeEvent.class, "blockList"));
        assertTrue(hasExtension(types, org.bukkit.entity.EntityType.class, "entityName"));
        assertTrue(hasExtension(types, org.bukkit.Art.class, "getByName"));
        assertEquals(org.bukkit.block.banner.PatternType.class, returnClass(types.resolveNamespace("default"), "patternType"));
        assertEquals(org.bukkit.enchantments.EnchantmentTarget.class, returnClass(types.resolveNamespace("default"), "enchantmentTarget"));
        assertEquals(org.bukkit.Note.Tone.class, returnClass(types.resolveNamespace("default"), "noteTone"));
        assertTrue(hasExtension(types, org.bukkit.entity.Painting.class, "setArt"));
        assertTrue(hasExtension(types, org.bukkit.event.player.PlayerChatEvent.class, "setMessage"));
        assertTrue(hasExtension(types, org.bukkit.inventory.meta.FireworkMeta.class, "addEffects"));
        assertEquals(org.bukkit.material.CocoaPlant.CocoaPlantSize.class,
                returnClass(types.resolveNamespace("default"), "cocoaPlantCocoaPlantSize"));
        assertTrue(hasExtension(types, org.bukkit.entity.Firework.class, "detonate"));
        assertTrue(hasExtension(types, org.bukkit.block.Block.class, "getRelative"));
        assertTrue(hasExtension(types, org.bukkit.block.Block.class, "setType"));
        assertTrue(hasExtension(types, org.bukkit.NamespacedKey.class, "namespace"));
        assertTrue(hasExtension(types, org.bukkit.Keyed.class, "key"));
        assertTrue(hasExtension(types, Nameable.class, "customName"));
        assertTrue(hasExtension(types, UnsafeValues.class, "removeAdvancement"));
        assertTrue(hasExtension(types, Effect.class, "data"));
        assertTrue(hasExtension(types, EntityEffect.class, "data"));
        assertTrue(hasExtension(types, Instrument.class, "type"));
        assertTrue(hasExtension(types, Statistic.class, "isSubstatistic"));
        assertTrue(hasExtension(types, Difficulty.class, "getByValue"));
        assertTrue(hasExtension(types, GameMode.class, "getByValue"));
        assertTrue(hasExtension(types, Rotation.class, "rotateClockwise"));
        assertTrue(hasExtension(types, WorldType.class, "getByName"));
        assertTrue(hasExtension(types, CoalType.class, "getByData"));
        assertTrue(hasExtension(types, CropState.class, "getByData"));
        assertTrue(hasExtension(types, GrassSpecies.class, "getByData"));
        assertTrue(hasExtension(types, SandstoneType.class, "getByData"));
        assertTrue(hasExtension(types, TreeSpecies.class, "getByData"));
        assertTrue(hasExtension(types, org.bukkit.block.PistonMoveReaction.class, "id"));
        assertTrue(hasExtension(types, org.bukkit.event.inventory.ClickType.class, "isShiftClick"));
        assertTrue(hasExtension(types, org.bukkit.scoreboard.Score.class, "player"));
        assertTrue(hasExtension(types, Note.class, "flat"));
        assertTrue(hasExtension(types, Note.Tone.class, "getById"));
        assertTrue(hasExtension(types, YamlConfiguration.class, "loadConfiguration"));
        assertTrue(hasExtension(types, YamlConfigurationOptions.class, "indent"));
        assertEquals(YamlConfigurationOptions.class,
                extensionReturnClass(types, YamlConfiguration.class, "options", 0));
        assertEquals(YamlConfigurationOptions.class,
                extensionReturnClass(types, YamlConfigurationOptions.class, "pathSeparator", 1));
        assertEquals(Effect.Type.class, returnClass(types.resolveNamespace("default"), "effectType"));
        assertTrue(hasExtension(types, org.bukkit.block.BlockFace.class, "oppositeFace"));
        assertTrue(hasExtension(types, org.bukkit.inventory.ItemFactory.class, "asMetaFor"));
        assertTrue(hasExtension(types, org.bukkit.inventory.ShapedRecipe.class, "setIngredient"));
        assertTrue(hasExtension(types, org.bukkit.inventory.ShapelessRecipe.class, "ingredientList"));
        assertTrue(hasExtension(types, org.bukkit.event.block.CauldronLevelChangeEvent.class, "setNewLevel"));
        assertTrue(hasExtension(types, org.bukkit.event.entity.EntityAirChangeEvent.class, "setAmount"));
        assertTrue(hasExtension(types, org.bukkit.event.entity.HorseJumpEvent.class, "setPower"));
        assertTrue(hasExtension(types, org.bukkit.event.entity.PlayerLeashEntityEvent.class, "leashHolder"));
        assertTrue(hasExtension(types, org.bukkit.event.player.PlayerPreLoginEvent.class, "disallow"));
        assertTrue(hasExtension(types, org.bukkit.entity.AreaEffectCloud.class, "addCustomEffect"));
        assertTrue(hasExtension(types, org.bukkit.entity.EnderDragon.class, "setPhase"));
        assertTrue(hasExtension(types, org.bukkit.entity.Evoker.class, "setCurrentSpell"));
        assertTrue(hasExtension(types, org.bukkit.entity.EvokerFangs.class, "setOwner"));
        assertTrue(hasExtension(types, org.bukkit.entity.PigZombie.class, "setAnger"));
        assertTrue(hasExtension(types, org.bukkit.entity.Sittable.class, "setSitting"));
        assertTrue(hasExtension(types, org.bukkit.entity.ComplexLivingEntity.class, "parts"));
        assertTrue(hasExtension(types, org.bukkit.entity.LightningStrike.class, "isEffect"));
        assertTrue(hasExtension(types, org.bukkit.enchantments.EnchantmentTarget.class, "includes"));
        assertTrue(hasExtension(types, org.bukkit.potion.PotionEffectTypeWrapper.class, "type"));
        assertTrue(hasExtension(types, org.bukkit.event.player.PlayerFishEvent.class, "isFishing"));
        assertTrue(hasExtension(types, org.bukkit.event.player.PlayerInteractEvent.class, "isRightClick"));
        assertTrue(hasExtension(types, org.bukkit.event.entity.EntityDamageEvent.class, "getOriginalDamage"));
        assertTrue(hasExtension(types, org.bukkit.event.player.PlayerLoginEvent.class, "setResult"));
        assertTrue(hasExtension(types, org.bukkit.event.entity.EntityDeathEvent.class, "killer"));
        assertTrue(hasExtension(types, org.bukkit.event.block.BlockBreakEvent.class, "isDropItems"));
        assertTrue(hasExtension(types, org.bukkit.event.player.PlayerKickEvent.class, "setReason"));
        assertTrue(hasExtension(types, org.bukkit.event.player.PlayerRespawnEvent.class, "isBedSpawn"));
        assertTrue(hasExtension(types, org.bukkit.entity.Explosive.class, "isIncendiary"));
        assertTrue(hasExtension(types, org.bukkit.entity.Ocelot.Type.class, "getType"));
        assertTrue(hasExtension(types, org.bukkit.entity.Llama.class, "inventory"));
        assertTrue(hasExtension(types, org.bukkit.inventory.LlamaInventory.class, "setDecor"));
        assertTrue(hasExtension(types, org.bukkit.entity.ShulkerBullet.class, "setTarget"));
        assertTrue(hasExtension(types, org.bukkit.entity.Spellcaster.class, "setSpell"));
        assertTrue(hasExtension(types, org.bukkit.entity.Parrot.class, "setVariant"));
        assertTrue(hasExtension(types, org.bukkit.entity.Wolf.class, "collarColor"));
        assertTrue(hasExtension(types, org.bukkit.entity.Rabbit.class, "setRabbitType"));
        assertTrue(hasExtension(types, org.bukkit.entity.Enderman.class, "carriedMaterial"));
        assertTrue(hasExtension(types, org.bukkit.inventory.meta.KnowledgeBookMeta.class, "addRecipe"));
        assertTrue(hasExtension(types, org.bukkit.enchantments.EnchantmentOffer.class, "setCost"));
        assertTrue(hasExtension(types, org.bukkit.map.MapCursorCollection.class, "addCursor"));
        assertTrue(hasExtension(types, org.bukkit.event.entity.AreaEffectCloudApplyEvent.class, "affectedEntities"));
        assertTrue(hasExtension(types, org.bukkit.event.entity.EnderDragonChangePhaseEvent.class, "setNewPhase"));
        assertTrue(hasExtension(types, org.bukkit.event.entity.EntityToggleGlideEvent.class, "isGliding"));
        assertTrue(hasExtension(types, org.bukkit.material.Cauldron.class, "isFull"));
        assertTrue(hasExtension(types, org.bukkit.material.Rails.class, "setDirection"));
        assertTrue(hasExtension(types, org.bukkit.material.PistonBaseMaterial.class, "isSticky"));
        assertTrue(hasExtension(types, org.bukkit.material.Diode.class, "setDelay"));
        assertTrue(hasExtension(types, org.bukkit.Note.class, "sharped"));
        assertTrue(hasExtension(types, org.bukkit.block.NoteBlock.class, "setNote"));
        assertTrue(hasExtension(types, org.bukkit.entity.TNTPrimed.class, "fuseTicks"));
        assertTrue(hasExtension(types, org.bukkit.entity.EnderSignal.class, "setDespawnTimer"));
        assertTrue(hasExtension(types, Hanging.class, "setFacingDirection"));
        assertTrue(hasExtension(types, IronGolem.class, "setPlayerCreated"));
        assertTrue(hasExtension(types, Slime.class, "setSize"));
        assertTrue(hasExtension(types, Guardian.class, "setElder"));
        assertTrue(hasExtension(types, org.bukkit.entity.Arrow.class, "setPickupStatus"));
        assertEquals(org.bukkit.entity.Arrow.PickupStatus.class,
                returnClass(types.resolveNamespace("default"), "arrowPickupStatus"));
        assertTrue(hasExtension(types, Zombie.class, "setVillagerProfession"));
        assertTrue(hasExtension(types, HopperMinecart.class, "setEnabled"));
        assertTrue(hasExtension(types, CommandMinecart.class, "setCommand"));
        assertTrue(hasExtension(types, org.bukkit.inventory.BrewerInventory.class, "holder"));
        assertTrue(hasExtension(types, org.bukkit.WorldBorder.class, "setCenter"));
        assertTrue(hasExtension(types, org.bukkit.Chunk.class, "unload"));
        assertTrue(hasExtension(types, org.bukkit.WorldCreator.class, "generator"));
        assertTrue(hasExtension(types, InventoryView.class, "setProperty"));
        assertTrue(hasExtension(types, InventoryView.Property.class, "id"));
        assertTrue(hasExtension(types, org.bukkit.World.class, "loadChunk"));
        assertTrue(hasExtension(types, org.bukkit.World.class, "unloadChunk"));
        assertTrue(hasExtension(types, World.class, "dropItemNaturally"));
        assertTrue(hasExtension(types, World.class, "generateTree"));
        assertTrue(hasExtension(types, World.class, "entities"));
        assertTrue(hasExtension(types, World.class, "createExplosion"));
        assertTrue(hasExtension(types, World.class, "setSpawnFlags"));
        assertTrue(hasExtension(types, World.class, "playEffect"));
        assertTrue(hasExtension(types, World.class, "spawnParticle"));

        Set<String> signatures = new LinkedHashSet<String>();
        for (JavaExtensionDescriptor extension : types.extensions()) {
            String signature = extensionSignature(extension);
            assertTrue(signatures.add(signature), "重复 Bukkit 扩展签名: " + signature);
        }
        Set<String> propertySignatures = new LinkedHashSet<String>();
        for (JavaExtensionPropertyDescriptor extension : types.extensionProperties()) {
            String signature = extension.getTargetType().getName()
                    + '#'
                    + extension.getProperty().getName();
            assertTrue(propertySignatures.add(signature), "重复 Bukkit 扩展属性: " + signature);
        }

        JavaNamespaceDescriptor namespace = types.resolveNamespace("default");
        for (JavaSymbolDescriptor symbol : namespace.getGlobals()) {
            if (symbol instanceof JavaFunctionDescriptor) {
                JavaFunctionDescriptor function = (JavaFunctionDescriptor) symbol;
                String signature = functionSignature(function);
                assertTrue(signatures.add("global#" + signature), "重复 Bukkit 全局函数签名: " + signature);
            }
        }
    }

    @Test
    @DisplayName("Bukkit 返回类型参与后续 Java 成员编译期检查")
    void shouldValidateBukkitMembersDuringCompilation() {
        JavaTypes.Builder builder = NovaBukkit.builder();
        builder.globalVariable("testConfig",
                variable -> variable.type(FileConfiguration.class).value(emptyConfiguration()));
        builder.globalVariable("testEquipment",
                variable -> variable.type(EntityEquipment.class).value(emptyProxy(EntityEquipment.class)));
        builder.globalVariable("testPluginDescription",
                variable -> variable.type(PluginDescriptionFile.class)
                        .value(new PluginDescriptionFile("Test", "1.0", "test.Main")));
        builder.globalVariable("testCommand",
                variable -> variable.type(Command.class).value(emptyCommand()));
        builder.globalVariable("testSender",
                variable -> variable.type(CommandSender.class).value(emptyProxy(CommandSender.class)));
        builder.globalVariable("testScheduler",
                variable -> variable.type(BukkitScheduler.class).value(emptyProxy(BukkitScheduler.class)));
        builder.globalVariable("testPlugin",
                variable -> variable.type(Plugin.class).value(emptyProxy(Plugin.class)));
        builder.globalVariable("testPluginManager",
                variable -> variable.type(PluginManager.class).value(emptyProxy(PluginManager.class)));
        builder.globalVariable("testRunnable",
                variable -> variable.type(Runnable.class).value((Runnable) () -> { }));
        builder.globalVariable("testContainer",
                variable -> variable.type(Container.class).value(emptyProxy(Container.class)));
        builder.globalVariable("testFurnace",
                variable -> variable.type(Furnace.class).value(emptyProxy(Furnace.class)));
        builder.globalVariable("testBrewingStand",
                variable -> variable.type(BrewingStand.class).value(emptyProxy(BrewingStand.class)));
        builder.globalVariable("testJukebox",
                variable -> variable.type(Jukebox.class).value(emptyProxy(Jukebox.class)));
        builder.globalVariable("testSign",
                variable -> variable.type(Sign.class).value(emptyProxy(Sign.class)));
        builder.globalVariable("testBeacon",
                variable -> variable.type(Beacon.class).value(emptyProxy(Beacon.class)));
        builder.globalVariable("testCommandBlock",
                variable -> variable.type(CommandBlock.class).value(emptyProxy(CommandBlock.class)));
        builder.globalVariable("testSpawner",
                variable -> variable.type(CreatureSpawner.class).value(emptyProxy(CreatureSpawner.class)));
        builder.globalVariable("testCreeper",
                variable -> variable.type(Creeper.class).value(emptyProxy(Creeper.class)));
        builder.globalVariable("testHorse",
                variable -> variable.type(Horse.class).value(emptyProxy(Horse.class)));
        builder.globalVariable("testMinecart",
                variable -> variable.type(Minecart.class).value(emptyProxy(Minecart.class)));
        builder.globalVariable("testInventoryView",
                variable -> variable.type(InventoryView.class).value(emptyInventoryView()));
        builder.globalVariable("testWorld",
                variable -> variable.type(World.class).value(emptyProxy(World.class)));
        builder.globalVariable("testTreeType",
                variable -> variable.type(TreeType.class).value(TreeType.TREE));
        builder.globalVariable("testEulerAngle",
                variable -> variable.type(EulerAngle.class).value(new EulerAngle(0.0, 0.0, 0.0)));
        builder.globalVariable("testKey",
                variable -> variable.type(NamespacedKey.class).value(new NamespacedKey("nova", "test")));
        builder.globalVariable("testNote",
                variable -> variable.type(Note.class).value(new Note(0)));
        builder.globalVariable("testNoteTone",
                variable -> variable.type(Note.Tone.class).value(Note.Tone.F));
        builder.globalVariable("testNameable",
                variable -> variable.type(Nameable.class).value(emptyProxy(Nameable.class)));
        builder.globalVariable("testUnsafeValues",
                variable -> variable.type(UnsafeValues.class).value(emptyProxy(UnsafeValues.class)));
        builder.globalVariable("testPermissionDefault",
                variable -> variable.type(PermissionDefault.class).value(PermissionDefault.TRUE));
        builder.globalVariable("testBossBar",
                variable -> variable.type(BossBar.class).value(emptyProxy(BossBar.class)));
        builder.globalVariable("testScoreboard",
                variable -> variable.type(Scoreboard.class).value(emptyProxy(Scoreboard.class)));
        builder.globalVariable("testScore",
                variable -> variable.type(org.bukkit.scoreboard.Score.class).value(emptyProxy(org.bukkit.scoreboard.Score.class)));
        Nova nova = new Nova();
        nova.install(builder.build());

        assertDoesNotThrow(() -> nova.compileToBytecode(
                "location(1.0, 2.0, 3.0).blockX", "bukkit-location-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "player(\"Alex\")?.name", "bukkit-player-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "location(1.0, 2.0, 3.0).x()", "bukkit-location-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "location(1.0, 2.0, 3.0).setX(4.0)", "bukkit-location-set-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "location(testWorld, 1.0, 2.0, 3.0).chunk().x()", "bukkit-location-chunk-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "player(\"Alex\")?.name()", "bukkit-player-name-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "player(\"Alex\")?.playerTime()", "bukkit-player-time-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "player(\"Alex\")?.location()", "bukkit-entity-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "player(\"Alex\")?.inventory()", "bukkit-inventory-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "player(\"Alex\")?.foodLevel()", "bukkit-player-extra-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "player(\"Alex\")?.setPlayerWeather(\"DOWNFALL\")", "bukkit-player-enum-alias-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "server().getPluginManager().plugins()", "bukkit-plugin-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "vector(1.0, 2.0, 3.0).blockX()", "bukkit-vector-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "testEulerAngle.add(1.0, 2.0, 3.0).z()", "bukkit-euler-angle-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "color(0).lerp(color(16777215), 0.5).hex()", "bukkit-color-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "testKey.minecraft(\"stone\").namespace()", "bukkit-namespaced-key-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "testNameable.customName()", "bukkit-nameable-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "testUnsafeValues.removeAdvancement(testKey)", "bukkit-unsafe-values-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "effect(\"CLICK1\")?.data()", "bukkit-effect-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "entityEffect(\"HURT\")?.data()", "bukkit-entity-effect-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "instrument(\"PIANO\")?.type()", "bukkit-instrument-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "statistic(\"JUMP\")?.isSubstatistic()", "bukkit-statistic-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "difficulty(\"NORMAL\")?.value()", "bukkit-difficulty-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "gameMode(\"SURVIVAL\")?.getByValue(1)", "bukkit-game-mode-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "rotation(\"NONE\")?.rotateClockwise()", "bukkit-rotation-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "worldType(\"NORMAL\")?.getByName(\"FLAT\")", "bukkit-world-type-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "coalType(\"COAL\")?.getByData(0)", "bukkit-coal-type-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "cropState(\"SEEDED\")?.getByData(0)", "bukkit-crop-state-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "grassSpecies(\"DEAD\")?.getByData(0)", "bukkit-grass-species-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "sandstoneType(\"CRACKED\")?.getByData(0)", "bukkit-sandstone-type-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "treeSpecies(\"GENERIC\")?.getByData(0)", "bukkit-tree-species-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "pistonMoveReaction(\"NORMAL\")?.id()", "bukkit-piston-move-reaction-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "clickType(\"SHIFT_LEFT\")?.isShiftClick()", "bukkit-click-type-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "testScore.player()", "bukkit-score-player-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "testNote.flat(0, testNoteTone).toString()", "bukkit-note-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "testNoteTone.getById(0)", "bukkit-note-tone-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "testPlugin.getDefaultWorldGenerator(\"world\", \"normal\")", "bukkit-plugin-generator-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "testPluginManager.getDefaultPermissions(true)", "bukkit-plugin-manager-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "testPluginManager.permissions()", "bukkit-plugin-manager-permissions-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "testPermissionDefault.getValue(true)", "bukkit-permission-default-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "testBossBar.setColor(\"RED\")", "bukkit-boss-bar-string-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "testScoreboard.getScores(server().getOfflinePlayer(\"Alex\"))", "bukkit-scoreboard-player-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "potionType(\"SPEED\")?.maxLevel()", "bukkit-potion-type-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "testWorld.getChunkAt(0, 0).chunkSnapshot()?.getData(0, 0, 0)", "bukkit-chunk-snapshot-data-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "server().name()", "bukkit-server-extra-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "world(\"world\")?.time()", "bukkit-world-extra-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "world(\"world\")?.loadChunk(0, 0, true)", "bukkit-world-load-chunk-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "testWorld.entities().size()", "bukkit-world-entities-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "testWorld.generateTree(location(1.0, 2.0, 3.0), testTreeType)",
                "bukkit-world-generate-tree-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "testWorld.setSpawnFlags(true, false)",
                "bukkit-world-spawn-flags-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "testWorld.canGenerateStructures()", "bukkit-world-structures-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "testWorld.setSpawnLocation(location(1.0, 2.0, 3.0))",
                "bukkit-world-spawn-location-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "testWorld.generator()", "bukkit-world-generator-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "testWorld.spawnParticle(\"SMOKE_NORMAL\", location(1.0, 2.0, 3.0), 1)",
                "bukkit-world-particle-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "worldCreator(\"nova\").seed(42).name()", "bukkit-world-creator-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "player(\"Alex\")?.maxHealth()", "bukkit-attribute-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "material(\"STONE\")?.id()", "bukkit-material-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "server().getOfflinePlayer(\"Alex\").isWhitelisted()", "bukkit-offline-player-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "testConfig.getString(\"path\", \"default\")",
                "bukkit-configuration-default-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "testPluginDescription.fullName()",
                "bukkit-plugin-description-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "testEquipment.helmet()", "bukkit-equipment-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "testCommand.execute(testSender, \"label\", [\"one\"])",
                "bukkit-command-execute-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "testScheduler.runTask(testPlugin, testRunnable)",
                "bukkit-scheduler-run-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "testContainer.inventory().size()",
                "bukkit-container-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "testContainer.setLock(\"vault\")",
                "bukkit-lockable-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "testFurnace.setBurnTime(200)",
                "bukkit-furnace-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "testBrewingStand.setFuelLevel(20)",
                "bukkit-brewing-stand-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "testJukebox.eject()",
                "bukkit-jukebox-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "testSign.setLine(0, \"Nova\")",
                "bukkit-sign-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "testBeacon.tier()",
                "bukkit-beacon-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "testCommandBlock.setCommand(\"say Nova\")",
                "bukkit-command-block-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "testSpawner.setSpawnCount(4)",
                "bukkit-spawner-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "testCreeper.setExplosionRadius(4)",
                "bukkit-creeper-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "testHorse.setCarryingChest(true)",
                "bukkit-horse-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "testMinecart.setMaxSpeed(0.8)",
                "bukkit-minecart-extension-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "testInventoryView.convertSlot(1)",
                "bukkit-inventory-view-slot-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "inventoryViewProperty(\"BURN_TIME\")?.id()",
                "bukkit-inventory-view-property-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "horseColor(\"WHITE\")?.name()",
                "bukkit-entity-enum-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "blockIgniteEventIgniteCause(\"LAVA\")",
                "bukkit-event-enum-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "statistic(\"JUMP\")",
                "bukkit-platform-enum-valid.nova"));
        assertThrows(RuntimeException.class, () -> nova.compileToBytecode(
                "location(1.0, 2.0, 3.0).missingMember", "bukkit-location-invalid.nova"));
        assertThrows(RuntimeException.class, () -> nova.compileToBytecode(
                "player(1)", "bukkit-player-argument-invalid.nova"));
        assertThrows(RuntimeException.class, () -> nova.compileToBytecode(
                "player(\"Alex\")?.playerTime(1)", "bukkit-extension-argument-invalid.nova"));
        assertThrows(RuntimeException.class, () -> nova.compileToBytecode(
                "player(\"Alex\")?.setFoodLevel(\"full\")", "bukkit-expanded-extension-argument-invalid.nova"));
        assertThrows(RuntimeException.class, () -> nova.compileToBytecode(
                "world(\"world\")?.setTime(\"noon\")", "bukkit-world-extension-argument-invalid.nova"));
        assertThrows(RuntimeException.class, () -> nova.compileToBytecode(
                "worldCreator(42)", "bukkit-world-creator-argument-invalid.nova"));
        assertThrows(RuntimeException.class, () -> nova.compileToBytecode(
                "location(1.0, 2.0, 3.0).x = \"bad\"", "bukkit-location-setter-type-invalid.nova"));
        assertThrows(RuntimeException.class, () -> nova.compileToBytecode(
                "location(1.0, 2.0, 3.0).blockX = 4", "bukkit-location-readonly-property-invalid.nova"));
        assertThrows(RuntimeException.class, () -> nova.compileToBytecode(
                "testConfig.getInt(\"path\", \"bad\")",
                "bukkit-configuration-default-invalid.nova"));
        assertThrows(RuntimeException.class, () -> nova.compileToBytecode(
                "testEquipment.setHelmetDropChance(\"high\")",
                "bukkit-equipment-setter-invalid.nova"));
        assertThrows(RuntimeException.class, () -> nova.compileToBytecode(
                "testScheduler.runTask(testPlugin, \"not runnable\")",
                "bukkit-scheduler-run-invalid.nova"));
    }

    @Test
    @DisplayName("不依赖服务端实例的 Bukkit 值对象工厂可以直接执行")
    void shouldRunBukkitValueConstructors() {
        Nova nova = new Nova();
        nova.install(NovaBukkit.create());

        Object locationResult = nova.compileToBytecode(
                "location(1.0, 2.0, 3.0)", "bukkit-location-run.nova").run();
        Object colorResult = nova.compileToBytecode(
                "color(255, 128, 0)", "bukkit-color-run.nova").run();
        Object extensionResult = nova.compileToBytecode(
                "location(1.0, 2.0, 3.0).x()", "bukkit-location-extension-run.nova").run();
        Object setterResult = nova.compileToBytecode(
                "val point = location(1.0, 2.0, 3.0)\npoint.x = 4.0\npoint.x",
                "bukkit-location-setter-run.nova").run();
        Object worldCreatorResult = nova.compileToBytecode(
                "worldCreator(\"nova-test\").seed(42).name()", "bukkit-world-creator-run.nova").run();

        assertTrue(locationResult instanceof Location);
        Location location = (Location) locationResult;
        assertEquals(1.0, location.getX());
        assertEquals(2.0, location.getY());
        assertEquals(3.0, location.getZ());
        assertEquals(Color.fromRGB(255, 128, 0), colorResult);
        assertEquals(1.0, extensionResult);
        assertEquals(4.0, setterResult);
        assertEquals("nova-test", worldCreatorResult);
    }

    @Test
    @DisplayName("配置与插件描述别名使用注册时的同一签名执行")
    void shouldRunConfigurationAndPluginDescriptionAliases() {
        FileConfiguration configuration = emptyConfiguration();
        PluginDescriptionFile description = new PluginDescriptionFile("Test", "1.0", "test.Main");
        JavaTypes.Builder builder = NovaBukkit.builder();
        builder.globalVariable("testConfig",
                variable -> variable.type(FileConfiguration.class).value(configuration));
        builder.globalVariable("testPluginDescription",
                variable -> variable.type(PluginDescriptionFile.class).value(description));
        Nova nova = new Nova();
        nova.install(builder.build());

        Object configResult = nova.compileToBytecode(
                "testConfig.set(\"answer\", 7)\ntestConfig.getInt(\"answer\", 0)",
                "bukkit-configuration-run.nova").run();
        Object descriptionResult = nova.compileToBytecode(
                "testPluginDescription.fullName()", "bukkit-plugin-description-run.nova").run();

        assertEquals(7, configResult);
        assertEquals(description.getFullName(), descriptionResult);
    }

    private List<JavaFunctionDescriptor> overloads(JavaNamespaceDescriptor namespace, String name) {
        List<JavaFunctionDescriptor> functions = new ArrayList<JavaFunctionDescriptor>();
        for (JavaSymbolDescriptor symbol : namespace.getGlobals()) {
            if (name.equals(symbol.getName()) && symbol instanceof JavaFunctionDescriptor) {
                functions.add((JavaFunctionDescriptor) symbol);
            }
        }
        return functions;
    }

    private Class<?> returnClass(JavaNamespaceDescriptor namespace, String name) {
        List<JavaFunctionDescriptor> functions = overloads(namespace, name);
        assertTrue(!functions.isEmpty());
        return functions.get(0).getReturnType().javaClass();
    }

    private String extensionSignature(JavaExtensionDescriptor extension) {
        StringBuilder signature = new StringBuilder();
        signature.append(extension.getTargetType().getName());
        signature.append('#');
        signature.append(functionSignature(extension.getFunction()));
        return signature.toString();
    }

    private String functionSignature(JavaFunctionDescriptor function) {
        StringBuilder signature = new StringBuilder();
        signature.append(function.getName());
        signature.append('(');
        List<JavaParameterDescriptor> parameters = function.getParameters();
        for (int index = 0; index < parameters.size(); index++) {
            if (index > 0) {
                signature.append(',');
            }
            signature.append(parameters.get(index).getType().javaClass().getName());
        }
        signature.append(')');
        return signature.toString();
    }

    private boolean hasProperty(JavaTypes types,
                                Class<?> targetType,
                                String name,
                                boolean mutable) {
        for (JavaExtensionPropertyDescriptor extension : types.extensionProperties()) {
            if (extension.getTargetType() == targetType
                    && name.equals(extension.getProperty().getName())
                    && extension.getProperty().isMutable() == mutable) {
                return true;
            }
        }
        return false;
    }

    private boolean hasExtension(JavaTypes types, Class<?> targetType, String name) {
        for (JavaExtensionDescriptor extension : types.extensions()) {
            if (extension.getTargetType() == targetType
                    && name.equals(extension.getFunction().getName())) {
                return true;
            }
        }
        return false;
    }

    private Class<?> extensionReturnClass(JavaTypes types,
                                          Class<?> targetType,
                                          String name,
                                          int parameterCount) {
        for (JavaExtensionDescriptor extension : types.extensions()) {
            JavaFunctionDescriptor function = extension.getFunction();
            if (extension.getTargetType() == targetType
                    && name.equals(function.getName())
                    && function.getParameters().size() == parameterCount) {
                return function.getReturnType().javaClass();
            }
        }
        throw new AssertionError("缺失 Bukkit 扩展: " + targetType.getName() + '#' + name);
    }

    private <T> T emptyProxy(Class<T> type) {
        Object proxy = Proxy.newProxyInstance(
                type.getClassLoader(), new Class<?>[]{type}, (instance, method, arguments) -> null);
        return type.cast(proxy);
    }

    private FileConfiguration emptyConfiguration() {
        return new FileConfiguration() {
            @Override
            public String saveToString() {
                return "";
            }

            @Override
            public void loadFromString(String contents) {
            }

            @Override
            protected String buildHeader() {
                return "";
            }
        };
    }

    private Command emptyCommand() {
        return new Command("test") {
            @Override
            public boolean execute(CommandSender sender, String label, String[] arguments) {
                return true;
            }
        };
    }

    private InventoryView emptyInventoryView() {
        return new InventoryView() {
            @Override
            public Inventory getTopInventory() {
                return emptyProxy(Inventory.class);
            }

            @Override
            public Inventory getBottomInventory() {
                return emptyProxy(Inventory.class);
            }

            @Override
            public org.bukkit.entity.HumanEntity getPlayer() {
                return emptyProxy(org.bukkit.entity.HumanEntity.class);
            }

            @Override
            public org.bukkit.event.inventory.InventoryType getType() {
                return org.bukkit.event.inventory.InventoryType.CHEST;
            }
        };
    }

    @Requires(classes = {"com.novalang.bukkit.missing.OptionalBukkitApi"})
    private static final class MissingRequiredRegistrar {
    }

    @Requires(classes = {"org.bukkit.Server"})
    private static final class PresentRequiredRegistrar {
    }

    @Requires(classes = {"org.bukkit.Server"}, methods = {"org.bukkit.Server#missingOptionalMethod"})
    private static final class MissingRequiredMethodRegistrar {
    }
}
