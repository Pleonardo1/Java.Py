import java.util.List;

/**
 * Intermediate AST node that represents one of a class's inherited
 * classes. Since Python supports multiple inheritance and Java
 * has interfaces as a form of multiple inheritance, there may
 * be more that one of these node in a single class's inheritance
 * clause.
 */
public class IntASTInherit extends AbstractIntASTNode {
    public IntASTInherit(IntASTNode parent, String inheritName) {
        super(parent, inheritName);
    }

    public IntASTInherit(String inheritName) {
        this(null, inheritName);
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
