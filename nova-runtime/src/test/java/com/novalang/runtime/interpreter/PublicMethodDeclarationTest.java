package com.novalang.runtime.interpreter;

import com.novalang.runtime.NovaValue;
import com.novalang.runtime.resolution.PublicMethodResolver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 非公开 JDK 实现类的方法声明规范化测试。
 */
class PublicMethodDeclarationTest {

    @Test
    @DisplayName("解释器可以调用 HashMap values 视图的 size")
    void hashMapValuesSizeUsesPublicDeclaration() {
        Interpreter interpreter = new Interpreter();
        interpreter.setReplMode(true);

        NovaValue result = interpreter.evalRepl(
                "Java.new(\"java.util.HashMap\").values().size()");

        assertEquals(0, result.asInt());
    }

    @Test
    @DisplayName("HashMap Values.size 规范化为 Collection.size 公共声明")
    void hashMapValuesSizeResolvesToPublicInterface() throws Exception {
        Object values = new HashMap<Object, Object>().values();
        Method implementation = values.getClass().getMethod("size");

        Method declaration = PublicMethodResolver.resolvePublicDeclaration(implementation);

        assertTrue(Modifier.isPublic(declaration.getDeclaringClass().getModifiers()));
        assertTrue(Collection.class.isAssignableFrom(declaration.getDeclaringClass()));
        assertEquals("size", declaration.getName());
    }

    @Test
    @DisplayName("不存在公共父类或接口声明时保持失败")
    void missingPublicDeclarationReturnsNull() throws Exception {
        Method implementation = HiddenType.class.getMethod("onlyHere");

        assertNull(PublicMethodResolver.resolvePublicDeclaration(implementation));
    }

    static class HiddenType {
        public void onlyHere() {
        }
    }
}
