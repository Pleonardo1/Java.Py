package python;

public abstract class AbstractPythonASTNode implements PythonASTNode {
    protected PythonASTNode parent;
    protected String text;

    public AbstractPythonASTNode(String text) {
        this.parent = null;
        this.text = text;
    }

    @Override
    public void setParent(PythonASTNode node) {
        this.parent = node;
    }

    @Override
    public PythonASTNode getParent() {
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
