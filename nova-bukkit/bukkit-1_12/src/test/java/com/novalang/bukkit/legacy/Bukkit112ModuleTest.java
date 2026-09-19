package com.novalang.bukkit.legacy;

import com.novalang.runtime.host.JavaTypes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/** 验证 Bukkit 1.12 版本扩展可以完整注册到 Core 构建器。 */
public final class Bukkit112ModuleTest {

    /** 版本模块注册不得依赖运行中的 Bukkit 服务实例。 */
    @Test
    public void registersVersionExtensions() {
        Bukkit112Module module = new Bukkit112Module();
        JavaTypes.Builder builder = JavaTypes.builder();
        assertDoesNotThrow(() -> module.register(builder));
    }
}
