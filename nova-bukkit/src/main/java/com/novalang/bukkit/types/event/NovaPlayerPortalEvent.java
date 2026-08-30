package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.TravelAgent;
import org.bukkit.event.player.PlayerPortalEvent;

/** 玩家传送门事件在 Spigot 1.12.2 中可用的别名。 */
@Requires(classes = {"org.bukkit.event.player.PlayerPortalEvent"})
public final class NovaPlayerPortalEvent {

    private NovaPlayerPortalEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        JavaTypeRef nullableTravelAgent = JavaTypeRef.javaType(TravelAgent.class).nullable();
        builder.extension(PlayerPortalEvent.class, "useTravelAgent", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> event(arguments).useTravelAgent()));
        builder.extension(PlayerPortalEvent.class, "useTravelAgent", function -> function
                .param("useTravelAgent", Boolean.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).useTravelAgent(NovaTypeSupport.argument(arguments, 1, Boolean.class));
                    return null;
                }));
        builder.extension(PlayerPortalEvent.class, "portalTravelAgent", function -> function
                .returns(nullableTravelAgent)
                .invoke(arguments -> event(arguments).getPortalTravelAgent()));
        builder.extension(PlayerPortalEvent.class, "setPortalTravelAgent", function -> function
                .param("travelAgent", nullableTravelAgent)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setPortalTravelAgent(NovaTypeSupport.argument(arguments, 1, TravelAgent.class));
                    return null;
                }));
    }

    private static PlayerPortalEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, PlayerPortalEvent.class);
    }
}
