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

    @Override
    public <T extends IntASTNode> T getChild(int i, Class<? extends T> type) {
        // ensure the index is valid
        if (i >= 0 && i < this.children.size()) {
            // iterate through the child nodes until the i-th
            // element (0-indexed) of type T is found
            int j = 0;
            for (IntASTNode x : this.children) {
                if (type.isInstance(x)) {
                    if (j++ == i) {
                        return type.cast(x);
                    }
                }
            }
        }
        //
        return null;
    }

    @Override
    public <T extends IntASTNode> List<T> getChildren(Class<? extends T> type) {
        ArrayList<T> out = new ArrayList<>();
        // add all child nodes of type T to the output
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
        // count all child nodes of type T
        for (IntASTNode x : this.children) {
            if (type.isInstance(x)) {
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
