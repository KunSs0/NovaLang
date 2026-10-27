package com.novalang.runtime.codegen;

import com.novalang.runtime.CompiledComponentFactory;
import com.novalang.runtime.CompiledNova;
import com.novalang.runtime.Nova;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 验证组件工厂在首次实例化前补齐模块初始化。
 *
 * <p>顶层声明由模块初始化写入运行时全局，组件工厂只绑定入口函数；
 * 缺少这一步时组件内读取顶层常量只能得到空值。</p>
 */
@DisplayName("组件工厂模块初始化")
class CompiledComponentTopLevelStateTest {

    /**
     * 顶层常量加一次可变计数，用于同时验证取值与初始化次数。
     */
    private static final String SOURCE =
            "import java java.util.function.Supplier\n" +
            "val KEY = \"abc\"\n" +
            "var INIT_COUNT = 0\n" +
            "INIT_COUNT = INIT_COUNT + 1\n" +
            "class Component : Supplier {\n" +
            "    fun get(): String { return KEY + \":\" + INIT_COUNT }\n" +
            "}\n" +
            "fun create(): Supplier = Component()\n";

    /**
     * 组件内必须读到初始化后的顶层值，且多次实例化不重复执行模块初始化。
     */
    @Test
    @DisplayName("组件可读取顶层声明且模块初始化只执行一次")
    void componentShouldSeeTopLevelStateInitializedOnce() {
        Nova nova = new Nova();
        CompiledNova program = nova.compileToBytecode(SOURCE, "component-top-level.nova");
        try {
            CompiledComponentFactory<Supplier> factory =
                    program.prepareComponentFactory("create", Supplier.class);
            assertEquals("abc:1", factory.create().get());
            assertEquals("abc:1", factory.create().get());
        } finally {
            program.close();
        }
    }
}
