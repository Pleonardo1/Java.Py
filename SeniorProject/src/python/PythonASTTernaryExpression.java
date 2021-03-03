package python;

import java.util.List;

public class PythonASTTernaryExpression extends AbstractPythonASTBranchNode implements PythonASTExpression {
    public PythonASTTernaryExpression() {
        super("");
    }

    @Override
    public void addChild(PythonASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof PythonASTExpression || child instanceof PythonASTTerminal) {
            // can only have three expression, at index 0, index 2, and index 4
            // can only have two terminals, at index 1 and index 3
            if (super.children.size() < 5) {
                if (child instanceof PythonASTExpression) {
                    // ensure only 3 expressions exist
                    if (this.getChildCount(PythonASTExpression.class) < 3) {
                        // have room for an expression
                        child.setParent(this);
                        super.children.add(child);
                        validate();
                        return;
                    }
                } else {
                    // ensure only 2 terminals exist
                    if (this.getChildCount(PythonASTTerminal.class) < 2) {
                        // have room for a terminal
                        child.setParent(this);
                        super.children.add(child);
                        validate();
                        return;
                    }
                }
            }
            // child violates condition of having only 3 expressions and 2 terminals
            throw new IllegalArgumentException("A ternary expression can only have 5 children: 3 expressions and 2 terminals");
        } else {
            throw new IllegalArgumentException("PythonASTTernaryStatement does not support children of type \""
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
            if (i % 2 == 0 && super.children.get(i) instanceof PythonASTTerminal) {
                // even indexes should not be terminals so reordering
                // may be needed
                reorder = true;
                break;
            } else if (i % 2 == 1 && super.children.get(i) instanceof PythonASTExpression) {
                // odd indexes should not be expressions so reordering
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
        List<PythonASTTerminal> ops = super.getChildren(PythonASTTerminal.class);
        List<PythonASTExpression> exprs = super.getChildren(PythonASTExpression.class);
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
