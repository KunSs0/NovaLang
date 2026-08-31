package com.novalang.bukkit.types.world;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.CropState;
import org.bukkit.material.Crops;

/** Spigot 1.12.2 旧版农作物材料数据的 Fluxon 函数别名。 */
public final class NovaLegacyCrops {

    private NovaLegacyCrops() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Crops.class, "state", function -> function.returns(CropState.class)
                .invoke(arguments -> crops(arguments).getState()));
        builder.extension(Crops.class, "setState", function -> function.param("state", CropState.class).returns(Void.TYPE).invoke(arguments -> {
            crops(arguments).setState(argument(arguments, 1, CropState.class));
            return null;
        }));
        builder.extension(Crops.class, "toString", function -> function.returns(String.class)
                .invoke(arguments -> crops(arguments).toString()));
        builder.extension(Crops.class, "clone", function -> function.returns(Crops.class)
                .invoke(arguments -> crops(arguments).clone()));
    }

    private static Crops crops(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Crops.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
