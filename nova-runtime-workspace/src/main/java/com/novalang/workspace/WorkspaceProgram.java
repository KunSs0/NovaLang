package com.novalang.workspace;

import com.novalang.runtime.CompiledNova;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 一个配置入口对应的已编译 Nova 字节码程序。
 */
public final class WorkspaceProgram {

    private final String entryName;
    private final String moduleId;
    private final CompiledNova compiled;
    private final WorkspaceSourceMap sourceMap;

    /**
     * 创建已编译 Workspace 程序。
     *
     * @param entryName 配置或虚拟入口名称
     * @param moduleId 规范模块标识
     * @param compiled Nova 字节码程序
     * @param sourceMap 合并源码到原始来源的逐行映射
     */
    WorkspaceProgram(String entryName,
                     String moduleId,
                     CompiledNova compiled,
                     WorkspaceSourceMap sourceMap) {
        this.entryName = entryName;
        this.moduleId = moduleId;
        this.compiled = compiled;
        this.sourceMap = sourceMap;
    }

    /** @return 配置或虚拟入口名称 */
    public String getEntryName() {
        return entryName;
    }

    /** @return 规范模块标识 */
    public String getModuleId() {
        return moduleId;
    }

    /** @return 入口合并源码的 Source Map */
    public WorkspaceSourceMap getSourceMap() {
        return sourceMap;
    }

    /** @return 程序导出的不可变函数名集合 */
    public Set<String> getAvailableFunctions() {
        return compiled.getAvailableFunctions();
    }

    /**
     * 使用隔离绑定执行入口 main。
     *
     * @param bindings 本次执行绑定
     * @return main 返回值
     */
    Object run(Map<String, Object> bindings) {
        Map<String, Object> isolated = bindings == null
                ? new LinkedHashMap<String, Object>()
                : new LinkedHashMap<String, Object>(bindings);
        try {
            return compiled.runIsolated(isolated);
        } catch (RuntimeException exception) {
            throw sourceMap.mapFailure("Workspace entry initialization failed", exception);
        }
    }

    /**
     * 使用隔离绑定调用导出函数。
     *
     * @param functionName 函数名称
     * @param bindings 本次调用绑定
     * @param arguments 函数参数
     * @return 函数返回值
     */
    Object call(String functionName, Map<String, Object> bindings, Object[] arguments) {
        Map<String, Object> actualBindings = bindings == null
                ? Collections.<String, Object>emptyMap() : bindings;
        Object[] actualArguments = arguments == null ? new Object[0] : arguments;
        try {
            return compiled.callIsolated(functionName, actualBindings, actualArguments);
        } catch (RuntimeException exception) {
            throw sourceMap.mapFailure("Workspace function '" + functionName + "' failed", exception);
        }
    }
}
