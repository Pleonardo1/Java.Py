package intermediate;

/**
 * Intermediate AST node that represents a constructor declaration.
 */
public class IntASTConstructor extends AbstractIntASTBranchNode implements IntASTMember {
    public IntASTConstructor(String className) {
        super(className);
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
                throw new IllegalArgumentException("Cannot have more than one set of formal parameters in a constructor declaration");
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
                throw new IllegalArgumentException("Cannot have more than one set of method body in a constructor declaration");
            }
        } else {
            throw new IllegalArgumentException("intermediate.IntASTConstructor does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public void setStatic(boolean isStatic) {
        if (!isStatic) {
            throw new UnsupportedOperationException("Constructors cannot be static members");
        }
    }
}
