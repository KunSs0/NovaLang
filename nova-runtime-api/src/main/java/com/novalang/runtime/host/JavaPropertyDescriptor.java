package com.novalang.runtime.host;

import java.util.List;

public final class JavaPropertyDescriptor extends JavaSymbolDescriptor {
    private final JavaTypeRef type;
    private final boolean mutable;

    public JavaPropertyDescriptor(String name,
                                  JavaTypeRef type,
                                  boolean mutable,
                                  String documentation,
                                  String deprecatedMessage,
                                  List<String> examples) {
        super(name, JavaSymbolKind.PROPERTY, documentation, deprecatedMessage, examples);
        this.type = type != null ? type : JavaTypeRefs.ANY;
        this.mutable = mutable;
    }

    public JavaTypeRef getType() {
        return type;
    }

    public boolean isMutable() {
        return mutable;
    }
}
