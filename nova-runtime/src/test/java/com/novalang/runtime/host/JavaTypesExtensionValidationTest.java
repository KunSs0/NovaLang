package com.novalang.runtime.host;

import com.novalang.runtime.Nova;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("JavaTypes 扩展函数编译与运行时校验")
class JavaTypesExtensionValidationTest {

    @Test
    @DisplayName("扩展签名同时驱动编译期类型和运行时调用")
    void extensionSignatureDrivesCompilationAndRuntime() {
        Nova nova = createNova();

        assertEquals("box", nova.compileToBytecode(
                "box.label()", "java-types-extension-label.nova").run());
        assertEquals(7, nova.compileToBytecode(
                "box.plus(4)", "java-types-extension-plus.nova").run());
        assertEquals("child", nova.compileToBytecode(
                "child.label()", "java-types-extension-inheritance.nova").run());
        assertEquals("box", nova.compileToBytecode(
                "box?.label()", "java-types-extension-safe-call.nova").run());
        assertEquals("native:box", nova.compileToBytecode(
                "box.nativeName()", "java-types-native-priority.nova").run());
        assertEquals("native:box", nova.compileToBytecode(
                "box?.nativeName()", "java-types-safe-native-priority.nova").run());
    }

    @Test
    @DisplayName("扩展函数错误参数在编译期失败")
    void invalidExtensionArgumentsFailDuringCompilation() {
        Nova nova = createNova();

        assertThrows(RuntimeException.class, () -> nova.compileToBytecode(
                "box.plus(\"4\")", "java-types-extension-invalid-type.nova"));
        assertThrows(RuntimeException.class, () -> nova.compileToBytecode(
                "box.plus()", "java-types-extension-invalid-arity.nova"));
        assertThrows(RuntimeException.class, () -> nova.compileToBytecode(
                "box?.plus(\"4\")", "java-types-extension-safe-invalid-type.nova"));
        assertThrows(RuntimeException.class, () -> nova.compileToBytecode(
                "box.missing()", "java-types-extension-missing.nova"));
    }

    @Test
    @DisplayName("相同参数数量的扩展重载按实际类型分派")
    void sameArityOverloadsUseActualArgumentTypes() {
        Nova nova = createNova();

        assertEquals("string:x", nova.compileToBytecode(
                "box.choose(\"x\")", "java-types-extension-string-overload.nova").run());
        assertEquals("int:2", nova.compileToBytecode(
                "box.choose(2)", "java-types-extension-int-overload.nova").run());
    }

    private Nova createNova() {
        TestBox box = new TestBox("box", 3);
        ChildBox child = new ChildBox("child", 5);
        JavaTypes javaTypes = JavaTypes.builder()
                .globalVariable("box", variable -> variable
                        .type(TestBox.class)
                        .value(box))
                .globalVariable("child", variable -> variable
                        .type(ChildBox.class)
                        .value(child))
                .extension(TestBox.class, "label", function -> function
                        .returns(String.class)
                        .invoke(arguments -> ((TestBox) arguments[0]).label))
                .extension(TestBox.class, "plus", function -> function
                        .param("value", Integer.class)
                        .returns(Integer.class)
                        .invoke(arguments -> ((TestBox) arguments[0]).base + ((Number) arguments[1]).intValue()))
                .extension(TestBox.class, "nativeName", function -> function
                        .returns(String.class)
                        .invoke(arguments -> "extension:" + ((TestBox) arguments[0]).label))
                .extension(TestBox.class, "choose", function -> function
                        .param("value", String.class)
                        .returns(String.class)
                        .invoke(arguments -> "string:" + arguments[1]))
                .extension(TestBox.class, "choose", function -> function
                        .param("value", Integer.class)
                        .returns(String.class)
                        .invoke(arguments -> "int:" + arguments[1]))
                .build();
        Nova nova = new Nova();
        nova.install(javaTypes);
        return nova;
    }

    public static class TestBox {
        private final String label;
        private final int base;

        TestBox(String label, int base) {
            this.label = label;
            this.base = base;
        }

        public String nativeName() {
            return "native:" + label;
        }
    }

    public static final class ChildBox extends TestBox {
        ChildBox(String label, int base) {
            super(label, base);
        }
    }
}
