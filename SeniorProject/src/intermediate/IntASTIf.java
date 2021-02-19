package intermediate;

public class IntASTIf extends AbstractIntASTBranchNode implements IntASTStatement {
    public IntASTIf () {super("");}

    @Override
    public void addChild(IntASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof IntASTExpression) {
            // first child should be an expression
            // TODO parExpression logic
            child.setParent(this);
            super.children.add(child);
        } else if (child instanceof IntASTStatement) {
            // Statement should follow an expression
            // TODO statement logic
            child.setParent(this);
            super.children.add(child);
        } else {
            throw new IllegalArgumentException("IntASTIf does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
}
