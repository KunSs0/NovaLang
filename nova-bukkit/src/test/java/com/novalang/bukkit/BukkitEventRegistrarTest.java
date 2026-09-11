package com.novalang.bukkit;

import com.novalang.runtime.Nova;
import com.novalang.runtime.NovaScheduler;
import com.novalang.runtime.SchedulerHolder;
import com.novalang.runtime.interpreter.JavaInterop;
import com.novalang.workspace.RuntimeWorkspace;
import com.novalang.workspace.WorkspaceGeneration;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.Server;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.event.Listener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BukkitEventRegistrarTest {

    @TempDir
    Path tempDirectory;

    @BeforeEach
    void installScriptClassLoader() {
        JavaInterop.setScriptClassLoader(BukkitEventRegistrarTest.class.getClassLoader());
    }

    @AfterEach
    void clearScriptClassLoader() {
        JavaInterop.setScriptClassLoader(null);
    }

    @Test
    void resolvesEventFromFullyQualifiedClassName() {
        assertSame(TestEvent.class,
                BukkitEventRegistrar.resolveEventType(TestEvent.class.getName()));
    }

    @Test
    void rejectsInvalidEventClassName() {
        assertThrows(IllegalArgumentException.class,
                () -> BukkitEventRegistrar.resolveEventType(" "));
        assertThrows(IllegalArgumentException.class,
                () -> BukkitEventRegistrar.resolveEventType(String.class.getName()));
        assertThrows(IllegalArgumentException.class,
                () -> BukkitEventRegistrar.resolveEventType("example.missing.UnknownEvent"));
    }

    @Test
    void rejectsResolutionWithoutScriptClassLoader() {
        JavaInterop.setScriptClassLoader(null);
        assertThrows(IllegalStateException.class,
                () -> BukkitEventRegistrar.resolveEventType(TestEvent.class.getName()));
    }

    @Test
    void standaloneRegistrationUsesBoundPluginAndIsIdempotentlyDisposable() throws Exception {
        AtomicReference<Plugin> owner = new AtomicReference<Plugin>();
        AtomicReference<Listener> registeredListener = new AtomicReference<Listener>();
        AtomicReference<EventExecutor> executor = new AtomicReference<EventExecutor>();
        Plugin plugin = pluginProxy(owner, registeredListener, executor);
        AtomicInteger invocations = new AtomicInteger();

        EventRegistration registration = BukkitEventRegistrar.forPlugin(plugin).listen(
                TestEvent.class.getName(), EventPriority.NORMAL, false,
                event -> invocations.incrementAndGet());

        assertSame(plugin, owner.get());
        executor.get().execute(registeredListener.get(), new TestEvent());
        assertSame(1, invocations.get());

        registration.dispose();
        registration.dispose();
        executor.get().execute(registeredListener.get(), new TestEvent());
        assertSame(1, invocations.get());
    }

    @Test
    void compilesStrongListenerMethodReferenceThroughNoBukkit() {
        Nova nova = new Nova();
        nova.install(NovaBukkit.create(pluginProxy()));
        nova.compileToBytecode(
                "import java com.novalang.bukkit.BukkitEventListener\n"
                        + "import java org.bukkit.event.Event\n"
                        + "import java org.bukkit.event.EventPriority\n"
                        + "fun onEvent(event: Event) { }\n"
                        + "NoBukkit.event.listen(\"" + TestEvent.class.getName()
                        + "\", EventPriority.NORMAL, false, ::onEvent)\n"
                        + "true",
                "nobukkit-listener.nova");
    }

    @Test
    void acceptsAnyParameterForListenerMethodReference() {
        Nova nova = new Nova();
        nova.install(NovaBukkit.create(pluginProxy()));
        nova.compileToBytecode(
                "import java org.bukkit.event.EventPriority\n"
                        + "fun onEvent(event: Any) { }\n"
                        + "NoBukkit.event.listen(\"" + TestEvent.class.getName()
                        + "\", EventPriority.NORMAL, false, ::onEvent)",
                "nobukkit-any-listener.nova");
    }

    @Test
    void workspaceRegistrationIsDisposedWithGeneration() throws Exception {
        AtomicReference<Listener> registeredListener = new AtomicReference<Listener>();
        AtomicReference<EventExecutor> executor = new AtomicReference<EventExecutor>();
        AtomicReference<Plugin> owner = new AtomicReference<Plugin>();
        Plugin plugin = pluginProxy(owner, registeredListener, executor);
        String eventClassName = TestEvent.class.getName().replace("$", "\\$");
        final Thread ownerThread = Thread.currentThread();
        SchedulerHolder.set(new NovaScheduler() {
            @Override
            public java.util.concurrent.Executor mainExecutor() {
                return Runnable::run;
            }

            @Override
            public java.util.concurrent.Executor asyncExecutor() {
                return Runnable::run;
            }

            @Override
            public boolean isMainThread() {
                return Thread.currentThread() == ownerThread;
            }

            @Override
            public Cancellable scheduleLater(long delayMs, Runnable task) {
                throw new UnsupportedOperationException("event lifecycle test does not schedule tasks");
            }

            @Override
            public Cancellable scheduleRepeat(long delayMs, long periodMs, Runnable task) {
                throw new UnsupportedOperationException("event lifecycle test does not schedule tasks");
            }
        });
        Files.write(tempDirectory.resolve("entry.nova"), (
                "import java org.bukkit.event.Event\n"
                        + "import java org.bukkit.event.EventPriority\n"
                        + "var count = 0\n"
                        + "fun onEvent(event: Event) { count = count + 1 }\n"
                        + "fun getCount(): Int { return count }\n"
                        + "fun main() { NoBukkit.event.listen(\"" + eventClassName
                        + "\", EventPriority.NORMAL, false, ::onEvent) }\n"
        ).getBytes(StandardCharsets.UTF_8));
        Files.write(tempDirectory.resolve("nova.config.yml"), (
                "version: 1\n"
                        + "name: bukkit-event-workspace\n"
                        + "aliases:\n"
                        + "  \"@\": \".\"\n"
                        + "sources:\n"
                        + "  - .\n"
                        + "entries:\n"
                        + "  - entry.nova\n"
                        + "runtime:\n"
                        + "  security: trusted-server\n"
                        + "  thread: main\n"
        ).getBytes(StandardCharsets.UTF_8));
        RuntimeWorkspace workspace = new RuntimeWorkspace(
                tempDirectory.resolve("nova.config.yml"),
                nova -> {
                    NovaBukkit.install(nova, plugin);
                    nova.setScriptClassLoader(BukkitEventRegistrarTest.class.getClassLoader());
                });
        try {
            workspace.load();
            assertSame(plugin, owner.get());
            executor.get().execute(registeredListener.get(), new TestEvent());
            assertSame(1, workspace.invoke("entry.nova", "getCount",
                    java.util.Collections.emptyMap(), null));
            WorkspaceGeneration generation = workspace.getGeneration();
            AtomicInteger explicitCount = new AtomicInteger();
            EventRegistration explicitRegistration = BukkitEventRegistrar.forWorkspace(
                    plugin, generation, generation.getRootScope()).listen(
                    TestEvent.class.getName(), EventPriority.NORMAL, false,
                    event -> explicitCount.incrementAndGet());
            executor.get().execute(registeredListener.get(), new TestEvent());
            assertEquals(1, explicitCount.get());
            explicitRegistration.dispose();
            executor.get().execute(registeredListener.get(), new TestEvent());
            assertEquals(1, explicitCount.get());
            workspace.dispose();
            assertThrows(RuntimeException.class, () -> workspace.invoke("entry.nova", "getCount",
                    java.util.Collections.emptyMap(), null));
        } finally {
            workspace.dispose();
            SchedulerHolder.clear();
        }
    }

    @Test
    void rejectsInvalidListenerMethodReferenceBeforeRegistration() {
        Nova nova = new Nova();
        nova.install(NovaBukkit.create(pluginProxy()));
        assertThrows(RuntimeException.class, () -> nova.compileToBytecode(
                "import java org.bukkit.event.Event\n"
                        + "import java org.bukkit.event.EventPriority\n"
                        + "fun missing(event: Event) { }\n"
                        + "NoBukkit.event.listen(\"" + TestEvent.class.getName()
                        + "\", EventPriority.NORMAL, false, ::unknown)",
                "nobukkit-unknown-listener.nova"));
    }

    private Plugin pluginProxy() {
        return pluginProxy(new AtomicReference<Plugin>(), new AtomicReference<Listener>(),
                new AtomicReference<EventExecutor>());
    }

    private Plugin pluginProxy(AtomicReference<Plugin> owner,
                               AtomicReference<Listener> registeredListener,
                               AtomicReference<EventExecutor> executor) {
        PluginManager manager = (PluginManager) Proxy.newProxyInstance(
                PluginManager.class.getClassLoader(),
                new Class<?>[]{PluginManager.class},
                (proxy, method, arguments) -> {
                    if ("registerEvent".equals(method.getName())) {
                        owner.set((Plugin) arguments[4]);
                        registeredListener.set((Listener) arguments[1]);
                        executor.set((EventExecutor) arguments[3]);
                    }
                    return null;
                });
        Server server = (Server) Proxy.newProxyInstance(
                Server.class.getClassLoader(),
                new Class<?>[]{Server.class},
                (proxy, method, arguments) -> {
                    if ("getPluginManager".equals(method.getName())) {
                        return manager;
                    }
                    return null;
                });
        return (Plugin) Proxy.newProxyInstance(
                Plugin.class.getClassLoader(),
                new Class<?>[]{Plugin.class},
                (proxy, method, arguments) -> {
                    if ("getServer".equals(method.getName())) {
                        return server;
                    }
                    return null;
                });
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
