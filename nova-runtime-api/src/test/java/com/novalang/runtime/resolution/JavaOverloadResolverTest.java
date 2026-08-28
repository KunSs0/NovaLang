package com.novalang.runtime.resolution;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Java 重载解析器的编译期未知类型与运行期具体类型回归测试。
 */
class JavaOverloadResolverTest {

    @Test
    void unknownObjectArgumentKeepsConstructorAvailableForRuntimeResolution() throws Exception {
        Constructor<?> constructor = JavaOverloadResolver.selectBestConstructor(
                Arrays.asList(OverloadFixture.class.getConstructors()),
                new Class<?>[]{Object.class});

        assertNotNull(constructor);
    }

    @Test
    void unknownObjectArgumentKeepsReferenceMethodAvailable() throws Exception {
        Method method = JavaOverloadResolver.selectBestMethod(
                Arrays.asList(OverloadFixture.class.getMethods()),
                false,
                new Class<?>[]{String.class, Object.class});

        assertNotNull(method);
        assertEquals("reference", method.getName());
    }

    @Test
    void unknownObjectArgumentKeepsPrimitiveMethodAvailable() throws Exception {
        Method method = JavaOverloadResolver.selectBestMethod(
                methodsNamed("primitive"),
                false,
                new Class<?>[]{Object.class});

        assertNotNull(method);
        assertEquals("primitive", method.getName());
    }

    @Test
    void classArgumentPrefersClassOverObjectOverload() throws Exception {
        Method method = JavaOverloadResolver.selectBestMethod(
                methodsNamed("classArgument"),
                true,
                new Class<?>[]{Class.class});

        assertNotNull(method);
        assertEquals(Class.class, method.getParameterTypes()[0]);
    }

    @Test
    void staticallyIncompatibleKnownArgumentsStillFail() throws Exception {
        Method method = JavaOverloadResolver.selectBestMethod(
                methodsNamed("primitive"),
                false,
                new Class<?>[]{String.class, Boolean.class});

        assertNull(method);
    }

    @Test
    void covariantReturnPrefersRealMethodOverCompilerBridge() {
        List<Method> methods = new ArrayList<Method>();
        for (Method method : CovariantImplementation.class.getMethods()) {
            if ("manager".equals(method.getName())) {
                methods.add(method);
            }
        }

        Method selected = JavaOverloadResolver.selectBestMethod(methods, false, new Class<?>[0]);

        assertNotNull(selected);
        assertFalse(selected.isBridge());
        assertFalse(selected.isSynthetic());
        assertEquals(DerivedManager.class, selected.getReturnType());
        assertEquals(CovariantImplementation.class, selected.getDeclaringClass());
    }

    private static List<Method> methodsNamed(String name) {
        List<Method> result = new ArrayList<Method>();
        for (Method method : OverloadFixture.class.getMethods()) {
            if (name.equals(method.getName())) {
                result.add(method);
            }
        }
        return result;
    }

    public static final class OverloadFixture {

        public OverloadFixture(Object value) {
        }

        public OverloadFixture(String value) {
        }

        public String reference(String value, Object extra) {
            return "reference";
        }

        public String reference(String value, Integer extra) {
            return "reference-integer";
        }

        public String primitive(int value) {
            return "primitive";
        }

        public String primitive(String value) {
            return "primitive-string";
        }

        public static String classArgument(Class<?> value) {
            return "class";
        }

        public static String classArgument(Object value) {
            return "object";
        }
    }

    public interface BaseManager {
    }

    public static final class DerivedManager implements BaseManager {
    }

    public interface ManagerProvider {
        BaseManager manager();
    }

    public static final class CovariantImplementation implements ManagerProvider {
        @Override
        public DerivedManager manager() {
            return new DerivedManager();
        }
    }
}
