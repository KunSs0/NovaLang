package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.types.value.NovaTypeSupport;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.net.InetSocketAddress;

/** Player 在 Spigot 1.12.2 中未由基础表覆盖的 Fluxon 别名。 */
final class NovaPlayerExtra {

    private NovaPlayerExtra() {
    }

    static void register(JavaTypes.Builder builder) {
        JavaTypeRef location = JavaTypeRef.javaType(Location.class);
        JavaTypeRef player = JavaTypeRef.javaType(Player.class);
        JavaTypeRef entity = JavaTypeRef.javaType(Entity.class);
        builder.extension(Player.class, "setDisplayName", f -> f.param("name", String.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Player.class).setDisplayName(NovaTypeSupport.argument(a, 1, String.class)); return null; }));
        builder.extension(Player.class, "setPlayerListName", f -> f.param("name", String.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Player.class).setPlayerListName(NovaTypeSupport.argument(a, 1, String.class)); return null; }));
        builder.extension(Player.class, "setCompassTarget", f -> f.param("location", location).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Player.class).setCompassTarget(NovaTypeSupport.argument(a, 1, Location.class)); return null; }));
        builder.extension(Player.class, "address", f -> f.returns(JavaTypeRef.javaType(InetSocketAddress.class).nullable()).invoke(a -> NovaTypeSupport.argument(a, 0, Player.class).getAddress()));
        builder.extension(Player.class, "sendRawMessage", f -> f.param("message", String.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Player.class).sendRawMessage(NovaTypeSupport.argument(a, 1, String.class)); return null; }));
        builder.extension(Player.class, "kickPlayer", f -> f.param("message", String.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Player.class).kickPlayer(NovaTypeSupport.argument(a, 1, String.class)); return null; }));
        builder.extension(Player.class, "chat", f -> f.param("message", String.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Player.class).chat(NovaTypeSupport.argument(a, 1, String.class)); return null; }));
        builder.extension(Player.class, "performCommand", f -> f.param("command", String.class).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Player.class).performCommand(NovaTypeSupport.argument(a, 1, String.class))));
        builder.extension(Player.class, "isSneaking", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Player.class).isSneaking()));
        builder.extension(Player.class, "setSneaking", f -> f.param("sneaking", Boolean.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Player.class).setSneaking(NovaTypeSupport.argument(a, 1, Boolean.class)); return null; }));
        builder.extension(Player.class, "isSprinting", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Player.class).isSprinting()));
        builder.extension(Player.class, "setSprinting", f -> f.param("sprinting", Boolean.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Player.class).setSprinting(NovaTypeSupport.argument(a, 1, Boolean.class)); return null; }));
        builder.extension(Player.class, "saveData", f -> f.returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Player.class).saveData(); return null; }));
        builder.extension(Player.class, "loadData", f -> f.returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Player.class).loadData(); return null; }));
        builder.extension(Player.class, "setSleepingIgnored", f -> f.param("ignored", Boolean.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Player.class).setSleepingIgnored(NovaTypeSupport.argument(a, 1, Boolean.class)); return null; }));
        builder.extension(Player.class, "isSleepingIgnored", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Player.class).isSleepingIgnored()));
        builder.extension(Player.class, "setPlayerTime", f -> f.param("time", Long.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Player.class).setPlayerTime(NovaTypeSupport.argument(a, 1, Long.class), false); return null; }));
        builder.extension(Player.class, "setPlayerTime", f -> f.param("time", Long.class).param("relative", Boolean.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Player.class).setPlayerTime(NovaTypeSupport.argument(a, 1, Long.class), NovaTypeSupport.argument(a, 2, Boolean.class)); return null; }));
        builder.extension(Player.class, "playerTimeOffset", f -> f.returns(Long.class).invoke(a -> NovaTypeSupport.argument(a, 0, Player.class).getPlayerTimeOffset()));
        builder.extension(Player.class, "isPlayerTimeRelative", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Player.class).isPlayerTimeRelative()));
        builder.extension(Player.class, "resetPlayerTime", f -> f.returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Player.class).resetPlayerTime(); return null; }));
        builder.extension(Player.class, "setPlayerWeather", f -> f.param("weather", org.bukkit.WeatherType.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Player.class).setPlayerWeather(NovaTypeSupport.argument(a, 1, org.bukkit.WeatherType.class)); return null; }));
        builder.extension(Player.class, "setPlayerWeather", f -> f.param("weather", String.class).returns(Void.TYPE).invoke(a -> {
            org.bukkit.WeatherType weather = NovaTypeSupport.findEnum(org.bukkit.WeatherType.class, NovaTypeSupport.argument(a, 1, String.class));
            if (weather == null) {
                return null;
            }
            NovaTypeSupport.argument(a, 0, Player.class).setPlayerWeather(weather);
            return null;
        }));
        builder.extension(Player.class, "resetPlayerWeather", f -> f.returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Player.class).resetPlayerWeather(); return null; }));
        builder.extension(Player.class, "giveExp", f -> f.param("amount", Integer.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Player.class).giveExp(NovaTypeSupport.argument(a, 1, Integer.class)); return null; }));
        builder.extension(Player.class, "giveExpLevels", f -> f.param("amount", Integer.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Player.class).giveExpLevels(NovaTypeSupport.argument(a, 1, Integer.class)); return null; }));
        builder.extension(Player.class, "exp", f -> f.returns(Float.class).invoke(a -> NovaTypeSupport.argument(a, 0, Player.class).getExp()));
        builder.extension(Player.class, "setExp", f -> f.param("exp", Float.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Player.class).setExp(NovaTypeSupport.argument(a, 1, Float.class)); return null; }));
        builder.extension(Player.class, "level", f -> f.returns(Integer.class).invoke(a -> NovaTypeSupport.argument(a, 0, Player.class).getLevel()));
        builder.extension(Player.class, "setLevel", f -> f.param("level", Integer.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Player.class).setLevel(NovaTypeSupport.argument(a, 1, Integer.class)); return null; }));
        builder.extension(Player.class, "totalExperience", f -> f.returns(Integer.class).invoke(a -> NovaTypeSupport.argument(a, 0, Player.class).getTotalExperience()));
        builder.extension(Player.class, "setTotalExperience", f -> f.param("experience", Integer.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Player.class).setTotalExperience(NovaTypeSupport.argument(a, 1, Integer.class)); return null; }));
        builder.extension(Player.class, "exhaustion", f -> f.returns(Float.class).invoke(a -> NovaTypeSupport.argument(a, 0, Player.class).getExhaustion()));
        builder.extension(Player.class, "setExhaustion", f -> f.param("exhaustion", Float.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Player.class).setExhaustion(NovaTypeSupport.argument(a, 1, Float.class)); return null; }));
        builder.extension(Player.class, "saturation", f -> f.returns(Float.class).invoke(a -> NovaTypeSupport.argument(a, 0, Player.class).getSaturation()));
        builder.extension(Player.class, "setSaturation", f -> f.param("saturation", Float.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Player.class).setSaturation(NovaTypeSupport.argument(a, 1, Float.class)); return null; }));
        builder.extension(Player.class, "foodLevel", f -> f.returns(Integer.class).invoke(a -> NovaTypeSupport.argument(a, 0, Player.class).getFoodLevel()));
        builder.extension(Player.class, "setFoodLevel", f -> f.param("food", Integer.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Player.class).setFoodLevel(NovaTypeSupport.argument(a, 1, Integer.class)); return null; }));
        builder.extension(Player.class, "allowFlight", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Player.class).getAllowFlight()));
        builder.extension(Player.class, "setAllowFlight", f -> f.param("allow", Boolean.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Player.class).setAllowFlight(NovaTypeSupport.argument(a, 1, Boolean.class)); return null; }));
        builder.extension(Player.class, "canSee", f -> f.param("player", player).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Player.class).canSee(NovaTypeSupport.argument(a, 1, Player.class))));
        builder.extension(Player.class, "isFlying", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Player.class).isFlying()));
        builder.extension(Player.class, "setFlying", f -> f.param("flying", Boolean.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Player.class).setFlying(NovaTypeSupport.argument(a, 1, Boolean.class)); return null; }));
        builder.extension(Player.class, "flySpeed", f -> f.returns(Float.class).invoke(a -> NovaTypeSupport.argument(a, 0, Player.class).getFlySpeed()));
        builder.extension(Player.class, "setFlySpeed", f -> f.param("speed", Float.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Player.class).setFlySpeed(NovaTypeSupport.argument(a, 1, Float.class)); return null; }));
        builder.extension(Player.class, "walkSpeed", f -> f.returns(Float.class).invoke(a -> NovaTypeSupport.argument(a, 0, Player.class).getWalkSpeed()));
        builder.extension(Player.class, "setWalkSpeed", f -> f.param("speed", Float.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Player.class).setWalkSpeed(NovaTypeSupport.argument(a, 1, Float.class)); return null; }));
        builder.extension(Player.class, "setTexturePack", f -> f.param("url", String.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Player.class).setTexturePack(NovaTypeSupport.argument(a, 1, String.class)); return null; }));
        builder.extension(Player.class, "setResourcePack", f -> f.param("url", String.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Player.class).setResourcePack(NovaTypeSupport.argument(a, 1, String.class)); return null; }));
        builder.extension(Player.class, "setResourcePack", f -> f.param("url", String.class).param("hash", byte[].class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Player.class).setResourcePack(NovaTypeSupport.argument(a, 1, String.class), NovaTypeSupport.argument(a, 2, byte[].class)); return null; }));
        builder.extension(Player.class, "scoreboard", f -> f.returns(Scoreboard.class).invoke(a -> NovaTypeSupport.argument(a, 0, Player.class).getScoreboard()));
        builder.extension(Player.class, "setScoreboard", f -> f.param("scoreboard", Scoreboard.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Player.class).setScoreboard(NovaTypeSupport.argument(a, 1, Scoreboard.class)); return null; }));
        builder.extension(Player.class, "isHealthScaled", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Player.class).isHealthScaled()));
        builder.extension(Player.class, "setHealthScaled", f -> f.param("scaled", Boolean.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Player.class).setHealthScaled(NovaTypeSupport.argument(a, 1, Boolean.class)); return null; }));
        builder.extension(Player.class, "setHealthScale", f -> f.param("scale", Double.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Player.class).setHealthScale(NovaTypeSupport.argument(a, 1, Double.class)); return null; }));
        builder.extension(Player.class, "spectatorTarget", f -> f.returns(JavaTypeRef.javaType(Entity.class).nullable()).invoke(a -> NovaTypeSupport.argument(a, 0, Player.class).getSpectatorTarget()));
        builder.extension(Player.class, "setSpectatorTarget", f -> f.param("target", entity).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Player.class).setSpectatorTarget(NovaTypeSupport.argument(a, 1, Entity.class)); return null; }));
        builder.extension(Player.class, "sendTitle", f -> f.param("title", String.class).param("subtitle", String.class).returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Player.class).sendTitle(NovaTypeSupport.argument(a, 1, String.class), NovaTypeSupport.argument(a, 2, String.class)); return null; }));
        builder.extension(Player.class, "resetTitle", f -> f.returns(Void.TYPE).invoke(a -> { NovaTypeSupport.argument(a, 0, Player.class).resetTitle(); return null; }));
        builder.extension(Player.class, "locale", f -> f.returns(JavaTypeRef.javaType(String.class).nullable()).invoke(a -> NovaTypeSupport.argument(a, 0, Player.class).getLocale()));
    }
}
