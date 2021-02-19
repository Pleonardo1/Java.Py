package intermediate;

public class IntASTParExpression extends AbstractIntASTBranchNode implements IntASTExpression {
    public IntASTParExpression() {
        super("");
    }

    @Override
    public void addChild(IntASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof IntASTExpression) {
            // ensure we only get one child
            if (super.children.isEmpty()) {
                child.setParent(this);
                super.children.add(child);
            } else {
                throw new IllegalArgumentException("A parentheses expression can only have one expression");
            }
        } else {
            throw new IllegalArgumentException("IntASTParExpression does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
}
