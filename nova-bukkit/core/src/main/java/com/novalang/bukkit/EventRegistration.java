package com.novalang.bukkit;

import com.novalang.workspace.WorkspaceResource;

/**
 * Bukkit 事件注册句柄。
 *
 * <p>句柄既可以由调用方主动释放，也可以由 Workspace ResourceScope 自动释放。</p>
 */
public interface EventRegistration extends WorkspaceResource {
}
