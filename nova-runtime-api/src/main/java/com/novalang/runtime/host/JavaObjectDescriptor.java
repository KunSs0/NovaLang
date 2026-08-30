package com.novalang.runtime.host;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public final class JavaObjectDescriptor extends JavaSymbolDescriptor {
    private final JavaTypeRef type;
    private final Object value;
    private final Supplier<?> supplier;
    private final List<JavaSymbolDescriptor> members;

    public JavaObjectDescriptor(String name,
                                JavaTypeRef type,
                                String documentation,
                                String deprecatedMessage,
                                List<String> examples,
                                Object value,
                                Supplier<?> supplier,
                                List<JavaSymbolDescriptor> members) {
        super(name, JavaSymbolKind.OBJECT, documentation, deprecatedMessage, examples);
        this.type = type != null ? type : JavaTypeRefs.ANY;
        this.value = value;
        this.supplier = supplier;
        this.members = Collections.unmodifiableList(new ArrayList<JavaSymbolDescriptor>(members != null ? members : Collections.<JavaSymbolDescriptor>emptyList()));
    }

    public JavaTypeRef getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public Supplier<?> getSupplier() {
        return supplier;
    }

    public List<JavaSymbolDescriptor> getMembers() {
        return members;
    }
}
