package com.novalang.runtime.host;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class JavaSymbolDescriptor {
    private final String name;
    private final JavaSymbolKind kind;
    private final String documentation;
    private final String deprecatedMessage;
    private final List<String> examples;

    protected JavaSymbolDescriptor(String name,
                                   JavaSymbolKind kind,
                                   String documentation,
                                   String deprecatedMessage,
                                   List<String> examples) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Symbol name must not be empty");
        }
        this.name = name;
        this.kind = kind;
        this.documentation = documentation;
        this.deprecatedMessage = deprecatedMessage;
        this.examples = Collections.unmodifiableList(new ArrayList<String>(examples != null ? examples : Collections.<String>emptyList()));
    }

    public String getName() {
        return name;
    }

    public JavaSymbolKind getKind() {
        return kind;
    }

    public String getDocumentation() {
        return documentation;
    }

    public String getDeprecatedMessage() {
        return deprecatedMessage;
    }

    public List<String> getExamples() {
        return examples;
    }
}
