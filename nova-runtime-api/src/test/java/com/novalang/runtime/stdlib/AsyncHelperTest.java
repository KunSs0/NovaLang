package com.novalang.runtime.stdlib;

import com.novalang.runtime.Function0;
import com.novalang.runtime.NovaScriptContext;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AsyncHelperTest {

    @Test
    void asyncRestoresScriptContextAndPreservesFailure() throws Exception {
        Map<String, Object> bindings = new HashMap<>();
        bindings.put("requestId", "snapshot-42");
        NovaScriptContext.init(bindings);
        try {
            Function0<Object> task = () -> {
                assertEquals("snapshot-42", NovaScriptContext.get("requestId"));
                throw new IllegalStateException("snapshot failed");
            };

            CompletableFuture<?> future = (CompletableFuture<?>) AsyncHelper.run(task);
            ExecutionException error = assertThrows(ExecutionException.class, future::get);
            assertInstanceOf(IllegalStateException.class, error.getCause());
            assertEquals("snapshot failed", error.getCause().getMessage());
        } finally {
            NovaScriptContext.clear();
        }
    }
}
