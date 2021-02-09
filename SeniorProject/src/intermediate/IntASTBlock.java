package intermediate;

/**
 * Intermediate AST node that represents a block statement.
 * May be standalone or serve as the body of another construct
 * such as a method or an if-statement.
 */
public class IntASTBlock extends AbstractIntASTBranchNode implements IntASTStatement, IntASTMember {
    private boolean isStatic = false;

    public IntASTBlock(IntASTNode parent) {
        super(parent, "");
    }

    public IntASTBlock() {
        super("");
    }

    @Override
    public void addChild(IntASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof IntASTStatement || child instanceof IntASTClass) {
            // add statements sequentially. class declarations are
            // technically allowed too
            child.setParent(this);
            super.children.add(child);
        } else {
            throw new IllegalArgumentException("intermediate.IntASTBlock does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }

    @Override
    public boolean isStatic() {
        return this.isStatic;
    }

    @Override
    public void setStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }
}
