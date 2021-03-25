package python;

public class PythonASTIfStatement extends AbstractPythonASTBranchNode {
    public PythonASTIfStatement() {
        super("");
    }

    @Override
    public void addChild(PythonASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof PythonASTTerminal) {
            if (child.getText().equals("if") || child.getText().equals("elif") || child.getText().equals("else") || child.getText().equals(":")) {
                child.setParent(this);
                super.children.add(child);
            } else {
                throw new IllegalArgumentException("PythonASTIfStatement only accepts the terminal children" +
                        " 'if', 'elif', and 'else'; " + child.getText());
            }
        } else if (child instanceof PythonASTExpression
                || child instanceof PythonASTSuite) {
            child.setParent(this);
            super.children.add(child);
        } else {
            throw new IllegalArgumentException("PythonASTIfStatement does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
}
