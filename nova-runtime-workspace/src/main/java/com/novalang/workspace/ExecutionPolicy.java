package com.novalang.workspace;

/**
 * Workspace 程序的固定执行策略。
 */
public enum ExecutionPolicy {
    /** 始终在宿主主线程执行。 */
    MAIN_THREAD,
    /** 直接在调用线程执行。 */
    CALLER_THREAD,
    /** 允许多个调用线程并发执行。 */
    PARALLEL_SAFE,
    /** 同一资源作用域内串行执行。 */
    SERIAL_SCOPE
}
