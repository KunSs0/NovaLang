package com.novalang.bukkit;

import com.novalang.runtime.Nova;
import com.novalang.runtime.host.JavaTypes;
import com.novalang.bukkit.types.entity.NovaEntity;
import com.novalang.bukkit.types.entity.NovaEntityHierarchy;
import com.novalang.bukkit.types.entity.NovaEntityMoreTypes;
import com.novalang.bukkit.types.entity.NovaEntityValueMoreTypes;
import com.novalang.bukkit.types.entity.NovaPlayer;
import com.novalang.bukkit.types.enums.NovaEnum;
import com.novalang.bukkit.types.event.NovaEventTypes;
import com.novalang.bukkit.types.gameplay.NovaGameplay;
import com.novalang.bukkit.types.inventory.NovaWorldInventory;
import com.novalang.bukkit.types.inventory.NovaInventoryMoreTypes;
import com.novalang.bukkit.types.inventory.NovaInventoryMetaMoreTypes;
import com.novalang.bukkit.types.inventory.NovaInventoryBlockMoreTypes;
import com.novalang.bukkit.types.platform.NovaPlatform;
import com.novalang.bukkit.types.server.NovaServer;
import com.novalang.bukkit.types.server.NovaServerPlatform;
import com.novalang.bukkit.types.server.NovaServerMoreTypes;
import com.novalang.bukkit.types.server.NovaServerBanTypes;
import com.novalang.bukkit.types.value.NovaLocation;
import com.novalang.bukkit.types.value.NovaValueFactory;
import com.novalang.bukkit.types.world.NovaWorld;
import com.novalang.bukkit.types.world.NovaWorldBlocks;
import com.novalang.bukkit.types.world.NovaWorldBlockMoreTypes;
import com.novalang.bukkit.types.world.NovaBlockInventoryMoreTypes;
import com.novalang.bukkit.types.world.NovaBlockStateMoreTypes;
import com.novalang.bukkit.types.world.NovaWorldToolMoreTypes;

/**
 * Bukkit 运行时函数及其编译期 Java 类型定义入口。
 *
 * <p>全局入口与 Fluxon platform-bukkit 保持同一职责边界。Bukkit 原生成员由 Nova
 * 根据真实 {@link Class} 反射解析，Fluxon 的函数别名则通过 JavaTypes 扩展描述显式注册。</p>
 *
 * <p>该基础表以当前模块声明的 Spigot API 1.12.2 为准，不包含新版 Particle、
 * Advancement 或 Paper 专有 API。</p>
 */
public final class NovaBukkit {

    private NovaBukkit() {
    }

    public static JavaTypes create() {
        return builder().build();
    }

    /** 单体 Nova 直接调用；Workspace 可把该方法引用作为 WorkspaceHost。 */
    public static Nova install(Nova nova) {
        if (nova == null) {
            throw new IllegalArgumentException("nova must not be null");
        }
        nova.install(create());
        return nova;
    }

    /** 创建预装 Bukkit API 的 builder，业务插件可继续追加自己的 JavaTypes。 */
    public static JavaTypes.Builder builder() {
        JavaTypes.Builder builder = JavaTypes.builder();
        NovaBukkitRegistrar.register(builder, NovaServer.class, NovaServer::register);
        NovaBukkitRegistrar.register(builder, NovaWorld.class, NovaWorld::register);
        NovaBukkitRegistrar.register(builder, NovaPlayer.class, NovaPlayer::register);
        NovaBukkitRegistrar.register(builder, NovaLocation.class, NovaLocation::register);
        NovaBukkitRegistrar.register(builder, NovaEntity.class, NovaEntity::register);
        NovaBukkitRegistrar.register(builder, NovaEntityHierarchy.class, NovaEntityHierarchy::register);
        NovaBukkitRegistrar.register(builder, NovaEntityMoreTypes.class, NovaEntityMoreTypes::register);
        NovaBukkitRegistrar.register(builder, NovaEntityValueMoreTypes.class, NovaEntityValueMoreTypes::register);
        NovaBukkitRegistrar.register(builder, NovaWorldInventory.class, NovaWorldInventory::register);
        NovaBukkitRegistrar.register(builder, NovaInventoryMoreTypes.class, NovaInventoryMoreTypes::register);
        NovaBukkitRegistrar.register(builder, NovaInventoryMetaMoreTypes.class, NovaInventoryMetaMoreTypes::register);
        NovaBukkitRegistrar.register(builder, NovaInventoryBlockMoreTypes.class, NovaInventoryBlockMoreTypes::register);
        NovaBukkitRegistrar.register(builder, NovaWorldBlocks.class, NovaWorldBlocks::register);
        NovaBukkitRegistrar.register(builder, NovaWorldBlockMoreTypes.class, NovaWorldBlockMoreTypes::register);
        NovaBukkitRegistrar.register(builder, NovaBlockInventoryMoreTypes.class, NovaBlockInventoryMoreTypes::register);
        NovaBukkitRegistrar.register(builder, NovaBlockStateMoreTypes.class, NovaBlockStateMoreTypes::register);
        NovaBukkitRegistrar.register(builder, NovaWorldToolMoreTypes.class, NovaWorldToolMoreTypes::register);
        NovaBukkitRegistrar.register(builder, NovaValueFactory.class, NovaValueFactory::register);
        NovaBukkitRegistrar.register(builder, NovaPlatform.class, NovaPlatform::register);
        NovaBukkitRegistrar.register(builder, NovaServerPlatform.class, NovaServerPlatform::register);
        NovaBukkitRegistrar.register(builder, NovaServerMoreTypes.class, NovaServerMoreTypes::register);
        NovaBukkitRegistrar.register(builder, NovaServerBanTypes.class, NovaServerBanTypes::register);
        NovaBukkitRegistrar.register(builder, NovaGameplay.class, NovaGameplay::register);
        NovaBukkitRegistrar.register(builder, NovaEnum.class, NovaEnum::register);
        NovaBukkitRegistrar.register(builder, NovaEventTypes.class, NovaEventTypes::register);
        builder.javaBeanPropertiesFromExtensions();
        return builder;
    }
}
