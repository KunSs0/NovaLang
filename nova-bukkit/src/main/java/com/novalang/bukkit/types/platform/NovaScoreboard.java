package com.novalang.bukkit.types.platform;

import com.novalang.bukkit.types.value.NovaTypeSupport;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import java.util.Set;

/** Spigot 1.12.2 Scoreboard API 的 Fluxon 别名。 */
final class NovaScoreboard {

    private NovaScoreboard() {
    }

    static void register(JavaTypes.Builder b) {
        b.extension(ScoreboardManager.class, "mainScoreboard", f -> f.returns(Scoreboard.class).invoke(a -> NovaTypeSupport.argument(a, 0, ScoreboardManager.class).getMainScoreboard()));
        b.extension(ScoreboardManager.class, "newScoreboard", f -> f.returns(Scoreboard.class).invoke(a -> NovaTypeSupport.argument(a, 0, ScoreboardManager.class).getNewScoreboard()));
        b.extension(Scoreboard.class, "registerNewObjective", f -> f.param("name", String.class).param("criteria", String.class).returns(Objective.class).invoke(a -> NovaTypeSupport.argument(a, 0, Scoreboard.class).registerNewObjective(NovaTypeSupport.argument(a, 1, String.class), NovaTypeSupport.argument(a, 2, String.class))));
        b.extension(Scoreboard.class, "getObjective", f -> f.param("name", String.class).returns(JavaTypeRef.javaType(Objective.class).nullable()).invoke(a -> NovaTypeSupport.argument(a, 0, Scoreboard.class).getObjective(NovaTypeSupport.argument(a, 1, String.class))));
        b.extension(Scoreboard.class, "objectives", f -> f.returns(JavaTypeRef.javaType(Set.class)).invoke(a -> NovaTypeSupport.argument(a, 0, Scoreboard.class).getObjectives()));
        b.extension(Scoreboard.class, "getScores", f -> f.param("entry", String.class).returns(JavaTypeRef.javaType(Set.class)).invoke(a -> NovaTypeSupport.argument(a, 0, Scoreboard.class).getScores(NovaTypeSupport.argument(a, 1, String.class))));
        b.extension(Scoreboard.class, "resetScores", f -> f.param("entry", String.class).invoke(a -> { NovaTypeSupport.argument(a, 0, Scoreboard.class).resetScores(NovaTypeSupport.argument(a, 1, String.class)); return null; }));
        b.extension(Scoreboard.class, "getPlayerTeam", f -> f.param("player", OfflinePlayer.class).returns(JavaTypeRef.javaType(Team.class).nullable()).invoke(a -> NovaTypeSupport.argument(a, 0, Scoreboard.class).getPlayerTeam(NovaTypeSupport.argument(a, 1, OfflinePlayer.class))));
        b.extension(Scoreboard.class, "getEntryTeam", f -> f.param("entry", String.class).returns(JavaTypeRef.javaType(Team.class).nullable()).invoke(a -> NovaTypeSupport.argument(a, 0, Scoreboard.class).getEntryTeam(NovaTypeSupport.argument(a, 1, String.class))));
        b.extension(Scoreboard.class, "getTeam", f -> f.param("name", String.class).returns(JavaTypeRef.javaType(Team.class).nullable()).invoke(a -> NovaTypeSupport.argument(a, 0, Scoreboard.class).getTeam(NovaTypeSupport.argument(a, 1, String.class))));
        b.extension(Scoreboard.class, "teams", f -> f.returns(JavaTypeRef.javaType(Set.class)).invoke(a -> NovaTypeSupport.argument(a, 0, Scoreboard.class).getTeams()));
        b.extension(Scoreboard.class, "registerNewTeam", f -> f.param("name", String.class).returns(Team.class).invoke(a -> NovaTypeSupport.argument(a, 0, Scoreboard.class).registerNewTeam(NovaTypeSupport.argument(a, 1, String.class))));
        b.extension(Scoreboard.class, "players", f -> f.returns(JavaTypeRef.javaType(Set.class)).invoke(a -> NovaTypeSupport.argument(a, 0, Scoreboard.class).getPlayers()));
        b.extension(Scoreboard.class, "entries", f -> f.returns(JavaTypeRef.javaType(Set.class)).invoke(a -> NovaTypeSupport.argument(a, 0, Scoreboard.class).getEntries()));
        b.extension(Scoreboard.class, "clearSlot", f -> f.param("slot", DisplaySlot.class).invoke(a -> { NovaTypeSupport.argument(a, 0, Scoreboard.class).clearSlot(NovaTypeSupport.argument(a, 1, DisplaySlot.class)); return null; }));

        b.extension(Objective.class, "name", f -> f.returns(String.class).invoke(a -> NovaTypeSupport.argument(a, 0, Objective.class).getName()));
        b.extension(Objective.class, "displayName", f -> f.returns(String.class).invoke(a -> NovaTypeSupport.argument(a, 0, Objective.class).getDisplayName()));
        b.extension(Objective.class, "criteria", f -> f.returns(String.class).invoke(a -> NovaTypeSupport.argument(a, 0, Objective.class).getCriteria()));
        b.extension(Objective.class, "displaySlot", f -> f.returns(JavaTypeRef.javaType(DisplaySlot.class).nullable()).invoke(a -> NovaTypeSupport.argument(a, 0, Objective.class).getDisplaySlot()));
        b.extension(Objective.class, "scoreboard", f -> f.returns(Scoreboard.class).invoke(a -> NovaTypeSupport.argument(a, 0, Objective.class).getScoreboard()));
        b.extension(Objective.class, "setDisplayName", f -> f.param("name", String.class).invoke(a -> { NovaTypeSupport.argument(a, 0, Objective.class).setDisplayName(NovaTypeSupport.argument(a, 1, String.class)); return null; }));
        b.extension(Objective.class, "setDisplaySlot", f -> f.param("slot", DisplaySlot.class).invoke(a -> { NovaTypeSupport.argument(a, 0, Objective.class).setDisplaySlot(NovaTypeSupport.argument(a, 1, DisplaySlot.class)); return null; }));
        b.extension(Objective.class, "getScore", f -> f.param("entry", String.class).returns(Score.class).invoke(a -> NovaTypeSupport.argument(a, 0, Objective.class).getScore(NovaTypeSupport.argument(a, 1, String.class))));

        b.extension(Score.class, "entry", f -> f.returns(String.class).invoke(a -> NovaTypeSupport.argument(a, 0, Score.class).getEntry()));
        b.extension(Score.class, "player", f -> f.returns(OfflinePlayer.class).invoke(a -> NovaTypeSupport.argument(a, 0, Score.class).getPlayer()));
        b.extension(Score.class, "score", f -> f.returns(Integer.class).invoke(a -> NovaTypeSupport.argument(a, 0, Score.class).getScore()));
        b.extension(Score.class, "isScoreSet", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Score.class).isScoreSet()));
        b.extension(Score.class, "objective", f -> f.returns(Objective.class).invoke(a -> NovaTypeSupport.argument(a, 0, Score.class).getObjective()));
        b.extension(Score.class, "scoreboard", f -> f.returns(Scoreboard.class).invoke(a -> NovaTypeSupport.argument(a, 0, Score.class).getScoreboard()));
        b.extension(Score.class, "setScore", f -> f.param("score", Integer.class).invoke(a -> { NovaTypeSupport.argument(a, 0, Score.class).setScore(NovaTypeSupport.argument(a, 1, Integer.class)); return null; }));

        b.extension(Team.class, "name", f -> f.returns(String.class).invoke(a -> NovaTypeSupport.argument(a, 0, Team.class).getName()));
        b.extension(Team.class, "displayName", f -> f.returns(String.class).invoke(a -> NovaTypeSupport.argument(a, 0, Team.class).getDisplayName()));
        b.extension(Team.class, "prefix", f -> f.returns(String.class).invoke(a -> NovaTypeSupport.argument(a, 0, Team.class).getPrefix()));
        b.extension(Team.class, "suffix", f -> f.returns(String.class).invoke(a -> NovaTypeSupport.argument(a, 0, Team.class).getSuffix()));
        b.extension(Team.class, "allowFriendlyFire", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Team.class).allowFriendlyFire()));
        b.extension(Team.class, "canSeeFriendlyInvisibles", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Team.class).canSeeFriendlyInvisibles()));
        b.extension(Team.class, "players", f -> f.returns(JavaTypeRef.javaType(Set.class)).invoke(a -> NovaTypeSupport.argument(a, 0, Team.class).getPlayers()));
        b.extension(Team.class, "entries", f -> f.returns(JavaTypeRef.javaType(Set.class)).invoke(a -> NovaTypeSupport.argument(a, 0, Team.class).getEntries()));
        b.extension(Team.class, "size", f -> f.returns(Integer.class).invoke(a -> NovaTypeSupport.argument(a, 0, Team.class).getSize()));
        b.extension(Team.class, "scoreboard", f -> f.returns(Scoreboard.class).invoke(a -> NovaTypeSupport.argument(a, 0, Team.class).getScoreboard()));
        b.extension(Team.class, "addPlayer", f -> f.param("player", OfflinePlayer.class).invoke(a -> { NovaTypeSupport.argument(a, 0, Team.class).addPlayer(NovaTypeSupport.argument(a, 1, OfflinePlayer.class)); return null; }));
        b.extension(Team.class, "removePlayer", f -> f.param("player", OfflinePlayer.class).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Team.class).removePlayer(NovaTypeSupport.argument(a, 1, OfflinePlayer.class))));
        b.extension(Team.class, "addEntry", f -> f.param("entry", String.class).invoke(a -> { NovaTypeSupport.argument(a, 0, Team.class).addEntry(NovaTypeSupport.argument(a, 1, String.class)); return null; }));
        b.extension(Team.class, "removeEntry", f -> f.param("entry", String.class).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Team.class).removeEntry(NovaTypeSupport.argument(a, 1, String.class))));
        b.extension(Team.class, "hasPlayer", f -> f.param("player", OfflinePlayer.class).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Team.class).hasPlayer(NovaTypeSupport.argument(a, 1, OfflinePlayer.class))));
        b.extension(Team.class, "hasEntry", f -> f.param("entry", String.class).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Team.class).hasEntry(NovaTypeSupport.argument(a, 1, String.class))));
    }
}
