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
    private final Set<String> extensionDeclarations = new LinkedHashSet<String>();
    private final Set<String> forwardedExtensionNames = new LinkedHashSet<String>();

    /**
     * 返回仅用于本组链接的扩展转发函数名，不能再次作为普通静态成员导出。
     * @return 转发函数名称集合。
     */
    Set<String> getForwardedExtensionNames() {
        return forwardedExtensionNames;
    }

    Set<String> getExtensionDeclarations() {
        return extensionDeclarations;
    }

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
