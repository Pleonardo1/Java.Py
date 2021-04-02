package intermediate;

import java.util.List;

public class IntASTTypeList extends AbstractIntASTBranchNode implements IntASTStatement {
    public IntASTTypeList() {
        super("");
    }

    @Override
    public void addChild(IntASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof IntASTIdentifier) {
            // add a type name to the list
            child.setParent(this);
            super.children.add(child);
        } else if (child instanceof IntASTTypeList) {
            // move all the children from the child IntASTTypeList
            // to this node
            IntASTTypeList c = (IntASTTypeList) child;
            for (IntASTNode t : c.children) {
                addChild(t);
            }
            c.children.clear();
        } else {
            throw new IllegalArgumentException("IntASTTypeList does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
    
    public IntASTIdentifier getIdentifier(int i) {
        return getChild(i, IntASTIdentifier.class);
    }
    
    public List<IntASTIdentifier> getIdentifier() {
        return getChildren(IntASTIdentifier.class);
    }
}
