package python;

public class PythonASTUnaryExpression extends AbstractPythonASTBranchNode implements PythonASTExpression {
    public PythonASTUnaryExpression() {
        super("");
    }

    @Override
    public void addChild(PythonASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof PythonASTExpression || child instanceof PythonASTTerminal) {
            // can only have two children: one expression and one terminal
            if (super.children.size() < 2) {
                if (child instanceof PythonASTExpression) {
                    // ensure only one expression exists
                    if (super.children.isEmpty() ||
                            !(super.children.get(0) instanceof PythonASTExpression)) {
                        child.setParent(this);
                        super.children.add(child);
                        return;
                    }
                } else {
                    // ensure only one terminal exists
                    if (super.children.isEmpty() ||
                            !(super.children.get(0) instanceof PythonASTTerminal)) {
                        child.setParent(this);
                        super.children.add(child);
                        return;
                    }
                }
            }
            // child violates condition of having only 1 expressions and 1 operator
            throw new IllegalArgumentException("A unary expression can only have 2 children: 1 expression and 1 operator");
        } else {
            throw new IllegalArgumentException("PythonASTUnaryExpression does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
}
