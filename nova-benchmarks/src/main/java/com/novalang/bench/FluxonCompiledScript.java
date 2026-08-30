package com.novalang.bench;

import org.tabooproject.fluxon.Fluxon;
import org.tabooproject.fluxon.compiler.CompileResult;
import org.tabooproject.fluxon.interpreter.bytecode.FluxonClassLoader;
import org.tabooproject.fluxon.runtime.FluxonRuntime;
import org.tabooproject.fluxon.runtime.RuntimeScriptBase;

final class FluxonCompiledScript {

    private final RuntimeScriptBase script;

    private FluxonCompiledScript(RuntimeScriptBase script) {
        this.script = script;
    }

    static FluxonCompiledScript compile(String source, String className) {
        try {
            CompileResult compileResult = Fluxon.compile(source, className);
            FluxonClassLoader classLoader = new FluxonClassLoader(Fluxon.class.getClassLoader());
            Class<?> scriptClass = compileResult.defineClass(classLoader);
            RuntimeScriptBase script = (RuntimeScriptBase) scriptClass.getDeclaredConstructor().newInstance();
            script.setCommandData(compileResult.getCommandDataArray());
            return new FluxonCompiledScript(script);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to create compiled Fluxon script: " + className, e);
        }
    }

    Object run() {
        return script.eval(FluxonRuntime.getInstance().newEnvironment());
    }
}
