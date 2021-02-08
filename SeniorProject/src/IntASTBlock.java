import java.util.ArrayList;
import java.util.List;

public class IntASTBlock extends AbstractIntASTNode implements IntASTExpression {
    private List<IntASTExpression> body;

    public IntASTBlock(IntASTNode parent, String text) {
        super(parent, text);
        this.body = new ArrayList<>();
    }

    public IntASTBlock(String text) {
        this(null, text);
    }

    @Override
    public IntASTNode getChild(int index) {
        return this.body.get(index);
    }

    @Override
    public <T extends IntASTNode> T getChild(int i, Class<? extends T> type) {
        if (i >= 0 && i < this.body.size()) {
            int j = 0;
            for (IntASTExpression x : this.body) {
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
        for (IntASTExpression x : this.body) {
            if (type.isInstance(x)) {
                out.add(type.cast(x));
            }
        }
        return out;
    }

    @Override
    public int getChildCount() {
        return this.body.size();
    }

    @Override
    public <T extends IntASTNode> int getChildCount(Class<? extends T> type) {
        int i = 0;
        for (IntASTExpression x : this.body) {
            if (type.isInstance(x)) {
                i++;
            }
        }
        return i;
    }

    @Override
    public void addChild(IntASTNode child) {
        if (!(child instanceof IntASTExpression)) {
            throw new IllegalArgumentException("IntASTBlock does not support children of type \""
                    + child.getClass().getName() + "\"");
        } else {
            // add expressions sequentially
            child.setParent(this);
            this.body.add((IntASTExpression) child);
        }
    }

    @Override
    public boolean isLeaf() {
        return this.body.isEmpty();
    }
}
