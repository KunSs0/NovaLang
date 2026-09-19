package com.novalang.runtime.stdlib;

import com.novalang.runtime.NovaScheduleContext;
import com.novalang.runtime.NovaScheduleContexts;
import com.novalang.runtime.NovaScriptContext;
import java.util.function.Supplier;

/** 同时传播语言绑定和宿主生命周期上下文；不在等待期间持有宿主执行锁。 */
final class CapturedTaskContext {
    final NovaScheduleContext owner = NovaScheduleContexts.current();
    private final NovaScriptContext script = NovaScriptContext.current();

    Object call(Supplier<Object> action) {
        NovaScriptContext previous = NovaScriptContext.current();
        try (AutoCloseable installed = owner == null ? null : owner.enter()) {
            NovaScriptContext.setCurrent(script);
            return action.get();
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalStateException("Cannot restore asynchronous script context", exception);
        } finally {
            NovaScriptContext.setCurrent(previous);
        }
    }
}
