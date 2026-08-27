package com.novalang.workspace;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link ResourceScope} 资源归属、销毁顺序和串行执行测试。
 */
@DisplayName("Workspace ResourceScope")
class ResourceScopeTest {

    @Test
    @DisplayName("子作用域和资源按注册逆序销毁")
    void shouldDisposeChildrenAndResourcesInReverseOrder() {
        List<String> order = new ArrayList<String>();
        ResourceScope root = ResourceScope.generation("root");
        root.register(() -> order.add("root-1"));
        root.register(() -> order.add("root-2"));
        ResourceScope first = root.openChild(ScopeType.BUSINESS_INSTANCE, "first");
        first.register(() -> order.add("first"));
        ResourceScope second = root.openChild(ScopeType.BUSINESS_INSTANCE, "second");
        second.register(() -> order.add("second"));

        root.dispose();

        assertEquals(Arrays.asList("second", "first", "root-2", "root-1"), order);
        assertEquals(ResourceScopeState.DISPOSED, root.getState());
        assertEquals(ResourceScopeState.DISPOSED, first.getState());
        assertEquals(ResourceScopeState.DISPOSED, second.getState());
    }

    @Test
    @DisplayName("dispose 幂等且资源只释放一次")
    void shouldDisposeOnlyOnce() {
        AtomicInteger calls = new AtomicInteger();
        ResourceScope root = ResourceScope.generation("root");
        root.register(() -> calls.incrementAndGet());

        root.dispose();
        root.dispose();

        assertEquals(1, calls.get());
    }

    @Test
    @DisplayName("单个资源失败仍继续清理其余资源")
    void shouldContinueDisposalAfterResourceFailure() {
        AtomicInteger calls = new AtomicInteger();
        ResourceScope root = ResourceScope.generation("root");
        root.register(() -> calls.incrementAndGet());
        root.register(() -> {
            throw new IllegalStateException("expected failure");
        });
        root.register(() -> calls.incrementAndGet());

        WorkspaceException exception = assertThrows(WorkspaceException.class, root::dispose);

        assertEquals(2, calls.get());
        assertEquals(ResourceScopeState.DISPOSED, root.getState());
        assertTrue(exception.getMessage().startsWith("Failed to dispose scope resources"));
    }

    @Test
    @DisplayName("已销毁作用域拒绝创建子节点和登记资源")
    void shouldRejectOperationsAfterDispose() {
        ResourceScope root = ResourceScope.generation("root");
        root.dispose();

        assertThrows(WorkspaceException.class,
                () -> root.openChild(ScopeType.INVOCATION, "late"));
        assertThrows(WorkspaceException.class,
                () -> root.register(() -> { }));
    }

    @Test
    @DisplayName("Generation 不能作为子作用域")
    void shouldRejectNestedGeneration() {
        ResourceScope root = ResourceScope.generation("root");

        assertThrows(WorkspaceException.class,
                () -> root.openChild(ScopeType.GENERATION, "nested"));
    }

    @Test
    @DisplayName("子作用域能够识别所属资源树")
    void shouldIdentifyOwningTree() {
        ResourceScope firstRoot = ResourceScope.generation("first");
        ResourceScope child = firstRoot.openChild(ScopeType.STAGE, "stage");
        ResourceScope secondRoot = ResourceScope.generation("second");

        assertTrue(child.belongsTo(firstRoot));
        assertFalse(child.belongsTo(secondRoot));
    }

    @Test
    @DisplayName("SERIAL_SCOPE 对同一作用域公平串行")
    void shouldSerializeExecutionWithinScope() throws Exception {
        ResourceScope root = ResourceScope.generation("root");
        AtomicInteger active = new AtomicInteger();
        AtomicInteger maximum = new AtomicInteger();
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(4);
        try {
            List<Future<Integer>> futures = new ArrayList<Future<Integer>>();
            for (int index = 0; index < 20; index++) {
                futures.add(executor.submit(() -> {
                    start.await();
                    return root.executeSerial(() -> {
                        int current = active.incrementAndGet();
                        maximum.accumulateAndGet(current, Math::max);
                        try {
                            Thread.yield();
                            return current;
                        } finally {
                            active.decrementAndGet();
                        }
                    });
                }));
            }
            start.countDown();
            for (Future<Integer> future : futures) {
                assertEquals(1, future.get().intValue());
            }
            assertEquals(1, maximum.get());
        } finally {
            executor.shutdownNow();
            root.dispose();
        }
    }
}
