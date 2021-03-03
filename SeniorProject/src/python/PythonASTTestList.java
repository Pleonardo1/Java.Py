package python;

public class PythonASTTestList extends AbstractPythonASTBranchNode {
    public PythonASTTestList() {
        super("");
    }

    @Override
    public void addChild(PythonASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof PythonASTTest) {
            child.setParent(this);
            super.children.add(child);
        } else {
            throw new IllegalArgumentException("PythonASTTestList does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
}
