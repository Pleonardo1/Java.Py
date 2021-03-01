package python;

public class PythonASTFileInput extends AbstractPythonASTBranchNode {
    public PythonASTFileInput() {
        super("");
    }

    @Override
    public void addChild(PythonASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof PythonASTStatement) {
            child.setParent(this);
            super.children.add(child);
        } else {
            throw new IllegalArgumentException("PythonASTFileInput does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
}
