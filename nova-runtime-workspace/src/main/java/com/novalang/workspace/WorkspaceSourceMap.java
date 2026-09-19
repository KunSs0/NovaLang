package com.novalang.workspace;

import com.novalang.compiler.parser.ParseException;
import com.novalang.runtime.interpreter.NovaRuntimeException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 入口合并源码到物理 Nova 文件或 YAML 虚拟源的逐行映射。
 */
public final class WorkspaceSourceMap {

    private static final Pattern MESSAGE_LINE = Pattern.compile("(?:at line|line)\\s+(\\d+)");

    private final List<LineMapping> mappings;

    /**
     * 创建不可变 Source Map。
     *
     * @param mappings 以生成源码行顺序排列的行映射
     */
    WorkspaceSourceMap(List<LineMapping> mappings) {
        this.mappings = Collections.unmodifiableList(new ArrayList<LineMapping>(mappings));
    }

    /**
     * 映射入口合并源码行号。
     *
     * @param generatedLine 入口合并源码行号
     * @return 原始位置；注释等没有来源的行返回 {@code null}
     */
    public MappedSourceLocation mapLine(int generatedLine) {
        if (generatedLine < 1 || generatedLine > mappings.size()) {
            return null;
        }
        LineMapping mapping = mappings.get(generatedLine - 1);
        if (mapping.sourceUnit == null) {
            return null;
        }
        return new MappedSourceLocation(mapping.sourceUnit, generatedLine, mapping.moduleLine);
    }

    /**
     * 从编译或运行异常中提取生成行号并映射到原始来源。
     *
     * @param failure 编译或执行异常
     * @return 原始位置；无法提取有效行号时返回 {@code null}
     */
    public MappedSourceLocation locate(Throwable failure) {
        Throwable current = failure;
        int depth = 0;
        while (current != null && depth < 16) {
            int line = extractDirectLine(current);
            MappedSourceLocation location = mapLine(line);
            if (location != null) {
                return location;
            }
            current = current.getCause();
            depth++;
        }
        return null;
    }

    /**
     * 在能够定位来源时创建带原始位置的 WorkspaceException。
     *
     * @param operation 英文操作描述
     * @param failure 原始异常
     * @return 包含原始位置的异常；无法定位时仍包含操作描述和原始原因
     */
    public WorkspaceException mapFailure(String operation, Throwable failure) {
        MappedSourceLocation location = locate(failure);
        if (location == null) {
            return new WorkspaceException(operation, failure);
        }
        return new WorkspaceException(operation + " at " + location.describe(), failure);
    }

    /** @return 合并源码总行数 */
    public int size() {
        return mappings.size();
    }

    /**
     * 从单层编译或运行异常中提取生成源码行号。
     *
     * @param failure 当前异常
     * @return 有效行号；无法提取时返回 {@code -1}
     */
    private int extractDirectLine(Throwable failure) {
        if (failure instanceof ParseException) {
            ParseException parseException = (ParseException) failure;
            // 未闭合括号会把真正打开位置写入消息，而 token 指向文件尾；优先读取消息位置。
            int messageLine = extractMessageLine(parseException.getMessage());
            if (messageLine > 0) {
                return messageLine;
            }
            if (parseException.getToken() != null) {
                return parseException.getToken().getLine();
            }
        }
        if (failure instanceof NovaRuntimeException) {
            NovaRuntimeException runtimeException = (NovaRuntimeException) failure;
            if (runtimeException.getLocation() != null) {
                return runtimeException.getLocation().getLine();
            }
        }

        for (StackTraceElement element : failure.getStackTrace()) {
            if (element.getLineNumber() > 0 && element.getClassName().contains("$Module")) {
                return element.getLineNumber();
            }
        }

        return extractMessageLine(failure.getMessage());
    }

    /**
     * 从异常消息的标准 line 片段中提取行号。
     *
     * @param message 异常消息
     * @return 有效行号；无法提取时返回 {@code -1}
     */
    private int extractMessageLine(String message) {
        if (message == null) {
            return -1;
        }
        Matcher matcher = MESSAGE_LINE.matcher(message);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return -1;
    }

    /**
     * 一行合并源码的内部来源记录。
     */
    static final class LineMapping {
        private final SourceUnit sourceUnit;
        private final int moduleLine;

        LineMapping(SourceUnit sourceUnit, int moduleLine) {
            this.sourceUnit = sourceUnit;
            this.moduleLine = moduleLine;
        }
    }
}
