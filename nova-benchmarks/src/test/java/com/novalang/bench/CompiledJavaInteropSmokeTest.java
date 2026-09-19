package com.novalang.bench;

import com.novalang.runtime.CompiledNova;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompiledJavaInteropSmokeTest {

    @Test
    void allCompiledInteropScenariosMatchJavaNativeBaseline() {
        for (Map.Entry<String, CompiledInteropScenario> entry : CompiledInteropScenarios.all().entrySet()) {
            String name = entry.getKey();
            CompiledInteropScenario scenario = entry.getValue();

            assertEquals(CompiledInteropScenarios.INTEROP_CALLS_PER_RUN,
                    scenario.getInteropCallsPerRun(), name + " interop calls per run");

            int expected = scenario.runJavaNative();
            CompiledNova compiled = ScriptBenchSupport.compileNova(scenario.getNovaSource());

            assertTrue(compiled.isBytecodeMode(), name + " must run as compiled bytecode");
            int actual = ScriptBenchSupport.toInt(compiled.run());
            assertEquals(expected, actual, name + " compiled result");
        }
    }
}
