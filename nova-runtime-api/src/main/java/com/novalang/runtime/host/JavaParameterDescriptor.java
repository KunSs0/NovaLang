package com.novalang.runtime.host;

public final class JavaParameterDescriptor {
    private final String name;
    private final JavaTypeRef type;
    private final boolean vararg;

    public JavaParameterDescriptor(String name, JavaTypeRef type, boolean vararg) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Parameter name must not be empty");
        }
        this.name = name;
        this.type = type != null ? type : JavaTypeRefs.ANY;
        this.vararg = vararg;
    }

    public String getName() {
        return name;
    }

    public JavaTypeRef getType() {
        return type;
    }

    public boolean isVararg() {
        return vararg;
    }
}
