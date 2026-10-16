package com.novalang.runtime.compilation;

import com.novalang.runtime.NovaScheduler;
import com.novalang.runtime.SchedulerHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.nio.file.Path;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** 使用合成模块复现跨文件单例门面调用，不依赖外部插件或业务资源。 */
@DisplayName("Compilation 跨文件单例对象门面")
class CompilationSingletonObjectReferenceTest {

    @TempDir
    Path root;

    private NovaScheduler previousScheduler;

    @BeforeEach
    void installScheduler() {
        previousScheduler = SchedulerHolder.get();
        SchedulerHolder.set(CompilationTestSupport.directScheduler());
    }

    @AfterEach
    void restoreScheduler() {
        if (previousScheduler == null) {
            SchedulerHolder.clear();
        } else {
            SchedulerHolder.set(previousScheduler);
        }
    }

    @ParameterizedTest(name = "{0} 应返回 {1}")
    @CsvSource({
            "FirstService.readValue(), first-ok",
            "SecondService.readValue(), second-ok",
            "ServiceRegistry.first.readValue(), first-ok",
            "ServiceRegistry.second.readValue(), second-ok"
    })
    void shouldCallSingletonAcrossModules(String expression, String expected) throws Exception {
        CompilationTestSupport.write(root, "lib/api.nova",
                "object FirstService {\n"
                        + "    fun readValue(): String { return \"first-ok\" }\n"
                        + "}\n"
                        + "object SecondService {\n"
                        + "    fun readValue(): String { return \"second-ok\" }\n"
                        + "}\n");
        CompilationTestSupport.write(root, "lib/facade.nova",
                "import \"@/lib/api\"\n"
                        + "object ServiceRegistry {\n"
                        + "    val first = FirstService\n"
                        + "    val second = SecondService\n"
                        + "}\n");
        CompilationTestSupport.write(root, "entry.nova",
                "import \"@/lib/api\"\n"
                        + "import \"@/lib/facade\"\n"
                        + "fun entryExecute(): String { return " + expression + " }\n");
        // 主文件引用的另一个模块共同使用公共库，覆盖导出单例的跨模块类型元数据。
        CompilationTestSupport.write(root, "other.nova",
                "import \"@/lib/facade\"\n"
                        + "fun otherExecute(): String { return ServiceRegistry.first.readValue() }\n");
        Path config = CompilationTestSupport.writeMain(root, "entry.nova", "other.nova");
        CompilationHarness compilation = new CompilationHarness(config, nova -> { });
        try {
            compilation.compileAndRun();
            assertEquals(expected, compilation.call("entryExecute", Collections.<String, Object>emptyMap()));
            assertEquals("first-ok", compilation.call("otherExecute", Collections.<String, Object>emptyMap()));
        } finally {
            compilation.close();
        }
    }
}
