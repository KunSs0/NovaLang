package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Door;

/** 旧版 Door 材料数据的 Fluxon 实例函数别名。 */
@Requires(classes = {"org.bukkit.material.Door"})
final class NovaLegacyDoor {

    private NovaLegacyDoor() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Door.class, "isOpen", function -> function.returns(Boolean.class).invoke(arguments -> door(arguments).isOpen()));
        builder.extension(Door.class, "setOpen", function -> function.param("open", Boolean.class).returns(Void.TYPE)
                .invoke(arguments -> { door(arguments).setOpen(NovaTypeSupport.argument(arguments, 1, Boolean.class)); return null; }));
        builder.extension(Door.class, "isTopHalf", function -> function.returns(Boolean.class).invoke(arguments -> door(arguments).isTopHalf()));
        builder.extension(Door.class, "setTopHalf", function -> function.param("topHalf", Boolean.class).returns(Void.TYPE)
                .invoke(arguments -> { door(arguments).setTopHalf(NovaTypeSupport.argument(arguments, 1, Boolean.class)); return null; }));
        builder.extension(Door.class, "hingeCorner", function -> function.returns(BlockFace.class).invoke(arguments -> door(arguments).getHingeCorner()));
        builder.extension(Door.class, "toString", function -> function.returns(String.class).invoke(arguments -> door(arguments).toString()));
        builder.extension(Door.class, "setFacingDirection", function -> function.param("face", BlockFace.class).returns(Void.TYPE)
                .invoke(arguments -> { door(arguments).setFacingDirection(NovaTypeSupport.argument(arguments, 1, BlockFace.class)); return null; }));
        builder.extension(Door.class, "facing", function -> function.returns(BlockFace.class).invoke(arguments -> door(arguments).getFacing()));
        builder.extension(Door.class, "hinge", function -> function.returns(Boolean.class).invoke(arguments -> door(arguments).getHinge()));
        builder.extension(Door.class, "setHinge", function -> function.param("hinge", Boolean.class).returns(Void.TYPE)
                .invoke(arguments -> { door(arguments).setHinge(NovaTypeSupport.argument(arguments, 1, Boolean.class)); return null; }));
        builder.extension(Door.class, "clone", function -> function.returns(Door.class).invoke(arguments -> door(arguments).clone()));
    }

    private static Door door(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Door.class);
    }
}
