package com.novalang.runtime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link CompiledNova#callIsolated(String, Map, Object...)} 隔离绑定测试。
 */
@DisplayName("CompiledNova 隔离函数调用")
class CompiledNovaIsolatedCallTest {

    @Test
    @DisplayName("不同调用绑定不会互相污染或回写输入 Map")
    void shouldIsolateBindingsBetweenCalls() {
        Nova nova = new Nova();
        nova.set("value", 0);
        CompiledNova compiled = nova.compileToBytecode(
                "fun next(): Int { value = value + 1; return value }", "isolated.nova");

        Map<String, Object> first = new HashMap<String, Object>();
        first.put("value", 10);
        Map<String, Object> second = new HashMap<String, Object>();
        second.put("value", 20);

        assertEquals(11, ((Number) compiled.callIsolated("next", first)).intValue());
        assertEquals(21, ((Number) compiled.callIsolated("next", second)).intValue());
        assertEquals(10, first.get("value"));
        assertEquals(20, second.get("value"));
        assertEquals(0, compiled.get("value"));
    }

    @Test
    @DisplayName("同一字节码程序支持并发隔离调用")
    void shouldSupportConcurrentIsolatedCalls() throws Exception {
        Nova nova = new Nova();
        nova.set("value", 0);
        CompiledNova compiled = nova.compileToBytecode(
                "fun calculate(): Int = value * 2", "parallel-isolated.nova");
        ExecutorService executor = Executors.newFixedThreadPool(8);
        try {
            List<Future<Integer>> futures = new ArrayList<Future<Integer>>();
            for (int index = 0; index < 100; index++) {
                final int value = index;
                futures.add(executor.submit(new Callable<Integer>() {
                    @Override
                    public Integer call() {
                        Map<String, Object> bindings = new HashMap<String, Object>();
                        bindings.put("value", value);
                        Number result = (Number) compiled.callIsolated("calculate", bindings);
                        return result.intValue();
                    }
                }));
            }
            for (int index = 0; index < futures.size(); index++) {
                assertEquals(index * 2, futures.get(index).get().intValue());
            }
        } finally {
            executor.shutdownNow();
        }
    }
}
