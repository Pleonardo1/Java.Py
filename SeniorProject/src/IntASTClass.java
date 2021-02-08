import java.util.ArrayList;
import java.util.List;

/**
 * Intermediate AST node that represents a class declaration. Since
 * Python does not have interfaces or abstract classes, these can
 * all be bundled into a single Intermediate AST node. Additionally,
 * since Java supports nested classes, this class extends the dummy
 * abstract class {@link IntASTMember} to show that.
 */
public class IntASTClass extends AbstractIntASTNode implements IntASTMember {
    private List<IntASTNode> children;

    public IntASTClass(IntASTNode parent, String className) {
        super(parent, className);
        this.children = new ArrayList<>();
    }

    public IntASTClass(String className) {
        this(null, className);
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
        if (child instanceof IntASTInherit) {
            // adding a class to inherit from, so add it before the
            // class's member declarations
            child.setParent(this);
            if (this.children.isEmpty() ||
                    (this.children.get(this.children.size() - 1) instanceof IntASTInherit)) {
                // insert normally
                this.children.add(child);
            } else {
                // find the last instance of an inheritance declaration
                // and insert the new node after that
                for (int i = 0; i < this.children.size(); i++) {
                    if (this.children.get(i) instanceof IntASTInherit) {
                        this.children.add(i + 1, child);
                        break;
                    }
                }
            }
        } else if (child instanceof IntASTMember) {
            // adding a member declaration
            child.setParent(this);
            this.children.add(child);
        } else {
            throw new IllegalArgumentException("IntASTClass does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }

    @Override
    public boolean isLeaf() {
        return this.children.isEmpty();
    }
}
