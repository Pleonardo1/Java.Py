package intermediate;

/**
 * Intermediate AST node that represents a class declaration. Since
 * Python does not have interfaces or abstract classes, these can
 * all be bundled into a single Intermediate AST node. Additionally,
 * since Java supports nested classes, this class extends the dummy
 * abstract class {@link IntASTMember} to show that.
 */
public class IntASTClass extends AbstractIntASTBranchNode implements IntASTMember, IntASTStatement {
    private boolean isStatic = false;
    private boolean isAbstract = false;

    public IntASTClass(String className) {
        super(className);
    }

    @Override
    public void addChild(IntASTNode child) {
        // check what kind of child is being added
        if (child == null) {
            return;
        } else if (child instanceof IntASTTypeList) {
            // adding a list of inherited classes, so ensure one doesn't
            // already exist
            if (super.children.isEmpty() ||
                    !(super.children.get(0) instanceof IntASTTypeList)) {
                // no existing inheritance list, so insert at the beginning
                child.setParent(this);
                super.children.add(0, child);
            } else {
                // this class already has an inheritance list defined, so
                // throw an exception
                throw new IllegalArgumentException("Cannot have more than one list of inherited types per class declaration");
            }
        } else if (child instanceof IntASTClassBody) {
            // adding the class's body, so ensure one doesn't
            // already exist
            if (super.children.isEmpty() ||
                    !(super.children.get(super.children.size()-1) instanceof IntASTClassBody)) {
                // no existing class body, so insert normally
                child.setParent(this);
                super.children.add(child);
            } else {
                // this class already has a body defined, so
                // throw an exception
                throw new IllegalArgumentException("Cannot have more than one body per class declaration");
            }
        } else {
            throw new IllegalArgumentException("IntASTClass does not support children of type \""
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
    
    public IntASTTypeList getTypeList() {
        return getChild(0, IntASTTypeList.class);
    }
    
    public IntASTClassBody getClassBody() {
        return getChild(0, IntASTClassBody.class);
    }
}
