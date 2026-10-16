package com.novalang.mock;

import com.novalang.runtime.Nova;

/**
 * mock 宿主 SPI。实现类由平台模块提供，以保证 Nova 程序 使用平台插件类加载器。
 */
public interface MockTestHost extends AutoCloseable {

    /**
     * 安装平台 mock 绑定。调用期间可以通过
     * {@link MockTestBindings#requireCurrent()} 取得本次绑定。
     */
    void installMockBindings(Nova nova, MockTestBindings bindings);

    /** 将当前测试绑定安装到普通 Nova 实例。 */
    default void install(Nova nova) {
        installMockBindings(nova, MockTestBindings.requireCurrent());
    }

    @Override
    default void close() {
    }
}
