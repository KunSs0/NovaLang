package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.TravelAgent;
import org.bukkit.event.entity.EntityPortalEvent;

/** 实体传送门事件的可选编译期别名。 */
@Requires(classes = {"org.bukkit.event.entity.EntityPortalEvent"})
public final class NovaEntityPortalEvent {

    private NovaEntityPortalEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(EntityPortalEvent.class, "useTravelAgent", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> event(arguments).useTravelAgent()));
        builder.extension(EntityPortalEvent.class, "useTravelAgent", function -> function
                .param("use", Boolean.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).useTravelAgent(argument(arguments, 1, Boolean.class));
                    return null;
                }));
        builder.extension(EntityPortalEvent.class, "portalTravelAgent", function -> function
                .returns(TravelAgent.class)
                .invoke(arguments -> event(arguments).getPortalTravelAgent()));
        builder.extension(EntityPortalEvent.class, "setPortalTravelAgent", function -> function
                .param("travelAgent", TravelAgent.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setPortalTravelAgent(argument(arguments, 1, TravelAgent.class));
                    return null;
                }));
    }

    private static EntityPortalEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, EntityPortalEvent.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
