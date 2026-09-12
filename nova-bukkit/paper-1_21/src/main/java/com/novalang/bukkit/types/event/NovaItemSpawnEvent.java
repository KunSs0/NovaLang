package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Item;
import org.bukkit.event.entity.ItemSpawnEvent;

/** 掉落实体生成事件的可选 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.event.entity.ItemSpawnEvent"})
public final class NovaItemSpawnEvent {

    private NovaItemSpawnEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(ItemSpawnEvent.class, "entity", function -> function
                .returns(Item.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, ItemSpawnEvent.class).getEntity()));
    }
}
