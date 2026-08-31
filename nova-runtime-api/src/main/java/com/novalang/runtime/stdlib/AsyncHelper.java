package com.novalang.runtime.stdlib;

import com.novalang.runtime.Function0;
import com.novalang.runtime.NovaScriptContext;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.function.Supplier;

/**
 * IR 编译路径的 async 辅助：将 lambda 对象包装为 Supplier 并提交给 CompletableFuture。
 */
public final class AsyncHelper {

    private static final Logger LOGGER = Logger.getLogger("Nova");

    private AsyncHelper() {}

    @SuppressWarnings("unchecked")
    public static Object run(Object lambda) {
        NovaScriptContext capturedContext = NovaScriptContext.current();
        Supplier<Object> task;
        if (lambda instanceof Function0) {
            Function0<Object> fn = (Function0<Object>) lambda;
            task = fn::invoke;
        } else if (lambda instanceof Supplier) {
            Supplier<Object> sup = (Supplier<Object>) lambda;
            task = sup;
        } else {
            task = () -> LambdaUtils.invoke0(lambda);
        }

        CompletableFuture<Object> future = CompletableFuture.supplyAsync(() -> {
            NovaScriptContext previousContext = NovaScriptContext.current();
            NovaScriptContext.setCurrent(capturedContext);
            try {
                return task.get();
            } finally {
                NovaScriptContext.setCurrent(previousContext);
            }
        });
        future.whenComplete((value, error) -> {
            if (error != null) {
                LOGGER.log(Level.SEVERE, "async task failed", error);
            }
        });
        return future;
    }
}
