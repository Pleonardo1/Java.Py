package intermediate;

import java.util.List;

public class IntASTExpressionList extends AbstractIntASTBranchNode implements IntASTStatement {
    public IntASTExpressionList() {
        super("");
    }

    @Override
    public void addChild(IntASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof IntASTExpression) {
            // add an expression to the list
            child.setParent(this);
            super.children.add(child);
        } else {
            throw new IllegalArgumentException("IntASTExpressionList does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
    
    public IntASTExpression getExpression(int i) {
        return getChild(i, IntASTExpression.class);
    }
    
    public List<IntASTExpression> getExpression() {
        return getChildren(IntASTExpression.class);
    }
}
