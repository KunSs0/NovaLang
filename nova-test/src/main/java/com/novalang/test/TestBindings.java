package com.novalang.test;

import com.novalang.runtime.Function1;
import com.novalang.runtime.Function2;
import com.novalang.runtime.Function3;
import com.novalang.runtime.Nova;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/** 单个测试用例的断言和 mock 绑定。 */
public final class TestBindings {
    private final AtomicInteger assertions;
    private final Map<String, Object> values = new LinkedHashMap<String, Object>();

    public TestBindings(AtomicInteger assertions, Map<String, Object> initial) {
        if (assertions == null) {
            throw new IllegalArgumentException("assertions must not be null");
        }
        this.assertions = assertions;
        if (initial != null) {
            values.putAll(initial);
        }
    }

    public int getAssertionCount() {
        return assertions.get();
    }

    public void install(Nova nova) {
        if (nova == null) {
            throw new IllegalArgumentException("nova must not be null");
        }
        nova.defineFunction("mockSet", new Function2<Object, Object, Object>() {
            @Override
            public Object invoke(Object name, Object value) {
                values.put(String.valueOf(name), value);
                return null;
            }
        });
        nova.registerExternalCallable("mockSet", 2);
        nova.defineFunction("mockValue", new Function1<Object, Object>() {
            @Override
            public Object invoke(Object name) {
                return values.get(String.valueOf(name));
            }
        });
        nova.registerExternalCallable("mockValue", 1);
        nova.defineFunction("assertTrue", new Function2<Object, Object, Object>() {
            @Override
            public Object invoke(Object value, Object message) {
                check(Boolean.TRUE.equals(value), String.valueOf(message));
                return null;
            }
        });
        nova.registerExternalCallable("assertTrue", 2);
        nova.defineFunction("assertFalse", new Function2<Object, Object, Object>() {
            @Override
            public Object invoke(Object value, Object message) {
                check(!Boolean.TRUE.equals(value), String.valueOf(message));
                return null;
            }
        });
        nova.registerExternalCallable("assertFalse", 2);
        nova.defineFunction("assertEquals", new Function3<Object, Object, Object, Object>() {
            @Override
            public Object invoke(Object expected, Object actual, Object message) {
                check(valuesEqual(expected, actual), String.valueOf(message)
                        + " expected=" + expected + " actual=" + actual);
                return null;
            }
        });
        nova.registerExternalCallable("assertEquals", 3);
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
}
