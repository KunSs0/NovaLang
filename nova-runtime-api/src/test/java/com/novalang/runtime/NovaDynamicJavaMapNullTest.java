package com.novalang.runtime;

import org.junit.jupiter.api.Test;
import java.util.LinkedHashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

/** Java Map 的空值键必须与缺失键区分。 */
class NovaDynamicJavaMapNullTest {
    @Test
    void existingNullKeyReturnsNull() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("active", null);
        assertTrue(map.containsKey("active"));
        assertNull(NovaDynamic.getMember(map, "active"));
    }

    @Test
    void missingKeyStillReportsUndefinedMember() {
        Map<String, Object> map = new LinkedHashMap<>();
        assertThrows(NovaException.class, () -> NovaDynamic.getMember(map, "active"));
    }

    @Test
    void nullKeyTakesPrecedenceOverBeanProperty() {
        Map<String, Object> map = new LinkedHashMap<>();
        assertEquals(true, NovaDynamic.getMember(map, "empty"));
        map.put("empty", null);
        assertNull(NovaDynamic.getMember(map, "empty"));
        map.remove("empty");
        assertEquals(true, NovaDynamic.getMember(map, "empty"));
    }

    @Test
    void presentFalseAndZeroRemainValues() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("active", false);
        assertEquals(false, NovaDynamic.getMember(map, "active"));
        map.put("active", 0);
        assertEquals(0, NovaDynamic.getMember(map, "active"));
    }
}
