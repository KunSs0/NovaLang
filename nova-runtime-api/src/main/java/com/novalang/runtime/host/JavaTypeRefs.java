package com.novalang.runtime.host;

public final class JavaTypeRefs {
    public static final JavaTypeRef OBJECT = JavaTypeRef.javaType(Object.class);
    public static final JavaTypeRef ANY = OBJECT;
    public static final JavaTypeRef UNIT = JavaTypeRef.javaType(Void.TYPE);
    public static final JavaTypeRef STRING = JavaTypeRef.javaType(String.class);
    public static final JavaTypeRef INT = JavaTypeRef.javaType(Integer.TYPE);
    public static final JavaTypeRef LONG = JavaTypeRef.javaType(Long.TYPE);
    public static final JavaTypeRef DOUBLE = JavaTypeRef.javaType(Double.TYPE);
    public static final JavaTypeRef FLOAT = JavaTypeRef.javaType(Float.TYPE);
    public static final JavaTypeRef BOOLEAN = JavaTypeRef.javaType(Boolean.TYPE);
    public static final JavaTypeRef DYNAMIC = JavaTypeRef.of("dynamic");

    private JavaTypeRefs() {}
}
