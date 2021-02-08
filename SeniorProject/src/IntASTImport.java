import java.util.List;

/**
 * Intermediate AST node that represents a package or class import.
 */
public class IntASTImport extends AbstractIntASTNode {
    public IntASTImport(IntASTNode parent, String importName) {
        super(parent, importName);
    }

    public IntASTImport(String importName) {
        this(null, importName);
    }

    @Override
    public IntASTNode getChild(int index) {
        return null;
    }

    @Override
    public <T extends IntASTNode> T getChild(int i, Class<? extends T> type) {
        return null;
    }

    @Override
    public <T extends IntASTNode> List<T> getChildren(Class<? extends T> type) {
        return null;
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public <T extends IntASTNode> int getChildCount(Class<? extends T> type) {
        return 0;
    }

    @Override
    public void addChild(IntASTNode child) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isLeaf() {
        return true;
    }
}
