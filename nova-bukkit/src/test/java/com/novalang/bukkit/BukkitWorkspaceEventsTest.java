package com.novalang.bukkit;

import com.novalang.runtime.interpreter.JavaInterop;
import com.novalang.runtime.Nova;
import com.novalang.runtime.NovaScheduler;
import com.novalang.runtime.SchedulerHolder;
import com.novalang.workspace.RuntimeWorkspace;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BukkitWorkspaceEventsTest {

    @TempDir
    Path tempDirectory;

    @BeforeEach
    void installWorkspaceClassLoader() {
        JavaInterop.setScriptClassLoader(BukkitWorkspaceEventsTest.class.getClassLoader());
    }

    @AfterEach
    void clearWorkspaceClassLoader() {
        JavaInterop.setScriptClassLoader(null);
    }

    @Test
    void resolvesEventFromFullyQualifiedClassName() {
        assertSame(TestEvent.class, BukkitWorkspaceEvents.resolveEventType(TestEvent.class.getName()));
    }

    @Test
    void rejectsBlankClassName() {
        assertThrows(
                IllegalArgumentException.class,
                () -> BukkitWorkspaceEvents.resolveEventType(" ")
        );
    }

    @Test
    void rejectsUnknownClassName() {
        assertThrows(
                IllegalArgumentException.class,
                () -> BukkitWorkspaceEvents.resolveEventType("example.missing.UnknownEvent")
        );
    }

    @Test
    void rejectsNonEventClass() {
        assertThrows(
                IllegalArgumentException.class,
                () -> BukkitWorkspaceEvents.resolveEventType(String.class.getName())
        );
    }

    @Test
    void rejectsResolutionOutsideWorkspaceExecution() {
        JavaInterop.setScriptClassLoader(null);

        assertThrows(
                IllegalStateException.class,
                () -> BukkitWorkspaceEvents.resolveEventType(TestEvent.class.getName())
        );
    }

    @Test
    void compilesNamedNovaListenerClass() {
        Nova nova = new Nova();
        nova.setScriptClassLoader(BukkitWorkspaceEventsTest.class.getClassLoader());
        Object result = nova.compileToBytecode(
                "import java com.novalang.bukkit.BukkitEventListener\n"
                        + "import java org.bukkit.event.Event\n"
                        + "class ScriptListener : BukkitEventListener {\n"
                        + "    override fun handle(event: Event) { }\n"
                        + "}\n"
                        + "val listener = ScriptListener()\n"
                        + "listener is BukkitEventListener",
                "bukkit-listener.nova"
        ).run();

        assertTrue(result instanceof Boolean && ((Boolean) result).booleanValue());
    }

    @Test
    void compilesForwardReferencedNovaListenerClass() {
        Nova nova = new Nova();
        nova.setScriptClassLoader(BukkitWorkspaceEventsTest.class.getClassLoader());
        Object result = nova.compileToBytecode(
                "import java com.novalang.bukkit.BukkitEventListener\n"
                        + "import java org.bukkit.event.Event\n"
                        + "fun register(listener: BukkitEventListener) { }\n"
                        + "fun install() { register(ScriptListener()) }\n"
                        + "class ScriptListener : BukkitEventListener {\n"
                        + "    override fun handle(event: Event) { }\n"
                        + "}\n"
                        + "install()",
                "bukkit-forward-listener.nova"
        ).run();

        assertTrue(result == null);
    }

    @Test
    void compilesWorkspaceCallWithListenerDefinedInAnotherCompilationGroup() throws Exception {
        final Thread owner = Thread.currentThread();
        SchedulerHolder.set(new NovaScheduler() {
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
                return Thread.currentThread() == owner;
            }

            @Override
            public Cancellable scheduleLater(long delayMs, Runnable task) {
                throw new UnsupportedOperationException("The listener compilation test does not schedule tasks");
            }

            @Override
            public Cancellable scheduleRepeat(long delayMs, long periodMs, Runnable task) {
                throw new UnsupportedOperationException("The listener compilation test does not schedule tasks");
            }
        });
        Files.write(tempDirectory.resolve("events.nova"), (
                "import java com.novalang.bukkit.BukkitEventListener\n"
                        + "object CreatorDungeonEvents {\n"
                        + "    fun registerBukkit(listener: BukkitEventListener) { }\n"
                        + "}\n").getBytes(StandardCharsets.UTF_8));
        Files.write(tempDirectory.resolve("anchor.nova"), (
                "import \"@/events\"\n"
                        + "fun anchor() { }\n").getBytes(StandardCharsets.UTF_8));
        Files.write(tempDirectory.resolve("entry.nova"), (
                "import \"@/events\"\n"
                        + "import java com.novalang.bukkit.BukkitEventListener\n"
                        + "import java org.bukkit.event.Event\n"
                        + "class SharedListener : BukkitEventListener {\n"
                        + "    override fun handle(event: Event) { }\n"
                        + "}\n"
                        + "fun execute() { CreatorDungeonEvents.registerBukkit(SharedListener()) }\n"
        ).getBytes(StandardCharsets.UTF_8));
        Files.write(tempDirectory.resolve("nova.config.yml"), (
                "version: 1\n"
                        + "name: bukkit-listener-workspace\n"
                        + "aliases:\n"
                        + "  \"@\": \".\"\n"
                        + "sources:\n"
                        + "  - \".\"\n"
                        + "entries:\n"
                        + "  - \"anchor.nova\"\n"
                        + "  - \"entry.nova\"\n"
                        + "runtime:\n"
                        + "  security: trusted-server\n"
                        + "  thread: main\n").getBytes(StandardCharsets.UTF_8));
        RuntimeWorkspace workspace = new RuntimeWorkspace(tempDirectory.resolve("nova.config.yml"),
                nova -> nova.setScriptClassLoader(BukkitWorkspaceEventsTest.class.getClassLoader()));

        try {
            workspace.load();
            assertTrue(workspace.invoke("entry.nova", "execute", java.util.Collections.emptyMap(), null) == null);
        } finally {
            workspace.dispose();
            SchedulerHolder.clear();
        }
    }

    static final class TestEvent extends Event {
        private static final HandlerList HANDLERS = new HandlerList();

        @Override
        public HandlerList getHandlers() {
            return HANDLERS;
        }

        public static HandlerList getHandlerList() {
            return HANDLERS;
        }
    }
}
