package com.novalang.bukkit.types.server;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.SimplePluginManager;

/** Spigot 1.12.2 SimplePluginManager 的实现类专有别名。 */
@Requires(classes = {"org.bukkit.plugin.SimplePluginManager"})
public final class NovaSimplePluginManager {

    private NovaSimplePluginManager() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(SimplePluginManager.class, "addPermission", function -> function
                .param("permission", Permission.class)
                .param("dirty", Boolean.class)
                .invoke(arguments -> {
                    manager(arguments).addPermission(
                            NovaTypeSupport.argument(arguments, 1, Permission.class),
                            NovaTypeSupport.argument(arguments, 2, Boolean.class));
                    return null;
                }));
        builder.extension(SimplePluginManager.class, "dirtyPermissibles", function -> function
                .invoke(arguments -> {
                    manager(arguments).dirtyPermissibles();
                    return null;
                }));
        builder.extension(SimplePluginManager.class, "useTimings", function -> function
                .param("use", Boolean.class)
                .invoke(arguments -> {
                    manager(arguments).useTimings(
                            NovaTypeSupport.argument(arguments, 1, Boolean.class));
                    return null;
                }));
    }

    private static SimplePluginManager manager(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, SimplePluginManager.class);
    }
}
