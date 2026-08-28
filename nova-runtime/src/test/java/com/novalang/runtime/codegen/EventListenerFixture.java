package com.novalang.runtime.codegen;

/**
 * 模拟事件总线持有的声明式监听器实例。
 */
public interface EventListenerFixture {

    void onEvent(String key, Object event);
}
