package com.novalang.bukkit.types.server;

import com.novalang.bukkit.NovaBukkitRegistrar;
import com.novalang.runtime.host.JavaTypes;

/** 第二批 Bukkit 平台类型的独立聚合器，不改 NovaBukkit 总入口。 */
public final class NovaServerPlatform {

    private NovaServerPlatform() {
    }

    public static void register(JavaTypes.Builder builder) {
        NovaServerExtra.register(builder);
        NovaOfflinePlayer.register(builder);
        NovaPermission.register(builder);
        NovaMetadata.register(builder);
        NovaBukkitRegistrar.register(builder, NovaFixedMetadataValue.class, NovaFixedMetadataValue::register);
        NovaBukkitRegistrar.register(builder, NovaLazyMetadataValue.class, NovaLazyMetadataValue::register);
        NovaBukkitRegistrar.register(builder, NovaMetadataValueAdapter.class, NovaMetadataValueAdapter::register);
        NovaBukkitRegistrar.register(builder, NovaEventExecutor.class, NovaEventExecutor::register);
        NovaBukkitRegistrar.register(builder, NovaPluginLoader.class, NovaPluginLoader::register);
        NovaBukkitRegistrar.register(builder, NovaSimplePluginManager.class, NovaSimplePluginManager::register);
        NovaHelp.register(builder);
        NovaBukkitRegistrar.register(builder, NovaHelpTopicFactory.class, NovaHelpTopicFactory::register);
        NovaBukkitRegistrar.register(builder, NovaGenericCommandHelpTopic.class,
                NovaGenericCommandHelpTopic::register);
        NovaBukkitRegistrar.register(builder, NovaIndexHelpTopic.class, NovaIndexHelpTopic::register);
        NovaBukkitRegistrar.register(builder, NovaServices.class, NovaServices::register);
        NovaBukkitRegistrar.register(builder, NovaPluginManager.class, NovaPluginManager::register);
        NovaBukkitRegistrar.register(builder, NovaUnsafeValues.class, NovaUnsafeValues::register);
    }
}
