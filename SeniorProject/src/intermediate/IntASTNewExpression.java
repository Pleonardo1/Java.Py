package intermediate;

public class IntASTNewExpression extends AbstractIntASTBranchNode implements IntASTExpression {

    public IntASTNewExpression() {
        super("");
    }

    @Override
    public void addChild(IntASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof IntASTStatement) {
            child.setParent(this);
            super.children.add(child);
        } else {
            throw new IllegalArgumentException("IntASTNewExpression does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
}
