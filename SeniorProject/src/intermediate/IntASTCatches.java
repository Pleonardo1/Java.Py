package intermediate;

import java.util.List;

public class IntASTCatches extends AbstractIntASTBranchNode implements IntASTStatement {
    public IntASTCatches(){super("");}

    @Override
    public void addChild(IntASTNode child) {
        if (child == null) {
            return;

        } else if (child instanceof IntASTCatchClause) {
            child.setParent(this);
            super.children.add(child);
        } else {
            throw new IllegalArgumentException("IntASTCatches does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
    
    public IntASTCatchClause getCatchClause(int i) {
        return getChild(i, IntASTCatchClause.class);
    }
    
    public List<IntASTCatchClause> getCatchClause() {
        return getChildren(IntASTCatchClause.class);
    }
}
