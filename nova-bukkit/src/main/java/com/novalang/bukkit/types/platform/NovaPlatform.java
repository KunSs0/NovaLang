package com.novalang.bukkit.types.platform;

import com.novalang.bukkit.NovaBukkitRegistrar;
import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;

/** 非 Server Bukkit 平台别名聚合入口。由宿主在组装 JavaTypes 时调用。 */
@Requires(classes = {"org.bukkit.Server"})
public final class NovaPlatform {

    private NovaPlatform() {
    }

    public static void register(JavaTypes.Builder builder) {
        NovaScheduler.register(builder);
        NovaPlugin.register(builder);
        NovaCommand.register(builder);
        NovaBukkitRegistrar.register(builder, NovaCommandHandlers.class, NovaCommandHandlers::register);
        NovaBukkitRegistrar.register(builder, NovaCommandMapExtra.class, NovaCommandMapExtra::register);
        NovaBukkitRegistrar.register(builder, NovaPluginCommandExtra.class, NovaPluginCommandExtra::register);
        NovaEvent.register(builder);
        NovaBukkitRegistrar.register(builder, NovaMessaging.class, NovaMessaging::register);
        NovaScoreboard.register(builder);
        NovaBukkitRegistrar.register(builder, NovaScoreboardExtra.class, NovaScoreboardExtra::register);
        NovaBukkitRegistrar.register(builder, NovaObjectiveExtra.class, NovaObjectiveExtra::register);
        NovaBukkitRegistrar.register(builder, NovaTeamOptions.class, NovaTeamOptions::register);
        NovaBukkitRegistrar.register(builder, NovaScoreboardStrings.class, NovaScoreboardStrings::register);
        NovaBukkitRegistrar.register(builder, NovaBossBar.class, NovaBossBar::register);
        NovaBukkitRegistrar.register(builder, NovaDragonBattle.class, NovaDragonBattle::register);
        NovaConfiguration.register(builder);
        NovaBukkitRegistrar.register(builder, NovaYamlConfiguration.class, NovaYamlConfiguration::register);
        NovaBukkitRegistrar.register(builder, NovaConversable.class, NovaConversable::register);
        NovaBukkitRegistrar.register(builder, NovaConversation.class, NovaConversation::register);
        NovaBukkitRegistrar.register(builder, NovaConversationContext.class, NovaConversationContext::register);
        NovaBukkitRegistrar.register(builder, NovaConversationFactory.class, NovaConversationFactory::register);
        NovaBukkitRegistrar.register(builder, NovaConversationPrefix.class, NovaConversationPrefix::register);
        NovaBukkitRegistrar.register(builder, NovaPluginNameConversationPrefix.class,
                NovaPluginNameConversationPrefix::register);
        NovaBukkitRegistrar.register(builder, NovaNullConversationPrefix.class,
                NovaNullConversationPrefix::register);
        NovaBukkitRegistrar.register(builder, NovaConversationAbandonedEvent.class,
                NovaConversationAbandonedEvent::register);
        NovaBukkitRegistrar.register(builder, NovaConversationCanceller.class, NovaConversationCanceller::register);
        NovaBukkitRegistrar.register(builder, NovaConversationAbandonedListener.class,
                NovaConversationAbandonedListener::register);
        NovaBukkitRegistrar.register(builder, NovaPrompt.class, NovaPrompt::register);
        NovaBukkitRegistrar.register(builder, NovaMessagePrompt.class, NovaMessagePrompt::register);
        NovaBukkitRegistrar.register(builder, NovaStringPrompt.class, NovaStringPrompt::register);
        NovaBukkitRegistrar.register(builder, NovaValidatingPrompt.class, NovaValidatingPrompt::register);
    }
}
