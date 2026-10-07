package com.novalang.runtime.codegen;

import com.novalang.runtime.CompiledNova;
import com.novalang.runtime.Nova;
import com.novalang.runtime.ScriptCallback;
import com.novalang.runtime.ScriptCallbackContexts;
import com.novalang.runtime.ScriptCallbacks;
import com.novalang.runtime.ScriptFunction;
import com.novalang.runtime.NovaScriptContext;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;

/** 使用普通编译脚本验证底层回调，不导入或创建 Workspace。 */
class ScriptCallbackTest {
    private static final String SOURCE = "import java com.novalang.runtime.codegen.ScriptCallbackTest.Host\n"
            + "fun read(value: String): String { return prefix + value }\n"
            + "fun register(host: Host) { host.accept(::read) }\n"
            + "fun registerLambda(host: Host, prefix: String) {\n"
            + " host.accept({ value: String -> prefix + value })\n}\n";

    @Test
    void directReferenceRestoresBindingsAndClosesWithScriptInstance() {
        Host host = new Host();
        CompiledNova compiled = new Nova().compileToBytecode(SOURCE, "callback-reference.nova");
        compiled.set("prefix", "captured:");
        compiled.run();
        compiled.call("register", host);
        assertEquals("captured:value", host.callback.invoke("value"));
        assertEquals("event:value", host.callback.invokeWithBindings(
                Collections.singletonMap("prefix", "event:"), "value"));
        assertNull(ScriptCallbackContexts.current());
        assertNull(NovaScriptContext.current());
        compiled.close();
        assertFalse(host.callback.isValid());
        assertEquals(1, host.closed);
        compiled.close();
        assertEquals(1, host.closed);
        assertThrows(IllegalStateException.class, () -> host.callback.invoke("value"));
    }

    @Test
    void capturedLambdaAndWrongArgumentCountUseBottomFunctionContract() {
        Host host = new Host();
        try (CompiledNova compiled = new Nova().compileToBytecode(SOURCE, "callback-lambda.nova")) {
            compiled.run();
            compiled.call("registerLambda", host, "local:");
            assertEquals("local:value", host.callback.invoke("value"));
            assertThrows(IllegalArgumentException.class, () -> host.callback.invoke());
        }
        assertEquals(1, host.closed);
    }

    @Test
    void interpretedScriptUsesSameInstanceLifetime() {
        Host host = new Host();
        Nova nova = new Nova();
        try (CompiledNova compiled = nova.compile(SOURCE, "interpreted-callback.nova")) {
            compiled.run();
            compiled.call("registerLambda", host, "interpreted:");
            assertEquals("interpreted:value", host.callback.invoke("value"));
        }
        assertEquals(1, host.closed);
        assertFalse(host.callback.isValid());
    }

    @Test
    void callbackRestoresScriptLoaderOnAnotherThread() throws Exception {
        Host host = new Host();
        ClassLoader loader = new ClassLoader(getClass().getClassLoader()) { };
        String source = "import java com.novalang.runtime.codegen.ScriptCallbackTest.Host\n"
                + "fun capture(host: Host) { host.accept({ Host.currentLoader() }) }\n";
        try (CompiledNova compiled = new Nova().compileToBytecode(source, "callback-loader.nova")) {
            compiled.setScriptClassLoader(loader);
            compiled.call("capture", host);
            java.util.concurrent.CompletableFuture<Object> result = java.util.concurrent.CompletableFuture.supplyAsync(() -> {
                Object value = host.callback.invoke();
                assertNull(com.novalang.runtime.interpreter.JavaInterop.getScriptClassLoader());
                assertNull(NovaScriptContext.current());
                return value;
            });
            assertSame(loader, result.get(5, java.util.concurrent.TimeUnit.SECONDS));
        }
    }

    public static final class Host {
        ScriptCallback callback;
        int closed;

        public static ClassLoader currentLoader() {
            return com.novalang.runtime.interpreter.JavaInterop.getScriptClassLoader();
        }

        public void accept(ScriptFunction function) {
            callback = ScriptCallbacks.bind(function);
            callback.register(() -> closed++);
        }
    }
}
