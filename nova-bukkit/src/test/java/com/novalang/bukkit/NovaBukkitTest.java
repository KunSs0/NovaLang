package com.novalang.bukkit;

import com.novalang.runtime.Nova;
import com.novalang.runtime.host.JavaExtensionDescriptor;
import com.novalang.runtime.host.JavaExtensionPropertyDescriptor;
import com.novalang.runtime.host.JavaFunctionDescriptor;
import com.novalang.runtime.host.JavaNamespaceDescriptor;
import com.novalang.runtime.host.JavaParameterDescriptor;
import com.novalang.runtime.host.JavaSymbolDescriptor;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
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
import org.bukkit.entity.Minecart;
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
    @DisplayName("注册 Fluxon platform-bukkit 对应的核心全局入口")
    void shouldExposeCoreBukkitFunctions() {
        JavaNamespaceDescriptor namespace = NovaBukkit.create().resolveNamespace("default");

        assertEquals(Server.class, returnClass(namespace, "server"));
        assertEquals(Player.class, returnClass(namespace, "player"));
        assertEquals(Villager.Profession.class, returnClass(namespace, "villagerProfession"));
        assertEquals(Location.class, returnClass(namespace, "location"));
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
        assertTrue(NovaBukkitRegistrar.isSatisfied(PresentRequiredRegistrar.class));
    }

    @Test
    @DisplayName("完整领域注册器不会生成重复扩展签名")
    void shouldExposeExpandedBukkitExtensionsWithoutDuplicates() {
        JavaTypes types = NovaBukkit.create();
        assertEquals(1770, types.extensions().size());
        assertTrue(types.extensionProperties().size() > 100);
        assertTrue(hasProperty(types, Location.class, "x", true));
        assertTrue(hasProperty(types, Player.class, "name", false));
        assertTrue(hasExtension(types, AsyncPlayerChatEvent.class, "setFormat"));
        assertTrue(hasExtension(types, EntityRegainHealthEvent.class, "setAmount"));
        assertTrue(hasExtension(types, PlayerDeathEvent.class, "setKeepInventory"));
        assertTrue(hasExtension(types, BlockDamageEvent.class, "setInstaBreak"));
        assertTrue(hasExtension(types, BlockMultiPlaceEvent.class, "replacedBlockStates"));
        assertTrue(hasExtension(types, PlayerItemDamageEvent.class, "setDamage"));
        assertTrue(hasExtension(types, PlayerItemBreakEvent.class, "brokenItem"));
        assertTrue(hasExtension(types, PlayerInteractEntityEvent.class, "rightClicked"));
        assertTrue(hasExtension(types, PlayerInteractAtEntityEvent.class, "clickedPosition"));
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
        assertTrue(hasExtension(types, Objective.class, "unregister"));
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
                "server().name()", "bukkit-server-extra-valid.nova"));
        assertDoesNotThrow(() -> nova.compileToBytecode(
                "world(\"world\")?.time()", "bukkit-world-extra-valid.nova"));
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

        assertTrue(locationResult instanceof Location);
        Location location = (Location) locationResult;
        assertEquals(1.0, location.getX());
        assertEquals(2.0, location.getY());
        assertEquals(3.0, location.getZ());
        assertEquals(Color.fromRGB(255, 128, 0), colorResult);
        assertEquals(1.0, extensionResult);
        assertEquals(4.0, setterResult);
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

    @Requires(classes = {"com.novalang.bukkit.missing.OptionalBukkitApi"})
    private static final class MissingRequiredRegistrar {
    }

    @Requires(classes = {"org.bukkit.Server"})
    private static final class PresentRequiredRegistrar {
    }
}
