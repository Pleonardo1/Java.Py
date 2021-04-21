package python;

public class PythonASTCompoundStatement extends AbstractPythonASTBranchNode {
    public PythonASTCompoundStatement() {
        super("");
    }

    @Override
    public void addChild(PythonASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof PythonASTIfStatement
                || child instanceof PythonASTWhileStatement
                || child instanceof PythonASTForStatement
                || child instanceof PythonASTTryStatement
                || child instanceof PythonASTSimpleStatement
//                || child instanceof PythonASTWithStatement
//                || child instanceof PythonASTDecorated
//                || child instanceof PythonASTAsync
                || child instanceof PythonASTFunction
                || child instanceof PythonASTClass) {
            child.setParent(this);
            super.children.add(child);
        } else if (child instanceof PythonASTCompoundStatement) {
            for (PythonASTNode node : child.getChildren()) {
                this.addChild(node);
            }
            ((PythonASTCompoundStatement) child).children.clear();
        } else {
            throw new IllegalArgumentException("PythonASTCompoundStatement does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
}
