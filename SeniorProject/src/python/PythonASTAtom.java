package python;

public class PythonASTAtom extends AbstractPythonASTBranchNode {
    public PythonASTAtom() {
        super("");
    }

    @Override
    public void addChild(PythonASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof PythonASTTerminal || child instanceof PythonASTTestList) {
            child.setParent(this);
            super.children.add(child);
        } else {
            throw new IllegalArgumentException("PythonASTAtom does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
}
