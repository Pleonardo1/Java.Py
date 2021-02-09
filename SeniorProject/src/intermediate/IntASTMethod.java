package intermediate;

/**
 * Intermediate AST node that represents a method declaration.
 */
public class IntASTMethod extends AbstractIntASTBranchNode implements  IntASTMember {
    private boolean isStatic = false;
    private boolean isAbstract = false;

    public IntASTMethod(IntASTNode parent, String methodName) {
        super(parent, methodName);
    }

    public IntASTMethod(String methodName) {
        super(methodName);
    }

    @Override
    public void addChild(IntASTNode child) {
        // check what kind of child is being added
        if (child == null) {
            return;
        } else if (child instanceof IntASTMethodParameters) {
            // adding the method parameters. ensure there's
            // no duplicate
            if (super.children.isEmpty() || !(super.children.get(0) instanceof  IntASTMethodParameters)) {
                // no existing children, or only one existing child
                // which is not a method parameter
                child.setParent(this);
                super.children.add(0, child);
            } else {
                // at least one existing child, which is a set of
                // method parameters
                throw new IllegalArgumentException("Cannot have more than one set of formal parameters in a method declaration");
            }
        } else if (child instanceof IntASTBlock) {
            // adding the method block. ensure there's no duplicate
            if (super.children.isEmpty() || !(super.children.get(super.children.size()-1) instanceof IntASTBlock)) {
                // no existing children, or only one existing child
                // which is not a block statement
                child.setParent(this);
                super.children.add(child);
            } else {
                // at least one existing child, including a
                // block statement
                throw new IllegalArgumentException("Cannot have more than one set of method body in a method declaration");
            }
        } else {
            throw new IllegalArgumentException("intermediate.IntASTMethod does not support children of type \""
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

    public boolean isAbstract() {
        return this.isAbstract;
    }

    public void setAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }
}
