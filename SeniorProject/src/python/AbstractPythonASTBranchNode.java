package python;

import intermediate.IntASTNode;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPythonASTBranchNode extends AbstractPythonASTNode {
    protected List<PythonASTNode> children;

    public AbstractPythonASTBranchNode(String text) {
        super(text);
        this.children = new ArrayList<>();
    }

    @Override
    public PythonASTNode getChild(int index) {
        return this.children.get(index);
    }

    @Override @SafeVarargs
    public final <T extends PythonASTNode> T getChild(int i, Class<? extends T> type, Class<? extends T>... exclude) {
        // ensure the index is valid
        if (i < 0) {
            throw new IndexOutOfBoundsException(i);
        }
        // iterate through the child nodes until the i-th
        // element (0-indexed) of type T is found
        int j = 0;
        outer:
        for (PythonASTNode x : this.children) {
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
    public List<PythonASTNode> getChildren() {
        return new ArrayList<>(this.children);
    }

    @Override @SafeVarargs
    public final <T extends PythonASTNode> List<T> getChildren(Class<? extends T> type, Class<? extends T>... exclude) {
        ArrayList<T> out = new ArrayList<>();
        // add all child nodes of type T to the output
        outer:
        for (PythonASTNode x : this.children) {
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
    public final <T extends PythonASTNode> int getChildCount(Class<? extends T> type, Class<? extends T>... exclude) {
        int i = 0;
        // count all child nodes of type T
        outer:
        for (PythonASTNode x : this.children) {
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
