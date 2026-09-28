package com.novalang.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestReportTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void shouldSummarizeAndWriteUtf8Json() throws Exception {
        TestReport report = new TestReport();
        report.add(new TestReport.TestResult(
                temporaryDirectory.resolve("pass.mock.nova"), true, 2, null));
        report.add(new TestReport.TestResult(
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
