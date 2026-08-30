package com.novalang.runtime.host;

import com.novalang.runtime.CompiledNova;
import com.novalang.runtime.Nova;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("JavaTypes 编译期校验")
class JavaTypesCompileValidationTest {

    @Test
    @DisplayName("已注册函数使用正确参数时可以编译执行")
    void shouldCompileRegisteredFunction() {
        Nova nova = createNova();

        CompiledNova compiled = nova.compileToBytecode("add(1, 2)", "java-types-valid.nova");

        assertEquals(3, compiled.run());
    }

    @Test
    @DisplayName("未注册函数必须在编译期失败")
    void shouldRejectUnknownFunctionDuringCompilation() {
        Nova nova = createNova();

        assertThrows(RuntimeException.class,
                () -> nova.compileToBytecode("missingFunction()", "java-types-missing.nova"));
    }

    @Test
    @DisplayName("严格 JavaTypes 模式允许调用后声明的 Nova 函数")
    void shouldCompileForwardDeclaredNovaFunctions() {
        Nova nova = createNova();
        String source = String.join("\n",
                "fun entry(): Int = createValue()",
                "class ForwardFunctions {",
                "    fun first(): Int = second()",
                "    fun second(): Int = 42",
                "}",
                "fun createValue(): Int = ForwardFunctions().first()",
                "entry()"
        );

        CompiledNova compiled = nova.compileToBytecode(source, "java-types-forward-functions.nova");

        assertEquals(42, compiled.run());
    }

    @Test
    @DisplayName("函数参数数量错误必须在编译期失败")
    void shouldRejectInvalidArgumentCountDuringCompilation() {
        Nova nova = createNova();

        assertThrows(RuntimeException.class,
                () -> nova.compileToBytecode("add(1)", "java-types-arity.nova"));
    }

    @Test
    @DisplayName("函数参数类型错误必须在编译期失败")
    void shouldRejectInvalidArgumentTypeDuringCompilation() {
        Nova nova = createNova();

        assertThrows(RuntimeException.class,
                () -> nova.compileToBytecode("add(\"1\", 2)", "java-types-argument.nova"));
    }

    @Test
    @DisplayName("Java 对象不存在的成员必须在编译期失败")
    void shouldRejectMissingJavaMemberDuringCompilation() {
        Nova nova = createNova();

        assertThrows(RuntimeException.class,
                () -> nova.compileToBytecode("player.missingMethod()", "java-types-member.nova"));
    }

    @Test
    @DisplayName("JavaBean 属性可以在编译期解析并执行")
    void shouldCompileJavaBeanProperty() {
        Nova nova = createNova();

        assertEquals("Alex", nova.compileToBytecode(
                "player.name", "java-types-property-valid.nova").run());
    }

    @Test
    @DisplayName("Java 对象不存在的属性必须在编译期失败")
    void shouldRejectMissingJavaPropertyDuringCompilation() {
        Nova nova = createNova();

        assertThrows(RuntimeException.class,
                () -> nova.compileToBytecode("player.missingProperty", "java-types-property-missing.nova"));
    }

    @Test
    @DisplayName("Java 函数返回类型必须传播到后续成员访问")
    void shouldPropagateJavaFunctionReturnType() {
        Nova nova = createNova();

        assertEquals("Alex", nova.compileToBytecode(
                "currentPlayer().name", "java-types-return-valid.nova").run());
        assertThrows(RuntimeException.class,
                () -> nova.compileToBytecode(
                        "currentPlayer().missingMethod()", "java-types-return-missing.nova"));
    }

    @Test
    @DisplayName("Java 泛型接收者的方法返回类型使用实际类型参数")
    void shouldResolveGenericReceiverMethodReturnType() {
        JavaTypeRef referenceType = JavaTypeRef.parameterized(
                "GenericReference", GenericReference.class, JavaTypeRefs.FLOAT);
        JavaTypes javaTypes = JavaTypes.builder()
                .globalVariable("reference", variable -> variable
                        .type(referenceType)
                        .value(new GenericReference<Float>(3.0f)))
                .build();
        Nova nova = new Nova();
        nova.install(javaTypes);
        String source = String.join("\n",
                "fun acceptFloat(value: Float): Float = value",
                "acceptFloat(reference.get())"
        );

        assertEquals(3.0f, nova.compileToBytecode(
                source, "java-types-generic-receiver-return.nova").run());
    }

    @Test
    @DisplayName("注册 object 成员使用正确签名时可以编译执行")
    void shouldCompileRegisteredObjectMember() {
        Nova nova = createNova();

        CompiledNova compiled = nova.compileToBytecode("api.echo(\"hello\")", "java-types-object-valid.nova");

        assertEquals("hello", compiled.run());
    }

    @Test
    @DisplayName("注册 object 成员参数错误必须在编译期失败")
    void shouldRejectInvalidObjectMemberArgument() {
        Nova nova = createNova();

        assertThrows(RuntimeException.class,
                () -> nova.compileToBytecode("api.echo(1)", "java-types-object-argument.nova"));
    }

