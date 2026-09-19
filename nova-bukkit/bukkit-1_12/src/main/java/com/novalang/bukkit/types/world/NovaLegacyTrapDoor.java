package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;
import org.bukkit.material.TrapDoor;

/** 旧版 TrapDoor 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.TrapDoor"})
final class NovaLegacyTrapDoor {

    private NovaLegacyTrapDoor() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(TrapDoor.class, "isOpen", function -> function.returns(Boolean.class).invoke(arguments -> trapDoor(arguments).isOpen()));
        builder.extension(TrapDoor.class, "setOpen", function -> function.param("open", Boolean.class).returns(Void.TYPE)
                .invoke(arguments -> { trapDoor(arguments).setOpen(NovaTypeSupport.argument(arguments, 1, Boolean.class)); return null; }));
        builder.extension(TrapDoor.class, "isInverted", function -> function.returns(Boolean.class).invoke(arguments -> trapDoor(arguments).isInverted()));
        builder.extension(TrapDoor.class, "setInverted", function -> function.param("inverted", Boolean.class).returns(Void.TYPE)
                .invoke(arguments -> { trapDoor(arguments).setInverted(NovaTypeSupport.argument(arguments, 1, Boolean.class)); return null; }));
        builder.extension(TrapDoor.class, "attachedFace", function -> function.returns(BlockFace.class).invoke(arguments -> trapDoor(arguments).getAttachedFace()));
        builder.extension(TrapDoor.class, "setFacingDirection", function -> function.param("face", BlockFace.class).returns(Void.TYPE)
                .invoke(arguments -> { trapDoor(arguments).setFacingDirection(NovaTypeSupport.argument(arguments, 1, BlockFace.class)); return null; }));
        builder.extension(TrapDoor.class, "toString", function -> function.returns(String.class).invoke(arguments -> trapDoor(arguments).toString()));
        builder.extension(TrapDoor.class, "clone", function -> function.returns(TrapDoor.class).invoke(arguments -> trapDoor(arguments).clone()));
    }

    private static TrapDoor trapDoor(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, TrapDoor.class);
    }
}
