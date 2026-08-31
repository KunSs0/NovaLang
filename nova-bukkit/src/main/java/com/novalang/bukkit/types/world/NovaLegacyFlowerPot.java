package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.material.FlowerPot;
import org.bukkit.material.MaterialData;

/** 旧版 FlowerPot 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.FlowerPot"})
final class NovaLegacyFlowerPot {

    private NovaLegacyFlowerPot() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(FlowerPot.class, "contents", function -> function.returns(JavaTypeRef.javaType(MaterialData.class).nullable())
                .invoke(arguments -> pot(arguments).getContents()));
        builder.extension(FlowerPot.class, "setContents", function -> function.param("contents", MaterialData.class).returns(Void.TYPE)
                .invoke(arguments -> { pot(arguments).setContents(NovaTypeSupport.argument(arguments, 1, MaterialData.class)); return null; }));
        builder.extension(FlowerPot.class, "toString", function -> function.returns(String.class).invoke(arguments -> pot(arguments).toString()));
        builder.extension(FlowerPot.class, "clone", function -> function.returns(FlowerPot.class).invoke(arguments -> pot(arguments).clone()));
    }

    private static FlowerPot pot(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, FlowerPot.class);
    }
}
