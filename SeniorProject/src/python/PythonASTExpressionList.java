package python;

public class PythonASTExpressionList extends AbstractPythonASTBranchNode {
    public PythonASTExpressionList() {
        super("");
    }

    @Override
    public void addChild(PythonASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof PythonASTExpression) {
            child.setParent(this);
            super.children.add(child);
        } else if (child instanceof PythonASTExpressionList) {
            for (PythonASTNode node : child.getChildren()) {
                addChild(node);
            }
            ((PythonASTExpressionList) child).children.clear();
        } else {
            throw new IllegalArgumentException("PythonASTExpressionList does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
}