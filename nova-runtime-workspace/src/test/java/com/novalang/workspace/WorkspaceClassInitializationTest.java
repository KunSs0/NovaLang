package com.novalang.workspace;

import com.novalang.runtime.NovaScheduler;
import com.novalang.runtime.SchedulerHolder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** 两个入口共享优化后的类，模拟宿主隔离调用进入公共库的构造链。 */
class WorkspaceClassInitializationTest {
    @TempDir
    Path root;

    @Test
    void sharedClassInitializesFieldsOnEveryConstructionAndReload() throws Exception {
        WorkspaceTestSupport.write(root, "lib/controller.nova",
                "class Controller(val id: String) {\n"
                        + " var active = true\n"
                        + " val values = mutableListOf<String>()\n"
                        + " init { values.add(id) }\n"
                        + " fun folded(): Int { return 1 + 2 }\n"
                        + " fun verify(): String {\n"
                        + "  if (!active) { error(\"inactive\") }\n"
                        + "  return values.get(0)\n"
                        + " }\n"
                        + "}\n");
        for (String entry : new String[]{"first", "second"}) {
            WorkspaceTestSupport.write(root, entry + ".nova",
                    "import \"@/lib/controller\"\n"
                            + "fun execute(): String { return Controller(\"" + entry + "\").verify() }\n");
        }
        Path config = WorkspaceTestSupport.writeConfig(root, "caller",
                "  - \"first.nova\"\n  - \"second.nova\"\n");
        NovaScheduler previous = SchedulerHolder.get();
        SchedulerHolder.set(WorkspaceTestSupport.directScheduler());
        RuntimeWorkspace workspace = new RuntimeWorkspace(config, nova -> { });
        try {
            for (int generation = 0; generation < 2; generation++) {
                workspace.load();
                for (String entry : new String[]{"first", "second", "first"}) {
                    assertEquals(entry, workspace.invoke(entry + ".nova", "execute", Collections.emptyMap(), null));
                }
                workspace.dispose();
                workspace = new RuntimeWorkspace(config, nova -> { });
            }
        } finally {
            workspace.dispose();
            if (previous == null) {
                SchedulerHolder.clear();
            } else {
                SchedulerHolder.set(previous);
            }
        }
    }
}
