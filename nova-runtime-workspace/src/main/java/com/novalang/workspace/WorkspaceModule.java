package com.novalang.workspace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 完成 import 重写后的 Workspace 模块。
 */
public final class WorkspaceModule {

    private final SourceUnit sourceUnit;
    private final String transformedSource;
    private final List<String> dependencies;

    /**
     * 创建已解析模块。
     *
     * @param sourceUnit 原始源码单元
     * @param transformedSource import 已替换为规范模块标识的源码
     * @param dependencies 直接依赖模块标识
     */
    WorkspaceModule(SourceUnit sourceUnit, String transformedSource, List<String> dependencies) {
        this.sourceUnit = sourceUnit;
        this.transformedSource = transformedSource;
        this.dependencies = Collections.unmodifiableList(new ArrayList<String>(dependencies));
    }

    /** @return 原始源码单元 */
    public SourceUnit getSourceUnit() {
        return sourceUnit;
    }

    /** @return import 已规范化的源码 */
    public String getTransformedSource() {
        return transformedSource;
    }

    /** @return 不可变直接依赖列表 */
    public List<String> getDependencies() {
        return dependencies;
    }
}
