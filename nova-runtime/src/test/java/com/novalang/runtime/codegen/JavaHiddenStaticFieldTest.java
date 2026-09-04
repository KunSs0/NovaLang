package com.novalang.runtime.codegen;

import com.novalang.runtime.CompiledNova;
import com.novalang.runtime.Nova;
import com.novalang.runtime.NovaDynamic;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/** 合成 Kotlin Companion 的 JVM 字段形状，不依赖 Bukkit 或业务配置。 */
class JavaHiddenStaticFieldTest {
    @Test
    void javaReflectionShouldSelectDeclaredChildField() throws Exception {
        assertSame(Child.Companion, Child.class.getField("Companion").get(null));
        assertEquals("child", Child.Companion.getAccessContainer(new Context()));
    }

    @Test
    void runtimeStaticFieldShouldMatchJavaReflection() {
        for (int i = 0; i < 2; i++) {
            assertSame(Child.Companion, NovaDynamic.getStaticFieldByClasses(new Class<?>[]{Child.class}, "Companion"));
        }
    }

    @Test
    void parentAndInheritedOnlyFieldsShouldStillResolve() {
        assertSame(Parent.Companion, NovaDynamic.getStaticFieldByClasses(new Class<?>[]{Parent.class}, "Companion"));
        assertSame(Parent.Companion, NovaDynamic.getStaticFieldByClasses(new Class<?>[]{InheritedOnly.class}, "Companion"));
    }

    @Test
    void runtimeDispatchWithCorrectReceiverShouldSucceed() {
        assertEquals("child", NovaDynamic.invoke1(Child.Companion, "getAccessContainer", new Context()));
    }

    @Test
    void classMemberLookupShouldKeepChildCompanion() {
        assertSame(Child.Companion, NovaDynamic.getMember(Child.class, "Companion"));
    }

    @Test
    void hiddenInstanceFieldReadAndWriteShouldUseChildDeclaration() {
        InstanceChild instance = new InstanceChild();
        assertEquals("child", NovaDynamic.getMember(instance, "value"));
        NovaDynamic.setMember(instance, "value", "changed");
        assertEquals("changed", instance.value);
        assertEquals("parent", ((InstanceParent) instance).value);
    }

    @Test
    void interfaceFieldResolutionShouldMatchClassGetField() throws Exception {
        Object expected = InterfaceChild.class.getField("TOKEN").get(null);
        assertSame(TokenSource.TOKEN, expected);
        assertSame(expected, NovaDynamic.getStaticFieldByClasses(new Class<?>[]{InterfaceChild.class}, "TOKEN"));
    }

    @ParameterizedTest(name = "isolated={0}, staticImport={1}")
    @CsvSource({"false,false", "false,true", "true,false", "true,true"})
    void compiledFieldShouldKeepChildCompanion(boolean isolated, boolean staticImport) {
        String owner = "com.novalang.runtime.codegen.JavaHiddenStaticFieldTest.Child";
        String imports = staticImport ? "import static " + owner + ".Companion\n" : "import java " + owner + "\n";
        String field = staticImport ? "Companion" : "Child.Companion";
        String source = imports
                + "fun receiver() { return " + field + " }\n"
                + "fun probe(ctx) { val companion = " + field + "; return companion.getAccessContainer(ctx) }\n";
        CompiledNova compiled = new Nova().compileToBytecode(source, "hidden-companion.nova");
        compiled.run();
        Object actual = isolated ? compiled.callIsolated("receiver", Collections.emptyMap()) : compiled.call("receiver");
        assertSame(Child.Companion, actual, "父类同名字段不能覆盖子类声明");
        for (int i = 0; i < 2; i++) {
            Object result = isolated
                    ? compiled.callIsolated("probe", Collections.emptyMap(), new Context())
                    : compiled.call("probe", new Context());
            assertEquals("child", result);
        }
    }

    public static class Parent {
        public static final ParentCompanion Companion = new ParentCompanion();
    }
    public static final class Child extends Parent {
        public static final ChildCompanion Companion = new ChildCompanion();
    }
    public static final class InheritedOnly extends Parent { }
    public static final class ParentCompanion { }
    public static final class ChildCompanion {
        public String getAccessContainer(Context context) { return "child"; }
    }
    public static final class Context { }
    public static class InstanceParent {
        public String value = "parent";
    }
    public static final class InstanceChild extends InstanceParent {
        public String value = "child";
    }
    public interface TokenSource {
        Object TOKEN = new Object();
    }
    public static class TokenParent {
        public static final Object TOKEN = new Object();
    }
    public static final class InterfaceChild extends TokenParent implements TokenSource { }
}
