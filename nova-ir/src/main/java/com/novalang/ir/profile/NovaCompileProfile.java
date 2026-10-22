package com.novalang.ir.profile;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 编译阶段耗时统计。
 *
 * <p>默认关闭；设置 {@code -Dnova.profile=true} 后按阶段累加纳秒并可通过
 * {@link #report(String)} 输出。仅用于定位性能问题，关闭时所有方法都是空操作。</p>
 */
public final class NovaCompileProfile {

    private static final boolean ENABLED = "true".equals(System.getProperty("nova.profile"));
    private static final Map<String, Long> NANOS = new ConcurrentHashMap<String, Long>();
    private static final Map<String, Long> COUNT = new ConcurrentHashMap<String, Long>();

    private NovaCompileProfile() {
    }

    /** @return 是否启用阶段统计 */
    public static boolean enabled() {
        return ENABLED;
    }

    /** @return 阶段起点；未启用时返回 0，配合 {@link #add(String, long)} 使用 */
    public static long start() {
        return ENABLED ? System.nanoTime() : 0L;
    }

    /** 记录一个阶段耗时。 */
    public static void add(String phase, long startNanos) {
        if (!ENABLED || startNanos == 0L) {
            return;
        }
        NANOS.merge(phase, System.nanoTime() - startNanos, Long::sum);
        COUNT.merge(phase, 1L, Long::sum);
    }

    /** 输出当前累计结果。 */
    public static void report(String title) {
        if (!ENABLED) {
            return;
        }
        long total = 0L;
        for (Long value : NANOS.values()) {
            total += value;
        }
        System.out.println("[NovaCompile] " + title + " accumulated=" + (total / 1000000L) + "ms");
        NANOS.entrySet().stream()
                .sorted((left, right) -> Long.compare(right.getValue(), left.getValue()))
                .forEach(entry -> System.out.println("[NovaCompile]   " + entry.getKey()
                        + " " + (entry.getValue() / 1000000L) + "ms x" + COUNT.get(entry.getKey())));
    }

    /** 清空累计，用于分轮对照。 */
    public static void reset() {
        NANOS.clear();
        COUNT.clear();
    }
}
