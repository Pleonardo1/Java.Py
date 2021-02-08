import java.util.List;

/**
 * Intermediate AST node that represents a package declaration.
 */
public class IntASTPackage extends AbstractIntASTNode {
    public IntASTPackage(IntASTNode parent, String packageName) {
        super(parent, packageName);
    }

    public IntASTPackage(String packageName) {
        this(null, packageName);
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
