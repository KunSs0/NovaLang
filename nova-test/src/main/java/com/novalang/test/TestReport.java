package com.novalang.test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Nova 测试报告。 */
public final class TestReport {
    private final List<TestResult> results = new ArrayList<TestResult>();

    public void add(TestResult result) {
        if (result == null) {
            throw new IllegalArgumentException("result must not be null");
        }
        results.add(result);
    }

    public int getTotal() {
        return results.size();
    }

    public int getPassed() {
        int count = 0;
        for (TestResult result : results) {
            if (result.isPassed()) {
                count++;
            }
        }
        return count;
    }

    public int getFailed() {
        return getTotal() - getPassed();
    }

    public List<TestResult> getResults() {
        return Collections.unmodifiableList(results);
    }

    public void writeJson(Path output) throws IOException {
        StringBuilder json = new StringBuilder("{\"total\":").append(getTotal())
                .append(",\"passed\":").append(getPassed())
                .append(",\"failed\":").append(getFailed()).append(",\"cases\":[");
        for (int index = 0; index < results.size(); index++) {
            if (index > 0) {
                json.append(',');
            }
            TestResult result = results.get(index);
            json.append("{\"file\":\"").append(escape(result.getFile().toString()))
                    .append("\",\"passed\":").append(result.isPassed())
                    .append(",\"assertions\":").append(result.getAssertions())
                    .append(",\"error\":");
            if (result.getError() == null) {
                json.append("null");
            } else {
                json.append('"').append(escape(result.getError())).append('"');
            }
            json.append('}');
        }
        json.append("]}\n");
        Path parent = output.toAbsolutePath().normalize().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.write(output, json.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\r", "\\r").replace("\n", "\\n");
    }

    public static final class TestResult {
        private final Path file;
        private final boolean passed;
        private final int assertions;
        private final String error;

        public TestResult(Path file, boolean passed, int assertions, String error) {
            this.file = file;
            this.passed = passed;
            this.assertions = assertions;
            this.error = error;
        }

        public Path getFile() {
            return file;
        }

        public boolean isPassed() {
            return passed;
        }

        public int getAssertions() {
            return assertions;
        }

        public String getError() {
            return error;
        }
    }
}
