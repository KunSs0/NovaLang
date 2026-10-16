package com.novalang.runtime.compilation;

import com.novalang.runtime.NovaScheduler;
import com.novalang.runtime.ScriptCallback;
import com.novalang.runtime.SchedulerHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/** 使用真实 Nova 字节码验证函数引用、捕获闭包、参数和生命周期。 */
class CompilationFunctionBindingTest {
    @TempDir Path root;
    private CompilationHarness compilation;
    private NovaScheduler previousScheduler;

    @BeforeEach
    void load() throws Exception {
        previousScheduler = SchedulerHolder.get();
        SchedulerHolder.set(CompilationTestSupport.directScheduler());
        CompilationTestSupport.write(root, "main.nova",
                "import java com.novalang.runtime.ScriptCallbacks\n"
                + "import java com.novalang.runtime.NovaScriptContext\n"
                + "import java com.novalang.runtime.Function2\n"
                + "fun zero(): Any? { return com.novalang.runtime.NovaScriptContext.getAll().get(\"tag\") }\n"
                + "fun one(value) { return value }\n"
                + "fun two(a, b) { return a.toString() + b.toString() }\n"
                + "fun three(a, b, c) { return a.toString() + b.toString() + c.toString() }\n"
                + "fun bindZero() { return ScriptCallbacks.bind(::zero) }\n"
                + "fun bindOne() { return ScriptCallbacks.bind(::one) }\n"
                + "fun bindTwo() { return ScriptCallbacks.bind(::two) }\n"
                + "fun bindThree() { return ScriptCallbacks.bind(::three) }\n"
                + "fun bindLambda(prefix: String) {\n"
                + " return ScriptCallbacks.bind({ a, b -> prefix + a.toString() + b.toString() })\n"
                + "}\n"
                + "class PairHandler : Function2<String, String, String> {\n"
                + " override fun invoke(a: String, b: String): String { return a + b }\n"
                + "}\n"
                + "fun bindInstance() { return ScriptCallbacks.bind(PairHandler()) }\n"
                + "fun nullable(value: String?): String? { return value }\n"
                + "fun bindNullable() { return ScriptCallbacks.bind(::nullable) }\n"
                + "val initialCallback = ScriptCallbacks.bind(::two)\n"
                + "fun boundDuringInitialization() { return initialCallback }\n");
        Path config = CompilationTestSupport.writeMain(root, "main.nova");
        compilation = new CompilationHarness(config, nova -> { });
        compilation.compileAndRun();
    }

    @AfterEach
    void dispose() {
        try {
            if (compilation != null) {
                compilation.close();
            }
        } finally {
            if (previousScheduler == null) {
                SchedulerHolder.clear();
            } else {
                SchedulerHolder.set(previousScheduler);
            }
        }
    }

    private ScriptCallback bind(String factory, Object... arguments) {
        return (ScriptCallback) compilation.call(factory, Collections.singletonMap("tag", "captured"), arguments);
    }

    @Test
    void callbacksCanBeCapturedDuringScriptInitialization() {
        ScriptCallback callback = bind("boundDuringInitialization");
        assertTrue(callback.isValid());
        assertEquals("ab", callback.invoke("a", "b"));
        compilation.close();
        assertFalse(callback.isValid());
    }

    @Test
    void referencesKeepArgumentsAndResults() {
        Object event = new Object();
        assertEquals("captured", bind("bindZero").invoke());
        assertSame(event, bind("bindOne").invoke(event));
        assertEquals("ab", bind("bindTwo").invoke("a", "b"));
        assertEquals("abc", bind("bindThree").invoke("a", "b", "c"));
    }

    @Test
    void capturedLambdaAndInterfaceInstanceUseSamePath() {
        assertEquals("prefix:ab", bind("bindLambda", "prefix:").invoke("a", "b"));
        assertEquals("ab", bind("bindInstance").invoke("a", "b"));
        assertNull(bind("bindNullable").invoke((Object) null));
    }

    @Test
    void invocationBindingsOverrideCapturedBindingsAndRestoreContext() {
        ScriptCallback callback = bind("bindZero");
        assertEquals("event", callback.invokeWithBindings(Collections.singletonMap("tag", "event")));
        assertEquals("captured", callback.invoke());
        assertNull(com.novalang.runtime.ScriptCallbackContexts.current());
    }

    @Test
    void disposedCompilationAndWrongArityAreRejected() {
        ScriptCallback callback = bind("bindTwo");
        assertThrows(IllegalArgumentException.class, () -> callback.invoke("one"));
        assertTrue(callback.isValid());
        compilation.close();
        assertFalse(callback.isValid());
        assertThrows(IllegalStateException.class, () -> callback.invoke("a", "b"));
    }
}
