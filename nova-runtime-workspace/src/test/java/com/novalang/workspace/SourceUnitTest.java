package com.novalang.workspace;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link SourceUnit} 虚拟源码行号映射测试。
 */
@DisplayName("Workspace SourceUnit")
class SourceUnitTest {

    @Test
    @DisplayName("将生成源码行号映射回 YAML 内容行")
    void shouldMapGeneratedLineToOrigin() {
        SourceUnit source = new SourceUnit("@generated/skill", "line1\nline2",
                Paths.get("skills.yml"), "skills.fire.action", 20, 2, null);

        assertEquals(20, source.mapGeneratedLine(1));
        assertEquals(20, source.mapGeneratedLine(2));
        assertEquals(21, source.mapGeneratedLine(4));
        assertEquals("" + Paths.get("skills.yml").toAbsolutePath().normalize()
                + " [skills.fire.action]:21", source.describeOrigin(4));
    }

    @Test
    @DisplayName("拒绝非法起始行和偏移")
    void shouldRejectInvalidLineMetadata() {
        assertThrows(IllegalArgumentException.class, () -> new SourceUnit(
                "@generated/a", "", null, null, 0, 0, null));
        assertThrows(IllegalArgumentException.class, () -> new SourceUnit(
                "@generated/a", "", null, null, 1, -1, null));
    }
}
