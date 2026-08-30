package com.novalang.bukkit.types.event;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.EnderDragon;
import org.bukkit.event.entity.EnderDragonChangePhaseEvent;

/** Spigot 1.12.2 末影龙阶段变化事件别名。 */
public final class NovaEnderDragonChangePhaseEvent {

    private NovaEnderDragonChangePhaseEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(EnderDragonChangePhaseEvent.class, "setNewPhase", function -> function
                .param("phase", EnderDragon.Phase.class).returns(Void.TYPE).invoke(arguments -> {
                    event(arguments).setNewPhase(argument(arguments, 1, EnderDragon.Phase.class));
                    return null;
                }));
    }

    private static EnderDragonChangePhaseEvent event(Object[] arguments) {
        return argument(arguments, 0, EnderDragonChangePhaseEvent.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
