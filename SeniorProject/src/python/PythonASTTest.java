package python;

public class PythonASTTest extends AbstractPythonASTBranchNode {
    public PythonASTTest() {
        super("");
    }

    @Override
    public void addChild(PythonASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof PythonASTExpression) {
            child.setParent(this);
            super.children.add(child);
        } else {
            throw new IllegalArgumentException("PythonASTTest does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
}
