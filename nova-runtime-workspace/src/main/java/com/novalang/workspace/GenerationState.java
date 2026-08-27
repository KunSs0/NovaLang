package com.novalang.workspace;

/**
 * 单个 Workspace Generation 的生命周期状态。
 */
public enum GenerationState {
    /** 代际已构建但尚未完成入口初始化。 */
    LOADING,
    /** 代际可执行入口和回调。 */
    ACTIVE,
    /** 代际拒绝新调用并等待正在执行的调用退出。 */
    DISPOSING,
    /** 代际资源及程序引用已释放。 */
    DISPOSED
}
