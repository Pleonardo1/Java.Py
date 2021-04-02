package python;

public class PythonASTForStatement extends AbstractPythonASTBranchNode {
    public PythonASTForStatement() {
        super("");
    }

    @Override 
    public void addChild(PythonASTNode child)  {
        if (child == null) {
            return;
        } else if (child instanceof PythonASTTerminal) {

            if (child.getText().equals("for") 
                || child.getText().equals("in")
                || child.getText().equals("else")
                || child.getText().equals(":")
                || child.getText().equals("range")
                || child.getText().equals("(")
                || child.getText().equals(")")
                || child.getText().equals(",")) {
                   child.setParent(this);
                   super.children.add(child);
            } else {
                throw new IllegalArgumentException("PythonASTForStatement only accepts the terminal children" +
                        " 'for', 'in', and 'else'; " + child.getText());
            }
        } else if (child instanceof PythonASTExpressionList
                || child instanceof PythonASTAtomExpression
                || child instanceof PythonASTSuite) {
            child.setParent(this);
            super.children.add(child);
        } else {
            throw new IllegalArgumentException("PythonASTForStatement does not support children of type \""
                    + child.getClass().getName() + "\"");
        }

    }

}