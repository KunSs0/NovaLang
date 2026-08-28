package com.novalang.runtime.codegen;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 模拟 Zeus 节点基类以及按路径查询和移除节点的稳定 API。
 */
public class NodeFixture {

    private final String name;
    private final Map<String, NodeFixture> children = new LinkedHashMap<>();
    private UiHostFixture.LifecycleLog lifecycleLog;
    private int removeCount;

    public NodeFixture(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addChild(String path, NodeFixture child) {
        children.put(path, child);
    }

    public NodeFixture getDeepChild(String path) {
        return children.get(path);
    }

    public NodeFixture removeSelf() {
        removeCount++;
        if (lifecycleLog != null) {
            lifecycleLog.add("driver.removeSelf");
        }
        return this;
    }

    public int getRemoveCount() {
        return removeCount;
    }

    public void setLifecycleLog(UiHostFixture.LifecycleLog lifecycleLog) {
        this.lifecycleLog = lifecycleLog;
    }
}
