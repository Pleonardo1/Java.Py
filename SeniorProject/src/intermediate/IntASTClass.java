package intermediate;

/**
 * Intermediate AST node that represents a class declaration. Since
 * Python does not have interfaces or abstract classes, these can
 * all be bundled into a single Intermediate AST node. Additionally,
 * since Java supports nested classes, this class extends the dummy
 * abstract class {@link IntASTMember} to show that.
 */
public class IntASTClass extends AbstractIntASTBranchNode implements IntASTMember {
    private boolean isStatic = false;
    private boolean isAbstract = false;

    public IntASTClass(IntASTNode parent, String className) {
        super(parent, className);
    }

    public IntASTClass(String className) {
        super(className);
    }

    @Override
    public void addChild(IntASTNode child) {
        // check what kind of child is being added
        if (child == null) {
            return;
        } else if (child instanceof IntASTInherit) {
            // adding a class to inherit from, so add it before the
            // class's member declarations
            child.setParent(this);
            if (super.children.isEmpty() ||
                    (super.children.get(super.children.size() - 1) instanceof IntASTInherit)) {
                // insert normally
                super.children.add(child);
            } else {
                // find the last instance of an inheritance declaration
                // and insert the new node after that
                for (int i = 0; i < super.children.size(); i++) {
                    if (super.children.get(i) instanceof IntASTInherit) {
                        super.children.add(i + 1, child);
                        break;
                    }
                }
            }
        } else if (child instanceof IntASTClassBody) {
            // adding the class's body, so ensure one doesn't
            // already exist
            if (super.children.isEmpty() ||
                    (super.children.get(super.children.size()-1) instanceof IntASTClassBody)) {
                // no existing class body, so insert normally
                child.setParent(this);
                super.children.add(child);
            } else {
                // this class already has a body defined, so
                // throw an exception
                throw new IllegalArgumentException("Cannot have more than one body per class declaration");
            }
        } else {
            throw new IllegalArgumentException("intermediate.IntASTClass does not support children of type \""
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
