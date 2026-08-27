package com.novalang.workspace;

/**
 * 单个入口的依赖闭包合并源码及其 Source Map。
 */
final class WorkspaceBundle {

    private final String source;
    private final WorkspaceSourceMap sourceMap;

    /**
     * 创建入口编译包。
     *
     * @param source 已移除字符串 import 的完整源码
     * @param sourceMap 逐行来源映射
     */
    WorkspaceBundle(String source, WorkspaceSourceMap sourceMap) {
        this.source = source;
        this.sourceMap = sourceMap;
    }

    /** @return 完整入口源码 */
    String getSource() {
        return source;
    }

    /** @return 逐行 Source Map */
    WorkspaceSourceMap getSourceMap() {
        return sourceMap;
    }
}
