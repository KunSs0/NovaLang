package com.novalang.workspace;

import com.novalang.runtime.Nova;

/**
 * 为 Workspace 宿主统一补充运行时绑定的扩展点。
 *
 * <p>扩展只负责安装编译期和运行期可见的绑定，不持有 Workspace 实例，也不参与资源生命周期。</p>
 */
@FunctionalInterface
public interface WorkspaceHostExtension {

    /**
     * 安装当前 Workspace 的公共宿主绑定。
     *
     * @param nova 当前 Workspace 独占的 Nova 实例
     * @param scriptClassLoader 当前宿主脚本类加载器
     */
    void install(Nova nova, ClassLoader scriptClassLoader);
}
