package com.novalang.runtime;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** 从不可变字节码准备一次程序索引，跨隔离作用域复用，绝不持有已加载的脚本 Class。 */
public final class CompiledProgramLayout {
    private final Map<String, String> functionOwners;
    private final String mainOwner;

    private CompiledProgramLayout(Map<String, String> owners, String mainOwner) {
        this.functionOwners = Collections.unmodifiableMap(new LinkedHashMap<>(owners));
        this.mainOwner = mainOwner;
    }

    public static CompiledProgramLayout fromBytecode(Map<String, byte[]> bytecode) {
        if (bytecode == null || bytecode.isEmpty()) {
            throw new IllegalArgumentException("Program bytecode must not be empty");
        }
        Map<String, String> owners = new LinkedHashMap<>();
        String[] main = new String[1];
        for (Map.Entry<String, byte[]> entry : bytecode.entrySet()) {
            String className = entry.getKey();
            new ClassReader(entry.getValue()).accept(new ClassVisitor(Opcodes.ASM9) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String descriptor,
                                                 String signature, String[] exceptions) {
                    int required = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC;
                    if ((access & required) == required) {
                        if (className.endsWith("$Module")) {
                            owners.putIfAbsent(name, className);
                        }
                        if (main[0] == null && "main".equals(name)
                                && Type.getArgumentTypes(descriptor).length == 0) {
                            main[0] = className;
                        }
                    }
                    return null;
                }
            }, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        }
        return new CompiledProgramLayout(owners, main[0]);
    }

    public boolean hasFunction(String name) {
        return functionOwners.containsKey(name);
    }

    Map<String, Class<?>> bindFunctions(Map<String, Class<?>> classes) {
        Map<String, Class<?>> result = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : functionOwners.entrySet()) {
            Class<?> owner = classes.get(entry.getValue());
            if (owner == null) {
                throw new IllegalArgumentException("Missing compiled class: " + entry.getValue());
            }
            result.put(entry.getKey(), owner);
        }
        return result;
    }

    MethodHandle bindMain(Map<String, Class<?>> classes) {
        if (mainOwner == null) {
            return null;
        }
        Class<?> owner = classes.get(mainOwner);
        if (owner == null) {
            throw new IllegalArgumentException("Missing main class: " + mainOwner);
        }
        try {
            return MethodHandles.lookup().unreflect(owner.getMethod("main"))
                    .asType(MethodType.methodType(Object.class));
        } catch (ReflectiveOperationException exception) {
            throw new IllegalArgumentException("Invalid main entry: " + mainOwner, exception);
        }
    }
}
