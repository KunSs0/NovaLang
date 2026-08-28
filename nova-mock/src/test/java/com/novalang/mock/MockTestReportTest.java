package com.novalang.mock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** 通用 mock 报告的 UTF-8 和汇总契约测试。 */
class MockTestReportTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void shouldSummarizeAndWriteJson() throws Exception {
        MockTestReport report = new MockTestReport();
        report.add(new MockTestReport.TestCase(
                temporaryDirectory.resolve("pass.mock.nova"), true, 2, null));
        report.add(new MockTestReport.TestCase(
                temporaryDirectory.resolve("fail.mock.nova"), false, 1, "断言失败"));

        Path output = temporaryDirectory.resolve("report.json");
        report.writeJson(output);
        String json = new String(Files.readAllBytes(output), StandardCharsets.UTF_8);

        assertEquals(2, report.getTotal());
        assertEquals(1, report.getPassed());
        assertEquals(1, report.getFailed());
        assertTrue(json.contains("\"failed\":1"));
        assertTrue(json.contains("断言失败"));
    }
}
