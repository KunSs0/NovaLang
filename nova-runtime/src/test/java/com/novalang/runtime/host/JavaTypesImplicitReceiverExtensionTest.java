package com.novalang.runtime.host;

import com.novalang.runtime.Nova;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("JavaTypes 扩展函数的隐式接收者解析")
class JavaTypesImplicitReceiverExtensionTest {

    @Test
    @DisplayName("继承类方法内的隐式扩展调用无法解析，但显式基类接收者可以解析")
    void inheritedJavaTypeExtensionSupportsImplicitReceiver() {
        Nova nova = createNova();

        String implicitSource = String.join("\n",
                "import java com.novalang.runtime.host.JavaTypesImplicitReceiverExtensionTest.Base",
                "class Child : Base() {",
                "    fun call(): Int {",
                "        return ping()",
                "    }",
                "}",
                "fun run(): Int = Child().call()",
                "run()"
        );
        assertEquals(7, nova.compileToBytecode(
                implicitSource,
                "java-types-implicit-receiver-extension.nova"
        ).run());

        String explicitSource = String.join("\n",
                "import java com.novalang.runtime.host.JavaTypesImplicitReceiverExtensionTest.Base",
                "class Child : Base() {",
                "    fun call(): Int {",
                "        return (this as Base).ping()",
                "    }",
                "}",
                "fun run(): Int = Child().call()",
                "run()"
        );
        assertEquals(7, nova.compileToBytecode(
                explicitSource,
                "java-types-explicit-receiver-extension.nova"
        ).run());
    }

    private Nova createNova() {
        JavaTypes javaTypes = JavaTypes.builder()
                .extension(Base.class, "ping", function -> function
                        .returns(Integer.class)
                        .invoke(arguments -> 7))
                .build();
        Nova nova = new Nova();
        nova.install(javaTypes);
        return nova;
    }

    public static class Base {
        public Base() {
        }
    }
}
