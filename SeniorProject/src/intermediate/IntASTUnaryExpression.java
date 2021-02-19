package intermediate;

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
}
