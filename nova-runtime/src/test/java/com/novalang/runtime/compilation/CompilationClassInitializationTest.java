package com.novalang.runtime.compilation;

import com.novalang.runtime.NovaScheduler;
import com.novalang.runtime.SchedulerHolder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** 主文件引用的两个模块共享优化后的类，模拟宿主隔离调用进入公共库的构造链。 */
class CompilationClassInitializationTest {
    @TempDir
    Path root;

    @Test
    void sharedClassInitializesFieldsOnEveryConstructionAndReload() throws Exception {
        CompilationTestSupport.write(root, "lib/controller.nova",
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
            CompilationTestSupport.write(root, entry + ".nova",
                    "import \"@/lib/controller\"\n"
                            + "fun " + entry + "Execute(): String { return Controller(\"" + entry + "\").verify() }\n");
        }
        Path config = CompilationTestSupport.writeMain(root, "first.nova", "second.nova");
        NovaScheduler previous = SchedulerHolder.get();
        SchedulerHolder.set(CompilationTestSupport.directScheduler());
        CompilationHarness compilation = new CompilationHarness(config, nova -> { });
        try {
            for (int generation = 0; generation < 2; generation++) {
                compilation.compileAndRun();
                for (String entry : new String[]{"first", "second", "first"}) {
                    assertEquals(entry, compilation.call(entry + "Execute", Collections.emptyMap()));
                }
                compilation.close();
                compilation = new CompilationHarness(config, nova -> { });
            }
        } finally {
            compilation.close();
            if (previous == null) {
                SchedulerHolder.clear();
            } else {
                SchedulerHolder.set(previous);
            }
        }
    }
}
