package com.novalang.workspace;

/**
 * Workspace 资源作用域类型。
 */
public enum ScopeType {
    /** Generation 根作用域。 */
    GENERATION,
    /** 与当前代际共同存续的常驻注册。 */
    PERSISTENT_REGISTRATION,
    /** 副本、任务、场景等宿主业务实例。 */
    BUSINESS_INSTANCE,
    /** 业务实例内部可单独切换的阶段。 */
    STAGE,
    /** 单次执行或由宿主明确管理的一次调用。 */
    INVOCATION
}
