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
        return submit(lambda, java.util.concurrent.ForkJoinPool.commonPool());
    }

    static CompletableFuture<Object> submit(Object lambda, java.util.concurrent.Executor executor) {
        Supplier<Object> task = () -> LambdaUtils.invoke0(lambda);
        CompletableFuture<Object> future = OwnedAsyncTask.submit(task, executor);
        future.whenComplete((value, error) -> {
            if (error != null && !future.isCancelled()) {
                LOGGER.log(Level.SEVERE, "async task failed", error);
            }
        });
        return future;
    }
}
