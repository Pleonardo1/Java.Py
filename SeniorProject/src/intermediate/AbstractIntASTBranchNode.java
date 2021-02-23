package intermediate;

import java.util.ArrayList;
import java.util.List;

/**
 * Minimal implementation of the {@link IntASTNode} interface
 * while including code for child handling.
 */
public abstract class AbstractIntASTBranchNode extends AbstractIntASTNode {
    /** Stores the child nodes of this branch node. */
    protected List<IntASTNode> children;

    public AbstractIntASTBranchNode(String text) {
        super(text);
        this.children = new ArrayList<>();
    }

    @Override
    public IntASTNode getChild(int index) {
        return this.children.get(index);
    }

    @Override @SafeVarargs
    public final <T extends IntASTNode> T getChild(int i, Class<? extends T> type, Class<? extends T> ...exclude) {
        // ensure the index is valid
        if (i < 0) {
            throw new IndexOutOfBoundsException(i);
        }
        // iterate through the child nodes until the i-th
        // element (0-indexed) of type T is found
        int j = 0;
        outer:
        for (IntASTNode x : this.children) {
            if (type.isInstance(x)) {
                for (Class<? extends T> c : exclude) {
                    // exclude children of the specified sub-type(s)
                    if (c.isInstance(x)) {
                        continue outer;
                    }
                }
                if (j++ == i) {
                    return type.cast(x);
                }
            }
        }
        // no child found so return null
        return null;
    }

    @Override
    public List<IntASTNode> getChildren() {
        return new ArrayList<>(this.children);
    }

    @Override @SafeVarargs
    public final <T extends IntASTNode> List<T> getChildren(Class<? extends T> type, Class<? extends T> ...exclude) {
        ArrayList<T> out = new ArrayList<>();
        // add all child nodes of type T to the output
        outer:
        for (IntASTNode x : this.children) {
            if (type.isInstance(x)) {
                for (Class<? extends T> c : exclude) {
                    // exclude children of the specified sub-type(s)
                    if (c.isInstance(x)) {
                        continue outer;
                    }
                }
                out.add(type.cast(x));
            }
        }
        return out;
    }

    @Override
    public int getChildCount() {
        return this.children.size();
    }

    @Override @SafeVarargs
    public final <T extends IntASTNode> int getChildCount(Class<? extends T> type, Class<? extends T> ...exclude) {
        int i = 0;
        // count all child nodes of type T
        outer:
        for (IntASTNode x : this.children) {
            if (type.isInstance(x)) {
                for (Class<? extends T> c : exclude) {
                    if (c.isInstance(x)) {
                        continue outer;
                    }
                }
                i++;
            }
        }
        return i;
    }

    @Override
    public boolean isLeaf() {
        return this.children.isEmpty();
    }
}
