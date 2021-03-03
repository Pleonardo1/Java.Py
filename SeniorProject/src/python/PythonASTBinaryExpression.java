package python;

import java.util.List;

public class PythonASTBinaryExpression extends AbstractPythonASTBranchNode implements PythonASTExpression {
    public PythonASTBinaryExpression() {
        super("");
    }

    @Override
    public void addChild(PythonASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof PythonASTExpression || child instanceof PythonASTTerminal) {
            // can only have two expression, at index 0 and index 2
            // can only have one terminal, at index 1
            if (super.children.size() < 3) {
                if (child instanceof PythonASTExpression) {
                    // ensure only 2 expressions exist
                    if (this.getChildCount(PythonASTExpression.class) < 2) {
                        // have room for an expression
                        child.setParent(this);
                        super.children.add(child);
                        validate();
                        return;
                    }
                } else {
                    // ensure only 1 terminal exists
                    if (this.getChildCount(PythonASTTerminal.class) < 1) {
                        // have room for a terminal
                        child.setParent(this);
                        super.children.add(child);
                        validate();
                        return;
                    }
                }
            }
            // child violates condition of having only 2 expressions and 1 terminal
            throw new IllegalArgumentException("A binary expression can only have 3 children: 2 expressions and 1 terminal");
        } else {
            throw new IllegalArgumentException("PythonASTBinaryStatement does not support children of type \""
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
