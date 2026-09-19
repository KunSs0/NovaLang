package com.novalang.workspace;

import com.novalang.runtime.Nova;

/**
 * 向一个新 Workspace 安装宿主对象、函数和扩展的适配器。
 *
 * <p>该方法在模块编译前只执行一次。会创建监听器、任务或订阅的 Host API 不应在
 * 安装阶段直接注册资源，而应在脚本调用期间通过
 * {@link WorkspaceExecutionContext#requireScope()} 获取所有者作用域。</p>
 */
@FunctionalInterface
public interface WorkspaceHost {

    /**
     * 将当前业务需要的稳定 Host Binding 安装到 Nova 编译环境。
     *
     * @param nova 当前 Workspace 独占的 Nova 编译门面
     */
    void install(Nova nova);
}
