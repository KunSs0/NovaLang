package com.novalang.bukkit.types.platform;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginAwareness;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoadOrder;

/** Spigot 1.12.2 plugin.yml 解析结果的 Fluxon 函数别名。 */
final class NovaPluginDescription {

    private NovaPluginDescription() {
    }

    static void register(JavaTypes.Builder builder) {
        JavaTypeRef strings = JavaTypeRef.listOf(JavaTypeRef.javaType(String.class));
        JavaTypeRef permissions = JavaTypeRef.listOf(JavaTypeRef.javaType(Permission.class));
        JavaTypeRef awareness = JavaTypeRef.setOf(JavaTypeRef.javaType(PluginAwareness.class));

        builder.extension(PluginDescriptionFile.class, "toString",
                function -> function.returns(String.class).invoke(arguments -> description(arguments).toString()));
        builder.extension(PluginDescriptionFile.class, "name",
                function -> function.returns(String.class).invoke(arguments -> description(arguments).getName()));
        builder.extension(PluginDescriptionFile.class, "version",
                function -> function.returns(String.class).invoke(arguments -> description(arguments).getVersion()));
        builder.extension(PluginDescriptionFile.class, "main",
                function -> function.returns(String.class).invoke(arguments -> description(arguments).getMain()));
        builder.extension(PluginDescriptionFile.class, "description",
                function -> function.returns(JavaTypeRef.javaType(String.class).nullable())
                        .invoke(arguments -> description(arguments).getDescription()));
        builder.extension(PluginDescriptionFile.class, "load",
                function -> function.returns(PluginLoadOrder.class).invoke(arguments -> description(arguments).getLoad()));
        builder.extension(PluginDescriptionFile.class, "authors",
                function -> function.returns(strings).invoke(arguments -> description(arguments).getAuthors()));
        builder.extension(PluginDescriptionFile.class, "website",
                function -> function.returns(JavaTypeRef.javaType(String.class).nullable())
                        .invoke(arguments -> description(arguments).getWebsite()));
        builder.extension(PluginDescriptionFile.class, "depend",
                function -> function.returns(strings).invoke(arguments -> description(arguments).getDepend()));
        builder.extension(PluginDescriptionFile.class, "softDepend",
                function -> function.returns(strings).invoke(arguments -> description(arguments).getSoftDepend()));
        builder.extension(PluginDescriptionFile.class, "loadBefore",
                function -> function.returns(strings).invoke(arguments -> description(arguments).getLoadBefore()));
        builder.extension(PluginDescriptionFile.class, "prefix",
                function -> function.returns(JavaTypeRef.javaType(String.class).nullable())
                        .invoke(arguments -> description(arguments).getPrefix()));
        builder.extension(PluginDescriptionFile.class, "permissions",
                function -> function.returns(permissions).invoke(arguments -> description(arguments).getPermissions()));
        builder.extension(PluginDescriptionFile.class, "permissionDefault",
                function -> function.returns(PermissionDefault.class)
                        .invoke(arguments -> description(arguments).getPermissionDefault()));
        builder.extension(PluginDescriptionFile.class, "awareness",
                function -> function.returns(awareness).invoke(arguments -> description(arguments).getAwareness()));
        builder.extension(PluginDescriptionFile.class, "fullName",
                function -> function.returns(String.class).invoke(arguments -> description(arguments).getFullName()));
        builder.extension(PluginDescriptionFile.class, "classLoaderOf",
                function -> function.returns(JavaTypeRef.javaType(String.class).nullable())
                        .invoke(arguments -> description(arguments).getClassLoaderOf()));
    }

    private static PluginDescriptionFile description(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, PluginDescriptionFile.class);
    }
}
