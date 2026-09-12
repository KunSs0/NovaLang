package com.novalang.bukkit.paper;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.novalang.bukkit.BukkitEventRegistrar;
import com.novalang.runtime.host.JavaTypes;
import com.novalang.bukkit.EventRegistration;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/** 验证 Paper 1.21 模块能够调用 Core 中的强类型事件注册器。 */
public class Paper121EventRegistrationTest {

    /** 验证 Paper 版本模块能够注册其版本扩展。 */
    @Test
    void registersPaperExtensions() {
        Paper121Module module = new Paper121Module();
        JavaTypes.Builder builder = JavaTypes.builder();
        assertDoesNotThrow(() -> module.register(builder));
    }

    private ServerMock server;
    private Plugin plugin;

    /** 创建 MockBukkit 服务端和已启用插件。 */
    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.createMockPlugin();
    }

    /** 释放 MockBukkit 全局状态。 */
    @AfterEach
    void tearDown() {
        if (MockBukkit.isMocked()) {
            MockBukkit.unmock();
        }
    }

    /** 验证事件类参数、派发和注销流程。 */
    @Test
    void registersAndDisposesListener() throws Exception {
        AtomicInteger count = new AtomicInteger();
        EventRegistration registration = BukkitEventRegistrar.forPlugin(plugin).listen(
                TestEvent.class, EventPriority.NORMAL, false, event -> count.incrementAndGet());
        server.getPluginManager().callEvent(new TestEvent());
        assertEquals(1, count.get());
        registration.dispose();
        server.getPluginManager().callEvent(new TestEvent());
        assertEquals(1, count.get());
    }

    /** Paper 1.21 测试事件。 */
    public static final class TestEvent extends Event {

        private static final HandlerList HANDLERS = new HandlerList();

        /** 返回事件处理器列表。 */
        @Override
        public HandlerList getHandlers() {
            return HANDLERS;
        }

        /** 返回静态事件处理器列表。 */
        public static HandlerList getHandlerList() {
            return HANDLERS;
        }
    }
}
