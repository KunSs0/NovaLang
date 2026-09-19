package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.server.ServiceEvent;
import org.bukkit.plugin.RegisteredServiceProvider;

/** 服务注册与注销事件共用的可选 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.event.server.ServiceEvent"})
public final class NovaServiceEvent {

    private NovaServiceEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(ServiceEvent.class, "provider", function -> function
                .returns(RegisteredServiceProvider.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, ServiceEvent.class).getProvider()));
    }
}
