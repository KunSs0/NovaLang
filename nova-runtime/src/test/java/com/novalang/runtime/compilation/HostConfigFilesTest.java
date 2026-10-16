package com.novalang.runtime.compilation;

import com.novalang.runtime.config.ConfigDocument;
import com.novalang.runtime.config.ConfigFiles;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

/** 业务配置通过显式根目录读取，保留原有类型和路径边界检查。 */
class HostConfigFilesTest {
    @TempDir
    Path root;

    /** 配置读取不依赖任何当前程序、线程上下文或运行容器。 */
    @Test
    void readsTypedYamlWithoutExecutionContext() throws Exception {
        Files.write(root.resolve("settings.yml"),
                "title: 中文\ncount: 7\nenabled: true\n".getBytes(StandardCharsets.UTF_8));
        ConfigDocument document = ConfigFiles.loadYaml(root, "settings.yml");
        assertEquals("中文", document.getString("title"));
        assertEquals(7, document.getInt("count"));
        assertTrue(document.getBoolean("enabled"));
        assertThrows(IllegalArgumentException.class, () -> document.getInt("title"));
    }

    /** 保留对绝对路径、上级目录和必需文件缺失的严格拒绝。 */
    @Test
    void rejectsEscapingAndMissingFiles() {
        assertThrows(IllegalArgumentException.class, () -> ConfigFiles.loadYaml(root, "../outside.yml"));
        assertThrows(IllegalArgumentException.class,
                () -> ConfigFiles.loadYaml(root, root.resolve("settings.yml").toString()));
        assertThrows(IllegalArgumentException.class, () -> ConfigFiles.loadYaml(root, "missing.yml"));
    }
}
