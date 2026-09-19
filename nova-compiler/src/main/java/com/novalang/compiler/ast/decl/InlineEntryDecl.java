package com.novalang.compiler.ast.decl;

import com.novalang.compiler.ast.SourceLocation;
import com.novalang.compiler.ast.stmt.Block;
import com.novalang.compiler.ast.type.TypeRef;
import java.util.Collections;

/** 行内源码合成的入口，保留独立的返回契约，后端仍按普通函数生成。 */
public final class InlineEntryDecl extends FunDecl {
    public InlineEntryDecl(SourceLocation location, String name, TypeRef returnType, Block body) {
        super(location, Collections.emptyList(), Collections.emptyList(), name,
                Collections.emptyList(), null, Collections.emptyList(), returnType,
                body, false, false, false);
    }
}
