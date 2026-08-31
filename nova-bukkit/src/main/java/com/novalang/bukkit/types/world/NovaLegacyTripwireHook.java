package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;
import org.bukkit.material.TripwireHook;

/** 旧版 TripwireHook 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.TripwireHook"})
final class NovaLegacyTripwireHook {

    private NovaLegacyTripwireHook() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(TripwireHook.class, "isConnected", function -> function.returns(Boolean.class)
                .invoke(arguments -> hook(arguments).isConnected()));
        builder.extension(TripwireHook.class, "setConnected", function -> function.param("connected", Boolean.class).returns(Void.TYPE)
                .invoke(arguments -> { hook(arguments).setConnected(NovaTypeSupport.argument(arguments, 1, Boolean.class)); return null; }));
        builder.extension(TripwireHook.class, "isActivated", function -> function.returns(Boolean.class)
                .invoke(arguments -> hook(arguments).isActivated()));
        builder.extension(TripwireHook.class, "setActivated", function -> function.param("activated", Boolean.class).returns(Void.TYPE)
                .invoke(arguments -> { hook(arguments).setActivated(NovaTypeSupport.argument(arguments, 1, Boolean.class)); return null; }));
        builder.extension(TripwireHook.class, "setFacingDirection", function -> function.param("face", BlockFace.class).returns(Void.TYPE)
                .invoke(arguments -> { hook(arguments).setFacingDirection(NovaTypeSupport.argument(arguments, 1, BlockFace.class)); return null; }));
        builder.extension(TripwireHook.class, "attachedFace", function -> function.returns(BlockFace.class)
                .invoke(arguments -> hook(arguments).getAttachedFace()));
        builder.extension(TripwireHook.class, "isPowered", function -> function.returns(Boolean.class)
                .invoke(arguments -> hook(arguments).isPowered()));
        builder.extension(TripwireHook.class, "clone", function -> function.returns(TripwireHook.class)
                .invoke(arguments -> hook(arguments).clone()));
        builder.extension(TripwireHook.class, "toString", function -> function.returns(String.class)
                .invoke(arguments -> hook(arguments).toString()));
    }

    private static TripwireHook hook(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, TripwireHook.class);
    }
}
