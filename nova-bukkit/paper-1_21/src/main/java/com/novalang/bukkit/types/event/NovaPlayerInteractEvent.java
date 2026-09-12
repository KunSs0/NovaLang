package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/** Spigot 1.12.2 PlayerInteractEvent 的 Fluxon 操作别名。 */
@Requires(classes = {"org.bukkit.event.player.PlayerInteractEvent"})
public final class NovaPlayerInteractEvent {

    private NovaPlayerInteractEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PlayerInteractEvent.class, "isLeftClick", function -> function.returns(Boolean.class).invoke(arguments -> {
            Action action = event(arguments).getAction();
            return action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK;
        }));
        builder.extension(PlayerInteractEvent.class, "isRightClick", function -> function.returns(Boolean.class).invoke(arguments -> {
            Action action = event(arguments).getAction();
            return action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
        }));
        builder.extension(PlayerInteractEvent.class, "isClickAir", function -> function.returns(Boolean.class).invoke(arguments -> {
            Action action = event(arguments).getAction();
            return action == Action.LEFT_CLICK_AIR || action == Action.RIGHT_CLICK_AIR;
        }));
        builder.extension(PlayerInteractEvent.class, "isClickBlock", function -> function.returns(Boolean.class).invoke(arguments -> {
            Action action = event(arguments).getAction();
            return action == Action.LEFT_CLICK_BLOCK || action == Action.RIGHT_CLICK_BLOCK;
        }));
        builder.extension(PlayerInteractEvent.class, "isPhysical", function -> function.returns(Boolean.class).invoke(arguments -> event(arguments).getAction() == Action.PHYSICAL));
        builder.extension(PlayerInteractEvent.class, "isBlockPlace", function -> function.returns(Boolean.class).invoke(arguments -> event(arguments).getAction() == Action.RIGHT_CLICK_BLOCK));
        builder.extension(PlayerInteractEvent.class, "isBlockInHand", function -> function.returns(Boolean.class).invoke(arguments -> event(arguments).isBlockInHand()));
        builder.extension(PlayerInteractEvent.class, "setUseInteractedBlock", function -> function.param("result", Event.Result.class).returns(Void.TYPE).invoke(arguments -> {
            event(arguments).setUseInteractedBlock(NovaTypeSupport.argument(arguments, 1, Event.Result.class));
            return null;
        }));
        builder.extension(PlayerInteractEvent.class, "setUseItemInHand", function -> function.param("result", Event.Result.class).returns(Void.TYPE).invoke(arguments -> {
            event(arguments).setUseItemInHand(NovaTypeSupport.argument(arguments, 1, Event.Result.class));
            return null;
        }));
    }

    private static PlayerInteractEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, PlayerInteractEvent.class);
    }
}
