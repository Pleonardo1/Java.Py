package intermediate;

public class IntASTField extends AbstractIntASTBranchNode implements IntASTMember {
    private boolean isStatic = false;

    public IntASTField() {
        super("");
    }

    @Override
    public void addChild(IntASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof IntASTStatement) {
            // only accept one child
            if (super.children.isEmpty()) {
                child.setParent(this);
                super.children.add(child);
            } else {
                throw new IllegalArgumentException("A field can only consist of one statement");
            }
        } else {
            throw new IllegalArgumentException("IntASTField does not support children of type \""
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

    public IntASTStatement getStatement() {
        return getChild(0, IntASTStatement.class);
    }
}
