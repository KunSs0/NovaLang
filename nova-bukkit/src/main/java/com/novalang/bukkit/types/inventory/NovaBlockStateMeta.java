package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.meta.BlockStateMeta;

@Requires(classes = {"org.bukkit.inventory.meta.BlockStateMeta"})
public final class NovaBlockStateMeta {
    private NovaBlockStateMeta() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(BlockStateMeta.class, "hasBlockState", function -> function.returns(Boolean.class)
                .invoke(arguments -> meta(arguments).hasBlockState()));
        builder.extension(BlockStateMeta.class, "setBlockState", function -> function.param("state", BlockState.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    meta(arguments).setBlockState(NovaTypeSupport.argument(arguments, 1, BlockState.class));
                    return null;
                }));
    }

    private static BlockStateMeta meta(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, BlockStateMeta.class);
    }
}
