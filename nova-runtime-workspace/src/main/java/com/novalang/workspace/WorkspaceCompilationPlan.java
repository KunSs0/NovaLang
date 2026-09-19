package com.novalang.workspace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 一个 Generation 的模块级编译计划。
 */
final class WorkspaceCompilationPlan {

    private final List<Group> groups;
    private final Map<String, Group> entryRootGroups;
    private final Map<String, List<Group>> entryReachableGroups;

    WorkspaceCompilationPlan(List<Group> groups,
                             Map<String, Group> entryRootGroups,
                             Map<String, List<Group>> entryReachableGroups) {
        this.groups = Collections.unmodifiableList(new ArrayList<Group>(groups));
        this.entryRootGroups = Collections.unmodifiableMap(
                new LinkedHashMap<String, Group>(entryRootGroups));
        Map<String, List<Group>> copied = new LinkedHashMap<String, List<Group>>();
        for (Map.Entry<String, List<Group>> entry : entryReachableGroups.entrySet()) {
            copied.put(entry.getKey(), Collections.unmodifiableList(
                    new ArrayList<Group>(entry.getValue())));
        }
        this.entryReachableGroups = Collections.unmodifiableMap(copied);
    }

    List<Group> getGroups() {
        return groups;
    }

    Group getEntryRootGroup(String entryName) {
        Group group = entryRootGroups.get(entryName);
        if (group == null) {
            throw new WorkspaceException("Workspace entry has no compilation group: " + entryName);
        }
        return group;
    }

    List<Group> getEntryReachableGroups(String entryName) {
        List<Group> result = entryReachableGroups.get(entryName);
        if (result == null) {
            throw new WorkspaceException("Workspace entry has no reachable compilation groups: " + entryName);
        }
        return result;
    }

    /** 同一消费者集合中的模块会一起编译一次。 */
    static final class Group {

        private final String id;
        private final String packageName;
        private final List<String> moduleIds;
        private final List<Group> dependencies;
        private final Set<String> consumers;

        Group(String id,
              String packageName,
              List<String> moduleIds,
              List<Group> dependencies,
              Set<String> consumers) {
            this.id = id;
            this.packageName = packageName;
            this.moduleIds = Collections.unmodifiableList(new ArrayList<String>(moduleIds));
            this.dependencies = Collections.unmodifiableList(new ArrayList<Group>(dependencies));
            this.consumers = Collections.unmodifiableSet(new LinkedHashSet<String>(consumers));
        }

        String getId() {
            return id;
        }

        String getPackageName() {
            return packageName;
        }

        List<String> getModuleIds() {
            return moduleIds;
        }

        List<Group> getDependencies() {
            return dependencies;
        }

        Set<String> getConsumers() {
            return consumers;
        }
    }
}
