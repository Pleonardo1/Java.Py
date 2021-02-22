package intermediate;

public class IntASTCatchClause extends AbstractIntASTBranchNode {
    public IntASTCatchClause(){super("");}

    @Override
    public void addChild(IntASTNode child) {
        if (child == null) {
            return;

        } else if (child instanceof IntASTIdentifier) {
            //CatchType -> QualifiedName -> Identifier
            child.setParent(this);
            super.children.add(child);

        } else if (child instanceof IntASTBlock) {
            //Block statement follows the RPAREN of the catchClause
            child.setParent(this);
            super.children.add(child);

        } else {
            throw new IllegalArgumentException("IntASTCatchClause does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
}
