package com.novalang.bukkit.types.event;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Entity;
import org.bukkit.event.block.CauldronLevelChangeEvent;

/** Spigot 1.12.2 炼药锅液位变化事件别名。 */
public final class NovaCauldronLevelChangeEvent {

    private NovaCauldronLevelChangeEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(CauldronLevelChangeEvent.class, "entity", function -> function
                .returns(JavaTypeRef.javaType(Entity.class).nullable())
                .invoke(arguments -> event(arguments).getEntity()));
        builder.extension(CauldronLevelChangeEvent.class, "reason", function -> function
                .returns(CauldronLevelChangeEvent.ChangeReason.class)
                .invoke(arguments -> event(arguments).getReason()));
        builder.extension(CauldronLevelChangeEvent.class, "oldLevel", function -> function
                .returns(Integer.class).invoke(arguments -> event(arguments).getOldLevel()));
        builder.extension(CauldronLevelChangeEvent.class, "newLevel", function -> function
                .returns(Integer.class).invoke(arguments -> event(arguments).getNewLevel()));
        builder.extension(CauldronLevelChangeEvent.class, "setNewLevel", function -> function
                .param("level", Integer.class).returns(Void.TYPE).invoke(arguments -> {
                    event(arguments).setNewLevel(argument(arguments, 1, Integer.class));
                    return null;
                }));
    }

    private static CauldronLevelChangeEvent event(Object[] arguments) {
        return argument(arguments, 0, CauldronLevelChangeEvent.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
