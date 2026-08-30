package com.novalang.bukkit.types.world;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Diode;

/** Spigot 1.12.2 旧版红石二极管材料数据的 Fluxon 函数别名。 */
public final class NovaLegacyDiode {

    private NovaLegacyDiode() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Diode.class, "setDelay", function -> function.param("delay", Integer.class).returns(Void.TYPE).invoke(arguments -> {
            diode(arguments).setDelay(argument(arguments, 1, Integer.class));
            return null;
        }));
        builder.extension(Diode.class, "delay", function -> function.returns(Integer.class)
                .invoke(arguments -> diode(arguments).getDelay()));
        builder.extension(Diode.class, "setFacingDirection", function -> function.param("face", BlockFace.class).returns(Void.TYPE).invoke(arguments -> {
            diode(arguments).setFacingDirection(argument(arguments, 1, BlockFace.class));
            return null;
        }));
        builder.extension(Diode.class, "facing", function -> function.returns(BlockFace.class)
                .invoke(arguments -> diode(arguments).getFacing()));
        builder.extension(Diode.class, "isPowered", function -> function.returns(Boolean.class)
                .invoke(arguments -> diode(arguments).isPowered()));
    }

    private static Diode diode(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Diode.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
