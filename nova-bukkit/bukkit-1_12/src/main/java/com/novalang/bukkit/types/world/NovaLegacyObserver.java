package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Observer;

/** 旧版 Observer 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.Observer"})
final class NovaLegacyObserver {

    private NovaLegacyObserver() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Observer.class, "isPowered", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> observer(arguments).isPowered()));
        builder.extension(Observer.class, "setFacingDirection", function -> function
                .param("face", BlockFace.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    observer(arguments).setFacingDirection(NovaTypeSupport.argument(arguments, 1, BlockFace.class));
                    return null;
                }));
        builder.extension(Observer.class, "facing", function -> function
                .returns(BlockFace.class)
                .invoke(arguments -> observer(arguments).getFacing()));
        builder.extension(Observer.class, "toString", function -> function
                .returns(String.class)
                .invoke(arguments -> observer(arguments).toString()));
        builder.extension(Observer.class, "clone", function -> function
                .returns(Observer.class)
                .invoke(arguments -> observer(arguments).clone()));
    }

    private static Observer observer(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Observer.class);
    }
}
