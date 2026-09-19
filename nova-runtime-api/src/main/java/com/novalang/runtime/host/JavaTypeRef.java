package com.novalang.runtime.host;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class JavaTypeRef {
    private final String displayName;
    private final Class<?> javaClass;
    private final boolean nullable;
    private final boolean dynamic;
    private final List<JavaTypeRef> typeArguments;

    private JavaTypeRef(String displayName,
                        Class<?> javaClass,
                        boolean nullable,
                        boolean dynamic,
                        List<JavaTypeRef> typeArguments) {
        this.displayName = displayName;
        this.javaClass = javaClass;
        this.nullable = nullable;
        this.dynamic = dynamic;
        this.typeArguments = Collections.unmodifiableList(new ArrayList<JavaTypeRef>(typeArguments));
    }

    public static JavaTypeRef of(String displayName) {
        if (displayName == null || displayName.trim().isEmpty()) {
            throw new IllegalArgumentException("displayName must not be empty");
        }
        String normalized = displayName.trim();
        boolean dynamic = "dynamic".equalsIgnoreCase(normalized);
        return new JavaTypeRef(normalized, null, false, dynamic, Collections.<JavaTypeRef>emptyList());
    }

    public static JavaTypeRef javaType(Class<?> javaClass) {
        if (javaClass == null) {
            throw new IllegalArgumentException("javaClass must not be null");
        }
        return new JavaTypeRef(displayNameFor(javaClass), javaClass, false, false,
                Collections.<JavaTypeRef>emptyList());
    }

    public static JavaTypeRef listOf(JavaTypeRef elementType) {
        return parameterized("List", java.util.List.class, elementType);
    }

    public static JavaTypeRef setOf(JavaTypeRef elementType) {
        return parameterized("Set", java.util.Set.class, elementType);
    }

    public static JavaTypeRef mapOf(JavaTypeRef keyType, JavaTypeRef valueType) {
        if (keyType == null || valueType == null) {
            throw new IllegalArgumentException("Map key and value types must not be null");
        }
        List<JavaTypeRef> arguments = new ArrayList<JavaTypeRef>();
        arguments.add(keyType);
        arguments.add(valueType);
        return new JavaTypeRef("Map", java.util.Map.class, false, false, arguments);
    }

    public static JavaTypeRef parameterized(String displayName, Class<?> javaClass, JavaTypeRef... arguments) {
        if (displayName == null || displayName.trim().isEmpty()) {
            throw new IllegalArgumentException("displayName must not be empty");
        }
        if (javaClass == null) {
            throw new IllegalArgumentException("javaClass must not be null");
        }
        List<JavaTypeRef> typeArguments = new ArrayList<JavaTypeRef>();
        if (arguments != null) {
            for (JavaTypeRef argument : arguments) {
                if (argument == null) {
                    throw new IllegalArgumentException("type argument must not be null");
                }
                typeArguments.add(argument);
            }
        }
        return new JavaTypeRef(displayName.trim(), javaClass, false, false, typeArguments);
    }

    public String displayName() {
        StringBuilder builder = new StringBuilder(displayName);
        if (!typeArguments.isEmpty()) {
            builder.append('<');
            for (int i = 0; i < typeArguments.size(); i++) {
                if (i > 0) {
                    builder.append(", ");
                }
                builder.append(typeArguments.get(i).displayName());
            }
            builder.append('>');
        }
        if (nullable) {
            builder.append('?');
        }
        return builder.toString();
    }

    public String baseName() {
        return displayName;
    }

    public Class<?> javaClass() {
        return javaClass;
    }

    public boolean hasJavaClass() {
        return javaClass != null;
    }

    public boolean isNullable() {
        return nullable;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public List<JavaTypeRef> typeArguments() {
        return typeArguments;
    }

    public JavaTypeRef nullable() {
        if (nullable) {
            return this;
        }
        return new JavaTypeRef(displayName, javaClass, true, dynamic, typeArguments);
    }

    public JavaTypeRef nonNull() {
        if (!nullable) {
            return this;
        }
        return new JavaTypeRef(displayName, javaClass, false, dynamic, typeArguments);
    }

    private static String displayNameFor(Class<?> javaClass) {
        if (javaClass == Void.TYPE || javaClass == Void.class) {
            return "Unit";
        }
        if (javaClass == Integer.TYPE || javaClass == Integer.class) {
            return "Int";
        }
        if (javaClass == Long.TYPE || javaClass == Long.class) {
            return "Long";
        }
        if (javaClass == Double.TYPE || javaClass == Double.class) {
            return "Double";
        }
        if (javaClass == Float.TYPE || javaClass == Float.class) {
            return "Float";
        }
        if (javaClass == Boolean.TYPE || javaClass == Boolean.class) {
            return "Boolean";
        }
        if (javaClass == Character.TYPE || javaClass == Character.class) {
            return "Char";
        }
        if (javaClass == String.class) {
            return "String";
        }
        if (javaClass == Object.class) {
            return "Any";
        }
        return javaClass.getSimpleName();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof JavaTypeRef)) return false;
        JavaTypeRef that = (JavaTypeRef) other;
        return nullable == that.nullable
                && dynamic == that.dynamic
                && Objects.equals(displayName, that.displayName)
                && Objects.equals(javaClass, that.javaClass)
                && Objects.equals(typeArguments, that.typeArguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(displayName, javaClass, nullable, dynamic, typeArguments);
    }

    @Override
    public String toString() {
        return displayName();
    }
}
