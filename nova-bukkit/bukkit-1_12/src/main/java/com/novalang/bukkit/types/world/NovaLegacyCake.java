package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.material.Cake;

/** 旧版 Cake 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.Cake"})
final class NovaLegacyCake {

    private NovaLegacyCake() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Cake.class, "slicesEaten", function -> function.returns(Integer.class).invoke(arguments -> cake(arguments).getSlicesEaten()));
        builder.extension(Cake.class, "slicesRemaining", function -> function.returns(Integer.class).invoke(arguments -> cake(arguments).getSlicesRemaining()));
        builder.extension(Cake.class, "setSlicesEaten", function -> function.param("slices", Integer.class).returns(Void.TYPE)
                .invoke(arguments -> { cake(arguments).setSlicesEaten(NovaTypeSupport.argument(arguments, 1, Integer.class)); return null; }));
        builder.extension(Cake.class, "setSlicesRemaining", function -> function.param("slices", Integer.class).returns(Void.TYPE)
                .invoke(arguments -> { cake(arguments).setSlicesRemaining(NovaTypeSupport.argument(arguments, 1, Integer.class)); return null; }));
        builder.extension(Cake.class, "toString", function -> function.returns(String.class).invoke(arguments -> cake(arguments).toString()));
        builder.extension(Cake.class, "clone", function -> function.returns(Cake.class).invoke(arguments -> cake(arguments).clone()));
    }

    private static Cake cake(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Cake.class);
    }
}
