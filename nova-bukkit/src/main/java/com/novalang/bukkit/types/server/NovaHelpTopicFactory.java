package com.novalang.bukkit.types.server;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.command.Command;
import org.bukkit.help.HelpTopic;
import org.bukkit.help.HelpTopicFactory;

/** HelpTopicFactory 的 Fluxon 可调用成员。 */
@Requires(classes = {"org.bukkit.help.HelpTopicFactory"})
public final class NovaHelpTopicFactory {

    private NovaHelpTopicFactory() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(HelpTopicFactory.class, "createTopic", function -> function
                .param("command", Command.class)
                .returns(JavaTypeRef.javaType(HelpTopic.class).nullable())
                .invoke(arguments -> factory(arguments).createTopic(
                        NovaTypeSupport.argument(arguments, 1, Command.class))));
    }

    @SuppressWarnings("unchecked")
    private static HelpTopicFactory<Command> factory(Object[] arguments) {
        return (HelpTopicFactory<Command>) NovaTypeSupport.argument(arguments, 0, HelpTopicFactory.class);
    }
}
