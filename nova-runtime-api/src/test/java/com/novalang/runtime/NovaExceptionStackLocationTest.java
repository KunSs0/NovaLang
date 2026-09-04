package com.novalang.runtime;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NovaExceptionStackLocationTest {

    @Test
    void shouldFindOriginalLocationThroughWrappers() {
        Throwable cause = new NullPointerException("null Boolean");
        cause.setStackTrace(new StackTraceElement[] {
                new StackTraceElement("host.Native", "invoke", "Native.java", 90),
                new StackTraceElement("script.$Module", "inner", "script.nova", 7)
        });
        NovaException exception = new NovaException("wrapped", new RuntimeException(cause));
        exception.setStackTrace(new StackTraceElement[] {
                new StackTraceElement("script.$Module", "outer", "script.nova", 20)
        });

        exception.attachStackLocation(frame -> frame.getClassName().equals("script.$Module"));

        assertEquals("script.nova", exception.getSourceFile());
        assertEquals(7, exception.getSourceLineNumber());
        assertSame(cause, exception.getCause().getCause());
        exception.attachStackLocation(frame -> true);
        assertEquals(7, exception.getSourceLineNumber());
    }

    @Test
    void shouldHandleCyclicCausesWithoutInventingScriptLocation() {
        NovaException exception = new NovaException("outer");
        RuntimeException cause = new RuntimeException("inner", exception);
        exception.initCause(cause);

        exception.attachStackLocation(frame -> frame.getClassName().equals("script.$Module"));

        assertNull(exception.getSourceFile());
        assertEquals(0, exception.getSourceLineNumber());
    }
}
