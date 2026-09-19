package com.novalang.bukkit.types.platform;

import com.novalang.bukkit.types.value.NovaTypeSupport;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.HandlerList;
import java.util.Collection;

/** Bukkit 事件基类、取消接口和 HandlerList 别名。 */
final class NovaEvent {

    private NovaEvent() {
    }

    static void register(JavaTypes.Builder b) {
        b.extension(Cancellable.class, "isCancelled", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Cancellable.class).isCancelled()));
        b.extension(Cancellable.class, "setCancelled", f -> f.param("cancelled", Boolean.class).invoke(a -> { NovaTypeSupport.argument(a, 0, Cancellable.class).setCancelled(NovaTypeSupport.argument(a, 1, Boolean.class)); return null; }));
        b.extension(Event.class, "eventName", f -> f.returns(String.class).invoke(a -> NovaTypeSupport.argument(a, 0, Event.class).getEventName()));
        b.extension(Event.class, "isAsynchronous", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Event.class).isAsynchronous()));
        b.extension(Event.class, "handlers", f -> f.returns(HandlerList.class).invoke(a -> NovaTypeSupport.argument(a, 0, Event.class).getHandlers()));
        b.extension(EventException.class, "cause", f -> f.returns(Throwable.class).invoke(a -> NovaTypeSupport.argument(a, 0, EventException.class).getCause()));
        b.extension(HandlerList.class, "registeredListeners", f -> f.returns(JavaTypeRef.javaType(org.bukkit.plugin.RegisteredListener[].class)).invoke(a -> NovaTypeSupport.argument(a, 0, HandlerList.class).getRegisteredListeners()));
        b.extension(HandlerList.class, "registerAll", f -> f.param("listeners", Collection.class).invoke(a -> { NovaTypeSupport.argument(a, 0, HandlerList.class).registerAll(NovaTypeSupport.argument(a, 1, Collection.class)); return null; }));
    }
}
