package com.novalang.runtime.codegen;

/**
 * 模拟玩家快捷栏选中项更新事件。
 */
public final class SelectedUpdateEventFixture {

    private final int selectedSlot;

    public SelectedUpdateEventFixture(int selectedSlot) {
        this.selectedSlot = selectedSlot;
    }

    public int getSelectedSlot() {
        return selectedSlot;
    }
}
