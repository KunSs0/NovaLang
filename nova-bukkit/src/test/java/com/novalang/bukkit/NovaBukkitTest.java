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
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Nova Bukkit 类型资源")
class NovaBukkitTest {

    @Test
    @DisplayName("注册 Fluxon platform-bukkit 对应的核心全局入口")
    void shouldExposeCoreBukkitFunctions() {
        JavaNamespaceDescriptor namespace = NovaBukkit.create().resolveNamespace("default");

        assertEquals(Server.class, returnClass(namespace, "server"));
        assertEquals(Player.class, returnClass(namespace, "player"));
        assertEquals(Location.class, returnClass(namespace, "location"));
        assertEquals(4, overloads(namespace, "location").size());
        assertEquals(3, overloads(namespace, "color").size());
    }

    @Test
    @DisplayName("完整领域注册器不会生成重复扩展签名")
    void shouldExposeExpandedBukkitExtensionsWithoutDuplicates() {
        JavaTypes types = NovaBukkit.create();
        assertEquals(1410, types.extensions().size());
        assertTrue(types.extensionProperties().size() > 100);
        assertTrue(hasProperty(types, Location.class, "x", true));
        assertTrue(hasProperty(types, Player.class, "name", false));

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
            signature.append(parameters.get(index).getType().displayName());
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
}
