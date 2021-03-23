package python;

public class PythonASTExceptClause extends AbstractPythonASTBranchNode {
    public PythonASTExceptClause() {
        super("");
    }
    @Override
    public void addChild(PythonASTNode child)  {
        if (child == null) {
            return;
        } else if(child instanceof PythonASTTerminal) {
            if(child.getText().equals("except")
                || child.getText().equals("as")) {
                   child.setParent(this);
                   super.children.add(child);
            } else {
                throw new IllegalArgumentException("PythonASTExceptClause only accepts the terminal children" +
                        " 'except' and 'as'; " + child.getText());
            }
        } else if (child instanceof PythonASTTest) {
            child.setParent(this);
            super.children.add(child);
        }
        else {
            throw new IllegalArgumentException("PythonASTExceptClause does not support children of type \""
                    + child.getClass().getName() + "\"");
        }

    }
}