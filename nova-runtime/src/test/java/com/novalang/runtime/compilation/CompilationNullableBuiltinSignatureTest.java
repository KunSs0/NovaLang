package com.novalang.runtime.compilation;

import com.novalang.runtime.CompiledNova;
import com.novalang.runtime.Nova;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 验证内置引用类型的可空标记在跨编译模块的签名中保留。
 */
class CompilationNullableBuiltinSignatureTest {
    @TempDir
    Path root;

    @Test
    void nullableBuiltinParametersAndReturnsSurviveModuleImports() throws Exception {
        CompilationTestSupport.write(root, "api.nova",
                "object Api {\n"
                        + " fun text(value: String?): String? { return value }\n"
                        + " fun values(value: List<String>?): List<String>? { return value }\n"
                        + " fun any(value: Any?): Any? { return value }\n"
                        + "}\n");
        Path entry = CompilationTestSupport.write(root, "main.nova",
                "import \"./api\"\n"
                        + "fun result(): Boolean {\n"
                        + " val text: String? = Api.text(null)\n"
                        + " val values: List<String>? = Api.values(null)\n"
                        + " val any: Any? = Api.any(null)\n"
                        + " return text == null && values == null && any == null\n"
                        + "}\n");
        try (CompiledNova program = new Nova().compileToBytecode(entry)) {
            program.run();
            assertEquals(Boolean.TRUE, program.call("result"));
        }
    }
}
