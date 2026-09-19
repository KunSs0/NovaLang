package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

/** 玩家死亡事件的可选编译期别名。 */
@Requires(classes = {"org.bukkit.event.entity.PlayerDeathEvent"})
public final class NovaPlayerDeathEvent {

    private NovaPlayerDeathEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PlayerDeathEvent.class, "player", function -> function
                .returns(Player.class)
                .invoke(arguments -> event(arguments).getEntity()));
        builder.extension(PlayerDeathEvent.class, "deathMessage", function -> function
                .returns(String.class)
                .invoke(arguments -> event(arguments).getDeathMessage()));
        builder.extension(PlayerDeathEvent.class, "setDeathMessage", function -> function
                .param("message", String.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setDeathMessage(argument(arguments, 1, String.class));
                    return null;
                }));
        builder.extension(PlayerDeathEvent.class, "newExp", function -> function
                .returns(Integer.class)
                .invoke(arguments -> event(arguments).getNewExp()));
        builder.extension(PlayerDeathEvent.class, "setNewExp", function -> function
                .param("experience", Integer.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setNewExp(argument(arguments, 1, Integer.class));
                    return null;
                }));
        builder.extension(PlayerDeathEvent.class, "newLevel", function -> function
                .returns(Integer.class)
                .invoke(arguments -> event(arguments).getNewLevel()));
        builder.extension(PlayerDeathEvent.class, "setNewLevel", function -> function
                .param("level", Integer.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setNewLevel(argument(arguments, 1, Integer.class));
                    return null;
                }));
        builder.extension(PlayerDeathEvent.class, "newTotalExp", function -> function
                .returns(Integer.class)
                .invoke(arguments -> event(arguments).getNewTotalExp()));
        builder.extension(PlayerDeathEvent.class, "setNewTotalExp", function -> function
                .param("totalExperience", Integer.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setNewTotalExp(argument(arguments, 1, Integer.class));
                    return null;
                }));
        builder.extension(PlayerDeathEvent.class, "keepLevel", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> event(arguments).getKeepLevel()));
        builder.extension(PlayerDeathEvent.class, "setKeepLevel", function -> function
                .param("keep", Boolean.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setKeepLevel(argument(arguments, 1, Boolean.class));
                    return null;
                }));
        builder.extension(PlayerDeathEvent.class, "keepInventory", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> event(arguments).getKeepInventory()));
        builder.extension(PlayerDeathEvent.class, "setKeepInventory", function -> function
                .param("keep", Boolean.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setKeepInventory(argument(arguments, 1, Boolean.class));
                    return null;
                }));
    }

    private static PlayerDeathEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, PlayerDeathEvent.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
