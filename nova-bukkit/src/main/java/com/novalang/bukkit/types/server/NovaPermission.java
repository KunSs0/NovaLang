package com.novalang.bukkit.types.server;

import com.novalang.bukkit.types.value.NovaTypeSupport;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.ServerOperator;
import org.bukkit.plugin.Plugin;
import java.util.Map;
import java.util.Set;
import java.util.List;

/** Spigot 1.12.2 权限对象与 Permissible 别名。 */
@SuppressWarnings("unchecked")
final class NovaPermission {

    private NovaPermission() {
    }

    static void register(JavaTypes.Builder b) {
        b.extension(ServerOperator.class, "isOp", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, ServerOperator.class).isOp()));
        b.extension(ServerOperator.class, "setOp", f -> f.param("op", Boolean.class).invoke(a -> { NovaTypeSupport.argument(a, 0, ServerOperator.class).setOp(NovaTypeSupport.argument(a, 1, Boolean.class)); return null; }));
        b.extension(Permissible.class, "isPermissionSet", f -> f.param("permission", String.class).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Permissible.class).isPermissionSet(NovaTypeSupport.argument(a, 1, String.class))));
        b.extension(Permissible.class, "isPermissionSet", f -> f.param("permission", Permission.class).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Permissible.class).isPermissionSet(NovaTypeSupport.argument(a, 1, Permission.class))));
        b.extension(Permissible.class, "hasPermission", f -> f.param("permission", String.class).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Permissible.class).hasPermission(NovaTypeSupport.argument(a, 1, String.class))));
        b.extension(Permissible.class, "hasPermission", f -> f.param("permission", Permission.class).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Permissible.class).hasPermission(NovaTypeSupport.argument(a, 1, Permission.class))));
        b.extension(Permissible.class, "addAttachment", f -> f.param("plugin", Plugin.class).returns(PermissionAttachment.class).invoke(a -> NovaTypeSupport.argument(a, 0, Permissible.class).addAttachment(NovaTypeSupport.argument(a, 1, Plugin.class))));
        b.extension(Permissible.class, "addAttachment", f -> f.param("plugin", Plugin.class).param("ticks", Integer.class).returns(PermissionAttachment.class).invoke(a -> NovaTypeSupport.argument(a, 0, Permissible.class).addAttachment(NovaTypeSupport.argument(a, 1, Plugin.class), NovaTypeSupport.argument(a, 2, Integer.class))));
        b.extension(Permissible.class, "addAttachment", f -> f.param("plugin", Plugin.class).param("permission", String.class).param("value", Boolean.class).returns(PermissionAttachment.class).invoke(a -> NovaTypeSupport.argument(a, 0, Permissible.class).addAttachment(NovaTypeSupport.argument(a, 1, Plugin.class), NovaTypeSupport.argument(a, 2, String.class), NovaTypeSupport.argument(a, 3, Boolean.class))));
        b.extension(Permissible.class, "addAttachment", f -> f.param("plugin", Plugin.class).param("permission", String.class).param("value", Boolean.class).param("ticks", Integer.class).returns(PermissionAttachment.class).invoke(a -> NovaTypeSupport.argument(a, 0, Permissible.class).addAttachment(NovaTypeSupport.argument(a, 1, Plugin.class), NovaTypeSupport.argument(a, 2, String.class), NovaTypeSupport.argument(a, 3, Boolean.class), NovaTypeSupport.argument(a, 4, Integer.class))));
        b.extension(Permissible.class, "removeAttachment", f -> f.param("attachment", PermissionAttachment.class).invoke(a -> { NovaTypeSupport.argument(a, 0, Permissible.class).removeAttachment(NovaTypeSupport.argument(a, 1, PermissionAttachment.class)); return null; }));
        b.extension(Permissible.class, "recalculatePermissions", f -> f.invoke(a -> { NovaTypeSupport.argument(a, 0, Permissible.class).recalculatePermissions(); return null; }));
        b.extension(Permissible.class, "effectivePermissions", f -> f.returns(JavaTypeRef.setOf(JavaTypeRef.javaType(PermissionAttachmentInfo.class))).invoke(a -> NovaTypeSupport.argument(a, 0, Permissible.class).getEffectivePermissions()));
        b.extension(Permission.class, "name", f -> f.returns(String.class).invoke(a -> NovaTypeSupport.argument(a, 0, Permission.class).getName()));
        b.extension(Permission.class, "children", f -> f.returns(Map.class).invoke(a -> NovaTypeSupport.argument(a, 0, Permission.class).getChildren()));
        b.extension(Permission.class, "default", f -> f.returns(org.bukkit.permissions.PermissionDefault.class).invoke(a -> NovaTypeSupport.argument(a, 0, Permission.class).getDefault()));
        b.extension(Permission.class, "setDefault", f -> f.param("defaultValue", org.bukkit.permissions.PermissionDefault.class).invoke(a -> { NovaTypeSupport.argument(a, 0, Permission.class).setDefault(NovaTypeSupport.argument(a, 1, org.bukkit.permissions.PermissionDefault.class)); return null; }));
        b.extension(Permission.class, "description", f -> f.returns(String.class).invoke(a -> NovaTypeSupport.argument(a, 0, Permission.class).getDescription()));
        b.extension(Permission.class, "setDescription", f -> f.param("description", String.class).invoke(a -> { NovaTypeSupport.argument(a, 0, Permission.class).setDescription(NovaTypeSupport.argument(a, 1, String.class)); return null; }));
        b.extension(Permission.class, "permissibles", f -> f.returns(JavaTypeRef.setOf(JavaTypeRef.javaType(Permissible.class))).invoke(a -> NovaTypeSupport.argument(a, 0, Permission.class).getPermissibles()));
        b.extension(Permission.class, "recalculatePermissibles", f -> f.invoke(a -> { NovaTypeSupport.argument(a, 0, Permission.class).recalculatePermissibles(); return null; }));
        b.extension(Permission.class, "addParent", f -> f.param("name", String.class).param("value", Boolean.class).invoke(a -> { NovaTypeSupport.argument(a, 0, Permission.class).addParent(NovaTypeSupport.argument(a, 1, String.class), NovaTypeSupport.argument(a, 2, Boolean.class)); return null; }));
        b.extension(Permission.class, "addParent", f -> f.param("parent", Permission.class).param("value", Boolean.class).invoke(a -> { NovaTypeSupport.argument(a, 0, Permission.class).addParent(NovaTypeSupport.argument(a, 1, Permission.class), NovaTypeSupport.argument(a, 2, Boolean.class)); return null; }));
        b.extension(Permission.class, "loadPermissions", f -> f.param("data", Map.class).param("name", String.class).param("defaultValue", org.bukkit.permissions.PermissionDefault.class).returns(JavaTypeRef.listOf(JavaTypeRef.javaType(Permission.class))).invoke(a -> Permission.loadPermissions(NovaTypeSupport.argument(a, 1, Map.class), NovaTypeSupport.argument(a, 2, String.class), NovaTypeSupport.argument(a, 3, org.bukkit.permissions.PermissionDefault.class))));
        b.extension(Permission.class, "loadPermission", f -> f.param("name", String.class).param("data", Map.class).returns(JavaTypeRef.javaType(Permission.class).nullable()).invoke(a -> Permission.loadPermission(NovaTypeSupport.argument(a, 1, String.class), NovaTypeSupport.argument(a, 2, Map.class))));
        b.extension(Permission.class, "loadPermission", f -> f.param("name", String.class).param("data", Map.class).param("defaultValue", org.bukkit.permissions.PermissionDefault.class).param("output", List.class).returns(JavaTypeRef.javaType(Permission.class).nullable()).invoke(a -> Permission.loadPermission(NovaTypeSupport.argument(a, 1, String.class), NovaTypeSupport.argument(a, 2, Map.class), NovaTypeSupport.argument(a, 3, org.bukkit.permissions.PermissionDefault.class), NovaTypeSupport.argument(a, 4, List.class))));
        b.extension(PermissionAttachment.class, "plugin", f -> f.returns(Plugin.class).invoke(a -> NovaTypeSupport.argument(a, 0, PermissionAttachment.class).getPlugin()));
        b.extension(PermissionAttachment.class, "permissible", f -> f.returns(Permissible.class).invoke(a -> NovaTypeSupport.argument(a, 0, PermissionAttachment.class).getPermissible()));
        b.extension(PermissionAttachment.class, "permissions", f -> f.returns(Map.class).invoke(a -> NovaTypeSupport.argument(a, 0, PermissionAttachment.class).getPermissions()));
        b.extension(PermissionAttachment.class, "setRemovalCallback", f -> f.param("callback", org.bukkit.permissions.PermissionRemovedExecutor.class).invoke(a -> { NovaTypeSupport.argument(a, 0, PermissionAttachment.class).setRemovalCallback(NovaTypeSupport.argument(a, 1, org.bukkit.permissions.PermissionRemovedExecutor.class)); return null; }));
        b.extension(PermissionAttachment.class, "setPermission", f -> f.param("permission", String.class).param("value", Boolean.class).invoke(a -> { NovaTypeSupport.argument(a, 0, PermissionAttachment.class).setPermission(NovaTypeSupport.argument(a, 1, String.class), NovaTypeSupport.argument(a, 2, Boolean.class)); return null; }));
        b.extension(PermissionAttachment.class, "setPermission", f -> f.param("permission", Permission.class).param("value", Boolean.class).invoke(a -> { NovaTypeSupport.argument(a, 0, PermissionAttachment.class).setPermission(NovaTypeSupport.argument(a, 1, Permission.class), NovaTypeSupport.argument(a, 2, Boolean.class)); return null; }));
        b.extension(PermissionAttachment.class, "unsetPermission", f -> f.param("permission", String.class).invoke(a -> { NovaTypeSupport.argument(a, 0, PermissionAttachment.class).unsetPermission(NovaTypeSupport.argument(a, 1, String.class)); return null; }));
        b.extension(PermissionAttachment.class, "unsetPermission", f -> f.param("permission", Permission.class).invoke(a -> { NovaTypeSupport.argument(a, 0, PermissionAttachment.class).unsetPermission(NovaTypeSupport.argument(a, 1, Permission.class)); return null; }));
        b.extension(PermissionAttachment.class, "removalCallback", f -> f.returns(JavaTypeRef.javaType(org.bukkit.permissions.PermissionRemovedExecutor.class).nullable()).invoke(a -> NovaTypeSupport.argument(a, 0, PermissionAttachment.class).getRemovalCallback()));
        b.extension(PermissionAttachment.class, "remove", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, PermissionAttachment.class).remove()));
        b.extension(PermissionAttachmentInfo.class, "permissible", f -> f.returns(Permissible.class).invoke(a -> NovaTypeSupport.argument(a, 0, PermissionAttachmentInfo.class).getPermissible()));
        b.extension(PermissionAttachmentInfo.class, "permission", f -> f.returns(String.class).invoke(a -> NovaTypeSupport.argument(a, 0, PermissionAttachmentInfo.class).getPermission()));
        b.extension(PermissionAttachmentInfo.class, "attachment", f -> f.returns(JavaTypeRef.javaType(PermissionAttachment.class).nullable()).invoke(a -> NovaTypeSupport.argument(a, 0, PermissionAttachmentInfo.class).getAttachment()));
        b.extension(PermissionAttachmentInfo.class, "value", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, PermissionAttachmentInfo.class).getValue()));
    }
}
