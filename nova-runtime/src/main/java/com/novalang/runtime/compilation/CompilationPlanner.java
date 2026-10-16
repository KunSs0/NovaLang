package com.novalang.runtime.compilation;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 按单入口依赖图的拓扑顺序生成编译计划，每个模块只进入编译管线一次。 */
final class CompilationPlanner {
    /**
     * 为每个可达模块生成独立编译组，保留模块状态和初始化顺序。
     * @param graph 唯一入口的完整依赖图
     * @param compilationIdentity 用于生成类包名的稳定编译身份
     * @return 依赖优先的不可变编译计划
     */
    CompilationPlan build(ModuleGraph graph, String compilationIdentity) {
        List<CompilationPlan.Group> groups = new ArrayList<>();
        Map<String, CompilationPlan.Group> byModule = new LinkedHashMap<>();
        String packageName = "com.novalang.runtime.generated.p" + shortHash(compilationIdentity);
        for (String moduleId : graph.getTopologicalOrder()) {
            Set<CompilationPlan.Group> reachable = new LinkedHashSet<>();
            for (String dependencyId : graph.requireModule(moduleId).getDependencies()) {
                CompilationPlan.Group dependency = byModule.get(dependencyId);
                reachable.addAll(dependency.getDependencies());
                reachable.add(dependency);
            }
            // 按全图拓扑顺序排列传递依赖，保证链接及初始化顺序稳定。
            List<CompilationPlan.Group> dependencies = new ArrayList<>();
            for (CompilationPlan.Group candidate : groups) {
                if (reachable.contains(candidate)) {
                    dependencies.add(candidate);
                }
            }
            List<String> modules = new ArrayList<>();
            modules.add(moduleId);
            String id = "module-" + shortHash(moduleId);
            CompilationPlan.Group group = new CompilationPlan.Group(
                    id, packageName + ".m" + shortHash(moduleId), modules, dependencies);
            groups.add(group);
            byModule.put(moduleId, group);
        }
        return new CompilationPlan(groups);
    }

    /**
     * 将稳定身份转换为合法且紧凑的生成类包名片段。
     * @param value 模块或程序身份
     * @return SHA-256 前八字节的十六进制文本
     */
    private String shortHash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            for (int index = 0; index < 8; index++) {
                result.append(String.format("%02x", bytes[index] & 0xff));
            }
            return result.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new CompilationException("SHA-256 is not available", exception);
        }
    }
}
