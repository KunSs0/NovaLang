package com.novalang.workspace;

/**
 * Runtime Workspace 的生命周期状态。
 */
public enum WorkspaceState {
    /** 尚未加载。 */
    NEW,
    /** 正在构建首个代际。 */
    LOADING,
    /** 当前代际可接收调用。 */
    ACTIVE,
    /** 最近一次加载失败，当前没有可用代际。 */
    FAILED,
    /** Workspace 已永久销毁。 */
    DISPOSED
}
