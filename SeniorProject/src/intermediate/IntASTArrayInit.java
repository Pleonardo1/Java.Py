package intermediate;

import java.util.List;

public class IntASTArrayInit extends AbstractIntASTBranchNode implements IntASTExpression {
    public IntASTArrayInit(){super("");}

    @Override
    public void addChild(IntASTNode child) {
        if (child == null) {
            return;

        } else if (child instanceof IntASTArrayInit
            || child instanceof IntASTExpression) {
            // Array of arrays or a normal array
            child.setParent(this);
            super.children.add(child);

        } else {
            throw new IllegalArgumentException("IntASTArrayInit does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }

    // convenience methods

    public IntASTArrayInit getArrayInitializer(int i) {
        return getChild(i, IntASTArrayInit.class);
    }

    public List<IntASTArrayInit> getArrayInitializers() {
        return getChildren(IntASTArrayInit.class);
    }

    public IntASTExpression getExpression(int i) {
        return getChild(i, IntASTExpression.class);
    }

    public List<IntASTExpression> getExpressions() {
        return getChildren(IntASTExpression.class);
    }

    public IntASTExpression getExpressionNotArrayInit(int i) {
        return getChild(i, IntASTExpression.class, IntASTArrayInit.class);
    }

    public List<IntASTExpression> getExpressionsNotArrayInits() {
        return getChildren(IntASTExpression.class, IntASTArrayInit.class);
    }
}