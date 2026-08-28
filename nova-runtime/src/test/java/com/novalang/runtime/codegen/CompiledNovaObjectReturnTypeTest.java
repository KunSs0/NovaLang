package com.novalang.runtime.codegen;

import com.novalang.ir.NovaIrCompiler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Nova object 动态返回类型")
class CompiledNovaObjectReturnTypeTest {

    @Test
    @DisplayName("显式 Any 的 object 方法返回集合时不得按 object 类型强制转换")
    void explicitAnyObjectMethodShouldKeepRuntimeReturnType() throws Exception {
        NovaIrCompiler compiler = new NovaIrCompiler();
        java.util.Map<String, Class<?>> loaded = compiler.compileAndLoad(
                "object Routes {\n"
                        + "    fun rotate(): Any {\n"
                        + "        val result = mutableListOf()\n"
                        + "        result.add(\"north\")\n"
                        + "        return result\n"
                        + "    }\n"
                        + "}\n"
                        + "object Test {\n"
                        + "    fun run(): Any {\n"
                        + "        return Routes.rotate().size()\n"
                        + "    }\n"
                        + "}\n",
                "object-any-return.nova");

        Class<?> testClass = loaded.get("Test");
        assertNotNull(testClass);
        Object instance = testClass.getField("INSTANCE").get(null);
        assertEquals(1, testClass.getDeclaredMethod("run").invoke(instance));
    }
}
