package com.novalang.bukkit;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.command.CommandSender;
import org.bukkit.help.HelpMap;
import org.bukkit.help.HelpTopic;
import org.bukkit.help.HelpTopicFactory;
import java.util.Collection;
import java.util.List;

/** Spigot 1.12.2 HelpMap/HelpTopic 别名。 */
final class NovaHelp {

    private NovaHelp() {
    }

    static void register(JavaTypes.Builder b) {
        b.extension(HelpMap.class, "getHelpTopic", f -> f.param("topicName", String.class).returns(JavaTypeRef.javaType(HelpTopic.class).nullable()).invoke(a -> NovaTypeSupport.argument(a, 0, HelpMap.class).getHelpTopic(NovaTypeSupport.argument(a, 1, String.class))));
        b.extension(HelpMap.class, "helpTopics", f -> f.returns(JavaTypeRef.javaType(Collection.class)).invoke(a -> NovaTypeSupport.argument(a, 0, HelpMap.class).getHelpTopics()));
        b.extension(HelpMap.class, "addTopic", f -> f.param("topic", HelpTopic.class).invoke(a -> { NovaTypeSupport.argument(a, 0, HelpMap.class).addTopic(NovaTypeSupport.argument(a, 1, HelpTopic.class)); return null; }));
        b.extension(HelpMap.class, "clear", f -> f.invoke(a -> { NovaTypeSupport.argument(a, 0, HelpMap.class).clear(); return null; }));
        b.extension(HelpMap.class, "registerHelpTopicFactory", f -> f.param("command", Class.class).param("factory", HelpTopicFactory.class).invoke(a -> { NovaTypeSupport.argument(a, 0, HelpMap.class).registerHelpTopicFactory(NovaTypeSupport.argument(a, 1, Class.class), NovaTypeSupport.argument(a, 2, HelpTopicFactory.class)); return null; }));
        b.extension(HelpMap.class, "ignoredPlugins", f -> f.returns(JavaTypeRef.listOf(JavaTypeRef.javaType(String.class))).invoke(a -> NovaTypeSupport.argument(a, 0, HelpMap.class).getIgnoredPlugins()));
        b.extension(HelpTopic.class, "canSee", f -> f.param("sender", CommandSender.class).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, HelpTopic.class).canSee(NovaTypeSupport.argument(a, 1, CommandSender.class))));
        b.extension(HelpTopic.class, "amendCanSee", f -> f.param("permission", String.class).invoke(a -> { NovaTypeSupport.argument(a, 0, HelpTopic.class).amendCanSee(NovaTypeSupport.argument(a, 1, String.class)); return null; }));
        b.extension(HelpTopic.class, "name", f -> f.returns(String.class).invoke(a -> NovaTypeSupport.argument(a, 0, HelpTopic.class).getName()));
        b.extension(HelpTopic.class, "shortText", f -> f.returns(String.class).invoke(a -> NovaTypeSupport.argument(a, 0, HelpTopic.class).getShortText()));
        b.extension(HelpTopic.class, "getFullText", f -> f.param("sender", CommandSender.class).returns(String.class).invoke(a -> NovaTypeSupport.argument(a, 0, HelpTopic.class).getFullText(NovaTypeSupport.argument(a, 1, CommandSender.class))));
        b.extension(HelpTopic.class, "amendTopic", f -> f.param("amendedPermission", String.class).param("amendedTopic", String.class).invoke(a -> { NovaTypeSupport.argument(a, 0, HelpTopic.class).amendTopic(NovaTypeSupport.argument(a, 1, String.class), NovaTypeSupport.argument(a, 2, String.class)); return null; }));
    }
}
