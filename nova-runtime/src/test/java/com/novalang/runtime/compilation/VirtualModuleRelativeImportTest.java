package com.novalang.runtime.compilation;

import com.novalang.runtime.CompiledNova;
import com.novalang.runtime.Nova;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 字符串编译路径下的相对导入解析。
 *
 * <p>UI 脚本在宿主侧没有物理文件，只能把资源 ID 注册成虚拟模块；
 * 相对导入若能按入口 moduleId 推导，UI 组件就能和磁盘编译保持同一套写法。</p>
 */
@DisplayName("virtual module relative imports")
class VirtualModuleRelativeImportTest {

    /** 同一目录下的兄弟模块应能用 ./ 引用。 */
    @Test
    @DisplayName("compiled string import resolves sibling module")
    void compiledStringImportShouldResolveSiblingModule() {
        Nova nova = new Nova();
        nova.registerModule(
                "fightcoremod:ui/screen/demo/model.nova",
                "fun modelValue(): Int = 7\n");

        CompiledNova program = nova.compileToBytecode(
                "import \"./model.nova\"\n" +
                        "fun entryValue(): Int = modelValue()\n",
                "fightcoremod:ui/screen/demo/entry.nova");

        assertEquals(7, ((Number) program.call("entryValue")).intValue());
        program.close();
    }

    /** 子目录里的模块应能用 ../ 回到上一级。 */
    @Test
    @DisplayName("compiled string import resolves parent module")
    void compiledStringImportShouldResolveParentModule() {
        Nova nova = new Nova();
        nova.registerModule(
                "fightcoremod:ui/screen/demo/shared/state.nova",
                "fun stateValue(): Int = 11\n");

        CompiledNova program = nova.compileToBytecode(
                "import \"../shared/state.nova\"\n" +
                        "fun pageValue(): Int = stateValue()\n",
                "fightcoremod:ui/screen/demo/page/entry.nova");

        assertEquals(11, ((Number) program.call("pageValue")).intValue());
        program.close();
    }
}
