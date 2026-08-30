package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.entity.EntityRegainHealthEvent;

/** 实体生命恢复事件的可选编译期别名。 */
@Requires(classes = {"org.bukkit.event.entity.EntityRegainHealthEvent"})
public final class NovaEntityHealthEvent {

    private NovaEntityHealthEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(EntityRegainHealthEvent.class, "amount", function -> function
                .returns(Double.class)
                .invoke(arguments -> event(arguments).getAmount()));
        builder.extension(EntityRegainHealthEvent.class, "setAmount", function -> function
                .param("amount", Double.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setAmount(argument(arguments, 1, Double.class));
                    return null;
                }));
        builder.extension(EntityRegainHealthEvent.class, "regainReason", function -> function
                .returns(EntityRegainHealthEvent.RegainReason.class)
                .invoke(arguments -> event(arguments).getRegainReason()));
    }

    private static EntityRegainHealthEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, EntityRegainHealthEvent.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
