package com.novalang.bukkit.types.platform;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/** Fluxon 计分板 API 的枚举字符串重载。 */
@Requires(classes = {"org.bukkit.scoreboard.Scoreboard"})
public final class NovaScoreboardStrings {

    private NovaScoreboardStrings() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Scoreboard.class, "clearSlot", function -> function
                .param("slot", String.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    DisplaySlot slot = NovaTypeSupport.findEnum(DisplaySlot.class, argument(arguments, 1, String.class));
                    if (slot != null) {
                        scoreboard(arguments).clearSlot(slot);
                    }
                    return null;
                }));
        builder.extension(Objective.class, "setDisplaySlot", function -> function
                .param("slot", String.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    DisplaySlot slot = NovaTypeSupport.findEnum(DisplaySlot.class, argument(arguments, 1, String.class));
                    if (slot != null) {
                        objective(arguments).setDisplaySlot(slot);
                    }
                    return null;
                }));
        builder.extension(Team.class, "setNameTagVisibility", function -> function
                .param("visibility", String.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    NameTagVisibility visibility = NovaTypeSupport.findEnum(NameTagVisibility.class, argument(arguments, 1, String.class));
                    if (visibility != null) {
                        team(arguments).setNameTagVisibility(visibility);
                    }
                    return null;
                }));
        builder.extension(Team.class, "getOption", function -> function
                .param("option", String.class)
                .returns(JavaTypeRef.javaType(Team.OptionStatus.class).nullable())
                .invoke(arguments -> {
                    Team.Option option = NovaTypeSupport.findEnum(Team.Option.class, argument(arguments, 1, String.class));
                    if (option == null) {
                        return null;
                    }
                    return team(arguments).getOption(option);
                }));
        builder.extension(Team.class, "setOption", function -> function
                .param("option", String.class)
                .param("status", Team.OptionStatus.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    Team.Option option = NovaTypeSupport.findEnum(Team.Option.class, argument(arguments, 1, String.class));
                    if (option != null) {
                        team(arguments).setOption(option, argument(arguments, 2, Team.OptionStatus.class));
                    }
                    return null;
                }));
        builder.extension(Team.class, "setOption", function -> function
                .param("option", Team.Option.class)
                .param("status", String.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    Team.OptionStatus status = NovaTypeSupport.findEnum(Team.OptionStatus.class, argument(arguments, 2, String.class));
                    if (status != null) {
                        team(arguments).setOption(argument(arguments, 1, Team.Option.class), status);
                    }
                    return null;
                }));
        builder.extension(Team.class, "setOption", function -> function
                .param("option", String.class)
                .param("status", String.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    Team.Option option = NovaTypeSupport.findEnum(Team.Option.class, argument(arguments, 1, String.class));
                    Team.OptionStatus status = NovaTypeSupport.findEnum(Team.OptionStatus.class, argument(arguments, 2, String.class));
                    if (option != null && status != null) {
                        team(arguments).setOption(option, status);
                    }
                    return null;
                }));
    }

    private static Scoreboard scoreboard(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Scoreboard.class);
    }

    private static Objective objective(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Objective.class);
    }

    private static Team team(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Team.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
