package python;

public class PythonASTImportStatement extends AbstractPythonASTBranchNode {
    public PythonASTImportStatement() {
        super("");
    }

    @Override
    public void addChild(PythonASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof PythonASTTerminal) {
            child.setParent(this);
            super.children.add(child);
        } else {
            throw new IllegalArgumentException("PythonASTImportStatement does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
}