package com.novalang.runtime.host;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class JavaNamespaceDescriptor {
    private final String name;
    private final List<String> extendsNamespaces;
    private final List<JavaSymbolDescriptor> globals;

    public JavaNamespaceDescriptor(String name, List<String> extendsNamespaces, List<JavaSymbolDescriptor> globals) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Namespace name must not be empty");
        }
        this.name = name;
        this.extendsNamespaces = Collections.unmodifiableList(new ArrayList<String>(extendsNamespaces != null ? extendsNamespaces : Collections.<String>emptyList()));
        this.globals = Collections.unmodifiableList(new ArrayList<JavaSymbolDescriptor>(globals != null ? globals : Collections.<JavaSymbolDescriptor>emptyList()));
    }

    public String getName() {
        return name;
    }

    public List<String> getExtendsNamespaces() {
        return extendsNamespaces;
    }

    public List<JavaSymbolDescriptor> getGlobals() {
        return globals;
    }
}
