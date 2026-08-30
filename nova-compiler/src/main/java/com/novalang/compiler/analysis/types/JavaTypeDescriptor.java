package com.novalang.compiler.analysis.types;

import com.novalang.runtime.resolution.JavaOverloadResolver;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Cached compile-time description of a Java type.
 * This is compiler-only metadata and never participates in runtime hot paths.
 */
public final class JavaTypeDescriptor {

    public enum Kind {
        CLASS,
        INTERFACE,
        ENUM,
        ANNOTATION
    }

    private final String simpleName;
    private final String qualifiedName;
    private final Kind kind;
    private final String superClassQualifiedName;
    private final List<String> interfaceQualifiedNames;
    private final int typeParameterCount;
    private final boolean functionalInterface;
    private final Method samMethod;

    public static final class JavaExecutableDescriptor {
        private final List<NovaType> paramTypes;
        private final NovaType returnType;
        private final boolean varArgs;

        JavaExecutableDescriptor(List<NovaType> paramTypes, NovaType returnType, boolean varArgs) {
            this.paramTypes = paramTypes;
            this.returnType = returnType;
            this.varArgs = varArgs;
        }

        public List<NovaType> getParamTypes() {
            return paramTypes;
        }

        public NovaType getReturnType() {
            return returnType;
        }

        public boolean isVarArgs() {
            return varArgs;
        }
    }

    JavaTypeDescriptor(Class<?> javaClass, Method samMethod) {
        this.simpleName = javaClass.getSimpleName();
        this.qualifiedName = javaClass.getName();
        this.kind = determineKind(javaClass);
        Class<?> superClass = javaClass.getSuperclass();
        this.superClassQualifiedName = superClass != null ? superClass.getName() : null;
        List<String> interfaceNames = new ArrayList<String>();
        for (Class<?> iface : javaClass.getInterfaces()) {
            interfaceNames.add(iface.getName());
        }
        this.interfaceQualifiedNames = Collections.unmodifiableList(interfaceNames);
        this.typeParameterCount = javaClass.getTypeParameters().length;
        this.functionalInterface = samMethod != null;
        this.samMethod = samMethod;
    }

