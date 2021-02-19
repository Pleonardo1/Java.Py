package intermediate;

public class IntASTDo extends AbstractIntASTBranchNode implements IntASTStatement {
    public IntASTDo() {super("");}

    @Override
    public void addChild(IntASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof IntASTStatement) {
            // first child should be an Statement
            // TODO statement logic
            child.setParent(this);
            super.children.add(child);
        } else if (child instanceof IntASTExpression) {
            // parExpression should follow a statement
            // TODO parExpression logic
            child.setParent(this);
            super.children.add(child);
        } else {
            throw new IllegalArgumentException("IntASTDo does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
}