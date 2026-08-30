package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Material;
import org.bukkit.event.block.BlockCanBuildEvent;

/** 方块建造检查事件的可选 Spigot 1.12.2 类型别名。 */
@Requires(classes = {"org.bukkit.event.block.BlockCanBuildEvent"})
public final class NovaBlockCanBuildEvent {

    private NovaBlockCanBuildEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(BlockCanBuildEvent.class, "isBuildable", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> event(arguments).isBuildable()));
        builder.extension(BlockCanBuildEvent.class, "setBuildable", function -> function
                .param("buildable", Boolean.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setBuildable(NovaTypeSupport.argument(arguments, 1, Boolean.class));
                    return null;
                }));
        builder.extension(BlockCanBuildEvent.class, "material", function -> function
                .returns(Material.class)
                .invoke(arguments -> event(arguments).getMaterial()));
    }

    private static BlockCanBuildEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, BlockCanBuildEvent.class);
    }
}
