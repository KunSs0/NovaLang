package com.novalang.runtime.compilation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

/** 编译配置只接受显式元数据，保留严格 Alias、版本、路径及 YAML 类型检查。 */
class CompilationConfigLoaderTest {
    @TempDir
    Path root;

    /** 验证 YAML 路径按配置文件目录解析，且不需要运行环境字段。 */
    @Test
    void loadsCompilerMetadata() throws Exception {
        CompilationConfig config = load(validConfig());
        assertEquals("test", config.getName());
        assertEquals(root.toAbsolutePath().normalize(), config.getAliases().get("@"));
    }

    /** 旧 runtime 设置必须明确迁移到 Nova 和宿主，禁止默默忽略。 */
    @Test
    void rejectsRuntimeConfiguration() {
        assertThrows(CompilationException.class,
                () -> load(validConfig() + "runtime: {security: trusted-server, thread: main}\n"));
    }

    /** 验证 Alias、重复字段、版本和源码目录错误都在加载时报告。 */
    @Test
    void rejectsInvalidMetadata() {
        assertThrows(CompilationException.class, () -> load(validConfig().replace("\"@\"", "shared")));
        assertThrows(CompilationException.class, () -> load(validConfig() + "version: 1\n"));
        assertThrows(CompilationException.class, () -> load(validConfig() + "thread: main\n"));
        assertThrows(CompilationException.class, () -> load(validConfig().replace("version: 1", "version: 2")));
        assertThrows(CompilationException.class, () -> load(validConfig() + "sources: [.]\n"));
        assertThrows(CompilationException.class, () -> load(validConfig() + "entries: [main.nova]\n"));
        assertThrows(CompilationException.class, () -> load(validConfig() + "virtualSources: []\n"));
        assertThrows(CompilationException.class, () -> load(validConfig() + "nonInitializingSources: []\n"));
    }

    /** 验证编程构建与 YAML 使用相同的命名规则，已生成配置不受后续修改影响。 */
    @Test
    void validatesAndSnapshotsProgrammaticMetadata() {
        CompilationConfig.Builder builder = new CompilationConfig.Builder();
        builder.alias("@", root);
        CompilationConfig config = builder.build();
        builder.alias("@other", root.resolve("other"));
        assertEquals(1, config.getAliases().size());
        assertThrows(UnsupportedOperationException.class, () -> config.getAliases().put("@third", root));
        assertThrows(CompilationException.class, () -> builder.alias("shared", root));
        assertThrows(CompilationException.class, () -> builder.alias("@", root));
        assertThrows(CompilationException.class, () -> builder.name("invalid name"));
        assertTrue(new CompilationConfig.Builder().build().getAliases().isEmpty());
    }

    /** 最小 YAML 仅声明版本即可，不强制项目名、Alias 或目录。 */
    @Test
    void acceptsEmptyOptionalMetadata() throws Exception {
        CompilationConfig config = load("version: 1\n");
        assertEquals("nova", config.getName());
        assertTrue(config.getAliases().isEmpty());
    }

    /** 将 UTF-8 配置写入临时目录后走实际 YAML 加载路径。 */
    private CompilationConfig load(String text) throws Exception {
        Path file = root.resolve("nova.config.yml");
        Files.write(file, text.getBytes(StandardCharsets.UTF_8));
        return new CompilationConfigLoader().load(file);
    }

    /** @return 没有运行时策略的最小项目编译配置 */
    private String validConfig() {
        return "version: 1\nname: test\naliases: {\"@\": .}\n";
    }
}
