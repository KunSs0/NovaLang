package com.novalang.compiler.analysis.types;

import java.util.Collections;

/**
 * The value produced when an explicitly imported Java type is used as an
 * expression argument.  Its runtime value is the JVM class literal, while
 * the descriptor identifies the represented class for diagnostics and future
 * generic checks.
 */
public final class JavaClassLiteralNovaType extends ClassNovaType {

    private final JavaTypeDescriptor representedType;

    public JavaClassLiteralNovaType(JavaTypeDescriptor representedType, boolean nullable) {
        super("Class", Collections.<NovaTypeArgument>emptyList(), nullable);
        this.representedType = representedType;
    }

    public JavaTypeDescriptor getRepresentedType() {
        return representedType;
    }

    @Override
    public NovaType withNullable(boolean nullable) {
        if (isNullable() == nullable) {
            return this;
        }
        return new JavaClassLiteralNovaType(representedType, nullable);
    }
}
