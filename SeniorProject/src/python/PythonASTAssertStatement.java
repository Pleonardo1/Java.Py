package python;

public class PythonASTAssertStatement extends AbstractPythonASTBranchNode {
    public PythonASTAssertStatement() {
        super("");
    }

    @Override
    public void addChild(PythonASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof PythonASTTest) {
            child.setParent(this);
            super.children.add(child);
        } else if (child instanceof PythonASTTerminal) {
            if (child.getText().equals("assert") || child.getText().equals(",")) {
                child.setParent(this);
                super.children.add(child);
            } else {
                throw new IllegalArgumentException("PythonASTAssertStatement only accepts the terminals " +
                        "'assert' and ','");
            }
        } else {
            throw new IllegalArgumentException("PythonASTAssertStatement does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
}