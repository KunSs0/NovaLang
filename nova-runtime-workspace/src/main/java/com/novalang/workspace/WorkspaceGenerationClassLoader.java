package com.novalang.workspace;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 一个 Workspace Generation 独占的可增量字节码 ClassLoader。
 */
final class WorkspaceGenerationClassLoader extends ClassLoader {

    private final Map<String, byte[]> pendingBytecode = new LinkedHashMap<String, byte[]>();

    WorkspaceGenerationClassLoader(ClassLoader parent) {
        super(parent);
    }

    synchronized Map<String, Class<?>> install(Map<String, byte[]> bytecode) {
        for (Map.Entry<String, byte[]> entry : bytecode.entrySet()) {
            String className = entry.getKey();
            if (findLoadedClass(className) != null || pendingBytecode.containsKey(className)) {
                throw new WorkspaceException(
                        "Workspace Generation class is already defined: " + className);
            }
            pendingBytecode.put(className, Arrays.copyOf(
                    entry.getValue(), entry.getValue().length));
        }

        Map<String, Class<?>> loaded = new LinkedHashMap<String, Class<?>>();
        try {
            for (String className : bytecode.keySet()) {
                loaded.put(className, loadClass(className));
            }
        } catch (ClassNotFoundException exception) {
            throw new WorkspaceException("Failed to load Workspace Generation class", exception);
        }
        return loaded;
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve)
            throws ClassNotFoundException {
        Class<?> loaded = findLoadedClass(name);
        if (loaded == null && pendingBytecode.containsKey(name)) {
            loaded = findClass(name);
        }
        if (loaded == null) {
            loaded = super.loadClass(name, false);
        }
        if (resolve) {
            resolveClass(loaded);
        }
        return loaded;
    }

    @Override
    protected synchronized Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] bytecode = pendingBytecode.remove(name);
        if (bytecode == null) {
            throw new ClassNotFoundException(name);
        }
        return defineClass(name, bytecode, 0, bytecode.length);
    }
}
