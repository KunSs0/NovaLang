package com.novalang.bukkit.types.server;

import com.novalang.bukkit.types.value.NovaTypeSupport;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.TimedRegisteredListener;
import java.util.Collection;
import java.util.List;

/** Spigot 1.12.2 Services 与 RegisteredListener 别名。 */
final class NovaServices {

    private NovaServices() {
    }

    @SuppressWarnings("unchecked")
    static void register(JavaTypes.Builder b) {
        b.extension(RegisteredListener.class, "listener", f -> f.returns(Listener.class).invoke(a -> NovaTypeSupport.argument(a, 0, RegisteredListener.class).getListener()));
        b.extension(RegisteredListener.class, "plugin", f -> f.returns(Plugin.class).invoke(a -> NovaTypeSupport.argument(a, 0, RegisteredListener.class).getPlugin()));
        b.extension(RegisteredListener.class, "priority", f -> f.returns(EventPriority.class).invoke(a -> NovaTypeSupport.argument(a, 0, RegisteredListener.class).getPriority()));
        b.extension(RegisteredListener.class, "callEvent", f -> f.param("event", Event.class).invoke(a -> { NovaTypeSupport.argument(a, 0, RegisteredListener.class).callEvent(NovaTypeSupport.argument(a, 1, Event.class)); return null; }));
        b.extension(RegisteredListener.class, "isIgnoringCancelled", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, RegisteredListener.class).isIgnoringCancelled()));
        b.extension(TimedRegisteredListener.class, "reset", f -> f.invoke(a -> { NovaTypeSupport.argument(a, 0, TimedRegisteredListener.class).reset(); return null; }));
        b.extension(TimedRegisteredListener.class, "count", f -> f.returns(Integer.class).invoke(a -> NovaTypeSupport.argument(a, 0, TimedRegisteredListener.class).getCount()));
        b.extension(TimedRegisteredListener.class, "totalTime", f -> f.returns(Long.class).invoke(a -> NovaTypeSupport.argument(a, 0, TimedRegisteredListener.class).getTotalTime()));
        b.extension(TimedRegisteredListener.class, "hasMultiple", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, TimedRegisteredListener.class).hasMultiple()));
        b.extension(ServicesManager.class, "register", f -> f.param("service", Class.class).param("provider", Object.class).param("plugin", Plugin.class).param("priority", ServicePriority.class).invoke(a -> { NovaTypeSupport.argument(a, 0, ServicesManager.class).register(NovaTypeSupport.argument(a, 1, Class.class), NovaTypeSupport.argument(a, 2, Object.class), NovaTypeSupport.argument(a, 3, Plugin.class), NovaTypeSupport.argument(a, 4, ServicePriority.class)); return null; }));
        b.extension(ServicesManager.class, "unregister", f -> f.param("service", Class.class).param("provider", Object.class).invoke(a -> { NovaTypeSupport.argument(a, 0, ServicesManager.class).unregister(NovaTypeSupport.argument(a, 1, Class.class), NovaTypeSupport.argument(a, 2, Object.class)); return null; }));
        b.extension(ServicesManager.class, "load", f -> f.param("service", Class.class).returns(JavaTypeRef.javaType(Object.class).nullable()).invoke(a -> NovaTypeSupport.argument(a, 0, ServicesManager.class).load(NovaTypeSupport.argument(a, 1, Class.class))));
        b.extension(ServicesManager.class, "getRegistration", f -> f.param("service", Class.class).returns(JavaTypeRef.javaType(org.bukkit.plugin.RegisteredServiceProvider.class).nullable()).invoke(a -> NovaTypeSupport.argument(a, 0, ServicesManager.class).getRegistration(NovaTypeSupport.argument(a, 1, Class.class))));
        b.extension(ServicesManager.class, "getRegistrations", f -> f.param("plugin", Plugin.class).returns(JavaTypeRef.listOf(JavaTypeRef.javaType(org.bukkit.plugin.RegisteredServiceProvider.class))).invoke(a -> NovaTypeSupport.argument(a, 0, ServicesManager.class).getRegistrations(NovaTypeSupport.argument(a, 1, Plugin.class))));
        b.extension(ServicesManager.class, "getRegistrations", f -> f.param("service", Class.class).returns(JavaTypeRef.javaType(Collection.class)).invoke(a -> NovaTypeSupport.argument(a, 0, ServicesManager.class).getRegistrations(NovaTypeSupport.argument(a, 1, Class.class))));
        b.extension(ServicesManager.class, "knownServices", f -> f.returns(JavaTypeRef.javaType(Collection.class)).invoke(a -> NovaTypeSupport.argument(a, 0, ServicesManager.class).getKnownServices()));
        b.extension(ServicesManager.class, "isProvidedFor", f -> f.param("service", Class.class).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, ServicesManager.class).isProvidedFor(NovaTypeSupport.argument(a, 1, Class.class))));
    }
}
