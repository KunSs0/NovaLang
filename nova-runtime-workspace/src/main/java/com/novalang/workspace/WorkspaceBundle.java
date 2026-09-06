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

    /** 为跨编译组调用保留脚本扩展签名，并链接到已经编译的静态实现。 */
    void exportExtensions(String packageName, WorkspaceCompilationExports exports, java.util.Map<String, Class<?>> classes) {
        Program program = parse(packageName);
        int index = 0;
        for (com.novalang.compiler.ast.decl.Declaration declaration : program.getDeclarations()) {
            if (declaration instanceof com.novalang.compiler.ast.decl.PropertyDecl) {
                com.novalang.compiler.ast.decl.PropertyDecl property =
                        (com.novalang.compiler.ast.decl.PropertyDecl) declaration;
                if (!property.isExtensionProperty()
                        || property.getModifiers().contains(com.novalang.compiler.ast.Modifier.PRIVATE)) {
                    continue;
                }
                Class<?> module = classes.get(packageName + ".$Module");
                if (module == null) {
                    continue;
                }
                for (java.lang.reflect.Method method : module.getDeclaredMethods()) {
                    if (!method.getName().startsWith("__extprop__")
                            || !method.getName().endsWith("__" + property.getName())) {
                        continue;
                    }
                    if (property.getReceiverType() instanceof com.novalang.compiler.ast.type.SimpleType) {
                        String receiver = ((com.novalang.compiler.ast.type.SimpleType) property.getReceiverType()).getName().getSimpleName();
                        if (!method.getParameterTypes()[0].getSimpleName().equals(receiver)) {
                            continue;
                        }
                    }
                    int signatureEnd = property.getGetter() != null
                            ? property.getGetter().getLocation().getOffset()
                            : property.getInitializer().getLocation().getOffset();
                    String header = source.substring(property.getLocation().getOffset(), signatureEnd).trim();
                    if (header.endsWith("=")) {
                        header = header.substring(0, header.length() - 1).trim();
                    }
                    String alias = "__workspace_property_" + packageName.replace('.', '_') + "_" + index++;
                    String link = "import static " + packageName + ".$Module." + method.getName() + " as " + alias
                            + "\n" + header + " get() = " + alias + "(this)";
                    exports.getExtensionDeclarations().add(link);
                }
                continue;
            }
            if (!(declaration instanceof com.novalang.compiler.ast.decl.FunDecl)) {
                continue;
            }
            com.novalang.compiler.ast.decl.FunDecl function =
                    (com.novalang.compiler.ast.decl.FunDecl) declaration;
            if (!function.isExtensionFunction() || function.getBody() == null
                    || function.getModifiers().contains(com.novalang.compiler.ast.Modifier.PRIVATE)) {
                continue;
            }
            String header = source.substring(function.getLocation().getOffset(),
                    function.getBody().getLocation().getOffset()).trim();
            if (header.endsWith("=")) {
                header = header.substring(0, header.length() - 1).trim();
            }
            String alias = "__workspace_extension_" + packageName.replace('.', '_') + "_" + index++;
            StringBuilder link = new StringBuilder("import static ");
            link.append(packageName).append(".$Module.").append(function.getName())
                    .append(" as ").append(alias).append('\n');
            link.append(header).append(" { return ").append(alias).append("(this");
            for (com.novalang.compiler.ast.decl.Parameter parameter : function.getParams()) {
                link.append(", ").append(parameter.getName());
            }
            link.append(") }");
            exports.getExtensionDeclarations().add(link.toString());
        }
    }

    /** @return 逐行 Source Map */
    WorkspaceSourceMap getSourceMap() {
        return sourceMap;
    }
}
