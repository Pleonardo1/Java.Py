package intermediate;

import java.util.List;

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
                if (child instanceof IntASTOperator) {
                    // ensure only 1 operator exists, and that it comes
                    // at index 1
                    if (getChildCount(IntASTOperator.class) < 1) {
                        // have room for an operator
                        child.setParent(this);
                        super.children.add(child);
                        validate();
                        return;
                    }
                } else {
                    // ensure only 2 expressions exist, and that they
                    // come at indexes 0 and 2
                    if (getChildCount(IntASTExpression.class, IntASTOperator.class) < 2) {
                        // have room for an expression
                        child.setParent(this);
                        super.children.add(child);
                        validate();
                        return;
                    }
                }
            }
            // child violates condition of having only 2 expressions and 1 operator
            throw new IllegalArgumentException("A binary expression can only have 3 children: 2 expressions and 1 operator");
        } else {
            throw new IllegalArgumentException("IntASTBinaryExpression does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }

    /**
     * Ensure that the ordering of the children is valid. If necessary,
     * swap some children around, but do not change the order of
     * children relative to other children of the same type.
     */
    private void validate() {
        // quickly check if any reordering may be needed
        boolean reorder = false;
        for (int i = 0; i < super.children.size(); i++) {
            if (i % 2 == 0 && super.children.get(i) instanceof IntASTOperator) {
                // even indexes should not be operators so reordering
                // may be needed
                reorder = true;
                break;
            } else if (i % 2 == 1 && !(super.children.get(i) instanceof IntASTOperator)) {
                // odd indexes should be operators so reordering
                // may be needed
                reorder = true;
                break;
            }
        }
        if (!reorder) {
            // no reordering needed
            return;
        }
        // make sure that expressions and operators come in the right order
        List<IntASTOperator> ops = super.getChildren(IntASTOperator.class);
        List<IntASTExpression> exprs = super.getChildren(IntASTExpression.class, IntASTOperator.class);
        super.children.clear();

        int o = 0, e = 0;
        while (o < ops.size() && e < exprs.size()) {
            // alternate which list to add from
            if (e <= o) {
                // add an expression
                super.children.add(exprs.get(e));
                e++;
            } else if (o < e) {
                // add an operator
                super.children.add(ops.get(o));
                o++;
            }
        }
        // add remaining expressions
        while (e < exprs.size()) {
            super.children.add(exprs.get(e));
            e++;
        }
        // add remaining operators
        while (o < ops.size()) {
            super.children.add(ops.get(o));
            o++;
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
    
    public IntASTExpression getExpressionNotOperator(int i) {
        return getChild(i, IntASTExpression.class, IntASTOperator.class);
    }
    
    public List<IntASTExpression> getExpressionNotOperator() {
        return getChildren(IntASTExpression.class, IntASTOperator.class);
    }
}
