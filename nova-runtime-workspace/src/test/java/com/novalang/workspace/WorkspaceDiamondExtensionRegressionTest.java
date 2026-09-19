package com.novalang.workspace;

import com.novalang.runtime.SchedulerHolder;
import com.novalang.runtime.interpreter.Interpreter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** 同一扩展经直接和间接依赖汇合时，只应生成一个合法入口。 */
class WorkspaceDiamondExtensionRegressionTest {
    @TempDir
    Path directory;

    @BeforeEach
    void installScheduler() {
        SchedulerHolder.set(WorkspaceTestSupport.directScheduler());
    }

    @AfterEach
    void clearScheduler() {
        Interpreter.resetGlobalSchedulerState();
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void shouldLoadExtensionImportedDirectlyAndThroughAnotherModule(boolean directCall) throws Exception {
        WorkspaceTestSupport.write(directory, "core.nova",
                "import java java.io.File\n"
                        + "import java java.lang.StringBuilder\n"
                        + "fun File.getEntityManager(): StringBuilder { return StringBuilder(this.getPath()) }\n"
                        + "val File.displayLabel: String get() = this.getPath()\n");
        WorkspaceTestSupport.write(directory, "helper.nova",
                "import \"@/core\"\n"
                        + "fun helper(): String { return File(\"helper\").getEntityManager().toString() }\n");
        WorkspaceTestSupport.write(directory, "entry.nova",
                "import \"@/core\"\n"
                        + "import \"@/helper\"\n"
                        + (directCall
                        ? "fun execute(): String { return File(\"entry\").getEntityManager().toString() + File(\"label\").displayLabel + helper() }\n"
                        : "fun execute(): String { return helper() }\n"));
        WorkspaceTestSupport.write(directory, "other.nova",
                "import \"@/helper\"\nfun execute(): String { return File(\"other\").getEntityManager().toString() + File(\"label\").displayLabel + helper() }\n");
        WorkspaceTestSupport.write(directory, "core-only.nova",
                "import \"@/core\"\nfun execute(): String { return File(\"core\").getEntityManager().toString() }\n");
        Path config = WorkspaceTestSupport.writeConfig(directory, "caller", "  - \"entry.nova\"\n  - \"other.nova\"\n  - \"core-only.nova\"\n");
        RuntimeWorkspace workspace = new RuntimeWorkspace(config, nova -> { });
        try {
            workspace.load();
            assertEquals(directCall ? "entrylabelhelper" : "helper", workspace.invoke("entry.nova", "execute",
                    Collections.<String, Object>emptyMap(), null));
            assertEquals("otherlabelhelper", workspace.invoke("other.nova", "execute",
                    Collections.<String, Object>emptyMap(), null));
            assertEquals("core", workspace.invoke("core-only.nova", "execute",
                    Collections.<String, Object>emptyMap(), null));
        } finally {
            workspace.dispose();
        }
    }
}
