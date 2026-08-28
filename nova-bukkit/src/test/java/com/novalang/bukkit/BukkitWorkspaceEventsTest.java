package com.novalang.bukkit;

import com.novalang.runtime.interpreter.JavaInterop;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BukkitWorkspaceEventsTest {

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
