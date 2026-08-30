package com.novalang.runtime.host;

import com.novalang.runtime.AbstractNovaValue;
import com.novalang.runtime.ExtensionMethod;
import com.novalang.runtime.Nova;
import com.novalang.runtime.NovaErrors;
import com.novalang.runtime.NovaNull;
import com.novalang.runtime.NovaValue;
import com.novalang.runtime.interpreter.NovaNativeFunction;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class JavaTypesInstaller {
    private JavaTypesInstaller() {}

    public static void install(Nova nova, JavaTypes javaTypes) {
        installNamespace(nova, javaTypes, "default");
    }

    public static void installNamespace(Nova nova, JavaTypes javaTypes, String namespaceName) {
        if (nova == null) {
            throw new IllegalArgumentException("nova must not be null");
        }
        if (javaTypes == null) {
            throw new IllegalArgumentException("javaTypes must not be null");
        }

        JavaNamespaceDescriptor namespace = javaTypes.resolveNamespace(namespaceName);
        nova.setJavaTypes(javaTypes, namespaceName);
        Map<String, List<JavaFunctionDescriptor>> functions = new LinkedHashMap<String, List<JavaFunctionDescriptor>>();
        for (JavaSymbolDescriptor symbol : namespace.getGlobals()) {
            if (symbol instanceof JavaFunctionDescriptor) {
                List<JavaFunctionDescriptor> overloads = functions.get(symbol.getName());
                if (overloads == null) {
                    overloads = new ArrayList<JavaFunctionDescriptor>();
                    functions.put(symbol.getName(), overloads);
                }
                overloads.add((JavaFunctionDescriptor) symbol);
            } else {
                installSymbol(nova, symbol);
            }
        }
        for (List<JavaFunctionDescriptor> overloads : functions.values()) {
            installFunctions(nova, overloads);
        }
        installExtensions(nova, namespace.getExtensions());
    }

    private static void installExtensions(Nova nova, List<JavaExtensionDescriptor> extensions) {
        Map<Class<?>, Map<String, List<JavaFunctionDescriptor>>> grouped =
                new LinkedHashMap<Class<?>, Map<String, List<JavaFunctionDescriptor>>>();
        for (JavaExtensionDescriptor extension : extensions) {
            Class<?> targetType = extension.getTargetType();
            Map<String, List<JavaFunctionDescriptor>> functionsByName = grouped.get(targetType);
            if (functionsByName == null) {
                functionsByName = new LinkedHashMap<String, List<JavaFunctionDescriptor>>();
                grouped.put(targetType, functionsByName);
            }
            JavaFunctionDescriptor function = extension.getFunction();
            List<JavaFunctionDescriptor> overloads = functionsByName.get(function.getName());
            if (overloads == null) {
                overloads = new ArrayList<JavaFunctionDescriptor>();
                functionsByName.put(function.getName(), overloads);
            }
            overloads.add(function);
        }
        for (Map.Entry<Class<?>, Map<String, List<JavaFunctionDescriptor>>> typeEntry : grouped.entrySet()) {
            for (List<JavaFunctionDescriptor> overloads : typeEntry.getValue().values()) {
                installExtensionOverloads(nova, typeEntry.getKey(), overloads);
            }
        }
    }

    private static void installExtensionOverloads(Nova nova,
                                                  Class<?> targetType,
                                                  List<JavaFunctionDescriptor> functions) {
        if (functions == null || functions.isEmpty()) {
            return;
        }
        String functionName = functions.get(0).getName();
        for (JavaFunctionDescriptor function : functions) {
            if (function.getInvoker() == null) {
                throw new IllegalStateException("Java extension has no invoker: " + functionName);
            }
            if (function.isVararg()) {
                throw new IllegalStateException("JavaTypes extensions do not support vararg: " + functionName);
            }
        }

        int arity = functions.size() == 1 ? functions.get(0).getParameters().size() + 1 : -1;
        NovaNativeFunction dispatcher = new NovaNativeFunction(functionName, arity, (ctx, args) -> {
            try {
                Object receiver = args.isEmpty() ? null : args.get(0).toJavaValue();
                Object[] javaArgs = new Object[Math.max(args.size() - 1, 0)];
                for (int i = 1; i < args.size(); i++) {
                    NovaValue arg = args.get(i);
                    javaArgs[i - 1] = arg != null ? arg.toJavaValue() : null;
                }
                JavaFunctionDescriptor function = resolveFunction(functions, javaArgs);
                Object result = invokeExtension(function, receiver, javaArgs);
                return result == null ? NovaNull.UNIT : AbstractNovaValue.fromJava(result);
            } catch (Exception exception) {
                throw NovaErrors.wrap("调用 Java 扩展函数 '" + functionName + "' 失败", exception);
            }
        });

        for (JavaFunctionDescriptor function : functions) {
            Class<?>[] parameterTypes = parameterClasses(function);
            Class<?> returnType = runtimeClass(function.getReturnType());
            ExtensionMethod<Object, Object> compiledMethod = new ExtensionMethod<Object, Object>() {
                @Override
                public Object invoke(Object receiver, Object[] arguments) throws Exception {
                    return invokeExtension(function, receiver, arguments);
                }
            };
            nova.registerTypedExtension(targetType, functionName, dispatcher,
                    parameterTypes, returnType, compiledMethod);
        }
    }

    private static Object invokeExtension(JavaFunctionDescriptor function,
                                          Object receiver,
                                          Object[] arguments) throws Exception {
        Object[] invocationArguments = new Object[arguments.length + 1];
        invocationArguments[0] = receiver;
        System.arraycopy(arguments, 0, invocationArguments, 1, arguments.length);
        return function.getInvoker().invoke(invocationArguments);
    }

    private static Class<?>[] parameterClasses(JavaFunctionDescriptor function) {
        List<JavaParameterDescriptor> parameters = function.getParameters();
        Class<?>[] parameterTypes = new Class<?>[parameters.size()];
        for (int i = 0; i < parameters.size(); i++) {
            parameterTypes[i] = runtimeClass(parameters.get(i).getType());
        }
        return parameterTypes;
    }

    private static Class<?> runtimeClass(JavaTypeRef type) {
        Class<?> javaClass = type != null ? type.javaClass() : null;
        if (javaClass == null) {
            return Object.class;
        }
        return boxedClass(javaClass);
    }

    private static void installSymbol(Nova nova, JavaSymbolDescriptor symbol) {
        if (symbol instanceof JavaVariableDescriptor) {
            installVariable(nova, (JavaVariableDescriptor) symbol);
            return;
        }
        if (symbol instanceof JavaPropertyDescriptor) {
            JavaPropertyDescriptor property = (JavaPropertyDescriptor) symbol;
            throw new IllegalStateException("Top-level property cannot be installed without runtime value: " + property.getName());
        }
        if (symbol instanceof JavaObjectDescriptor) {
            installObject(nova, (JavaObjectDescriptor) symbol);
        }
    }

    private static void installVariable(Nova nova, JavaVariableDescriptor variable) {
        Object value = resolveRuntimeValue(variable.getValue(), variable.getSupplier(), variable.getName());
        if (variable.isMutable()) {
            nova.set(variable.getName(), value);
        } else {
            nova.defineVal(variable.getName(), value);
        }
    }

    private static void installObject(Nova nova, JavaObjectDescriptor objectDescriptor) {
        Object value = resolveRuntimeValue(objectDescriptor.getValue(), objectDescriptor.getSupplier(), objectDescriptor.getName());
        nova.defineVal(objectDescriptor.getName(), value);
    }

    private static void installFunctions(Nova nova, List<JavaFunctionDescriptor> functions) {
        if (functions == null || functions.isEmpty()) {
            return;
        }
        for (JavaFunctionDescriptor function : functions) {
            if (function.getInvoker() == null) {
                throw new IllegalStateException("Java function has no invoker: " + function.getName());
            }
        }
        String functionName = functions.get(0).getName();
        int arity = functions.size() == 1 && !functions.get(0).isVararg()
                ? functions.get(0).getParameters().size()
                : -1;
        NovaNativeFunction nativeFunction = new NovaNativeFunction(functionName, arity, (ctx, args) -> {
            try {
                Object[] javaArgs = new Object[args.size()];
                for (int i = 0; i < args.size(); i++) {
                    NovaValue arg = args.get(i);
                    javaArgs[i] = arg != null ? arg.toJavaValue() : null;
                }
                JavaFunctionDescriptor function = resolveFunction(functions, javaArgs);
                Object result = function.getInvoker().invoke(javaArgs);
                return result == null ? NovaNull.UNIT : AbstractNovaValue.fromJava(result);
            } catch (Exception e) {
                throw NovaErrors.wrap("调用 Java 函数 '" + functionName + "' 失败", e);
            }
        });
        nova.defineVal(functionName, nativeFunction);
    }

    private static JavaFunctionDescriptor resolveFunction(List<JavaFunctionDescriptor> functions, Object[] arguments) {
        JavaFunctionDescriptor best = null;
        int bestScore = Integer.MIN_VALUE;
        boolean ambiguous = false;
        for (JavaFunctionDescriptor function : functions) {
            int score = scoreFunction(function, arguments);
            if (score == Integer.MIN_VALUE) {
                continue;
            }
            if (score > bestScore) {
                best = function;
                bestScore = score;
                ambiguous = false;
            } else if (score == bestScore) {
                ambiguous = true;
            }
        }
        if (best == null) {
            throw new IllegalArgumentException("No matching Java function overload: " + functions.get(0).getName());
        }
        if (ambiguous) {
            throw new IllegalArgumentException("Ambiguous Java function overload: " + functions.get(0).getName());
        }
        return best;
    }

    private static int scoreFunction(JavaFunctionDescriptor function, Object[] arguments) {
        List<JavaParameterDescriptor> parameters = function.getParameters();
        int declaredCount = parameters.size();
        int minimumCount = function.isVararg() ? Math.max(declaredCount - 1, 0) : declaredCount;
        if (arguments.length < minimumCount) {
            return Integer.MIN_VALUE;
        }
        if (!function.isVararg() && arguments.length != declaredCount) {
            return Integer.MIN_VALUE;
        }
        int score = function.isVararg() ? -1 : 0;
        for (int i = 0; i < arguments.length; i++) {
            int parameterIndex = i < declaredCount ? i : declaredCount - 1;
            if (parameterIndex < 0) {
                return Integer.MIN_VALUE;
            }
            JavaTypeRef type = parameters.get(parameterIndex).getType();
            Class<?> targetClass = type.javaClass();
            if (targetClass == null || targetClass == Object.class) {
                continue;
            }
            targetClass = boxedClass(targetClass);
            Object argument = arguments[i];
            if (argument == null) {
                if (!type.isNullable() && type.javaClass().isPrimitive()) {
                    return Integer.MIN_VALUE;
                }
                continue;
            }
            Class<?> sourceClass = argument.getClass();
            if (targetClass == sourceClass) {
                score += 4;
            } else if (targetClass.isAssignableFrom(sourceClass)) {
                score += 2;
            } else if (Number.class.isAssignableFrom(targetClass) && argument instanceof Number) {
                score += 1;
            } else {
                return Integer.MIN_VALUE;
            }
        }
        return score;
    }

    private static Class<?> boxedClass(Class<?> type) {
        if (!type.isPrimitive()) {
            return type;
        }
        if (type == Integer.TYPE) {
            return Integer.class;
        }
        if (type == Long.TYPE) {
            return Long.class;
        }
        if (type == Double.TYPE) {
            return Double.class;
        }
        if (type == Float.TYPE) {
            return Float.class;
        }
        if (type == Boolean.TYPE) {
            return Boolean.class;
        }
        if (type == Character.TYPE) {
            return Character.class;
        }
        if (type == Byte.TYPE) {
            return Byte.class;
        }
        if (type == Short.TYPE) {
            return Short.class;
        }
        return type;
    }

    private static Object resolveRuntimeValue(Object value, java.util.function.Supplier<?> supplier, String name) {
        if (supplier != null) {
            return supplier.get();
        }
        if (value != null) {
            return value;
        }
        throw new IllegalStateException("JavaTypes entry has no runtime value: " + name);
    }
}
