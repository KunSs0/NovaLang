package com.novalang.workspace;

/**
 * 入口合并源码中的某一行映射到原始 SourceUnit 后的位置。
 */
public final class MappedSourceLocation {

    private final SourceUnit sourceUnit;
    private final int generatedLine;
    private final int moduleLine;

    /**
     * 创建不可变映射位置。
     *
     * @param sourceUnit 原始源码单元
     * @param generatedLine 入口合并源码行号
     * @param moduleLine 原始 SourceUnit 内行号
     */
    MappedSourceLocation(SourceUnit sourceUnit, int generatedLine, int moduleLine) {
        this.sourceUnit = sourceUnit;
        this.generatedLine = generatedLine;
        this.moduleLine = moduleLine;
    }

    /** @return 原始源码单元 */
    public SourceUnit getSourceUnit() {
        return sourceUnit;
    }

    /** @return 入口合并源码行号 */
    public int getGeneratedLine() {
        return generatedLine;
    }

    /** @return 原始 SourceUnit 内行号 */
    public int getModuleLine() {
        return moduleLine;
    }

    /** @return 最终映射后的原始文件行号 */
    public int getOriginLine() {
        return sourceUnit.mapGeneratedLine(moduleLine);
    }

    /**
     * 生成人类可读的原始位置。
     *
     * @return 文件、业务路径和行号组合
     */
    public String describe() {
        return sourceUnit.describeOrigin(moduleLine);
    }
}
