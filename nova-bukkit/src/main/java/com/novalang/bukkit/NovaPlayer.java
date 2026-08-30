package com.novalang.bukkit;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

/** Bukkit Player 全局查询入口和 Fluxon 函数别名。 */
final class NovaPlayer {

    private NovaPlayer() {
    }

    static void register(JavaTypes.Builder builder) {
        registerGlobals(builder);
        registerExtensions(builder);
    }

    private static void registerGlobals(JavaTypes.Builder builder) {
        JavaTypeRef nullablePlayer = JavaTypeRef.javaType(Player.class).nullable();
        builder.globalFunction("player", function -> function
                .param("id", UUID.class)
                .returns(nullablePlayer)
                .invoke1(UUID.class, Bukkit::getPlayer));
        builder.globalFunction("player", function -> function
                .param("name", String.class)
                .returns(nullablePlayer)
                .invoke1(String.class, Bukkit::getPlayerExact));
        builder.globalFunction("players", function -> function
                .returns(JavaTypeRef.listOf(JavaTypeRef.javaType(Player.class)))
                .invoke0(() -> new ArrayList<Player>(Bukkit.getOnlinePlayers())));
    }

    private static void registerExtensions(JavaTypes.Builder builder) {
        builder.extension(Player.class, "name", function -> function
                .returns(String.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Player.class).getName()));
        builder.extension(Player.class, "displayName", function -> function
                .returns(String.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Player.class).getDisplayName()));
        builder.extension(Player.class, "playerListName", function -> function
                .returns(String.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Player.class).getPlayerListName()));
        builder.extension(Player.class, "compassTarget", function -> function
                .returns(Location.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Player.class).getCompassTarget()));
        builder.extension(Player.class, "bedSpawnLocation", function -> function
                .returns(JavaTypeRef.javaType(Location.class).nullable())
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Player.class).getBedSpawnLocation()));
        builder.extension(Player.class, "playerTime", function -> function
                .returns(Long.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Player.class).getPlayerTime()));
        builder.extension(Player.class, "playerWeather", function -> function
                .returns(JavaTypeRef.javaType(WeatherType.class).nullable())
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Player.class).getPlayerWeather()));
        builder.extension(Player.class, "healthScale", function -> function
                .returns(Double.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Player.class).getHealthScale()));
    }
}
