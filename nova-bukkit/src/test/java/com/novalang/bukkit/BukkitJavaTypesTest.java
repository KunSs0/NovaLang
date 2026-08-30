package com.novalang.bukkit;

import com.novalang.runtime.Nova;
import com.novalang.runtime.host.JavaFunctionDescriptor;
import com.novalang.runtime.host.JavaNamespaceDescriptor;
import com.novalang.runtime.host.JavaSymbolDescriptor;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Bukkit JavaTypes")
class BukkitJavaTypesTest {

    @Test
    @DisplayName("注册 Fluxon platform-bukkit 对应的核心全局入口")
    void shouldExposeCoreBukkitFunctions() {
        JavaNamespaceDescriptor namespace = BukkitJavaTypes.create().resolveNamespace("default");

        assertEquals(Server.class, returnClass(namespace, "server"));
        assertEquals(Player.class, returnClass(namespace, "player"));
        assertEquals(Location.class, returnClass(namespace, "location"));
        assertEquals(4, overloads(namespace, "location").size());
        assertEquals(3, overloads(namespace, "color").size());
    }

    @Test
    @DisplayName("Bukkit 返回类型参与后续 Java 成员编译期检查")
    void shouldValidateBukkitMembersDuringCompilation() {
        Nova nova = BukkitJavaTypes.install(new Nova());

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
        assertThrows(RuntimeException.class, () -> nova.compileToBytecode(
                "location(1.0, 2.0, 3.0).missingMember", "bukkit-location-invalid.nova"));
        assertThrows(RuntimeException.class, () -> nova.compileToBytecode(
                "player(1)", "bukkit-player-argument-invalid.nova"));
        assertThrows(RuntimeException.class, () -> nova.compileToBytecode(
                "player(\"Alex\")?.playerTime(1)", "bukkit-extension-argument-invalid.nova"));
    }

    @Test
    @DisplayName("不依赖服务端实例的 Bukkit 值对象工厂可以直接执行")
    void shouldRunBukkitValueConstructors() {
        Nova nova = new Nova();
        nova.install(BukkitJavaTypes.create());

        Object locationResult = nova.compileToBytecode(
                "location(1.0, 2.0, 3.0)", "bukkit-location-run.nova").run();
        Object colorResult = nova.compileToBytecode(
                "color(255, 128, 0)", "bukkit-color-run.nova").run();
        Object extensionResult = nova.compileToBytecode(
                "location(1.0, 2.0, 3.0).x()", "bukkit-location-extension-run.nova").run();

        assertTrue(locationResult instanceof Location);
        Location location = (Location) locationResult;
        assertEquals(1.0, location.getX());
        assertEquals(2.0, location.getY());
        assertEquals(3.0, location.getZ());
        assertEquals(Color.fromRGB(255, 128, 0), colorResult);
        assertEquals(1.0, extensionResult);
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
}
