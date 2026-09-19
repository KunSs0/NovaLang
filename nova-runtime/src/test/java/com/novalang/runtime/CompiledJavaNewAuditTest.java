package com.novalang.runtime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Java.new 返回外部对象后的编译模式调用回归测试。
 */
@DisplayName("编译模式 Java.new 互操作回归")
class CompiledJavaNewAuditTest {

    @Test
    @DisplayName("Java.new HashMap 后调用 values().size()")
    void javaNewHashMapValuesSize() {
        Object result = new Nova().compileToBytecode(
                "val map = Java.new(\"java.util.HashMap\")\n" +
                        "map.put(\"key\", \"value\")\n" +
                        "map.values().size()",
                "compiled-java-new-audit.nova").run();

        assertEquals(1, ((Number) result).intValue());
    }

    @Test
    @DisplayName("Java.new HashMap 后调用 put 和 get")
    void javaNewHashMapPutAndGet() {
        Object result = new Nova().compileToBytecode(
                "val map = Java.new(\"java.util.HashMap\")\n" +
                        "map.put(\"key\", \"value\")\n" +
                        "map.get(\"key\")",
                "compiled-java-new-put-audit.nova").run();

        assertEquals("value", result);
    }

    @Test
    @DisplayName("解释模式 Java.new HashMap 后调用 values().size()")
    void interpretedJavaNewHashMapValuesSize() {
        Object result = new Nova().eval(
                "val map = Java.new(\"java.util.HashMap\")\n" +
                        "map.put(\"key\", \"value\")\n" +
                        "map.values().size()");

        assertEquals(1, ((Number) result).intValue());
    }
}
