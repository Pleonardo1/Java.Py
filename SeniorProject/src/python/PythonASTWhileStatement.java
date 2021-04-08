package python;

public class PythonASTWhileStatement extends AbstractPythonASTBranchNode {
    public PythonASTWhileStatement() {
        super("");
    }

    @Override
    public void addChild(PythonASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof PythonASTTerminal) {
            if (child.getText().equals("while") || child.getText().equals("else")|| child.getText().equals(":")) {
                child.setParent(this);
                super.children.add(child);
            } else {
                throw new IllegalArgumentException("PythonASTWhile only accepts the terminal children" +
                        " 'while', ':', and 'else'; " + child.getText());
            }
        } else if (child instanceof PythonASTExpression
                || child instanceof PythonASTSuite
                || child instanceof PythonASTSimpleStatement) {
            child.setParent(this);
            super.children.add(child);
        } else {
            throw new IllegalArgumentException("PythonASTWhile does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
}
