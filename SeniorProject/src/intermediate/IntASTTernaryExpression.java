package intermediate;

import java.util.List;

public class IntASTTernaryExpression extends AbstractIntASTBranchNode implements IntASTExpression {
    public IntASTTernaryExpression() {
        super("");
    }

    @Override
    public void addChild(IntASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof IntASTExpression) {
            // can only have three expressions, at index 0, index 2, and index 4,
            // in addition to two operators, at index 1 and index 3
            if (super.children.size() < 5) {
                if (child instanceof IntASTOperator) {
                    // ensure only 2 operators exist, and that they
                    // come at indexes 1 and 3
                    if (getChildCount(IntASTOperator.class) < 2) {
                        // have room for an operator
                        child.setParent(this);
                        super.children.add(child);
                        validate();
                        return;
                    }
                } else {
                    // ensure only 3 expressions exist, and that they
                    // come at indexes 0, 2, and 4
                    if (super.children.size() - getChildCount(IntASTOperator.class) < 3) {
                        // have room for an expression
                        child.setParent(this);
                        super.children.add(child);
                        validate();
                        return;
                    }
                }
            }
            // child violates condition of having only 3 expressions and 2 operators
            throw new IllegalArgumentException("A ternary expression can only have 5 children: 3 expressions and 2 operators");
        } else {
            throw new IllegalArgumentException("IntASTTernaryExpression does not support children of type \""
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
        List<IntASTExpression> exprs = super.getChildren(IntASTExpression.class);
        exprs.removeAll(ops);
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
}
