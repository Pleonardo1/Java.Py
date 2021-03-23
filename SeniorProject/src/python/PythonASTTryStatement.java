package python;

public class PythonASTTryStatement extends AbstractPythonASTBranchNode {
    public PythonASTTryStatement() {
        super("");
    }

    @Override
    public void addChild(PythonASTNode child)  {
        if (child == null) {
            return;
        } else if(child instanceof PythonASTTerminal) {
            if(child.getText().equals("try")
                || child.getText().equals("else")
                || child.getText().equals("finally")
                || child.getText().equals(":")) {
                   child.setParent(this);
                   super.children.add(child);
            } else {
                throw new IllegalArgumentException("PythonASTTryStatement only accepts the terminal children" +
                        " 'try', 'else', ,'except' and 'finally'; " + child.getText());
            }
        } else if (child instanceof PythonASTSuite
                || child instanceof PythonASTExceptClause) {
            child.setParent(this);
            super.children.add(child);
        }
        else {
            throw new IllegalArgumentException("PythonASTTryStatement does not support children of type \""
                    + child.getClass().getName() + "\"");
        }

    }

}