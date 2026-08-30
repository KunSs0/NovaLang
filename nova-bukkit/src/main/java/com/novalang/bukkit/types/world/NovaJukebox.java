package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Material;
import org.bukkit.block.Jukebox;

/** Jukebox 方块状态的 Spigot 1.12.2 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.block.Jukebox"})
final class NovaJukebox {

    private NovaJukebox() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Jukebox.class, "playing", function -> function.returns(Material.class)
                .invoke(arguments -> jukebox(arguments).getPlaying()));
        builder.extension(Jukebox.class, "setPlaying", function -> function.param("record", Material.class)
                .returns(Void.TYPE).invoke(arguments -> {
                    jukebox(arguments).setPlaying(NovaTypeSupport.argument(arguments, 1, Material.class));
                    return null;
                }));
        builder.extension(Jukebox.class, "isPlaying", function -> function.returns(Boolean.class)
                .invoke(arguments -> jukebox(arguments).isPlaying()));
        builder.extension(Jukebox.class, "eject", function -> function.returns(Boolean.class)
                .invoke(arguments -> jukebox(arguments).eject()));
    }

    private static Jukebox jukebox(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Jukebox.class);
    }
}
