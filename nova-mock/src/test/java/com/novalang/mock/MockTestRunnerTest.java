package com.novalang.mock;

import com.novalang.runtime.Nova;
import com.novalang.runtime.NovaScheduler;
import com.novalang.runtime.SchedulerHolder;
import com.novalang.runtime.interpreter.Interpreter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** 验证单文件 Workspace、通用断言和失败汇总。 */
class MockTestRunnerTest {

    @TempDir
    Path temporaryDirectory;

    @BeforeEach
    void installScheduler() {
        SchedulerHolder.set(new DirectScheduler());
    }

    @AfterEach
    void clearScheduler() {
        Interpreter.resetGlobalSchedulerState();
    }

    @Test
    void shouldRunOneMockFileInIsolatedWorkspace() throws Exception {
        Path file = temporaryDirectory.resolve("sample.mock.nova");
        Files.write(file, ("fun test() {\n"
                + "  mockSet(\"input\", 2)\n"
                + "  assertEquals(2.0, mockValue(\"input\"), \"numeric mock value\")\n"
                + "  val random = randomInt(2, 3)\n"
                + "  assertTrue(random >= 2 && random <= 3, \"stdlib randomInt\")\n"
                + "}\n").getBytes(StandardCharsets.UTF_8));
        MockTestReport report = new MockTestRunner().run(
                file, Collections.<Path>emptyList(), Collections.<String, Path>emptyMap(),
                Collections.<String, Object>emptyMap(), new TestHost(), null);
        assertEquals(1, report.getTotal());
        assertEquals(1, report.getPassed(), report.getCases().get(0).getError());
        assertEquals(2, report.getCases().get(0).getAssertions());
    }

    @Test
    void shouldReportAssertionFailure() throws Exception {
        Path file = writeMock("failure.mock.nova",
                "fun test() { assertTrue(false, \"expected failure\") }\n");

        MockTestReport report = run(file, new TestHost(), Collections.<String, Path>emptyMap());

        assertEquals(1, report.getFailed());
        assertTrue(report.getCases().get(0).getError().contains("expected failure"));
    }

    @Test
    void shouldIsolateBindingsBetweenFiles() throws Exception {
        writeMock("a.mock.nova", "fun test() { mockSet(\"input\", 7) }\n");
        writeMock("b.mock.nova",
                "fun test() { assertTrue(mockValue(\"input\") == null, \"binding leaked\") }\n");

        MockTestReport report = run(
                temporaryDirectory, new TestHost(), Collections.<String, Path>emptyMap());

        assertEquals(2, report.getPassed());
    }

    @Test
    void shouldTurnCleanupFailureIntoFailedReport() throws Exception {
        Path file = writeMock("cleanup.mock.nova", "fun test() { assertTrue(true, \"ok\") }\n");
        MockTestHost host = new TestHost() {
            @Override
            public void close() {
                throw new IllegalStateException("cleanup failed");
            }
        };

        MockTestReport report = run(file, host, Collections.<String, Path>emptyMap());

        assertEquals(1, report.getFailed());
        assertTrue(report.getCases().get(0).getError().contains("cleanup failed"));
    }

    @Test
    void shouldRejectReservedAliasOverride() throws Exception {
        Path file = writeMock("alias.mock.nova", "fun test() { assertTrue(true, \"ok\") }\n");
        Map<String, Path> aliases = new LinkedHashMap<String, Path>();
        aliases.put("@mock", temporaryDirectory);

        MockTestReport report = run(file, new TestHost(), aliases);

        assertEquals(1, report.getFailed());
        assertTrue(report.getCases().get(0).getError().contains("不得覆盖保留映射"));
    }

    @Test
    void shouldDiscoverBusinessWorkspaceAliasesFromServerConfigs() throws Exception {
        Path serverRoot = temporaryDirectory.resolve("server");
        Path businessRoot = serverRoot.resolve("plugins").resolve("SamplePlugin").resolve("script");
        Path testRoot = serverRoot.resolve("tests").resolve("nova");
        Files.createDirectories(serverRoot);
        Files.write(serverRoot.resolve("server.properties"), new byte[0]);
        Files.createDirectories(businessRoot);
        Files.createDirectories(serverRoot.resolve("plugins").resolve("NovaLang").resolve("libs"));
        Files.createDirectories(testRoot);
        Files.write(businessRoot.resolve("business.api.nova"),
                "fun businessValue(): Int { return 42 }\n".getBytes(StandardCharsets.UTF_8));
        Files.write(businessRoot.resolve("nova.config.yml"),
                ("version: 1\n"
                        + "name: sample\n"
                        + "aliases:\n"
                        + "  '@sample': .\n"
                        + "sources:\n"
                        + "  - .\n"
                        + "entries:\n"
                        + "  - '@sample/business.api'\n"
                        + "runtime:\n"
                        + "  security: trusted-server\n"
                        + "  thread: caller\n").getBytes(StandardCharsets.UTF_8));
        Path file = testRoot.resolve("business.mock.nova");
        Files.write(file,
                ("import \"@sample/business.api\"\n"
                        + "fun test() { assertEquals(42, businessValue(), \"business alias\") }\n")
                        .getBytes(StandardCharsets.UTF_8));

        MockTestReport report = run(file, new TestHost(), Collections.<String, Path>emptyMap());

        assertEquals(1, report.getPassed());
        assertEquals(1, report.getCases().get(0).getAssertions());
    }

    @Test
    void shouldNotCatchFatalJvmErrors() throws Exception {
        Path file = writeMock("fatal.mock.nova", "fun test() { assertTrue(true, \"ok\") }\n");
        MockTestHost host = new TestHost() {
            @Override
            public void installMockBindings(Nova nova, MockTestBindings bindings) {
                throw new OutOfMemoryError("fatal");
            }
        };

        assertThrows(OutOfMemoryError.class,
                () -> run(file, host, Collections.<String, Path>emptyMap()));
    }

    private Path writeMock(String name, String source) throws Exception {
        Path file = temporaryDirectory.resolve(name);
        Files.write(file, source.getBytes(StandardCharsets.UTF_8));
        return file;
    }

    private MockTestReport run(Path target,
                               MockTestHost host,
                               Map<String, Path> aliases) {
        return new MockTestRunner().run(
                target, Collections.<Path>emptyList(), aliases,
                Collections.<String, Object>emptyMap(), host, null);
    }

    private static class TestHost implements MockTestHost {
        @Override
        public void installMockBindings(Nova nova, MockTestBindings bindings) {
            bindings.install(nova);
        }
    }

    private static final class DirectScheduler implements NovaScheduler {
        @Override
        public Executor mainExecutor() { return Runnable::run; }
        @Override
        public Executor asyncExecutor() { return Runnable::run; }
        @Override
        public boolean isMainThread() { return true; }
        @Override
        public Cancellable scheduleLater(long delayMs, Runnable task) { throw new UnsupportedOperationException(); }
        @Override
        public Cancellable scheduleRepeat(long delayMs, long periodMs, Runnable task) { throw new UnsupportedOperationException(); }
    }
}
