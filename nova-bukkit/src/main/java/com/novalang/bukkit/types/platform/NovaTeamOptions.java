package com.novalang.bukkit.types.platform;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Team;

/** 计分板队伍外观与选项的可选编译期别名。 */
@Requires(classes = {"org.bukkit.scoreboard.Team"})
@SuppressWarnings("deprecation")
public final class NovaTeamOptions {

    private NovaTeamOptions() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Team.class, "setDisplayName", function -> function
                .param("name", String.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    team(arguments).setDisplayName(argument(arguments, 1, String.class));
                    return null;
                }));
        builder.extension(Team.class, "setPrefix", function -> function
                .param("prefix", String.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    team(arguments).setPrefix(argument(arguments, 1, String.class));
                    return null;
                }));
        builder.extension(Team.class, "setSuffix", function -> function
                .param("suffix", String.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    team(arguments).setSuffix(argument(arguments, 1, String.class));
                    return null;
                }));
        builder.extension(Team.class, "color", function -> function
                .returns(ChatColor.class)
                .invoke(arguments -> team(arguments).getColor()));
        builder.extension(Team.class, "setColor", function -> function
                .param("color", ChatColor.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    team(arguments).setColor(argument(arguments, 1, ChatColor.class));
                    return null;
                }));
        builder.extension(Team.class, "setAllowFriendlyFire", function -> function
                .param("allowed", Boolean.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    team(arguments).setAllowFriendlyFire(argument(arguments, 1, Boolean.class));
                    return null;
                }));
        builder.extension(Team.class, "setCanSeeFriendlyInvisibles", function -> function
                .param("enabled", Boolean.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    team(arguments).setCanSeeFriendlyInvisibles(argument(arguments, 1, Boolean.class));
                    return null;
                }));
        builder.extension(Team.class, "nameTagVisibility", function -> function
                .returns(NameTagVisibility.class)
                .invoke(arguments -> team(arguments).getNameTagVisibility()));
        builder.extension(Team.class, "setNameTagVisibility", function -> function
                .param("visibility", NameTagVisibility.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    team(arguments).setNameTagVisibility(argument(arguments, 1, NameTagVisibility.class));
                    return null;
                }));
        builder.extension(Team.class, "getOption", function -> function
                .param("option", Team.Option.class)
                .returns(Team.OptionStatus.class)
                .invoke(arguments -> team(arguments).getOption(argument(arguments, 1, Team.Option.class))));
        builder.extension(Team.class, "setOption", function -> function
                .param("option", Team.Option.class)
                .param("status", Team.OptionStatus.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    team(arguments).setOption(
                            argument(arguments, 1, Team.Option.class),
                            argument(arguments, 2, Team.OptionStatus.class));
                    return null;
                }));
        builder.extension(Team.class, "unregister", function -> function
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    team(arguments).unregister();
                    return null;
                }));
    }

    private static Team team(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Team.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
