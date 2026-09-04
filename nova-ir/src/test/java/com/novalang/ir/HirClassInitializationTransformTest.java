package com.novalang.ir;

import com.novalang.compiler.ast.decl.Program;
import com.novalang.compiler.ast.expr.Literal;
import com.novalang.compiler.lexer.Lexer;
import com.novalang.compiler.parser.Parser;
import com.novalang.ir.hir.HirTransformer;
import com.novalang.ir.hir.decl.HirClass;
import com.novalang.ir.hir.decl.HirModule;
import com.novalang.ir.lowering.AstToHirLowering;
import com.novalang.ir.pass.hir.HirConstantFolding;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HirClassInitializationTransformTest {
    private HirClass lower(String source) {
        Program program = new Parser(new Lexer(source), "class-init.nova").parse();
        HirModule module = new AstToHirLowering().lower(program);
        return (HirClass) module.getDeclarations().get(0);
    }

    @Test
    void unchangedClassRetainsIdentity() {
        HirClass original = lower("class Sample(val id: String) { val enabled = true; init { println(id) } }");
        assertSame(original, new HirTransformer().transform(original));
    }

    @Test
    void foldedFieldsAreSharedWithOrderedInitializers() {
        HirClass original = lower("class Sample(val id: String) { val count = 1 + 2; init { println(id) } }");
        HirClass transformed = (HirClass) new HirConstantFolding().transform(original);
        assertNotSame(original, transformed);
        assertEquals(2, transformed.getInstanceInitializers().size());
        assertSame(transformed.getFields().get(1), transformed.getInstanceInitializers().get(0));
        assertEquals(3, ((Literal) transformed.getFields().get(1).getInitializer()).getValue());
        assertSame(original.getInstanceInitializers().get(1), transformed.getInstanceInitializers().get(1));
    }

    @Test
    void changingOnlyInitBlockRebuildsClass() {
        HirClass original = lower("class Sample(val id: String) { init { println(1 + 2) } }");
        HirClass transformed = (HirClass) new HirConstantFolding().transform(original);
        assertNotSame(original, transformed);
        assertEquals(1, transformed.getInstanceInitializers().size());
        assertNotSame(original.getInstanceInitializers().get(0), transformed.getInstanceInitializers().get(0));
        assertSame(transformed, new HirConstantFolding().transform(transformed));
    }
}
