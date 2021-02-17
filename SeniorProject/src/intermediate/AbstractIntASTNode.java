package intermediate;

/**
 * Minimal implementation of the {@link IntASTNode} interface.
 */
public abstract class AbstractIntASTNode implements IntASTNode {

    protected IntASTNode parent;
    protected String text;

    public AbstractIntASTNode(String text) {
        this.parent = null;
        this.text = text;
    }

    @Override
    public void setParent(IntASTNode node) {
        this.parent = node;
    }

    @Override
    public IntASTNode getParent() {
        return this.parent;
    }

    @Override
    public boolean isRoot() {
        return this.parent == null;
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return getText();
    }
}
