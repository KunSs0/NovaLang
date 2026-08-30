package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerExpChangeEvent;

/** 玩家经验变更事件的可选编译期别名。 */
@Requires(classes = {"org.bukkit.event.player.PlayerExpChangeEvent"})
public final class NovaPlayerExpChangeEvent {

    private NovaPlayerExpChangeEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PlayerExpChangeEvent.class, "player", function -> function
                .returns(Player.class)
                .invoke(arguments -> event(arguments).getPlayer()));
        builder.extension(PlayerExpChangeEvent.class, "amount", function -> function
                .returns(Integer.class)
                .invoke(arguments -> event(arguments).getAmount()));
        builder.extension(PlayerExpChangeEvent.class, "setAmount", function -> function
                .param("amount", Integer.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setAmount(NovaTypeSupport.argument(arguments, 1, Integer.class));
                    return null;
                }));
    }

    private static PlayerExpChangeEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, PlayerExpChangeEvent.class);
    }
}
