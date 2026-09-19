package com.novalang.runtime;

import com.novalang.runtime.interpreter.NovaRuntimeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class CompiledNovaCallLocationTest {

    @ParameterizedTest
    @ValueSource(strings = {"call", "direct", "isolated"})
    void shouldReportBooleanUnboxingLocation(String mode) {
        CompiledNova compiled = new Nova().compileToBytecode(
                "fun execute(value: Boolean?): Boolean {\n"
                        + "    return !value\n"
                        + "}\n", "boolean-call.nova");

        NovaRuntimeException exception = assertThrows(NovaRuntimeException.class,
                () -> invoke(compiled, mode, "execute", (Object) null));

        assertInstanceOf(NullPointerException.class, exception.getCause());
        assertEquals("boolean-call.nova", exception.getSourceFile());
        assertEquals(2, exception.getSourceLineNumber());
        assertTrue(exception.getMessage().contains("--> boolean-call.nova:2"));
        assertEquals(true, invoke(compiled, mode, "execute", false));
    }

    @Test
    void shouldReportInnerFunctionLocation() {
        CompiledNova compiled = new Nova().compileToBytecode(
                "fun divide(value: Int): Int {\n"
                        + "    return 10 / value\n"
                        + "}\n"
                        + "fun execute(): Int {\n"
                        + "    return divide(0)\n"
                        + "}\n", "nested-call.nova");

        NovaRuntimeException exception = assertThrows(NovaRuntimeException.class,
                () -> compiled.callIsolated("execute", null));

        assertInstanceOf(ArithmeticException.class, exception.getCause());
        assertEquals("nested-call.nova", exception.getSourceFile());
        assertEquals(2, exception.getSourceLineNumber());
    }

    @Test
    void shouldNotReportHostLocationForMissingFunction() {
        CompiledNova compiled = new Nova().compileToBytecode(
                "fun execute(): Int = 1", "missing-call.nova");

        NovaRuntimeException exception = assertThrows(NovaRuntimeException.class,
                () -> compiled.callIsolated("missing", null));

        assertNull(exception.getSourceFile());
        assertEquals(0, exception.getSourceLineNumber());
    }

    private Object invoke(CompiledNova compiled, String mode, String name, Object... args) {
        if ("direct".equals(mode)) {
            return compiled.callDirect(name, new HashMap<String, Object>(), args);
        }
        if ("isolated".equals(mode)) {
            return compiled.callIsolated(name, null, args);
        }
        return compiled.call(name, args);
    }
}
