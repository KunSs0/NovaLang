package com.novalang.compiler.ast.expr;

import com.novalang.compiler.ast.AstVisitor;
import com.novalang.compiler.ast.SourceLocation;
import com.novalang.compiler.ast.type.TypeRef;

import java.util.List;

/**
 * 级联表达式（~. 操作符）。
 *
 * <pre>
 *   target ~.method(args) ~.method2(args) ...
 * </pre>
 *
 * 语义：依次调用 target 上的方法（副作用），整个表达式返回 target。
 */
public class CascadeExpr extends Expression {
    private final Expression target;
    private final List<CascadeCall> calls;

    public CascadeExpr(SourceLocation location, Expression target, List<CascadeCall> calls) {
        super(location);
        this.target = target;
        this.calls = calls;
    }

    public Expression getTarget() {
        return target;
    }

    public List<CascadeCall> getCalls() {
        return calls;
    }

    @Override
    public <R, C> R accept(AstVisitor<R, C> visitor, C context) {
        return visitor.visitCascadeExpr(this, context);
    }

    /**
     * 级联链中的单次调用。
     */
    public static final class CascadeCall {
        private final String methodName;
        private final List<TypeRef> typeArgs;
        private final List<CallExpr.Argument> args;
        private final LambdaExpr trailingLambda;
        private final SourceLocation location;

        public CascadeCall(SourceLocation location, String methodName,
                           List<TypeRef> typeArgs, List<CallExpr.Argument> args,
                           LambdaExpr trailingLambda) {
            this.location = location;
            this.methodName = methodName;
            this.typeArgs = typeArgs;
            this.args = args;
            this.trailingLambda = trailingLambda;
        }

        public String getMethodName() { return methodName; }
        public List<TypeRef> getTypeArgs() { return typeArgs; }
        public List<CallExpr.Argument> getArgs() { return args; }
        public LambdaExpr getTrailingLambda() { return trailingLambda; }
        public SourceLocation getLocation() { return location; }
        public boolean hasMethodCall() { return args != null || trailingLambda != null; }
    }
}
