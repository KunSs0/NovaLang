package com.novalang.workspace;

import com.novalang.runtime.CompiledNova;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 一个配置入口对应的模块级已编译 Nova 程序视图。
 */
public final class WorkspaceProgram {

    private final String entryName;
    private final String moduleId;
    private final List<CompiledUnit> units;

    WorkspaceProgram(String entryName,
                     String moduleId,
                     CompiledNova compiled,
                     WorkspaceSourceMap sourceMap) {
        this(entryName, moduleId,
                Collections.singletonList(new CompiledUnit(compiled, sourceMap)));
    }

    WorkspaceProgram(String entryName,
                     String moduleId,
                     List<CompiledUnit> units) {
        if (units == null || units.isEmpty()) {
            throw new IllegalArgumentException("Workspace program units must not be empty");
        }
        this.entryName = entryName;
        this.moduleId = moduleId;
        this.units = Collections.unmodifiableList(new ArrayList<CompiledUnit>(units));
    }

    /** @return 配置或虚拟入口名称 */
    public String getEntryName() {
        return entryName;
    }

    /** @return 规范模块标识 */
    public String getModuleId() {
        return moduleId;
    }

    /** @return 入口根编译组的 Source Map */
    public WorkspaceSourceMap getSourceMap() {
        return units.get(0).sourceMap;
    }

    /** @return 入口及其依赖模块导出的不可变函数名集合 */
    public Set<String> getAvailableFunctions() {
        Set<String> functions = new LinkedHashSet<String>();
        for (CompiledUnit unit : units) {
            functions.addAll(unit.compiled.getAvailableFunctions());
        }
        return Collections.unmodifiableSet(functions);
    }

    /** 使用隔离绑定执行当前编译组的 main 初始化。 */
    Object run(Map<String, Object> bindings) {
        Map<String, Object> isolated = bindings == null
                ? new LinkedHashMap<String, Object>()
                : new LinkedHashMap<String, Object>(bindings);
        CompiledUnit unit = units.get(0);
        try {
            return unit.compiled.runIsolated(isolated);
        } catch (RuntimeException exception) {
            throw unit.sourceMap.mapFailure(
                    "Workspace module initialization failed", exception);
        }
    }

    /** 使用隔离绑定调用入口或依赖模块导出的函数。 */
    Object call(String functionName, Map<String, Object> bindings, Object[] arguments) {
        Map<String, Object> actualBindings = bindings == null
                ? Collections.<String, Object>emptyMap() : bindings;
        Object[] actualArguments = arguments == null ? new Object[0] : arguments;
        for (CompiledUnit unit : units) {
            if (!unit.compiled.getAvailableFunctions().contains(functionName)) {
                continue;
            }
            try {
                return unit.compiled.callIsolated(
                        functionName, actualBindings, actualArguments);
            } catch (RuntimeException exception) {
                throw unit.sourceMap.mapFailure(
                        "Workspace function '" + functionName + "' failed", exception);
            }
        }
        throw new WorkspaceException("Workspace function does not exist in entry '"
                + entryName + "': " + functionName);
    }

    static CompiledUnit unit(CompiledNova compiled, WorkspaceSourceMap sourceMap) {
        return new CompiledUnit(compiled, sourceMap);
    }

    /** 一个模块编译组及其独立 Source Map。 */
    static final class CompiledUnit {

        private final CompiledNova compiled;
        private final WorkspaceSourceMap sourceMap;

        private CompiledUnit(CompiledNova compiled, WorkspaceSourceMap sourceMap) {
            this.compiled = compiled;
            this.sourceMap = sourceMap;
        }
    }
}
