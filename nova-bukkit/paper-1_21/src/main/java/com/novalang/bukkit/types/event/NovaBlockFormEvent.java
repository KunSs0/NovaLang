package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockState;
import org.bukkit.event.block.BlockFormEvent;

/** 方块形成事件的可选 Spigot 1.12.2 类型别名。 */
@Requires(classes = {"org.bukkit.event.block.BlockFormEvent"})
public final class NovaBlockFormEvent {

    private NovaBlockFormEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(BlockFormEvent.class, "newState", function -> function
                .returns(BlockState.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, BlockFormEvent.class).getNewState()));
    }
}
