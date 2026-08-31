package com.novalang.bukkit.types.server;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.command.CommandSender;
import org.bukkit.help.IndexHelpTopic;

/** IndexHelpTopic 的 Fluxon 可调用成员。 */
@Requires(classes = {"org.bukkit.help.IndexHelpTopic"})
public final class NovaIndexHelpTopic {

    private NovaIndexHelpTopic() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(IndexHelpTopic.class, "canSee", function -> function
                .param("sender", CommandSender.class)
                .returns(Boolean.class)
                .invoke(arguments -> topic(arguments).canSee(
                        NovaTypeSupport.argument(arguments, 1, CommandSender.class))));
        builder.extension(IndexHelpTopic.class, "amendCanSee", function -> function
                .param("permission", String.class)
                .invoke(arguments -> {
                    topic(arguments).amendCanSee(NovaTypeSupport.argument(arguments, 1, String.class));
                    return null;
                }));
        builder.extension(IndexHelpTopic.class, "getFullText", function -> function
                .param("sender", CommandSender.class)
                .returns(String.class)
                .invoke(arguments -> topic(arguments).getFullText(
                        NovaTypeSupport.argument(arguments, 1, CommandSender.class))));
    }

    private static IndexHelpTopic topic(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, IndexHelpTopic.class);
    }
}
