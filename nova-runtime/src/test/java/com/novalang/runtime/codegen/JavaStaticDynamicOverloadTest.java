package com.novalang.runtime.codegen;

import com.novalang.runtime.CompiledNova;
import com.novalang.runtime.Nova;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** 动态参数不能在编译期被固定到 String 或 Object 静态重载。 */
class JavaStaticDynamicOverloadTest {
    @ParameterizedTest(name = "isolated={0}, staticImport={1}")
    @CsvSource({"false,false", "false,true", "true,false", "true,true"})
    void shouldDispatchUsingActualArgumentOnEveryCall(boolean isolated, boolean staticImport) {
        String owner = "com.novalang.runtime.codegen.JavaStaticDynamicOverloadTest.Lookup";
        String imports = staticImport
                ? "import static " + owner + ".find\nimport static " + owner + ".describe\n"
                : "import java " + owner + "\n";
        String prefix = staticImport ? "" : "Lookup.";
        String source = imports
                + "fun probe(value) { if (value == null) { return null }; return " + prefix + "find(value) }\n"
                + "fun broad(value: Any) { return " + prefix + "describe(value) }\n"
                + "fun literal() { return " + prefix + "find(\"literal\") }\n";
        CompiledNova compiled = new Nova().compileToBytecode(source, "static-dynamic-overload.nova");
        compiled.run();
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000123");
        // 反复经过同一调用点，不能缓存第一个参数类型后误用另一个重载。
        for (Object value : new Object[]{id, "player", id, "second", null}) {
            Object expected = value == null ? null : (value instanceof UUID ? "uuid:" : "name:") + value;
            Object actual = isolated
                    ? compiled.callIsolated("probe", Collections.emptyMap(), value)
                    : compiled.call("probe", value);
            assertEquals(expected, actual);
        }
        for (Object value : new Object[]{id, "text", 42}) {
            String expected = (value instanceof UUID ? "uuid:" : "object:") + value;
            Object actual = isolated
                    ? compiled.callIsolated("broad", Collections.emptyMap(), value)
                    : compiled.call("broad", value);
            assertEquals(expected, actual);
        }
        assertEquals("name:literal", compiled.call("literal"));
    }

    public static final class Lookup {
        public static String find(String name) { return "name:" + name; }
        public static String find(UUID id) { return "uuid:" + id; }
        public static String describe(Object value) { return "object:" + value; }
        public static String describe(UUID id) { return "uuid:" + id; }
    }
}
