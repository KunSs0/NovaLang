package com.novalang.mock;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** .mock.nova 执行报告。 */
public final class MockTestReport {
    private final List<TestCase> cases = new ArrayList<TestCase>();

    public void add(TestCase result) { cases.add(result); }
    public int getTotal() { return cases.size(); }
    public int getPassed() {
        int result = 0;
        for (TestCase testCase : cases) {
            if (testCase.isPassed()) { result++; }
        }
        return result;
    }
    public int getFailed() { return getTotal() - getPassed(); }
    public List<TestCase> getCases() { return Collections.unmodifiableList(cases); }

    public void writeJson(Path output) throws IOException {
        StringBuilder json = new StringBuilder("{\"total\":").append(getTotal())
                .append(",\"passed\":").append(getPassed())
                .append(",\"failed\":").append(getFailed()).append(",\"cases\":[");
        for (int index = 0; index < cases.size(); index++) {
            if (index > 0) { json.append(','); }
            TestCase result = cases.get(index);
            json.append("{\"file\":\"").append(escape(result.getFile().toString()))
                    .append("\",\"passed\":").append(result.isPassed())
                    .append(",\"assertions\":").append(result.getAssertions())
                    .append(",\"error\":");
            if (result.getError() == null) { json.append("null"); }
            else { json.append('"').append(escape(result.getError())).append('"'); }
            json.append('}');
        }
        json.append("]}\n");
        Path parent = output.toAbsolutePath().normalize().getParent();
        if (parent != null) { Files.createDirectories(parent); }
        Files.write(output, json.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\r", "\\r").replace("\n", "\\n");
    }

    public static final class TestCase {
        private final Path file;
        private final boolean passed;
        private final int assertions;
        private final String error;
        public TestCase(Path file, boolean passed, int assertions, String error) {
            this.file = file; this.passed = passed; this.assertions = assertions; this.error = error;
        }
        public Path getFile() { return file; }
        public boolean isPassed() { return passed; }
        public int getAssertions() { return assertions; }
        public String getError() { return error; }
    }
}
