package com.novalang.runtime.compilation;

import com.novalang.runtime.CompiledComponentFactory;
import com.novalang.runtime.Nova;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 验证隔离组件作用域在首次实例化前执行模块初始化。
 *
 * <p>宿主预编译 UI 组件脚本时只拿到字节码，工厂通过
 * {@link BytecodeArtifactCache.BytecodeArtifact#prepareComponentFactory} 建立作用域；
 * 该路径没有外部初始化调用，必须由工厂自己补齐。</p>
 */
@DisplayName("隔离组件作用域初始化")
class IsolatedComponentInitializationTest {

    /**
     * 顶层常量加一次可变计数，覆盖取值与初始化次数两个断言。
     */
    private static final String SOURCE =
            "import java java.util.function.Supplier\n"
                    + "val KEY = \"abc\"\n"
                    + "var INIT_COUNT = 0\n"
                    + "INIT_COUNT = INIT_COUNT + 1\n"
                    + "class Component : Supplier {\n"
                    + "    fun get(): String { return KEY + \":\" + INIT_COUNT }\n"
                    + "}\n"
                    + "fun createComponent(): Supplier = Component()\n";

    @Test
    @DisplayName("作用域内组件读取顶层声明且初始化只执行一次")
    void isolatedScopeShouldInitializeTopLevelStateOnce(@TempDir Path directory) throws Exception {
        Path entry = directory.resolve("component.nova");
        Files.write(entry, SOURCE.getBytes(StandardCharsets.UTF_8));
        Nova nova = new Nova();
        Map<String, byte[]> bytecode =
                nova.compileToBytecodeArtifact(entry, new CompilationConfig.Builder().build());
        BytecodeArtifactCache.BytecodeArtifact artifact =
                new BytecodeArtifactCache.BytecodeArtifact(bytecode);
        IsolatedComponentFactory<Supplier> factory = artifact.prepareComponentFactory(
                nova, getClass().getClassLoader(), "createComponent", Supplier.class);
        CompiledComponentFactory<Supplier> scope = factory.createScope();
        assertEquals("abc:1", scope.create().get());
        assertEquals("abc:1", scope.create().get());
    }
}
