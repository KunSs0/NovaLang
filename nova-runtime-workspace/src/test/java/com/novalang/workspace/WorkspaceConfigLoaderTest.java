package com.novalang.workspace;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link WorkspaceConfigLoader} 强类型配置校验测试。
 */
@DisplayName("Workspace 配置加载")
class WorkspaceConfigLoaderTest {

    @TempDir
    Path tempDirectory;

    @Test
    @DisplayName("解析合法配置并以配置目录解析路径")
    void shouldLoadValidConfig() throws Exception {
        WorkspaceTestSupport.write(tempDirectory, "scripts/main.nova", "fun value() = 1");
        Path configFile = WorkspaceTestSupport.writeConfig(
                tempDirectory, "parallel-safe", "  - \"scripts/main\"\n");

        WorkspaceConfig config = new WorkspaceConfigLoader().load(configFile);

        assertEquals(1, config.getVersion());
        assertEquals("test-workspace", config.getName());
        assertEquals(ExecutionPolicy.PARALLEL_SAFE, config.getExecutionPolicy());
        assertEquals(WorkspaceConfig.SecurityMode.TRUSTED_SERVER, config.getSecurityMode());
        assertEquals(tempDirectory.toAbsolutePath().normalize(), config.getSourceRoots().get(0));
        assertEquals(tempDirectory.toAbsolutePath().normalize(), config.getAliases().get("@"));
    }

    @Test
    @DisplayName("Alias 必须以 @ 开头")
    void shouldRejectAliasWithoutAtPrefix() throws Exception {
        Path config = writeRaw("version: 1\nname: invalid\naliases:\n  shared: .\n"
                + "sources: [. ]\nentries: [main]\nruntime:\n"
                + "  security: trusted-server\n  thread: caller\n");

        WorkspaceException exception = assertThrows(WorkspaceException.class,
                () -> new WorkspaceConfigLoader().load(config));

        assertTrue(exception.getMessage().contains("Alias must start with @"));
    }

    @Test
    @DisplayName("拒绝重复 YAML key")
    void shouldRejectDuplicateKeys() throws Exception {
        Path config = writeRaw("version: 1\nversion: 1\nname: duplicate\n"
                + "aliases: {\"@\": .}\nsources: [.]\nentries: [main]\n"
                + "runtime: {security: trusted-server, thread: caller}\n");

        assertThrows(WorkspaceException.class, () -> new WorkspaceConfigLoader().load(config));
    }

    @Test
    @DisplayName("拒绝不支持的配置版本")
    void shouldRejectUnsupportedVersion() throws Exception {
        Path config = writeRaw(validConfig().replace("version: 1", "version: 2"));

        WorkspaceException exception = assertThrows(WorkspaceException.class,
                () -> new WorkspaceConfigLoader().load(config));

        assertEquals("Unsupported Workspace config version: 2", exception.getMessage());
    }

    @Test
    @DisplayName("拒绝未知安全策略")
    void shouldRejectUnknownSecurityMode() throws Exception {
        Path config = writeRaw(validConfig().replace("trusted-server", "permissive"));

        WorkspaceException exception = assertThrows(WorkspaceException.class,
                () -> new WorkspaceConfigLoader().load(config));

        assertEquals("Unsupported runtime.security: permissive", exception.getMessage());
    }

    @Test
    @DisplayName("拒绝未知线程策略")
    void shouldRejectUnknownThreadPolicy() throws Exception {
        Path config = writeRaw(validConfig().replace("thread: caller", "thread: automatic"));

        WorkspaceException exception = assertThrows(WorkspaceException.class,
                () -> new WorkspaceConfigLoader().load(config));

        assertEquals("Unsupported runtime.thread: automatic", exception.getMessage());
    }

    @Test
    @DisplayName("sources 不能为空")
    void shouldRejectEmptySources() throws Exception {
        Path config = writeRaw(validConfig().replace("sources: [.]", "sources: []"));

        assertThrows(WorkspaceException.class, () -> new WorkspaceConfigLoader().load(config));
    }

    @Test
    @DisplayName("entries 不能为空")
    void shouldRejectEmptyEntries() throws Exception {
        Path config = writeRaw(validConfig().replace("entries: [main]", "entries: []"));

        assertThrows(WorkspaceException.class, () -> new WorkspaceConfigLoader().load(config));
    }

    @Test
    @DisplayName("name 必须满足强格式")
    void shouldRejectInvalidName() throws Exception {
        Path config = writeRaw(validConfig().replace("name: valid", "name: invalid name"));

        WorkspaceException exception = assertThrows(WorkspaceException.class,
                () -> new WorkspaceConfigLoader().load(config));

        assertTrue(exception.getMessage().startsWith("Workspace name contains invalid characters"));
    }

    private Path writeRaw(String content) throws Exception {
        return WorkspaceTestSupport.write(tempDirectory, "nova.config.yml", content);
    }

    private String validConfig() {
        return "version: 1\nname: valid\naliases: {\"@\": .}\n"
                + "sources: [.]\nentries: [main]\n"
                + "runtime: {security: trusted-server, thread: caller}\n";
    }
}
