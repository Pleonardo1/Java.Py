package python;

public class PythonASTSimpleStatement extends AbstractPythonASTBranchNode {
    public PythonASTSimpleStatement() {
        super("");
    }

    @Override
    public void addChild(PythonASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof PythonASTSmallStatement) {
            child.setParent(this);
            super.children.add(child);
        } else {
            throw new IllegalArgumentException("PythonASTSimpleStatement does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
}
