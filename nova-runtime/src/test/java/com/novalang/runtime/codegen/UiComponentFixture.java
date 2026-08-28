package com.novalang.runtime.codegen;

/**
 * 模拟 Zeus 客户端持有的编译后 UI 组件实例。
 */
public interface UiComponentFixture {

    void onAttach(UiHostFixture host);

    void onInventoryEvent(Object event);

    void onTweenComplete();

    void playTween();

    void onDestroy();

    int getSelectedSlot();

    int getTweenCompleteCount();

    boolean isDisposed();
}
