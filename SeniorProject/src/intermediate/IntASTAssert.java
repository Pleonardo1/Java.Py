package intermediate;

import java.util.List;

public class IntASTAssert extends AbstractIntASTBranchNode implements IntASTExpression {
    public IntASTAssert() {
        super("");
    }

    @Override
    public void addChild(IntASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof IntASTExpression) {
            // TODO refine number of expressions in an assertion
            child.setParent(this);
            super.children.add(child);
        } else {
            throw new IllegalArgumentException("IntASTAssert does not support children of type \""
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
