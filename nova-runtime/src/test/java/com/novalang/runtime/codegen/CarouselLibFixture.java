package com.novalang.runtime.codegen;

/**
 * 模拟由原生库注册表返回、再由业务组件强转的轮播库。
 */
public final class CarouselLibFixture implements ZeusLibFixture {

    private int controllerCreateCount;
    private UiHostFixture.TrackedDisposable lastController;

    @Override
    public String id() {
        return "carousel";
    }

    public DisposableFixture createController(UiHostFixture.LifecycleLog lifecycleLog) {
        controllerCreateCount++;
        lastController = new UiHostFixture.TrackedDisposable("controller.dispose", lifecycleLog);
        return lastController;
    }

    public int getControllerCreateCount() {
        return controllerCreateCount;
    }

    public UiHostFixture.TrackedDisposable getLastController() {
        return lastController;
    }
}
