package intermediate;

public class IntASTForControl extends AbstractIntASTBranchNode {
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
                if (child instanceof IntASTExpression) {
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
}
