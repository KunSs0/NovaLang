package com.novalang.workspace;

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
@DisplayName("Workspace 跨文件单例对象门面")
class WorkspaceSingletonObjectReferenceTest {

    @TempDir
    Path root;

    private NovaScheduler previousScheduler;

    @BeforeEach
    void installScheduler() {
        previousScheduler = SchedulerHolder.get();
        SchedulerHolder.set(WorkspaceTestSupport.directScheduler());
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
        WorkspaceTestSupport.write(root, "lib/api.nova",
                "object FirstService {\n"
                        + "    fun readValue(): String { return \"first-ok\" }\n"
                        + "}\n"
                        + "object SecondService {\n"
                        + "    fun readValue(): String { return \"second-ok\" }\n"
                        + "}\n");
        WorkspaceTestSupport.write(root, "lib/facade.nova",
                "import \"@/lib/api\"\n"
                        + "object ServiceRegistry {\n"
                        + "    val first = FirstService\n"
                        + "    val second = SecondService\n"
                        + "}\n");
        WorkspaceTestSupport.write(root, "entry.nova",
                "import \"@/lib/api\"\n"
                        + "import \"@/lib/facade\"\n"
                        + "fun execute(): String { return " + expression + " }\n");
        // 第二个入口让公共库进入共享编译组，覆盖导出单例的跨组类型元数据。
        WorkspaceTestSupport.write(root, "other.nova",
                "import \"@/lib/facade\"\n"
                        + "fun execute(): String { return ServiceRegistry.first.readValue() }\n");
        Path config = WorkspaceTestSupport.writeConfig(root, "caller",
                "  - \"entry.nova\"\n  - \"other.nova\"\n");
        RuntimeWorkspace workspace = new RuntimeWorkspace(config, nova -> { });
        try {
            workspace.load();
            assertEquals(expected, workspace.invoke(
                    "entry.nova", "execute", Collections.<String, Object>emptyMap(), null));
            assertEquals("first-ok", workspace.invoke(
                    "other.nova", "execute", Collections.<String, Object>emptyMap(), null));
        } finally {
            workspace.dispose();
        }
    }
}
