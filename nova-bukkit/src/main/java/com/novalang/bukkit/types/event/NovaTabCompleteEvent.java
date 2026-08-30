package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.command.CommandSender;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.List;

/** 命令补全事件的可选 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.event.server.TabCompleteEvent"})
public final class NovaTabCompleteEvent {

    private NovaTabCompleteEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        JavaTypeRef completions = JavaTypeRef.listOf(JavaTypeRef.javaType(String.class));
        builder.extension(TabCompleteEvent.class, "sender", function -> function
                .returns(CommandSender.class)
                .invoke(arguments -> event(arguments).getSender()));
        builder.extension(TabCompleteEvent.class, "buffer", function -> function
                .returns(String.class)
                .invoke(arguments -> event(arguments).getBuffer()));
        builder.extension(TabCompleteEvent.class, "completions", function -> function
                .returns(completions)
                .invoke(arguments -> event(arguments).getCompletions()));
        builder.extension(TabCompleteEvent.class, "setCompletions", function -> function
                .param("completions", completions)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setCompletions(completions(arguments, 1));
                    return null;
                }));
    }

    private static TabCompleteEvent event(Object[] arguments) {
        return argument(arguments, 0, TabCompleteEvent.class);
    }

    @SuppressWarnings("unchecked")
    private static List<String> completions(Object[] arguments, int index) {
        return argument(arguments, index, List.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
