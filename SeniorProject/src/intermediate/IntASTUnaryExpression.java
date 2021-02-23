package intermediate;

import java.util.List;

public class IntASTUnaryExpression extends AbstractIntASTBranchNode implements IntASTExpression {
    public IntASTUnaryExpression() {
        super("");
    }

    @Override
    public void addChild(IntASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof IntASTExpression) {
            // can only have two children: one expression and one operator
            if (super.children.size() < 2) {
                if (child instanceof IntASTOperator) {
                    // ensure only 1 operator exists
                    if (super.children.isEmpty() ||
                            !(super.children.get(0) instanceof IntASTOperator)) {
                        child.setParent(this);
                        super.children.add(child);
                        return;
                    }
                } else {
                    // ensure only one expression exists
                    if (super.children.isEmpty() ||
                            super.children.get(0) instanceof IntASTOperator) {
                        child.setParent(this);
                        super.children.add(child);
                        return;
                    }
                }
            }
            // child violates condition of having only 1 expressions and 1 operator
            throw new IllegalArgumentException("A unary expression can only have 2 children: 1 expression and 1 operator");
        } else {
            throw new IllegalArgumentException("IntASTUnaryExpression does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
    
    public IntASTExpression getExpression(int i) {
        return getChild(i, IntASTExpression.class);
    }
    
    public List<IntASTExpression> getExpression() {
        return getChildren(IntASTExpression.class);
    }
    
    public IntASTOperator getOperator() {
        return getChild(0, IntASTOperator.class);
    }
    
    public IntASTExpression getExpressionNotOperator() {
        return getChild(0, IntASTExpression.class, IntASTOperator.class);
    }
}
