package intermediate;

public class IntASTCastExpression extends AbstractIntASTBranchNode implements IntASTExpression {
    public IntASTCastExpression() {
        super("");
    }

    @Override
    public void addChild(IntASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof IntASTExpression) {
            if (super.children.size() < 2) {
                child.setParent(this);
                super.children.add(child);
            } else {
                throw new IllegalArgumentException("Cannot have more than 2 children per cast expression");
            }
        } else {
            throw new IllegalArgumentException("IntASTCastExpression does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
}
