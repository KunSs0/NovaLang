package com.novalang.runtime.codegen;

/**
 * 模拟事件句柄和原生库控制器共同具备的显式释放契约。
 */
public interface DisposableFixture {

    void dispose();
}
