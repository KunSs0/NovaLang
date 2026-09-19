package com.novalang.runtime.codegen;

/**
 * 模拟宿主侧持有的组件生命周期接口。
 */
public interface ComponentHandleFixture {

    void onCreate(String componentId);

    void onData(int delta);

    void bindCallback(CallbackSinkFixture sink, int delta);

    String getComponentId();

    int getValue();

    void dispose();

    boolean isDisposed();
}
