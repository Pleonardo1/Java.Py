package intermediate;

public class IntASTMethodCall extends AbstractIntASTBranchNode implements IntASTExpression {
    public IntASTMethodCall() {
        super("");
    }

    @Override
    public void addChild(IntASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof IntASTIdentifier) {
            child.setParent(this);
            super.children.add(child);
        } else if (child instanceof IntASTExpressionList) {
            child.setParent(this);
            super.children.add(child);
        } else {
            throw new IllegalArgumentException("IntASTExpressionList does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }

    public IntASTIdentifier getIdentifier() {
        return getChild(0, IntASTIdentifier.class);
    }

    public IntASTExpressionList getExpressionList() {
        return getChild(0, IntASTExpressionList.class);
    }
}
