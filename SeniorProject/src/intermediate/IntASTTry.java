package intermediate;

public class IntASTTry extends AbstractIntASTBranchNode implements IntASTStatement {
    public IntASTTry() {super("");}

    @Override
    public void addChild(IntASTNode child) {
        if (child == null) {
            return;

        } else if (super.children.size() == 0 && child instanceof IntASTBlock) {
            // First child should be a block
            child.setParent(this);
            super.children.add(child);

        } else if (super.children.size() == 1 &&
                (child instanceof IntASTCatches || child instanceof IntASTBlock)) {
            // Adding a catch clause or finally block
            child.setParent(this);
            super.children.add(child);

        } else if (super.children.size() == 2 && child instanceof IntASTBlock) {
            // Already has a try and Catch so final child is finallyBlock
            child.setParent(this);
            super.children.add(child);

        } else {
            throw new IllegalArgumentException("IntASTTry does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
}
