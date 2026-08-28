package com.novalang.mock;

import com.novalang.runtime.Function1;
import com.novalang.runtime.Function2;
import com.novalang.runtime.Function3;
import com.novalang.runtime.Nova;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/** 单个 mock 文件的隔离绑定、断言计数和外部输入值。 */
public final class MockTestBindings {

    private static final ThreadLocal<MockTestBindings> CURRENT = new ThreadLocal<MockTestBindings>();

    private final AtomicInteger assertions;
    private final Map<String, Object> values = new LinkedHashMap<String, Object>();

    public MockTestBindings(AtomicInteger assertions, Map<String, Object> initial) {
        this.assertions = assertions;
        if (initial != null) {
            values.putAll(initial);
        }
    }

    /** 安装本次绑定上下文，返回可关闭的恢复句柄。 */
    public Scope installCurrent() {
        MockTestBindings previous = CURRENT.get();
        CURRENT.set(this);
        return new Scope(previous);
    }

    /** @return 当前 Workspace 初始化所使用的绑定 */
    public static MockTestBindings requireCurrent() {
        MockTestBindings bindings = CURRENT.get();
        if (bindings == null) {
            throw new IllegalStateException("No active Nova mock binding");
        }
        return bindings;
    }

    /** @return 本次断言数 */
    public int getAssertionCount() {
        return assertions.get();
    }

    /** 为 Nova 安装通用 mock 值与断言；不覆盖 Nova 标准库函数。 */
    public void install(Nova nova) {
        nova.defineFunction("mockSet", new Function2<Object, Object, Object>() {
            @Override
            public Object invoke(Object name, Object value) {
                values.put(String.valueOf(name), value);
                return null;
            }
        });
        nova.defineFunction("mockValue", new Function1<Object, Object>() {
            @Override
            public Object invoke(Object name) {
                return values.get(String.valueOf(name));
            }
        });
        nova.defineFunction("assertTrue", new Function2<Object, Object, Object>() {
            @Override
            public Object invoke(Object value, Object message) {
                check(Boolean.TRUE.equals(value), String.valueOf(message));
                return null;
            }
        });
        nova.defineFunction("assertFalse", new Function2<Object, Object, Object>() {
            @Override
            public Object invoke(Object value, Object message) {
                check(!Boolean.TRUE.equals(value), String.valueOf(message));
                return null;
            }
        });
        nova.defineFunction("assertEquals", new Function3<Object, Object, Object, Object>() {
            @Override
            public Object invoke(Object expected, Object actual, Object message) {
                boolean equal = valuesEqual(expected, actual);
                check(equal, String.valueOf(message) + " expected=" + expected + " actual=" + actual);
                return null;
            }
        });
    }

    private void check(boolean condition, String message) {
        assertions.incrementAndGet();
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static boolean valuesEqual(Object expected, Object actual) {
        if (expected instanceof Number && actual instanceof Number) {
            BigDecimal left = new BigDecimal(expected.toString());
            BigDecimal right = new BigDecimal(actual.toString());
            return left.compareTo(right) == 0;
        }
        return expected == null ? actual == null : expected.equals(actual);
    }

    /** 恢复上一份线程绑定。 */
    public static final class Scope implements AutoCloseable {
        private final MockTestBindings previous;
        private boolean closed;

        Scope(MockTestBindings previous) {
            this.previous = previous;
        }

        @Override
        public void close() {
            if (closed) {
                return;
            }
            closed = true;
            if (previous == null) {
                CURRENT.remove();
            } else {
                CURRENT.set(previous);
            }
        }
    }
}
