import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Intermediate AST node that represents an entire compilation unit.
 * Instances of this class will always be root nodes, though the
 * root of the AST may not always be an instance of this class.
 */
public class IntASTCompilationUnit extends AbstractIntASTNode {
    /**
     * This node's children. Consists of a package declaration,
     * import statements and class definitions.
     */
    private List<IntASTNode> children;

    /**
     * Instantiate a Compilation Unit node with the given name.
     * This would normally be the name of the source file the
     * AST originated from.
     *
     * @param unitName The name of the compilation unit
     */
    public IntASTCompilationUnit(String unitName) {
        super(null, unitName);
    }

    @Override
    public IntASTNode getChild(int index) {
        return this.children.get(index);
    }

    @Override
    public <T extends IntASTNode> T getChild(int i, Class<? extends T> type) {
        if (i >= 0 && i < this.children.size()) {
            int j = 0;
            for (IntASTNode x : this.children) {
                if (type.isInstance(x)) {
                    if (j++ == i) {
                        return type.cast(x);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public <T extends IntASTNode> List<T> getChildren(Class<? extends T> type) {
        ArrayList<T> out = new ArrayList<>();
        for (IntASTNode x : this.children) {
            if (type.isInstance(x)) {
                out.add(type.cast(x));
            }
        }
        return out;
    }

    @Override
    public int getChildCount() {
        return this.children.size();
    }

    @Override
    public <T extends IntASTNode> int getChildCount(Class<? extends T> type) {
        int i = 0;
        for (IntASTNode x : this.children) {
            if (type.isInstance(x)) {
                i++;
            }
        }
        return i;
    }

    @Override
    public void addChild(IntASTNode child) {
        // check what kind of child is being added
        if (child instanceof IntASTPackage) {
            // adding a package declaration, so ensure one doesn't
            // already exist
            if (this.children.isEmpty() || !(this.children.get(0) instanceof IntASTPackage)) {
                // no existing declaration, so add the new child
                child.setParent(this);
                this.children.add(0, child);
            } else {
                // this compilation unit already has a package
                // declaration, so throw an exception
                throw new IllegalArgumentException("Cannot have more than one package declaration per compilation unit");
            }
        } else if (child instanceof IntASTImport) {
            // adding an import declaration, so ensure it gets inserted
            // into the correct spot in the children list
            child.setParent(this);
            if (this.children.isEmpty() ||
                    (this.children.get(this.children.size()-1) instanceof IntASTPackage) ||
                    (this.children.get(this.children.size()-1) instanceof IntASTImport)) {
                // insert normally
                this.children.add(child);
            } else {
                // find the last instance of a package or import
                // declaration and insert the new import after that
                for (int i = this.children.size(); i > 0; i--) {
                    if ((this.children.get(i-1) instanceof IntASTPackage) ||
                            (this.children.get(i-1) instanceof IntASTImport)) {
                        this.children.add(i, child);
                        break;
                    }
                }
            }
        } else if (child instanceof IntASTClass) {
            // adding a class declaration. this goes after everything
            // else and order doesn't really matter.
            child.setParent(this);
            this.children.add(child);
        } else {
            throw new IllegalArgumentException("IntASTCompilationUnit does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }

    @Override
    public boolean isLeaf() {
        return this.children.isEmpty();
    }
}
