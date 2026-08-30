package com.novalang.bukkit.types.server;

import com.novalang.bukkit.types.value.NovaTypeSupport;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/** Spigot 1.12.2 OfflinePlayer 别名。 */
final class NovaOfflinePlayer {

    private NovaOfflinePlayer() {
    }

    static void register(JavaTypes.Builder b) {
        b.extension(OfflinePlayer.class, "isOnline", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, OfflinePlayer.class).isOnline()));
        b.extension(OfflinePlayer.class, "name", f -> f.returns(JavaTypeRef.javaType(String.class).nullable()).invoke(a -> NovaTypeSupport.argument(a, 0, OfflinePlayer.class).getName()));
        b.extension(OfflinePlayer.class, "uniqueId", f -> f.returns(JavaTypeRef.javaType(java.util.UUID.class)).invoke(a -> NovaTypeSupport.argument(a, 0, OfflinePlayer.class).getUniqueId()));
        b.extension(OfflinePlayer.class, "isBanned", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, OfflinePlayer.class).isBanned()));
        b.extension(OfflinePlayer.class, "isWhitelisted", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, OfflinePlayer.class).isWhitelisted()));
        b.extension(OfflinePlayer.class, "setWhitelisted", f -> f.param("whitelisted", Boolean.class).invoke(a -> { NovaTypeSupport.argument(a, 0, OfflinePlayer.class).setWhitelisted(NovaTypeSupport.argument(a, 1, Boolean.class)); return null; }));
        b.extension(OfflinePlayer.class, "player", f -> f.returns(JavaTypeRef.javaType(Player.class).nullable()).invoke(a -> NovaTypeSupport.argument(a, 0, OfflinePlayer.class).getPlayer()));
        b.extension(OfflinePlayer.class, "firstPlayed", f -> f.returns(Long.class).invoke(a -> NovaTypeSupport.argument(a, 0, OfflinePlayer.class).getFirstPlayed()));
        b.extension(OfflinePlayer.class, "lastPlayed", f -> f.returns(Long.class).invoke(a -> NovaTypeSupport.argument(a, 0, OfflinePlayer.class).getLastPlayed()));
        b.extension(OfflinePlayer.class, "hasPlayedBefore", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, OfflinePlayer.class).hasPlayedBefore()));
        b.extension(OfflinePlayer.class, "bedSpawnLocation", f -> f.returns(JavaTypeRef.javaType(Location.class).nullable()).invoke(a -> NovaTypeSupport.argument(a, 0, OfflinePlayer.class).getBedSpawnLocation()));
    }
}