    private static Kind determineKind(Class<?> javaClass) {
        if (javaClass.isAnnotation()) return Kind.ANNOTATION;
        if (javaClass.isEnum()) return Kind.ENUM;
        if (javaClass.isInterface()) return Kind.INTERFACE;
        return Kind.CLASS;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public Kind getKind() {
        return kind;
    }

    public String getSuperClassQualifiedName() {
        return superClassQualifiedName;
    }

    public List<String> getInterfaceQualifiedNames() {
        return interfaceQualifiedNames;
    }

    public int getTypeParameterCount() {
        return typeParameterCount;
    }

    public boolean isFunctionalInterface() {
        return functionalInterface;
    }

    public Method getSamMethod() {
        return samMethod;
    }

    public boolean isAssignableFrom(JavaTypeDescriptor other) {
        if (other == null) return false;
        try {
            Class<?> target = loadClassWithoutInitialization(qualifiedName);
            Class<?> source = loadClassWithoutInitialization(other.qualifiedName);
            if (target == null || source == null) return false;
            return target.isAssignableFrom(source);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public FunctionNovaType toSamFunctionType(boolean nullable) {
        if (samMethod == null) return null;
        List<NovaType> paramTypes = new ArrayList<NovaType>();
        for (Class<?> paramType : samMethod.getParameterTypes()) {
            paramTypes.add(JavaTypeOracle.get().toNovaType(paramType, false));
        }
        NovaType returnType = JavaTypeOracle.get().toNovaType(samMethod.getReturnType(), false);
        return new FunctionNovaType(null, paramTypes, returnType, nullable);
    }

    public JavaExecutableDescriptor resolveMethod(String methodName, List<NovaType> argTypes, boolean staticOnly) {
        return resolveMethod(methodName, argTypes, staticOnly,
                Collections.<NovaTypeArgument>emptyList());
    }

    public JavaExecutableDescriptor resolveMethod(String methodName, List<NovaType> argTypes,
                                                  boolean staticOnly,
                                                  List<NovaTypeArgument> receiverTypeArguments) {
        Class<?> javaClass = loadJavaClass();
        if (javaClass == null) return null;
        List<Method> candidates = new ArrayList<Method>();
        for (Method method : javaClass.getMethods()) {
            if (methodName.equals(method.getName())) {
                candidates.add(method);
            }
        }
        Method bestMethod = JavaOverloadResolver.selectBestMethod(
                candidates, staticOnly, JavaTypeOracle.get().toJavaArgumentTypes(argTypes));
        return bestMethod != null
                ? toExecutableDescriptor(bestMethod, receiverTypeArguments)
                : null;
    }

    public JavaExecutableDescriptor resolveConstructor(List<NovaType> argTypes) {
        Class<?> javaClass = loadJavaClass();
        if (javaClass == null) return null;
        Constructor<?> bestCtor = JavaOverloadResolver.selectBestConstructor(
                Arrays.asList(javaClass.getConstructors()),
                JavaTypeOracle.get().toJavaArgumentTypes(argTypes));
        return bestCtor != null ? toExecutableDescriptor(bestCtor) : null;
    }

    public List<JavaExecutableDescriptor> methodOverloads(String methodName, boolean staticOnly) {
        return methodOverloads(methodName, staticOnly,
                Collections.<NovaTypeArgument>emptyList());
    }

    public List<JavaExecutableDescriptor> methodOverloads(String methodName, boolean staticOnly,
                                                         List<NovaTypeArgument> receiverTypeArguments) {
        Class<?> javaClass = loadJavaClass();
        if (javaClass == null) return Collections.emptyList();
        List<JavaExecutableDescriptor> overloads = new ArrayList<JavaExecutableDescriptor>();
        for (Method method : javaClass.getMethods()) {
            if (!methodName.equals(method.getName())) continue;
            if (Modifier.isStatic(method.getModifiers()) != staticOnly) continue;
            overloads.add(toExecutableDescriptor(method, receiverTypeArguments));
        }
        return overloads;
    }

    public List<JavaExecutableDescriptor> constructorOverloads() {
        Class<?> javaClass = loadJavaClass();
        if (javaClass == null) return Collections.emptyList();
        List<JavaExecutableDescriptor> overloads = new ArrayList<JavaExecutableDescriptor>();
        for (Constructor<?> ctor : javaClass.getConstructors()) {
            overloads.add(toExecutableDescriptor(ctor));
        }
        return overloads;
    }

    public NovaType resolveProperty(String memberName, boolean staticOnly) {
        return resolveProperty(memberName, staticOnly,
                Collections.<NovaTypeArgument>emptyList());
    }

    public NovaType resolveProperty(String memberName, boolean staticOnly,
                                    List<NovaTypeArgument> receiverTypeArguments) {
        Class<?> javaClass = loadJavaClass();
        if (javaClass == null || memberName == null || memberName.isEmpty()) {
            return null;
        }
        Map<TypeVariable<?>, NovaType> typeBindings = receiverTypeBindings(
                javaClass, receiverTypeArguments);
        try {
            Field field = javaClass.getField(memberName);
            if (Modifier.isStatic(field.getModifiers()) == staticOnly) {
                return toNovaType(field.getGenericType(), typeBindings);
            }
        } catch (NoSuchFieldException ignored) {
        }

        String capitalized = Character.toUpperCase(memberName.charAt(0)) + memberName.substring(1);
        String[] getterNames = { "get" + capitalized, "is" + capitalized, memberName };
        for (String getterName : getterNames) {
            try {
                Method getter = javaClass.getMethod(getterName);
                if (Modifier.isStatic(getter.getModifiers()) != staticOnly) {
                    continue;
                }
                if ("is".concat(capitalized).equals(getterName)
                        && getter.getReturnType() != Boolean.TYPE
                        && getter.getReturnType() != Boolean.class) {
                    continue;
                }
                return toNovaType(getter.getGenericReturnType(), typeBindings);
            } catch (NoSuchMethodException ignored) {
            }
        }
        return null;
    }

    /**
     * 判断 Java 属性是否存在可写入口。公开且非 final 的字段，或单参数 JavaBean
     * {@code setXxx(...)} 方法，都会被视为可写属性。
     */
    public boolean hasWritableProperty(String memberName, boolean staticOnly) {
        Class<?> javaClass = loadJavaClass();
        if (javaClass == null || memberName == null || memberName.isEmpty()) {
            return false;
        }
        try {
            Field field = javaClass.getField(memberName);
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) == staticOnly && !Modifier.isFinal(modifiers)) {
                return true;
            }
        } catch (NoSuchFieldException ignored) {
        }

        String setterName = "set" + Character.toUpperCase(memberName.charAt(0))
                + memberName.substring(1);
        for (Method method : javaClass.getMethods()) {
            if (setterName.equals(method.getName())
                    && method.getParameterCount() == 1
                    && Modifier.isStatic(method.getModifiers()) == staticOnly) {
                return true;
            }
        }
        return false;
    }

    /**
     * 按赋值表达式的静态类型解析 Java 属性 setter。返回值的第一个参数类型就是
     * setter 接受的属性类型；不存在匹配 setter 时返回 {@code null}。
     */
    public JavaExecutableDescriptor resolvePropertySetter(String memberName,
                                                           NovaType valueType,
                                                           boolean staticOnly) {
        return resolvePropertySetter(memberName, valueType, staticOnly,
                Collections.<NovaTypeArgument>emptyList());
    }

    public JavaExecutableDescriptor resolvePropertySetter(String memberName,
                                                           NovaType valueType,
                                                           boolean staticOnly,
                                                           List<NovaTypeArgument> receiverTypeArguments) {
        Class<?> javaClass = loadJavaClass();
        if (javaClass == null || memberName == null || memberName.isEmpty()) {
            return null;
        }
        Map<TypeVariable<?>, NovaType> typeBindings = receiverTypeBindings(
                javaClass, receiverTypeArguments);
        try {
            Field field = javaClass.getField(memberName);
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) == staticOnly && !Modifier.isFinal(modifiers)) {
                List<NovaType> parameterTypes = Collections.singletonList(
                        toNovaType(field.getGenericType(), typeBindings));
                return new JavaExecutableDescriptor(parameterTypes, NovaTypes.UNIT, false);
            }
        } catch (NoSuchFieldException ignored) {
        }

