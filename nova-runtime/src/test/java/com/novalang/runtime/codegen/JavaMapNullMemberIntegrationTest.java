package com.novalang.runtime.codegen;

import com.novalang.runtime.Nova;
import org.junit.jupiter.api.Test;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

/** 使用真实编译产物验证 Java 快照空值及动态调用点重复读取。 */
class JavaMapNullMemberIntegrationTest {
    @Test
    void compiledMapKeyOverridesPreviouslyReadBeanProperty() {
        Nova nova = new Nova();
        com.novalang.runtime.CompiledNova program = nova.compileToBytecode(
                "fun read(snapshot: Any) { return snapshot.empty }", "map-key-precedence.nova");
        Map<String, Object> map = new LinkedHashMap<>();
        assertEquals(true, program.call("read", map));
        map.put("empty", null);
        assertNull(program.call("read", map));
        map.put("empty", "自定义值");
        assertEquals("自定义值", program.call("read", map));
        map.remove("empty");
        assertEquals(true, program.call("read", map));
    }

    @Test
    void compiledMemberReadTracksNullAndValueTransitions() {
        Nova nova = new Nova();
        com.novalang.runtime.CompiledNova program = nova.compileToBytecode(
                "fun read(snapshot: Any?) { if (snapshot == null) { return null }; return snapshot.active }",
                "java-map-null.nova");
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("active", null);
        assertNull(program.call("read", map));
        map.put("active", "斩击");
        assertEquals("斩击", program.call("read", map));
        map.put("active", null);
        assertNull(program.call("read", map));
        map.put("active", "横斩");
        assertEquals("横斩", program.call("read", map));
        Map<String, Object> other = new HashMap<>();
        other.put("active", null);
        assertNull(program.call("read", other));
        assertNull(program.call("read", (Object) null));
    }
}