    @Test
    @DisplayName("注册 object 不存在的成员必须在编译期失败")
    void shouldRejectUnknownRegisteredObjectMember() {
        Nova nova = createNova();

        assertThrows(RuntimeException.class,
                () -> nova.compileToBytecode("api.missing()", "java-types-object-member.nova"));
    }

    @Test
    @DisplayName("注册 object 属性不能作为函数调用")
    void shouldRejectPropertyUsedAsFunction() {
        Nova nova = createNova();

        assertThrows(RuntimeException.class,
                () -> nova.compileToBytecode("api.name()", "java-types-object-property.nova"));
    }

    @Test
    @DisplayName("Java 函数重载在编译期和运行期选择一致")
    void shouldResolveJavaFunctionOverload() {
        Nova nova = createNova();

        assertEquals("string:x", nova.compileToBytecode(
                "choose(\"x\")", "java-types-overload-string.nova").run());
        assertEquals("int:3", nova.compileToBytecode(
                "choose(3)", "java-types-overload-int.nova").run());
    }

    @Test
    @DisplayName("Java 函数没有匹配重载时必须在编译期失败")
    void shouldRejectMissingJavaFunctionOverload() {
        Nova nova = createNova();

        assertThrows(RuntimeException.class,
                () -> nova.compileToBytecode("choose(true)", "java-types-overload-missing.nova"));
    }

    @Test
    @DisplayName("扩展属性 getter 和 setter 使用同一份描述完成编译与执行")
    void shouldCompileAndRunExtensionPropertySetter() {
        ExtensionPropertyBean bean = new ExtensionPropertyBean();
        JavaTypes javaTypes = JavaTypes.builder()
                .globalVariable("settings", variable -> variable
                        .type(ExtensionPropertyBean.class)
                        .value(bean))
                .extensionProperty(ExtensionPropertyBean.class, "label", property -> property
                        .type(String.class)
                        .getter(arguments -> ((ExtensionPropertyBean) arguments[0]).label)
                        .setter(arguments -> {
                            ((ExtensionPropertyBean) arguments[0]).label = (String) arguments[1];
                            return null;
                        }))
                .extensionProperty(ExtensionPropertyBean.class, "id", property -> property
                        .type(String.class)
                        .getter(arguments -> ((ExtensionPropertyBean) arguments[0]).id))
                .build();
        Nova nova = new Nova();
        nova.install(javaTypes);

        Object result = nova.compileToBytecode(
                "settings.label = \"changed\"\nsettings.label",
                "java-extension-property-valid.nova").run();

        assertEquals("changed", result);
        assertEquals("changed", bean.label);
        assertThrows(RuntimeException.class, () -> nova.compileToBytecode(
                "settings.label = 1", "java-extension-property-type-invalid.nova"));
        assertThrows(RuntimeException.class, () -> nova.compileToBytecode(
                "settings.id = \"other\"", "java-extension-property-readonly.nova"));
    }

    private Nova createNova() {
        JavaTypes javaTypes = JavaTypes.builder()
                .globalFunction("add", function -> function
                        .param("left", JavaTypeRefs.INT)
                        .param("right", JavaTypeRefs.INT)
                        .returns(JavaTypeRefs.INT)
                        .invoke2(Integer.class, Integer.class, (left, right) -> left + right))
                .globalFunction("choose", function -> function
                        .param("value", JavaTypeRefs.STRING)
                        .returns(JavaTypeRefs.STRING)
                        .invoke1(String.class, value -> "string:" + value))
                .globalFunction("choose", function -> function
                        .param("value", JavaTypeRefs.INT)
                        .returns(JavaTypeRefs.STRING)
                        .invoke1(Integer.class, value -> "int:" + value))
                .globalFunction("currentPlayer", function -> function
                        .returns(JavaTypeRef.javaType(TestPlayer.class))
                        .invoke0(() -> new TestPlayer("Alex")))
                .globalVariable("player", variable -> variable
                        .type(JavaTypeRef.javaType(TestPlayer.class))
                        .value(new TestPlayer("Alex")))
                .globalObject("api", object -> object
                        .type(JavaTypeRef.javaType(TestApi.class))
                        .value(new TestApi())
                        .property("name", property -> property
                                .type(JavaTypeRefs.STRING)
                                .readonly())
                        .function("echo", function -> function
                                .param("message", JavaTypeRefs.STRING)
                                .returns(JavaTypeRefs.STRING)))
                .build();

        Nova nova = new Nova();
        nova.install(javaTypes);
        return nova;
    }

    public static final class TestPlayer {
        private final String name;

        public TestPlayer(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static final class TestApi {
        public final String name = "test";

        public String echo(String message) {
            return message;
        }
    }

    public static final class ExtensionPropertyBean {
        private final String id = "settings";
        private String label = "initial";
    }

    public static final class GenericReference<T> {
        private final T value;

        public GenericReference(T value) {
            this.value = value;
        }

        public T get() {
            return value;
        }
    }
}
