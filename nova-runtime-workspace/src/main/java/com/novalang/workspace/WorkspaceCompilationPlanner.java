package com.novalang.workspace;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 按模块的入口消费者集合生成依赖优先的编译组。
 */
final class WorkspaceCompilationPlanner {

    WorkspaceCompilationPlan build(WorkspaceModuleGraph graph, String workspaceIdentity) {
        Map<String, Set<String>> reachableByEntry = collectReachableByEntry(graph);
        Map<String, Set<String>> consumersByModule = collectConsumersByModule(
                graph, reachableByEntry);

        Map<String, MutableGroup> groupsByConsumers = new LinkedHashMap<String, MutableGroup>();
        Map<String, MutableGroup> groupByModule = new LinkedHashMap<String, MutableGroup>();
        for (String moduleId : graph.getTopologicalOrder()) {
            Set<String> consumers = consumersByModule.get(moduleId);
            if (consumers == null || consumers.isEmpty()) {
                continue;
            }
            String key = consumerKey(consumers);
            MutableGroup group = groupsByConsumers.get(key);
            if (group == null) {
                group = new MutableGroup(consumers);
                groupsByConsumers.put(key, group);
            }
            group.moduleIds.add(moduleId);
            groupByModule.put(moduleId, group);
        }

        for (MutableGroup group : groupsByConsumers.values()) {
            for (String moduleId : group.moduleIds) {
                WorkspaceModule module = graph.requireModule(moduleId);
                for (String dependencyId : module.getDependencies()) {
                    MutableGroup dependency = groupByModule.get(dependencyId);
                    if (dependency != null && dependency != group) {
                        group.directDependencies.add(dependency);
                    }
                }
            }
        }

        List<MutableGroup> orderedMutableGroups = topologicalGroups(groupsByConsumers.values());
        String workspacePackage = "com.novalang.workspace.generated.w"
                + shortHash(workspaceIdentity);
        Map<MutableGroup, WorkspaceCompilationPlan.Group> immutableGroups =
                new IdentityHashMap<MutableGroup, WorkspaceCompilationPlan.Group>();
        List<WorkspaceCompilationPlan.Group> groups =
                new ArrayList<WorkspaceCompilationPlan.Group>();
        for (int index = 0; index < orderedMutableGroups.size(); index++) {
            MutableGroup mutable = orderedMutableGroups.get(index);
            List<WorkspaceCompilationPlan.Group> dependencies =
                    new ArrayList<WorkspaceCompilationPlan.Group>();
            for (MutableGroup dependency : transitiveDependencies(mutable, orderedMutableGroups)) {
                dependencies.add(immutableGroups.get(dependency));
            }
            String id = "group-" + index;
            WorkspaceCompilationPlan.Group group = new WorkspaceCompilationPlan.Group(
                    id, workspacePackage + ".g" + index, mutable.moduleIds,
                    dependencies, mutable.consumers);
            immutableGroups.put(mutable, group);
            groups.add(group);
        }

        Map<String, WorkspaceCompilationPlan.Group> entryRootGroups =
                new LinkedHashMap<String, WorkspaceCompilationPlan.Group>();
        Map<String, List<WorkspaceCompilationPlan.Group>> entryReachableGroups =
                new LinkedHashMap<String, List<WorkspaceCompilationPlan.Group>>();
        for (Map.Entry<String, String> entry : graph.getEntries().entrySet()) {
            MutableGroup root = groupByModule.get(entry.getValue());
            entryRootGroups.put(entry.getKey(), immutableGroups.get(root));
            List<WorkspaceCompilationPlan.Group> reachableGroups =
                    new ArrayList<WorkspaceCompilationPlan.Group>();
            for (WorkspaceCompilationPlan.Group group : groups) {
                if (group.getConsumers().contains(entry.getKey())) {
                    reachableGroups.add(group);
                }
            }
            entryReachableGroups.put(entry.getKey(), reachableGroups);
        }
        return new WorkspaceCompilationPlan(groups, entryRootGroups, entryReachableGroups);
    }

