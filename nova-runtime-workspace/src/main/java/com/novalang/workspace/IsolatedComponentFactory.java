package com.novalang.workspace;

import com.novalang.runtime.CompiledComponentFactory;
import com.novalang.runtime.CompiledProgramLayout;
import com.novalang.runtime.Nova;
import java.util.Map;

/** 共享不可变程序索引，每次 createScope 创建独立类、绑定和模块静态状态。 */
public final class IsolatedComponentFactory<T> {
    private final WorkspaceBytecodeArtifactCache.BytecodeArtifact artifact;
    private final CompiledProgramLayout layout;
    private final Nova nova;
    private final ClassLoader parent;
    private final String entry;
    private final Class<T> componentType;

    IsolatedComponentFactory(WorkspaceBytecodeArtifactCache.BytecodeArtifact artifact,
                             CompiledProgramLayout layout, Nova nova, ClassLoader parent,
                             String entry, Class<T> componentType) {
        if (nova == null || entry == null || componentType == null || !layout.hasFunction(entry)) {
            throw new IllegalArgumentException("Invalid isolated component factory: " + entry);
        }
        this.artifact = artifact;
        this.layout = layout;
        this.nova = nova;
        this.parent = parent;
        this.entry = entry;
        this.componentType = componentType;
    }

    public CompiledComponentFactory<T> createScope() {
        Map<String, Class<?>> classes = artifact.load(parent);
        synchronized (nova) {
            return nova.createCompiledNova(classes, layout).prepareComponentFactory(entry, componentType);
        }
    }
}
