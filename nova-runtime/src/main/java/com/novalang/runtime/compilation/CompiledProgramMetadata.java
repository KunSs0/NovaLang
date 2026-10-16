package com.novalang.runtime.compilation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 一个 CompiledNova 的模块链接结果，只保存入口、初始化顺序及诊断索引。
 * <p>它没有独立生命周期，也不承担执行隔离；所有类归属于同一个编译程序。</p>
 */
public final class CompiledProgramMetadata {
    private final ModuleGraph graph;
    private final Map<String, Class<?>> functions;
    private final Map<String, Class<?>> moduleClasses;
    private final List<Class<?>> initializers;
    private final Map<String, SourceMap> sourceMaps;

    /** 复制编译器生成的索引，禁止编译完成后修改链接关系。 */
    CompiledProgramMetadata(ModuleGraph graph, Map<String, Class<?>> functions,
                            Map<String, Class<?>> moduleClasses, List<Class<?>> initializers, Map<String, SourceMap> sourceMaps) {
        this.graph = graph;
        this.moduleClasses = Collections.unmodifiableMap(new LinkedHashMap<>(moduleClasses));
        this.functions = Collections.unmodifiableMap(new LinkedHashMap<>(functions));
        this.initializers = Collections.unmodifiableList(new ArrayList<>(initializers));
        this.sourceMaps = Collections.unmodifiableMap(new LinkedHashMap<>(sourceMaps));
    }

    /** @return 编译时已经验证的不可变模块依赖图 */
    public ModuleGraph getModuleGraph() {
        return graph;
    }

    /** @return 依赖优先的模块初始化顺序 */
    public List<Class<?>> getInitializers() {
        return initializers;
    }

    /**
     * 定位单入口程序可见的函数；入口自身声明优先于依赖声明。
     * @param function 函数名称
     * @return 函数所属类
     * @throws CompilationException 函数不存在时抛出
     */
    public Class<?> requireFunction(String function) {
        Class<?> owner = functions.get(function);
        if (owner == null) {
            throw new CompilationException("Function does not exist: " + function);
        }
        return owner;
    }

    /**
     * 严格定位指定模块的函数，避免多个虚拟动作同名时选中其他模块。
     */
    public Class<?> requireFunction(String moduleId, String function) {
        Class<?> owner = moduleClasses.get(moduleId);
        if (owner != null) {
            for (java.lang.reflect.Method method : owner.getDeclaredMethods()) {
                int modifiers = method.getModifiers();
                if (method.getName().equals(function) && java.lang.reflect.Modifier.isStatic(modifiers)
                        && java.lang.reflect.Modifier.isPublic(modifiers)) {
                    return owner;
                }
            }
        }
        throw new CompilationException("Function does not exist: " + moduleId + "#" + function);
    }

    /**
     * 按异常栈中实际脚本类选择 Source Map，避免将依赖异常映射到入口文件。
     * @param operation 操作描述
     * @param failure 原始失败
     * @return 包含原始源码位置的异常
     */
    public CompilationException mapFailure(String operation, Throwable failure) {
        Throwable current = failure;
        while (current != null) {
            for (StackTraceElement frame : current.getStackTrace()) {
                SourceMap sourceMap = sourceMaps.get(frame.getClassName());
                if (sourceMap != null && frame.getLineNumber() > 0) {
                    MappedSourceLocation location = sourceMap.mapLine(frame.getLineNumber());
                    if (location != null) {
                        return new CompilationException(operation + " at " + location.describe(), failure);
                    }
                }
            }
            current = current.getCause();
        }
        return new CompilationException(operation, failure);
    }
}
