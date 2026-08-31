package com.novalang.bukkit.types.platform;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.OfflinePlayer;

/** 计分板目标检索的可选编译期别名。 */
@Requires(classes = {"org.bukkit.scoreboard.Scoreboard"})
public final class NovaScoreboardExtra {

    private NovaScoreboardExtra() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Scoreboard.class, "objectivesByCriteria", function -> function
                .param("criteria", String.class)
                .returns(JavaTypeRef.setOf(JavaTypeRef.javaType(Objective.class)))
                .invoke(arguments -> scoreboard(arguments).getObjectivesByCriteria(argument(arguments, 1, String.class))));
        builder.extension(Scoreboard.class, "getObjectivesByCriteria", function -> function
                .param("criteria", String.class)
                .returns(JavaTypeRef.setOf(JavaTypeRef.javaType(Objective.class)))
                .invoke(arguments -> scoreboard(arguments).getObjectivesByCriteria(argument(arguments, 1, String.class))));
        builder.extension(Scoreboard.class, "getObjective", function -> function
                .param("slot", DisplaySlot.class)
                .returns(JavaTypeRef.javaType(Objective.class).nullable())
                .invoke(arguments -> scoreboard(arguments).getObjective(argument(arguments, 1, DisplaySlot.class))));
        builder.extension(Scoreboard.class, "getScores", function -> function
                .param("player", OfflinePlayer.class)
                .returns(JavaTypeRef.setOf(JavaTypeRef.javaType(Score.class)))
                .invoke(arguments -> scoreboard(arguments).getScores(argument(arguments, 1, OfflinePlayer.class))));
        builder.extension(Scoreboard.class, "resetScores", function -> function
                .param("player", OfflinePlayer.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    scoreboard(arguments).resetScores(argument(arguments, 1, OfflinePlayer.class));
                    return null;
                }));
    }

    private static Scoreboard scoreboard(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Scoreboard.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
