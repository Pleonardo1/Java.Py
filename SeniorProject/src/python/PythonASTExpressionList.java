package python;

public class PythonASTExpressionList extends AbstractPythonASTBranchNode {
    public PythonASTExpressionList() {
        super("");
    }

    //Todo finish the exprlist 
    //: (expr | star_expr) (',' (expr | star_expr))* ','?
    //star_expr
    //: '*' expr

    @Override
    public void addChild(PythonASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof PythonASTExpression) {
            child.setParent(this);
            super.children.add(child);
        } else {
            throw new IllegalArgumentException("PythonASTExpressionList does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
}