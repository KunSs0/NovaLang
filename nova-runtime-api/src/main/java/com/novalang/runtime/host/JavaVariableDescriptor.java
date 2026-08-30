package com.novalang.runtime.host;

import java.util.List;
import java.util.function.Supplier;

public final class JavaVariableDescriptor extends JavaSymbolDescriptor {
    private final JavaTypeRef type;
    private final boolean mutable;
    private final Object value;
    private final Supplier<?> supplier;

    public JavaVariableDescriptor(String name,
                                  JavaTypeRef type,
                                  boolean mutable,
                                  String documentation,
                                  String deprecatedMessage,
                                  List<String> examples,
                                  Object value,
                                  Supplier<?> supplier) {
        super(name, JavaSymbolKind.VARIABLE, documentation, deprecatedMessage, examples);
        this.type = type != null ? type : JavaTypeRefs.ANY;
        this.mutable = mutable;
        this.value = value;
        this.supplier = supplier;
    }

    public JavaTypeRef getType() {
        return type;
    }

    public boolean isMutable() {
        return mutable;
    }

    public Object getValue() {
        return value;
    }

    public Supplier<?> getSupplier() {
        return supplier;
    }
}
