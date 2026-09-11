package com.novalang.workspace;

import com.novalang.runtime.Nova;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 进程级 Workspace 宿主扩展注册表。
 *
 * <p>注册表只保存公共接入扩展，不保存 Workspace、脚本或业务资源，因此不会形成
 * 隐藏的 Workspace 全局生命周期。</p>
 */
public final class WorkspaceHostExtensions {

    private static final CopyOnWriteArrayList<WorkspaceHostExtension> EXTENSIONS =
            new CopyOnWriteArrayList<WorkspaceHostExtension>();

    private WorkspaceHostExtensions() {
    }

    /**
     * 注册一个宿主扩展。
     *
     * @param extension 扩展实现
     * @return 可用于卸载扩展的句柄
     */
    public static Registration register(WorkspaceHostExtension extension) {
        if (extension == null) {
            throw new IllegalArgumentException("extension must not be null");
        }
        EXTENSIONS.addIfAbsent(extension);
        return new Registration(extension);
    }

    /** 为新建的 Nova 依次执行全部已注册扩展。 */
    public static void install(Nova nova, ClassLoader scriptClassLoader) {
        if (nova == null) {
            throw new IllegalArgumentException("nova must not be null");
        }
        for (WorkspaceHostExtension extension : EXTENSIONS) {
            extension.install(nova, scriptClassLoader);
        }
    }

    /** 测试和宿主卸载使用的扩展句柄。 */
    public static final class Registration implements AutoCloseable {

        private final WorkspaceHostExtension extension;
        private volatile boolean closed;

        private Registration(WorkspaceHostExtension extension) {
            this.extension = extension;
        }

        @Override
        public void close() {
            if (!closed) {
                closed = true;
                EXTENSIONS.remove(extension);
            }
        }
    }
}
