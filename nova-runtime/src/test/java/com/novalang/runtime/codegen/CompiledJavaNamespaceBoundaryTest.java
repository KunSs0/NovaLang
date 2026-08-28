package com.novalang.runtime.codegen;

import com.novalang.runtime.Nova;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("编译模式 Java 命名空间调用边界")
class CompiledJavaNamespaceBoundaryTest {

    @Test
    @DisplayName("Java.new 返回原生对象后可调用 HashMap.values")
    void javaNewHashMapValuesReturnsNativeCollection() {
        Object result = new Nova().compileToBytecode(
                "Java.new(\"java.util.HashMap\").values()", "test.nova").run();

        assertTrue(result instanceof Collection,
                "Java.new 的返回值应解包为原生 Java 对象");
    }

    @Test
    @DisplayName("Java.field 返回原生 Locale 后可调用 toLanguageTag")
    void javaFieldLocaleRootToLanguageTag() {
        Object result = new Nova().compileToBytecode(
                "Java.field(\"java.util.Locale\", \"ROOT\").toLanguageTag()",
                "test.nova").run();

        assertEquals("und", result);
    }
}
