package com.novalang.workspace;

import java.nio.file.Path;

/**
 * Workspace 编译器接收的统一源码单元。
 *
 * <p>物理 Nova 文件和由 YAML 等业务配置生成的虚拟源码都通过该类型进入模块图，
 * 从而保留原始文件、业务路径及行号映射。</p>
 */
public final class SourceUnit {

    private final String moduleId;
    private final String sourceText;
    private final Path originFile;
    private final String originPath;
    private final int originLine;
    private final int generatedLineOffset;
    private final Path physicalFile;

    /**
     * 创建源码单元。
     *
     * @param moduleId Workspace 内唯一模块标识
     * @param sourceText UTF-8 Nova 源码
     * @param originFile 原始文件；纯虚拟源允许为 {@code null}
     * @param originPath 原始文件内的业务路径，例如 YAML key
     * @param originLine 原始内容起始行，最小为 1
     * @param generatedLineOffset 生成源码前置行数，最小为 0
     * @param physicalFile 实际 Nova 文件路径；虚拟源为 {@code null}
     */
    public SourceUnit(String moduleId,
                      String sourceText,
                      Path originFile,
                      String originPath,
                      int originLine,
                      int generatedLineOffset,
                      Path physicalFile) {
        if (moduleId == null || moduleId.trim().isEmpty()) {
            throw new IllegalArgumentException("moduleId must not be blank");
        }
        if (sourceText == null) {
            throw new IllegalArgumentException("sourceText must not be null");
        }
        if (originLine < 1) {
            throw new IllegalArgumentException("originLine must be greater than or equal to 1");
        }
        if (generatedLineOffset < 0) {
            throw new IllegalArgumentException("generatedLineOffset must not be negative");
        }
        this.moduleId = moduleId;
        this.sourceText = sourceText;
        this.originFile = originFile == null ? null : originFile.toAbsolutePath().normalize();
        this.originPath = originPath;
        this.originLine = originLine;
        this.generatedLineOffset = generatedLineOffset;
        this.physicalFile = physicalFile == null ? null : physicalFile.toAbsolutePath().normalize();
    }

    /**
     * 创建物理 Nova 文件对应的源码单元。
     *
     * @param moduleId Workspace 模块标识
     * @param sourceText 文件内容
     * @param physicalFile 文件绝对路径
     * @return 文件源码单元
     */
    public static SourceUnit physical(String moduleId, String sourceText, Path physicalFile) {
        return new SourceUnit(moduleId, sourceText, physicalFile, null, 1, 0, physicalFile);
    }

    /** @return Workspace 模块标识 */
    public String getModuleId() {
        return moduleId;
    }

    /** @return 原始 Nova 源码 */
    public String getSourceText() {
        return sourceText;
    }

    /** @return 原始文件，可能为 {@code null} */
    public Path getOriginFile() {
        return originFile;
    }

    /** @return 原始文件中的业务路径，可能为 {@code null} */
    public String getOriginPath() {
        return originPath;
    }

    /** @return 原始内容起始行 */
    public int getOriginLine() {
        return originLine;
    }

    /** @return 生成源码前置行数 */
    public int getGeneratedLineOffset() {
        return generatedLineOffset;
    }

    /** @return 物理 Nova 文件，虚拟源返回 {@code null} */
    public Path getPhysicalFile() {
        return physicalFile;
    }

    /**
     * 将生成源码行号映射回原始文件行号。
     *
     * @param generatedLine 编译器报告的生成源码行号
     * @return 原始文件行号
     */
    public int mapGeneratedLine(int generatedLine) {
        if (generatedLine < 1) {
            throw new IllegalArgumentException("generatedLine must be greater than or equal to 1");
        }
        int contentLine = generatedLine - generatedLineOffset;
        if (contentLine < 1) {
            contentLine = 1;
        }
        return originLine + contentLine - 1;
    }

    /**
     * 生成人类可读的原始来源位置。
     *
     * @param generatedLine 生成源码行号
     * @return 文件、业务路径和原始行号组合
     */
    public String describeOrigin(int generatedLine) {
        String file = originFile == null ? moduleId : originFile.toString();
        StringBuilder description = new StringBuilder(file);
        if (originPath != null && !originPath.isEmpty()) {
            description.append(" [").append(originPath).append(']');
        }
        description.append(':').append(mapGeneratedLine(generatedLine));
        return description.toString();
    }
}
