package com.novalang.test;

import com.novalang.runtime.SchedulerHolder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

/** 项目测试入口通过编译元数据生成普通 Nova 程序，并隔离生产入口和各测试。 */
class ProjectTestRunnerTest {
    @TempDir
    Path root;

    /** 验证无需生产入口即可发现并编译测试文件，同时释放测试调度器。 */
    @Test
    void discoversTestsWithoutCompilingProductionEntry() throws Exception {
        writeConfig();
        write("pass.mock.nova", "fun test() { assertEquals(7, 3 + 4, \"数字\") }\n");
        TestReport report = new ProjectTestRunner().run(root, null);
        assertEquals(1, report.getTotal());
        assertEquals(1, report.getPassed(), report.getResults().get(0).getError());
        assertNull(SchedulerHolder.get());
    }

    /** 一份测试失败仍继续执行后续测试，程序资源和调度器不泄漏。 */
    @Test
    void reportsFailureAndContinuesOtherTests() throws Exception {
        writeConfig();
        write("a-fail.mock.nova", "fun test() { assertTrue(false, \"expected failure\") }\n");
        write("b-pass.mock.nova", "fun test() { assertTrue(true, \"pass\") }\n");
        TestReport report = new ProjectTestRunner().run(root, null);
        assertEquals(2, report.getTotal());
        assertEquals(1, report.getPassed());
        assertEquals(1, report.getFailed());
        assertNull(SchedulerHolder.get());
    }

    /** 只提供编译元数据，测试文件由执行器直接作为唯一入口。 */
    private void writeConfig() throws Exception {
        write("nova.config.yml", "version: 1\nname: tests\naliases: {\"@\": .}\n"
                + "test: {}\n");
    }

    /** 以 UTF-8 写入测试源码或编译元数据。 */
    private void write(String name, String content) throws Exception {
        Files.write(root.resolve(name), content.getBytes(StandardCharsets.UTF_8));
    }
}
