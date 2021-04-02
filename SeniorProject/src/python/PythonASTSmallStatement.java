package python;

public class PythonASTSmallStatement extends AbstractPythonASTBranchNode {
    public PythonASTSmallStatement() {
        super("");
    }

    @Override
    public void addChild(PythonASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof PythonASTExpression
//                || child instanceof PythonASTDeleteStatement
                || child instanceof PythonASTPassStatement
                || child instanceof PythonASTFlowStatement
                || child instanceof PythonASTImportStatement
//                || child instanceof PythonASTGlobalStatement
//                || child instanceof PythonASTNonlocalStatement
                || child instanceof PythonASTAssertStatement) {
            child.setParent(this);
            super.children.add(child);
        } else {
            throw new IllegalArgumentException("PythonASTSmallStatement does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
}
