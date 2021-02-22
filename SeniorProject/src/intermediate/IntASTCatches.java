package intermediate;

public class IntASTCatches extends AbstractIntASTBranchNode implements IntASTStatement {
    public IntASTCatches(){super("");}

    @Override
    public void addChild(IntASTNode child) {
        if (child == null) {
            return;

        } else if (child instanceof IntASTCatchClause) {
            //
            child.setParent(this);
            super.children.add(child);

        } else {
            throw new IllegalArgumentException("IntASTCatches does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
}
