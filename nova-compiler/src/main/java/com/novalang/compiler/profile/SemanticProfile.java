package com.novalang.compiler.profile;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 语义分析阶段耗时统计。
 *
 * <p>默认关闭；设置 {@code -Dnova.profile=true} 后按阶段累加纳秒，并通过
 * {@link #report(String)} 输出。用于定位 SemanticAnalyzer 内部热点，关闭时所有
 * 方法都是空操作。</p>
 */
public final class SemanticProfile {

    private static final boolean ENABLED = "true".equals(System.getProperty("nova.profile"));
    private static final Map<String, Long> NANOS = new ConcurrentHashMap<String, Long>();
    private static final Map<String, Long> COUNT = new ConcurrentHashMap<String, Long>();
    private static final Map<String, Long> EVENTS = new ConcurrentHashMap<String, Long>();

    private SemanticProfile() {
    }

    /** @return 是否启用语义阶段统计 */
    public static boolean enabled() {
        return ENABLED;
    }

    /** @return 阶段起点；未启用时返回 0，配合 {@link #add(String, long)} 使用 */
    public static long start() {
        return ENABLED ? System.nanoTime() : 0L;
    }

    /** 记录一个语义阶段耗时。 */
    public static void add(String phase, long startNanos) {
        if (!ENABLED || startNanos == 0L) {
            return;
        }
        NANOS.merge(phase, System.nanoTime() - startNanos, Long::sum);
        COUNT.merge(phase, 1L, Long::sum);
    }

    /** 记录一次事件计数。 */
    public static void increment(String name) {
        if (ENABLED) {
            EVENTS.merge(name, 1L, Long::sum);
        }
    }

    /** 输出当前累计结果。 */
    public static void report(String title) {
        if (!ENABLED) {
            return;
        }
        System.out.println("[SemanticProfile] " + title);
        NANOS.entrySet().stream()
                .sorted((left, right) -> Long.compare(right.getValue(), left.getValue()))
                .forEach(entry -> System.out.println("[SemanticProfile]   " + entry.getKey()
                        + " " + (entry.getValue() / 1000000L) + "ms x" + COUNT.get(entry.getKey())));
        EVENTS.entrySet().stream()
                .sorted((left, right) -> Long.compare(right.getValue(), left.getValue()))
                .forEach(entry -> System.out.println("[SemanticProfile]   " + entry.getKey()
                        + " count=" + entry.getValue()));
    }

    /** 清空累计，用于分轮对照。 */
    public static void reset() {
        NANOS.clear();
        COUNT.clear();
        EVENTS.clear();
    }
}