    private Map<String, Set<String>> collectReachableByEntry(WorkspaceModuleGraph graph) {
        Map<String, Set<String>> result = new LinkedHashMap<String, Set<String>>();
        for (Map.Entry<String, String> entry : graph.getEntries().entrySet()) {
            Set<String> reachable = new LinkedHashSet<String>();
            collectReachable(graph, entry.getValue(), reachable);
            result.put(entry.getKey(), reachable);
        }
        return result;
    }

    private void collectReachable(WorkspaceModuleGraph graph,
                                  String moduleId,
                                  Set<String> reachable) {
        if (!reachable.add(moduleId)) {
            return;
        }
        WorkspaceModule module = graph.requireModule(moduleId);
        for (String dependency : module.getDependencies()) {
            collectReachable(graph, dependency, reachable);
        }
    }

    private Map<String, Set<String>> collectConsumersByModule(
            WorkspaceModuleGraph graph,
            Map<String, Set<String>> reachableByEntry) {
        Map<String, Set<String>> result = new LinkedHashMap<String, Set<String>>();
        for (String moduleId : graph.getTopologicalOrder()) {
            result.put(moduleId, new LinkedHashSet<String>());
        }
        for (Map.Entry<String, Set<String>> entry : reachableByEntry.entrySet()) {
            for (String moduleId : entry.getValue()) {
                result.get(moduleId).add(entry.getKey());
            }
        }
        return result;
    }

    private String consumerKey(Set<String> consumers) {
        StringBuilder key = new StringBuilder();
        for (String consumer : consumers) {
            if (key.length() > 0) {
                key.append('\u0000');
            }
            key.append(consumer);
        }
        return key.toString();
    }

    private List<MutableGroup> topologicalGroups(Iterable<MutableGroup> groups) {
        List<MutableGroup> result = new ArrayList<MutableGroup>();
        Set<MutableGroup> visited = Collections.newSetFromMap(
                new IdentityHashMap<MutableGroup, Boolean>());
        Set<MutableGroup> visiting = Collections.newSetFromMap(
                new IdentityHashMap<MutableGroup, Boolean>());
        for (MutableGroup group : groups) {
            visitGroup(group, visited, visiting, result);
        }
        return result;
    }

    private void visitGroup(MutableGroup group,
                            Set<MutableGroup> visited,
                            Set<MutableGroup> visiting,
                            List<MutableGroup> result) {
        if (visited.contains(group)) {
            return;
        }
        if (!visiting.add(group)) {
            throw new WorkspaceException("Workspace compilation group dependency cycle");
        }
        for (MutableGroup dependency : group.directDependencies) {
            visitGroup(dependency, visited, visiting, result);
        }
        visiting.remove(group);
        visited.add(group);
        result.add(group);
    }

    private List<MutableGroup> transitiveDependencies(MutableGroup group,
                                                       List<MutableGroup> orderedGroups) {
        Set<MutableGroup> reachable = Collections.newSetFromMap(
                new IdentityHashMap<MutableGroup, Boolean>());
        collectGroupDependencies(group, reachable);
        List<MutableGroup> result = new ArrayList<MutableGroup>();
        for (MutableGroup candidate : orderedGroups) {
            if (reachable.contains(candidate)) {
                result.add(candidate);
            }
        }
        return result;
    }

    private void collectGroupDependencies(MutableGroup group, Set<MutableGroup> result) {
        for (MutableGroup dependency : group.directDependencies) {
            if (result.add(dependency)) {
                collectGroupDependencies(dependency, result);
            }
        }
    }

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
            throw new WorkspaceException("SHA-256 is not available", exception);
        }
    }

    private static final class MutableGroup {

        private final List<String> moduleIds = new ArrayList<String>();
        private final Set<MutableGroup> directDependencies =
                Collections.newSetFromMap(new IdentityHashMap<MutableGroup, Boolean>());
        private final Set<String> consumers;

        private MutableGroup(Set<String> consumers) {
            this.consumers = new LinkedHashSet<String>(consumers);
        }
    }
}
