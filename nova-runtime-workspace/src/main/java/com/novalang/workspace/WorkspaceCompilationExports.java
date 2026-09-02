package com.novalang.workspace;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/** 一个已编译模块组可供后续组链接的公开符号。 */
final class WorkspaceCompilationExports {

    private final Set<String> typeNames;
    private final Set<String> objectNames;
    private final Set<String> staticMemberNames;
    private final Set<String> javaImportDeclarations;

    WorkspaceCompilationExports(Set<String> typeNames,
                                Set<String> objectNames,
                                Set<String> staticMemberNames,
                                Set<String> javaImportDeclarations) {
        this.typeNames = Collections.unmodifiableSet(
                new LinkedHashSet<String>(typeNames));
        this.objectNames = Collections.unmodifiableSet(
                new LinkedHashSet<String>(objectNames));
        this.staticMemberNames = Collections.unmodifiableSet(
                new LinkedHashSet<String>(staticMemberNames));
        this.javaImportDeclarations = Collections.unmodifiableSet(
                new LinkedHashSet<String>(javaImportDeclarations));
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

    Set<String> getJavaImportDeclarations() {
        return javaImportDeclarations;
    }

    static WorkspaceCompilationExports empty() {
        return new WorkspaceCompilationExports(
                Collections.<String>emptySet(), Collections.<String>emptySet(),
                Collections.<String>emptySet(),
                Collections.<String>emptySet());
    }
}
