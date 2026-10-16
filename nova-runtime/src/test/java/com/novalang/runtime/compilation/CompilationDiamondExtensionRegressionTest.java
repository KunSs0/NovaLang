package com.novalang.runtime.compilation;

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
class CompilationDiamondExtensionRegressionTest {
    @TempDir
    Path directory;

    @BeforeEach
    void installScheduler() {
        SchedulerHolder.set(CompilationTestSupport.directScheduler());
    }

    @AfterEach
    void clearScheduler() {
        Interpreter.resetGlobalSchedulerState();
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void shouldLoadExtensionImportedDirectlyAndThroughAnotherModule(boolean directCall) throws Exception {
        CompilationTestSupport.write(directory, "core.nova",
                "import java java.io.File\n"
                        + "import java java.lang.StringBuilder\n"
                        + "fun File.getEntityManager(): StringBuilder { return StringBuilder(this.getPath()) }\n"
                        + "val File.displayLabel: String get() = this.getPath()\n");
        CompilationTestSupport.write(directory, "helper.nova",
                "import \"@/core\"\n"
                        + "fun helper(): String { return File(\"helper\").getEntityManager().toString() }\n");
        CompilationTestSupport.write(directory, "entry.nova",
                "import \"@/core\"\n"
                        + "import \"@/helper\"\n"
                        + (directCall
                        ? "fun entryExecute(): String { return File(\"entry\").getEntityManager().toString() + File(\"label\").displayLabel + helper() }\n"
                        : "fun entryExecute(): String { return helper() }\n"));
        CompilationTestSupport.write(directory, "other.nova",
                "import \"@/helper\"\nfun otherExecute(): String { return File(\"other\").getEntityManager().toString() + File(\"label\").displayLabel + helper() }\n");
        CompilationTestSupport.write(directory, "core-only.nova",
                "import \"@/core\"\nfun core_onlyExecute(): String { return File(\"core\").getEntityManager().toString() }\n");
        Path config = CompilationTestSupport.writeMain(directory, "entry.nova", "other.nova", "core-only.nova");
        CompilationHarness compilation = new CompilationHarness(config, nova -> { });
        try {
            compilation.compileAndRun();
            assertEquals(directCall ? "entrylabelhelper" : "helper", compilation.call("entryExecute", Collections.<String, Object>emptyMap()));
            assertEquals("otherlabelhelper", compilation.call("otherExecute", Collections.<String, Object>emptyMap()));
            assertEquals("core", compilation.call("core_onlyExecute", Collections.<String, Object>emptyMap()));
        } finally {
            compilation.close();
        }
    }
}
