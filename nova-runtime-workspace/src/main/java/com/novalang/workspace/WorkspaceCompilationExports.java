package com.novalang.workspace;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/** 一个已编译模块组可供后续组链接的公开符号。 */
final class WorkspaceCompilationExports {

    private final Set<String> typeNames;
    private final Set<String> objectNames;
    private final Set<String> staticMemberNames;

    WorkspaceCompilationExports(Set<String> typeNames,
                                Set<String> objectNames,
                                Set<String> staticMemberNames) {
        this.typeNames = Collections.unmodifiableSet(
                new LinkedHashSet<String>(typeNames));
        this.objectNames = Collections.unmodifiableSet(
                new LinkedHashSet<String>(objectNames));
        this.staticMemberNames = Collections.unmodifiableSet(
                new LinkedHashSet<String>(staticMemberNames));
    }

    Set<String> getTypeNames() {
        return typeNames;
    }

    Set<String> getStaticMemberNames() {
        return staticMemberNames;
    }

    Set<String> getObjectNames() {
        return objectNames;
    }

    static WorkspaceCompilationExports empty() {
        return new WorkspaceCompilationExports(
                Collections.<String>emptySet(), Collections.<String>emptySet(),
                Collections.<String>emptySet());
    }
}
