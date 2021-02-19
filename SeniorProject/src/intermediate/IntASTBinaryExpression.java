package intermediate;

public class IntASTBinaryExpression extends AbstractIntASTBranchNode implements IntASTExpression {
    public IntASTBinaryExpression() {
        super("");
    }

    @Override
    public void addChild(IntASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof IntASTExpression) {
            // can only have two expressions, at index 0 and at index 2,
            // in addition to one operator at index 1
            if (super.children.size() < 3) {
                /*
                if (child instanceof IntASTOperator) {
                    // ensure only 1 operator exists, and that it
                    // comes at index 1
                    if (super.children.isEmpty() ||
                            (super.children.size() == 1 && !(super.children.get(0) instanceof IntASTOperator))) {
                        // either 0 children or 1 child that is not an operator
                        child.setParent(this);
                        super.children.add(child);
                        return;
                    } else if (super.children.size() == 2 && getChildCount(IntASTOperator.class) == 0) {
                        // 2 children, both of which are not operators
                        child.setParent(this);
                        // insert the operator in the middle
                        super.children.add(1, child);
                        return;
                    }
                } else {
                    // ensure only 2 expressions exist, and that they
                    // are at index 0 and 2
                    if (super.children.isEmpty() ||
                            (super.children.size() == 1 && !(super.children.get(0) instanceof IntASTOperator)) ||
                            (super.children.size() == 2 && !(super.children.get(1) instanceof IntASTOperator))) {
                        // no children, or 1 child that is not an operator, or
                        // 2 children, consisting of an expression followed by an
                        // operator, so add normally
                        child.setParent(this);
                        super.children.add(child);
                        return;
                    } else if (super.children.size() == 1 && super.children.get(0) instanceof IntASTOperator) {
                        // 1 child that is an operator, so insert before the operator
                        child.setParent(this);
                        super.children.add(0, child);
                        return;
                    }
                }
                 */
                child.setParent(this);
                super.children.add(child);
                return;
            }
            // child violates condition of having only 2 expressions and 1 operator
            throw new IllegalArgumentException("A binary expression can only have 3 children: 2 expressions and 1 operator");
        } else {
            throw new IllegalArgumentException("IntASTBinaryExpression does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
}
