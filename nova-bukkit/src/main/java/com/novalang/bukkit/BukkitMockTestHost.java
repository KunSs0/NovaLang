package com.novalang.bukkit;

import com.novalang.mock.MockTestBindings;
import com.novalang.mock.MockTestHost;
import com.novalang.runtime.Nova;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/** Bukkit mock provider；造出的玩家只存在于当前测试 Workspace 生命周期。 */
public final class BukkitMockTestHost implements MockTestHost {

    private final Map<String, MockPlayer> players = new LinkedHashMap<String, MockPlayer>();

    @Override
    public void installMockBindings(Nova nova, MockTestBindings bindings) {
        bindings.install(nova);
        nova.set("mock", new BukkitMockApi(this));
    }

    @Override
    public void close() {
        players.clear();
    }

    Player player(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("mock.player 玩家 ID 不能为空");
        }
        MockPlayer player = players.get(id);
        if (player == null) {
            player = new MockPlayer(id);
            players.put(id, player);
        }
        return player.proxy();
    }

    int playerCount() {
        return players.size();
    }

    /** Nova 脚本可见的 provider API；不暴露 Proxy/反射实现细节。 */
    public static final class BukkitMockApi {
        private final BukkitMockTestHost owner;

        BukkitMockApi(BukkitMockTestHost owner) {
            this.owner = owner;
        }

        public Player player(String id) {
            return owner.player(id);
        }
    }

    /**
     * Bukkit Player 测试替身。
     *
     * <p>Nova 无法实例化宿主接口，Player 又庞大且随 Bukkit 版本演进；因此仅在
     * provider 内用 Proxy 构造测试替身。反射不会暴露给 Nova 脚本，未实现方法明确
     * 抛出 UnsupportedOperationException，不返回系统 fallback。</p>
     */
    private static final class MockPlayer implements InvocationHandler {
        private final String name;
        private final UUID uniqueId;
        private final Player proxy;

        MockPlayer(String name) {
            this.name = name;
            String identity = "NovaMockPlayer:" + name;
            uniqueId = UUID.nameUUIDFromBytes(identity.getBytes(StandardCharsets.UTF_8));
            proxy = (Player) Proxy.newProxyInstance(
                    Player.class.getClassLoader(), new Class<?>[]{Player.class}, this);
        }

        Player proxy() {
            return proxy;
        }

        @Override
        public Object invoke(Object object, Method method, Object[] arguments) {
            String methodName = method.getName();
            if ("getName".equals(methodName)) {
                return name;
            }
            if ("getUniqueId".equals(methodName)) {
                return uniqueId;
            }
            if ("isOnline".equals(methodName)) {
                return false;
            }
            if ("getPlayer".equals(methodName)) {
                return proxy;
            }
            if ("equals".equals(methodName)) {
                return object == arguments[0];
            }
            if ("hashCode".equals(methodName)) {
                return uniqueId.hashCode();
            }
            if ("toString".equals(methodName)) {
                return "MockPlayer{" + name + "}";
            }
            throw new UnsupportedOperationException("Bukkit mock method is not implemented: " + method);
        }
    }
}
