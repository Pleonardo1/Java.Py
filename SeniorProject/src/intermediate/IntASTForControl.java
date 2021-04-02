package intermediate;
import java.util.List;

public class IntASTForControl extends AbstractIntASTBranchNode {

    private boolean isEnhanced = false;

    public IntASTForControl() {
        super("");
    }

    @Override
    public void addChild(IntASTNode child) {
        if (child == null) {
            return;
        } else if (super.children.size() == 0) {
            // don't already have any children
            if (child instanceof IntASTIdentifier || child instanceof IntASTExpressionList) {
                // start of for control
                child.setParent(this);
                super.children.add(child);
            } else {
                throw new IllegalArgumentException("IntASTForControl does not support children of type \""
                        + child.getClass().getName() + "\"");
            }
        } else if (super.children.size() == 1) {
            // only have one child
            if (super.children.get(0) instanceof IntASTIdentifier) {
                // have enhanced for control
                if (child.getText().equals(":")) {
                    isEnhanced = true;
                    child.setParent(this);
                    super.children.add(child);
                } else {
                    throw new IllegalArgumentException("invalid for control");
                }
            } else {
                // have regular for control
                if (child instanceof IntASTExpression) {
                    child.setParent(this);
                    super.children.add(child);
                } else {
                    throw new IllegalArgumentException("invalid for control");
                }
            }
        } else if (super.children.size() == 2) {
            // have two children
            if (super.children.get(0) instanceof IntASTIdentifier) {
                // have enhanced for control
                if (child instanceof IntASTExpression) {
                    child.setParent(this);
                    super.children.add(child);
                } else {
                    throw new IllegalArgumentException("invalid for control");
                }
            } else {
                // have regular for control
                if (child instanceof IntASTExpression || child instanceof IntASTExpressionList) {
                    child.setParent(this);
                    super.children.add(child);
                } else {
                    throw new IllegalArgumentException("invalid for control");
                }
            }
        } else if (super.children.size() >= 3) {
            throw new IllegalArgumentException("invalid for control");
        } else {
            throw new IllegalArgumentException("IntASTForControl does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }

    public boolean isEnhanced () {
        return isEnhanced;
    }

    public List<IntASTIdentifier> getIdentifier() {
        return getChildren(IntASTIdentifier.class);
    }

    public IntASTIdentifier getIdentifier(int i) {
        return getChild(i, IntASTIdentifier.class);
    }
    
    public List<IntASTExpression> getExpression() {
        return getChildren(IntASTExpression.class);
    }
    
    public IntASTExpression getExpression(int i) {
        return getChild(i, IntASTExpression.class);
    }

    public List<IntASTExpressionList> getExpressionList() {
        return getChildren(IntASTExpressionList.class);
    }
    
    public IntASTExpressionList getExpressionList(int i) {
        return getChild(i, IntASTExpressionList.class);
    }
}
