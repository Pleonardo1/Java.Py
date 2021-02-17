package intermediate;

/**
 * Intermediate AST node that represents the body of a class
 * declaration.
 */
public class IntASTClassBody extends AbstractIntASTBranchNode {
    public IntASTClassBody() {
        super("");
    }

    @Override
    public void addChild(IntASTNode child) {
        // check what kind of child is being added
        if (child == null) {
            return;
        } else if (child instanceof IntASTMember) {
            // adding a member declaration
            child.setParent(this);
            super.children.add(child);
        } else if (child instanceof IntASTBlock) {
            // TODO add support for block statements within a class body?
            throw new UnsupportedOperationException("Block statements not supported within class bodies");
        } else {
            throw new IllegalArgumentException("intermediate.IntASTClassBody does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
}
