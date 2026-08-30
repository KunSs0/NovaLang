package com.novalang.runtime.host;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class JavaNamespaceDescriptor {
    private final String name;
    private final List<String> extendsNamespaces;
    private final List<JavaSymbolDescriptor> globals;
    private final List<JavaExtensionDescriptor> extensions;

    public JavaNamespaceDescriptor(String name, List<String> extendsNamespaces, List<JavaSymbolDescriptor> globals) {
        this(name, extendsNamespaces, globals, Collections.<JavaExtensionDescriptor>emptyList());
    }

    public JavaNamespaceDescriptor(String name,
                                   List<String> extendsNamespaces,
                                   List<JavaSymbolDescriptor> globals,
                                   List<JavaExtensionDescriptor> extensions) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Namespace name must not be empty");
        }
        this.name = name;
        this.extendsNamespaces = Collections.unmodifiableList(new ArrayList<String>(extendsNamespaces != null ? extendsNamespaces : Collections.<String>emptyList()));
        this.globals = Collections.unmodifiableList(new ArrayList<JavaSymbolDescriptor>(globals != null ? globals : Collections.<JavaSymbolDescriptor>emptyList()));
        this.extensions = Collections.unmodifiableList(new ArrayList<JavaExtensionDescriptor>(extensions != null ? extensions : Collections.<JavaExtensionDescriptor>emptyList()));
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

    public List<JavaExtensionDescriptor> getExtensions() {
        return extensions;
    }
}
