package com.novalang.runtime.stdlib.spi;

import java.util.*;

/**
 * JSON/YAML 提供者注册表。
 *
 * <p>启动时通过 ServiceLoader 一次性扫描所有 classpath 中的 provider，
 * 按 priority 选择最优实现。结果缓存，后续调用零开销。</p>
 *
 * <p>支持运行时手动切换：{@code setJsonProvider("builtin")}。</p>
 */
public final class SerializationProviders {

    private static volatile JsonProvider jsonProvider;
    private static volatile YamlProvider yamlProvider;

    // 所有已发现的 provider（name → instance）
    private static final Map<String, JsonProvider> jsonProviders = new LinkedHashMap<>();
    private static final Map<String, YamlProvider> yamlProviders = new LinkedHashMap<>();

    static {
        // ServiceLoader 扫描（一次性）。
        // 必须同时使用 NovaLang 自身的 classloader 与线程上下文 classloader：
        // 插件环境下（Paper PluginClassLoader）调用线程的 TCCL 往往看不到本 jar 的
        // META-INF/services，只依赖 TCCL 会让 provider 全部缺失并永久缓存为 null。
        ClassLoader ownLoader = SerializationProviders.class.getClassLoader();
        loadJsonProviders(ownLoader);
        loadJsonProviders(Thread.currentThread().getContextClassLoader());
        loadYamlProviders(ownLoader);
        loadYamlProviders(Thread.currentThread().getContextClassLoader());
        // 选择最高优先级
        jsonProvider = selectBest(jsonProviders.values());
        yamlProvider = selectBest(yamlProviders.values());
    }

    /**
     * 从一个 classloader 收集 JSON provider。
     *
     * <p>同一个 provider 可能被多个 classloader 同时发现，按 name 覆盖写即可。</p>
     *
     * @param loader 待扫描的 classloader，为 null 时跳过
     */
    private static void loadJsonProviders(ClassLoader loader) {
        if (loader == null) {
            return;
        }
        for (JsonProvider p : ServiceLoader.load(JsonProvider.class, loader)) {
            jsonProviders.put(p.name(), p);
        }
    }

    /**
     * 从一个 classloader 收集 YAML provider。
     *
     * @param loader 待扫描的 classloader，为 null 时跳过
     */
    private static void loadYamlProviders(ClassLoader loader) {
        if (loader == null) {
            return;
        }
        for (YamlProvider p : ServiceLoader.load(YamlProvider.class, loader)) {
            yamlProviders.put(p.name(), p);
        }
    }

    private SerializationProviders() {}

    // ============ 获取当前 provider ============

    public static JsonProvider json() {
        return jsonProvider;
    }

    public static YamlProvider yaml() {
        return yamlProvider;
    }

    // ============ 手动切换 ============

    /**
     * 按名称切换 JSON provider。
     * @return 切换后的 provider 名称，未找到返回 null 且不切换
     */
    public static String setJsonProvider(String name) {
        JsonProvider p = jsonProviders.get(name);
        if (p != null) {
            jsonProvider = p;
            return p.name();
        }
        return null;
    }

    public static String setYamlProvider(String name) {
        YamlProvider p = yamlProviders.get(name);
        if (p != null) {
            yamlProvider = p;
            return p.name();
        }
        return null;
    }

    // ============ 查询 ============

    /** 列出所有可用的 JSON provider 名称 */
    public static List<String> listJsonProviders() {
        return new ArrayList<>(jsonProviders.keySet());
    }

    /** 列出所有可用的 YAML provider 名称 */
    public static List<String> listYamlProviders() {
        return new ArrayList<>(yamlProviders.keySet());
    }

    // ============ 手动注册（供非 ServiceLoader 环境使用） ============

    public static void registerJsonProvider(JsonProvider p) {
        jsonProviders.put(p.name(), p);
        if (jsonProvider == null || p.priority() > jsonProvider.priority()) {
            jsonProvider = p;
        }
    }

    public static void registerYamlProvider(YamlProvider p) {
        yamlProviders.put(p.name(), p);
        if (yamlProvider == null || p.priority() > yamlProvider.priority()) {
            yamlProvider = p;
        }
    }

    // ============ 辅助 ============

    @SuppressWarnings("unchecked")
    private static <T> T selectBest(Collection<T> providers) {
        T best = null;
        int bestPriority = Integer.MIN_VALUE;
        for (T p : providers) {
            int pri;
            if (p instanceof JsonProvider) pri = ((JsonProvider) p).priority();
            else if (p instanceof YamlProvider) pri = ((YamlProvider) p).priority();
            else continue;
            if (pri > bestPriority) {
                bestPriority = pri;
                best = p;
            }
        }
        return best;
    }
}
