package com.novalang.compiler.analysis;

import com.novalang.compiler.analysis.types.ClassNovaType;
import com.novalang.compiler.analysis.types.NovaType;
import com.novalang.compiler.analysis.types.SuperTypeRegistry;
import com.novalang.compiler.analysis.types.TypeResolver;
import com.novalang.compiler.ast.expr.Expression;
import com.novalang.runtime.NovaTypeRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("TypeInferenceEngine member inference")
class TypeInferenceEngineTest {

    private TypeInferenceEngine newEngine() {
        Map<Expression, NovaType> exprTypes = new IdentityHashMap<Expression, NovaType>();
        TypeResolver typeResolver = new TypeResolver();
        TypeUnifier unifier = new TypeUnifier(exprTypes, new SuperTypeRegistry(), typeResolver);
        return new TypeInferenceEngine(exprTypes, unifier);
    }

    @Test
    @DisplayName("same-name registry overloads should merge to a common return type")
    void registryOverloadsShouldUseCommonReturnType() {
        String typeName = "__TypeInferenceOverloadCommon__";
        NovaTypeRegistry.registerType(typeName, Arrays.asList(
                NovaTypeRegistry.MethodInfo.method("pick", 1, "pick an int", "Int"),
                NovaTypeRegistry.MethodInfo.method("pick", 1, "pick a string", "String")));

        TypeInferenceEngine engine = newEngine();
        NovaType inferred = engine.lookupMemberType(new ClassNovaType(typeName, false), "pick");

        assertNotNull(inferred, "Expected registry overload inference to produce a type");
        assertEquals("Any", inferred.toDisplayString(),
                "Overload families should use the common supertype of all candidate return types");
    }

    @Test
    @DisplayName("type-specific registry members should win before Any fallback members")
    void typeSpecificRegistryMembersShouldBeatAnyFallback() {
        String typeName = "__TypeInferenceSpecificFallback__";
        NovaTypeRegistry.registerType(typeName, Arrays.asList(
                NovaTypeRegistry.MethodInfo.method("toString", 0, "specialized stringifier", "Boolean")));

        TypeInferenceEngine engine = newEngine();
        NovaType inferred = engine.lookupMemberType(new ClassNovaType(typeName, false), "toString");

        assertNotNull(inferred, "Expected type-specific registry methods to resolve");
        assertEquals("Boolean", inferred.toDisplayString(),
                "Type-specific members should resolve before inherited Any members");
    }

    @Test
    @DisplayName("builtin Nova type names must not be resolved through the Java classloader")
    void builtinNovaTypeNamesShouldNotTouchJavaClassLoader() {
        AtomicInteger classLoaderCalls = new AtomicInteger();
        ClassLoader countingLoader = new ClassLoader(null) {
            @Override
            protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
                classLoaderCalls.incrementAndGet();
                throw new ClassNotFoundException(name);
            }
        };

        Thread thread = Thread.currentThread();
        ClassLoader previousLoader = thread.getContextClassLoader();
        thread.setContextClassLoader(countingLoader);
        try {
            TypeInferenceEngine engine = newEngine();
            for (NovaTypeRegistry.TypeInfo info : NovaTypeRegistry.getBuiltinTypes()) {
                assertNotNull(engine.resolveNovaTypeFromName(info.name),
                        "Builtin Nova type should resolve locally: " + info.name);
            }
        } finally {
            thread.setContextClassLoader(previousLoader);
        }

        assertEquals(0, classLoaderCalls.get(),
                "Builtin Nova types are compiler-known names and must not trigger Class.forName");
    }
}
