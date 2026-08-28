package com.novalang.bukkit;

import com.novalang.mock.MockTestReport;
import com.novalang.mock.MockTestRunner;
import com.novalang.runtime.NovaScheduler;
import com.novalang.runtime.SchedulerHolder;
import com.novalang.runtime.interpreter.Interpreter;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Bukkit mock provider 的 Player 类型、身份稳定性及生命周期测试。 */
class BukkitMockTestHostTest {

    @TempDir
    Path temporaryDirectory;

    @BeforeEach
    void installScheduler() {
        SchedulerHolder.set(new DirectScheduler());
    }

    @AfterEach
    void clearScheduler() {
        Interpreter.resetGlobalSchedulerState();
    }

    @Test
    void shouldProvideStableOfflinePlayerProxy() {
        BukkitMockTestHost host = new BukkitMockTestHost();
        Player first = host.player("tester");
        Player second = host.player("tester");

        assertTrue(first instanceof Player);
        assertSame(first, second);
        assertEquals("tester", first.getName());
        assertEquals(UUID.nameUUIDFromBytes(
                        "NovaMockPlayer:tester".getBytes(java.nio.charset.StandardCharsets.UTF_8)),
                first.getUniqueId());
        assertFalse(first.isOnline());
        assertSame(first, first.getPlayer());
        assertEquals(first, first);
        assertEquals(first.hashCode(), first.getUniqueId().hashCode());
        assertTrue(first.toString().contains("tester"));
        assertThrows(UnsupportedOperationException.class, first::getAddress);
        assertThrows(IllegalArgumentException.class, () -> host.player(" "));
    }

    @Test
    void shouldReleasePlayersOnClose() {
        BukkitMockTestHost host = new BukkitMockTestHost();
        host.player("tester");
        assertEquals(1, host.playerCount());
        host.close();
        assertEquals(0, host.playerCount());
        assertEquals("tester", host.player("tester").getName());
        assertEquals(1, host.playerCount());
    }

    @Test
    void shouldCompileAndRunMockPlayerThroughWorkspace() throws Exception {
        Path file = temporaryDirectory.resolve("player.mock.nova");
        Files.write(file, ("fun test() {\n"
                + "  val player = mock.player(\"offline\")\n"
                + "  assertEquals(\"offline\", player.getName(), \"player identity\")\n"
                + "}\n").getBytes(StandardCharsets.UTF_8));
        MockTestReport report = new MockTestRunner().run(
                file, java.util.Collections.<Path>emptyList(),
                java.util.Collections.<String, Path>emptyMap(),
                java.util.Collections.<String, Object>emptyMap(),
                new BukkitMockTestHost(), null);
        assertEquals(1, report.getPassed(), report.getCases().get(0).getError());
    }

    private static final class DirectScheduler implements NovaScheduler {
        @Override
        public Executor mainExecutor() {
            return Runnable::run;
        }

        @Override
        public Executor asyncExecutor() {
            return Runnable::run;
        }

        @Override
        public boolean isMainThread() {
            return true;
        }

        @Override
        public Cancellable scheduleLater(long delayMs, Runnable task) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Cancellable scheduleRepeat(long delayMs, long periodMs, Runnable task) {
            throw new UnsupportedOperationException();
        }
    }
}
