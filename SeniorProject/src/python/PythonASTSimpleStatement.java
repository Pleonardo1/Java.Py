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
        } else if (child instanceof PythonASTTerminal.PythonASTNewline) {
            if (getChildCount(PythonASTTerminal.PythonASTNewline.class) == 0) {
                child.setParent(this);
                super.children.add(child);
            } else {
                throw new IllegalArgumentException("Cannot have more than 1 child of type PythonASTNewline");
            }
        } else {
            throw new IllegalArgumentException("PythonASTSimpleStatement does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
}
