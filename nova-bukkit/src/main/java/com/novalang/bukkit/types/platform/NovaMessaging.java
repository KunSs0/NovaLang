package com.novalang.bukkit.types.platform;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.plugin.messaging.PluginMessageListenerRegistration;
import org.bukkit.plugin.messaging.PluginMessageRecipient;
import java.util.List;

/** Bukkit plugin messaging 与服务通道别名。 */
@Requires(classes = {
        "org.bukkit.plugin.messaging.Messenger",
        "org.bukkit.plugin.messaging.PluginMessageRecipient",
        "org.bukkit.plugin.messaging.PluginMessageListenerRegistration"
})
final class NovaMessaging {

    private NovaMessaging() {
    }

    static void register(JavaTypes.Builder b) {
        b.extension(PluginMessageRecipient.class, "listeningPluginChannels", f -> f.returns(JavaTypeRef.listOf(JavaTypeRef.javaType(String.class))).invoke(a -> NovaTypeSupport.argument(a, 0, PluginMessageRecipient.class).getListeningPluginChannels()));
        b.extension(PluginMessageRecipient.class, "sendPluginMessage", f -> f.param("source", Plugin.class).param("channel", String.class).param("message", byte[].class).invoke(a -> { NovaTypeSupport.argument(a, 0, PluginMessageRecipient.class).sendPluginMessage(NovaTypeSupport.argument(a, 1, Plugin.class), NovaTypeSupport.argument(a, 2, String.class), NovaTypeSupport.argument(a, 3, byte[].class)); return null; }));
        b.extension(PluginMessageListenerRegistration.class, "channel", f -> f.returns(String.class).invoke(a -> NovaTypeSupport.argument(a, 0, PluginMessageListenerRegistration.class).getChannel()));
        b.extension(PluginMessageListenerRegistration.class, "plugin", f -> f.returns(Plugin.class).invoke(a -> NovaTypeSupport.argument(a, 0, PluginMessageListenerRegistration.class).getPlugin()));
        b.extension(PluginMessageListenerRegistration.class, "listener", f -> f.returns(PluginMessageListener.class).invoke(a -> NovaTypeSupport.argument(a, 0, PluginMessageListenerRegistration.class).getListener()));
        b.extension(PluginMessageListenerRegistration.class, "isValid", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, PluginMessageListenerRegistration.class).isValid()));
        b.extension(Messenger.class, "incomingChannels", f -> f.returns(JavaTypeRef.listOf(JavaTypeRef.javaType(String.class))).invoke(a -> NovaTypeSupport.argument(a, 0, Messenger.class).getIncomingChannels()));
        b.extension(Messenger.class, "outgoingChannels", f -> f.returns(JavaTypeRef.listOf(JavaTypeRef.javaType(String.class))).invoke(a -> NovaTypeSupport.argument(a, 0, Messenger.class).getOutgoingChannels()));
        b.extension(Messenger.class, "isIncomingChannelRegistered", f -> f.param("plugin", Plugin.class).param("channel", String.class).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Messenger.class).isIncomingChannelRegistered(NovaTypeSupport.argument(a, 1, Plugin.class), NovaTypeSupport.argument(a, 2, String.class))));
        b.extension(Messenger.class, "isOutgoingChannelRegistered", f -> f.param("plugin", Plugin.class).param("channel", String.class).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Messenger.class).isOutgoingChannelRegistered(NovaTypeSupport.argument(a, 1, Plugin.class), NovaTypeSupport.argument(a, 2, String.class))));
        b.extension(Messenger.class, "registerIncomingPluginChannel", f -> f.param("plugin", Plugin.class).param("channel", String.class).param("listener", PluginMessageListener.class).invoke(a -> { NovaTypeSupport.argument(a, 0, Messenger.class).registerIncomingPluginChannel(NovaTypeSupport.argument(a, 1, Plugin.class), NovaTypeSupport.argument(a, 2, String.class), NovaTypeSupport.argument(a, 3, PluginMessageListener.class)); return null; }));
        b.extension(Messenger.class, "registerOutgoingPluginChannel", f -> f.param("plugin", Plugin.class).param("channel", String.class).invoke(a -> { NovaTypeSupport.argument(a, 0, Messenger.class).registerOutgoingPluginChannel(NovaTypeSupport.argument(a, 1, Plugin.class), NovaTypeSupport.argument(a, 2, String.class)); return null; }));
        b.extension(Messenger.class, "unregisterIncomingPluginChannel", f -> f.param("plugin", Plugin.class).param("channel", String.class).invoke(a -> { NovaTypeSupport.argument(a, 0, Messenger.class).unregisterIncomingPluginChannel(NovaTypeSupport.argument(a, 1, Plugin.class), NovaTypeSupport.argument(a, 2, String.class)); return null; }));
        b.extension(Messenger.class, "unregisterOutgoingPluginChannel", f -> f.param("plugin", Plugin.class).param("channel", String.class).invoke(a -> { NovaTypeSupport.argument(a, 0, Messenger.class).unregisterOutgoingPluginChannel(NovaTypeSupport.argument(a, 1, Plugin.class), NovaTypeSupport.argument(a, 2, String.class)); return null; }));
    }
}