        String setterName = "set" + Character.toUpperCase(memberName.charAt(0))
                + memberName.substring(1);
        List<Method> candidates = new ArrayList<Method>();
        for (Method method : javaClass.getMethods()) {
            if (setterName.equals(method.getName()) && method.getParameterCount() == 1) {
                candidates.add(method);
            }
        }
        Class<?>[] argumentTypes = JavaTypeOracle.get().toJavaArgumentTypes(
                Collections.singletonList(valueType));
        Method bestMethod = JavaOverloadResolver.selectBestMethod(
                candidates, staticOnly, argumentTypes);
        return bestMethod != null
                ? toExecutableDescriptor(bestMethod, receiverTypeArguments)
                : null;
    }

    private JavaExecutableDescriptor toExecutableDescriptor(Method method) {
        return toExecutableDescriptor(method, Collections.<NovaTypeArgument>emptyList());
    }

    private JavaExecutableDescriptor toExecutableDescriptor(
            Method method, List<NovaTypeArgument> receiverTypeArguments) {
        Class<?> javaClass = loadJavaClass();
        Map<TypeVariable<?>, NovaType> typeBindings = receiverTypeBindings(
                javaClass, receiverTypeArguments);
        List<NovaType> paramTypes = new ArrayList<NovaType>();
        for (Type paramType : method.getGenericParameterTypes()) {
            paramTypes.add(toNovaType(paramType, typeBindings));
        }
        NovaType returnType = toNovaType(method.getGenericReturnType(), typeBindings);
        return new JavaExecutableDescriptor(paramTypes, returnType, method.isVarArgs());
    }

    private Map<TypeVariable<?>, NovaType> receiverTypeBindings(
            Class<?> javaClass, List<NovaTypeArgument> receiverTypeArguments) {
        Map<TypeVariable<?>, NovaType> bindings =
                new LinkedHashMap<TypeVariable<?>, NovaType>();
        if (javaClass == null || receiverTypeArguments == null) {
            return bindings;
        }
        TypeVariable<?>[] typeParameters = javaClass.getTypeParameters();
        int count = Math.min(typeParameters.length, receiverTypeArguments.size());
        for (int i = 0; i < count; i++) {
            NovaTypeArgument argument = receiverTypeArguments.get(i);
            NovaType argumentType = argument != null ? argument.getType() : null;
            if (argumentType != null) {
                bindings.put(typeParameters[i], argumentType);
            }
        }
        return bindings;
    }

    private NovaType toNovaType(Type type, Map<TypeVariable<?>, NovaType> typeBindings) {
        if (type instanceof Class<?>) {
            return JavaTypeOracle.get().toNovaType((Class<?>) type, false);
        }
        if (type instanceof TypeVariable<?>) {
            NovaType boundType = typeBindings.get(type);
            return boundType != null ? boundType : NovaTypes.ANY;
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();
            if (!(rawType instanceof Class<?>)) {
                return NovaTypes.ANY;
            }
            List<NovaTypeArgument> arguments = new ArrayList<NovaTypeArgument>();
            for (Type argument : parameterizedType.getActualTypeArguments()) {
                arguments.add(NovaTypeArgument.invariant(
                        toNovaType(argument, typeBindings)));
            }
            NovaType rawNovaType = JavaTypeOracle.get().toNovaType(
                    (Class<?>) rawType, false);
            if (rawNovaType instanceof JavaClassNovaType) {
                JavaClassNovaType javaType = (JavaClassNovaType) rawNovaType;
                return new JavaClassNovaType(javaType.getDescriptor(), arguments, false);
            }
            if (rawNovaType instanceof ClassNovaType) {
                return new ClassNovaType(rawNovaType.getTypeName(), arguments, false);
            }
            return rawNovaType;
        }
        if (type instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) type;
            Type[] upperBounds = wildcardType.getUpperBounds();
            if (upperBounds.length > 0) {
                return toNovaType(upperBounds[0], typeBindings);
            }
            return NovaTypes.ANY;
        }
        if (type instanceof GenericArrayType) {
            GenericArrayType arrayType = (GenericArrayType) type;
            NovaType elementType = toNovaType(
                    arrayType.getGenericComponentType(), typeBindings);
            return new ClassNovaType("Array",
                    Collections.singletonList(NovaTypeArgument.invariant(elementType)), false);
        }
        return NovaTypes.ANY;
    }

    private JavaExecutableDescriptor toExecutableDescriptor(Constructor<?> constructor) {
        List<NovaType> paramTypes = new ArrayList<NovaType>();
        for (Class<?> paramType : constructor.getParameterTypes()) {
            paramTypes.add(JavaTypeOracle.get().toNovaType(paramType, false));
        }
        NovaType returnType = new JavaClassNovaType(this, false);
        return new JavaExecutableDescriptor(paramTypes, returnType, constructor.isVarArgs());
    }

    private Class<?> loadJavaClass() {
        try {
            ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
            if (contextLoader != null) {
                return Class.forName(qualifiedName, false, contextLoader);
            }
            return Class.forName(qualifiedName, false, JavaTypeDescriptor.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static Class<?> loadClassWithoutInitialization(String name) throws ClassNotFoundException {
        ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        if (contextLoader != null) {
            return Class.forName(name, false, contextLoader);
        }
        return Class.forName(name, false, JavaTypeDescriptor.class.getClassLoader());
    }

    static Method findSamMethod(Class<?> javaClass) {
        if (!javaClass.isInterface()) return null;
        Method candidate = null;
        for (Method method : javaClass.getMethods()) {
            if (Modifier.isStatic(method.getModifiers())) continue;
            if (method.isDefault()) continue;
            if (method.getDeclaringClass() == Object.class) continue;
            if (!Modifier.isAbstract(method.getModifiers())) continue;
            if (candidate != null) return null;
            candidate = method;
        }
        return candidate;
    }
}
