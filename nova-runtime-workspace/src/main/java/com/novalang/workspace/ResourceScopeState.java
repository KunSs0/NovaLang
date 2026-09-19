package com.novalang.workspace;

/**
 * ResourceScope 生命周期状态。
 */
public enum ResourceScopeState {
    /** 可创建子作用域并登记资源。 */
    ACTIVE,
    /** 正在递归释放资源。 */
    DISPOSING,
    /** 已完成释放。 */
    DISPOSED
}
