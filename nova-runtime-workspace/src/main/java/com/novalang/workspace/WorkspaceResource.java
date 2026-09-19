package com.novalang.workspace;

/**
 * 可由 ResourceScope 统一销毁的宿主资源。
 *
 * <p>监听器、任务、RPC、Provider 和订阅适配器均应实现该接口，并在注册成功后立即
 * 登记到当前作用域。</p>
 */
@FunctionalInterface
public interface WorkspaceResource {

    /**
     * 释放宿主资源。
     *
     * @throws Exception 资源释放失败时抛出；作用域仍会继续释放其他资源
     */
    void dispose() throws Exception;
}
