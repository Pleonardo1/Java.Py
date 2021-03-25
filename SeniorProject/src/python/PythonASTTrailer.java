package python;

public class PythonASTTrailer extends AbstractPythonASTBranchNode {
    public PythonASTTrailer() {
        super("");
    }

    @Override
    public void addChild(PythonASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof PythonASTTerminal
                || child instanceof PythonASTArgList
                || child instanceof PythonASTExpressionList) {
            child.setParent(this);
            super.children.add(child);
        } else {
            throw new IllegalArgumentException("PythonASTTrailer does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
}
