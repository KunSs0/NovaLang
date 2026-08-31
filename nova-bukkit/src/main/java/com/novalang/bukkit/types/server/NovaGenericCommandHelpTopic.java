package com.novalang.bukkit.types.server;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.command.CommandSender;
import org.bukkit.help.GenericCommandHelpTopic;

/** GenericCommandHelpTopic 的 Fluxon 可调用成员。 */
@Requires(classes = {"org.bukkit.help.GenericCommandHelpTopic"})
public final class NovaGenericCommandHelpTopic {

    private NovaGenericCommandHelpTopic() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(GenericCommandHelpTopic.class, "canSee", function -> function
                .param("sender", CommandSender.class)
                .returns(Boolean.class)
                .invoke(arguments -> topic(arguments).canSee(
                        NovaTypeSupport.argument(arguments, 1, CommandSender.class))));
    }

    private static GenericCommandHelpTopic topic(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, GenericCommandHelpTopic.class);
    }
}
