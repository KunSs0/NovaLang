package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerFishEvent;

/** 玩家钓鱼事件的可选编译期别名。 */
@Requires(classes = {"org.bukkit.event.player.PlayerFishEvent"})
public final class NovaPlayerFishEvent {

    private NovaPlayerFishEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PlayerFishEvent.class, "player", function -> function
                .returns(Player.class)
                .invoke(arguments -> event(arguments).getPlayer()));
        builder.extension(PlayerFishEvent.class, "hook", function -> function
                .returns(FishHook.class)
                .invoke(arguments -> event(arguments).getHook()));
        builder.extension(PlayerFishEvent.class, "caught", function -> function
                .returns(JavaTypeRef.javaType(Entity.class).nullable())
                .invoke(arguments -> event(arguments).getCaught()));
        builder.extension(PlayerFishEvent.class, "state", function -> function
                .returns(PlayerFishEvent.State.class)
                .invoke(arguments -> event(arguments).getState()));
        builder.extension(PlayerFishEvent.class, "expToDrop", function -> function
                .returns(Integer.class)
                .invoke(arguments -> event(arguments).getExpToDrop()));
        builder.extension(PlayerFishEvent.class, "setExpToDrop", function -> function
                .param("experience", Integer.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setExpToDrop(NovaTypeSupport.argument(arguments, 1, Integer.class));
                    return null;
                }));
        builder.extension(PlayerFishEvent.class, "handlerList", function -> function
                .returns(HandlerList.class)
                .invoke(arguments -> PlayerFishEvent.getHandlerList()));
        builder.extension(PlayerFishEvent.class, "isFishing", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> event(arguments).getState() == PlayerFishEvent.State.FISHING));
        builder.extension(PlayerFishEvent.class, "isCaughtFish", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> event(arguments).getState() == PlayerFishEvent.State.CAUGHT_FISH));
        builder.extension(PlayerFishEvent.class, "isCaughtEntity", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> event(arguments).getState() == PlayerFishEvent.State.CAUGHT_ENTITY));
        builder.extension(PlayerFishEvent.class, "isInGround", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> event(arguments).getState() == PlayerFishEvent.State.IN_GROUND));
        builder.extension(PlayerFishEvent.class, "isFailedAttempt", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> event(arguments).getState() == PlayerFishEvent.State.FAILED_ATTEMPT));
    }

    private static PlayerFishEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, PlayerFishEvent.class);
    }
}
