package com.novalang.workspace;

import com.novalang.compiler.ast.decl.Program;
import com.novalang.compiler.lexer.Lexer;
import com.novalang.compiler.parser.Parser;
import java.util.ArrayList;
import java.util.List;

/**
 * 单个入口的依赖闭包合并源码及其 Source Map。
 */
final class WorkspaceBundle {

    private final List<InlinePart> inlineParts;
    private final String source;
    private final WorkspaceSourceMap sourceMap;

    /**
     * 创建入口编译包。
     *
     * @param source 已移除字符串 import 的完整源码
     * @param sourceMap 逐行来源映射
     */
    WorkspaceBundle(String source, WorkspaceSourceMap sourceMap, List<InlinePart> inlineParts) {
        this.inlineParts = new ArrayList<InlinePart>(inlineParts);
        this.source = source;
        this.sourceMap = sourceMap;
    }

    /** 保longsword: |-
  import "creator.dungeon"

  val ctx = currentContext()
  val player = currentPlayer()

  if (player == null) {
      return false
  }

  if (!CreatorDungeon.isPlayerInDungeon(ctx, player)) {
      player.sendMessage("§c你当前不在这个训练副本中。")
      return false
  }

  // 后续发放武器、推进目标的逻辑……

  return true */
    Program parse(String fileName) {
        String[] lines = source.split("\\n", -1);
        String[] ordinary = lines.clone();
        for (InlinePart part : inlineParts) {
            for (int index = part.start; index < part.end; index++) {
                ordinary[index] = "";
            }
        }
        String moduleText = String.join("\n", ordinary);
        Parser parser = new Parser(
                new Lexer(moduleText, fileName), fileName);
        Program program = parser.parse();
        for (InlinePart part : inlineParts) {
            StringBuilder text = new StringBuilder();
            for (int index = 0; index < part.end; index++) {
                if (index >= part.start) {
                    text.append(lines[index]);
                }
                text.append('\n');
            }
            Parser inlineParser = new Parser(
                    new Lexer(text.toString(), fileName), fileName);
            Program inline = inlineParser.parseInline(
                    part.source.getInlineEntryName(), part.source.getInlineReturnType());
            program.getImports().addAll(inline.getImports());
            program.getDeclarations().addAll(inline.getDeclarations());
        }
        return program;
    }

    static final class InlinePart {
        final SourceUnit source;
        final int start;
        final int end;

        InlinePart(SourceUnit source, int start, int end) {
            this.source = source;
            this.start = start;
            this.end = end;
        }
    }

    /** 编译语义与源码共同参与缓存键，普通模块不能冒充同文本的行内入口。 */
    String getCacheSource() {
        StringBuilder key = new StringBuilder("workspace-ast-inline-v1\n");
        for (InlinePart part : inlineParts) {
            String name = part.source.getInlineEntryName();
            String type = part.source.getInlineReturnType();
            key.append(part.start).append(':').append(part.end).append(':')
                    .append(name.length()).append(':').append(name)
                    .append(type.length()).append(':').append(type).append('\n');
        }
        return key.append("source:").append(source).toString();
    }

    /** @return 用于编译组诊断和检查的源码 */
    String getSource() {
        return source;
    }

    /** @return 逐行 Source Map */
    WorkspaceSourceMap getSourceMap() {
        return sourceMap;
    }
}
