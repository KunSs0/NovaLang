package com.novalang.bukkit.types.server;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionRemovedExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.Map;

/** 从 Spigot 1.12.2 sources.jar 核验的服务端/权限补充别名。 */
@Requires(classes = {
        "org.bukkit.plugin.Plugin",
        "org.bukkit.plugin.PluginDescriptionFile",
        "org.bukkit.permissions.PermissionRemovedExecutor"
})
public final class NovaServerMoreTypes {
    private NovaServerMoreTypes() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Plugin.class, "onLoad", function -> function
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    plugin(arguments).onLoad();
                    return null;
                }));
        builder.extension(Plugin.class, "onEnable", function -> function
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    plugin(arguments).onEnable();
                    return null;
                }));
        builder.extension(Plugin.class, "onDisable", function -> function
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    plugin(arguments).onDisable();
                    return null;
                }));
        builder.extension(Plugin.class, "defaultWorldGenerator", function -> function
                .param("worldName", String.class)
                .param("id", String.class)
                .returns(JavaTypeRef.javaType(ChunkGenerator.class).nullable())
                .invoke(arguments -> plugin(arguments).getDefaultWorldGenerator(
                        NovaTypeSupport.argument(arguments, 1, String.class),
                        NovaTypeSupport.argument(arguments, 2, String.class))));
        builder.extension(Plugin.class, "getDefaultWorldGenerator", function -> function
                .param("worldName", String.class)
                .param("id", String.class)
                .returns(JavaTypeRef.javaType(ChunkGenerator.class).nullable())
                .invoke(arguments -> plugin(arguments).getDefaultWorldGenerator(
                        NovaTypeSupport.argument(arguments, 1, String.class),
                        NovaTypeSupport.argument(arguments, 2, String.class))));
        builder.extension(PluginDescriptionFile.class, "commands", function -> function
                .returns(JavaTypeRef.mapOf(JavaTypeRef.javaType(String.class), JavaTypeRef.mapOf(
                        JavaTypeRef.javaType(String.class), JavaTypeRef.javaType(Object.class))))
                .invoke(arguments -> description(arguments).getCommands()));
        builder.extension(PermissionRemovedExecutor.class, "attachmentRemoved", function -> function
                .param("attachment", PermissionAttachment.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    NovaTypeSupport.argument(arguments, 0, PermissionRemovedExecutor.class)
                            .attachmentRemoved(NovaTypeSupport.argument(arguments, 1, PermissionAttachment.class));
                    return null;
                }));
    }

    private static Plugin plugin(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Plugin.class);
    }

    private static PluginDescriptionFile description(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, PluginDescriptionFile.class);
    }
}
