package python;

public class PythonASTStatement extends AbstractPythonASTBranchNode {
    public PythonASTStatement() {
        super("");
    }

    @Override
    public void addChild(PythonASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof PythonASTSimpleStatement
                || child instanceof PythonASTCompoundStatement) {
            child.setParent(this);
            super.children.add(child);
        } else {
            throw new IllegalArgumentException("PythonASTStatement does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
}
