import java.util.ArrayList;
import java.util.List;

/**
 * Intermediate AST node that represents a method declaration.
 */
public class IntASTMethod extends AbstractIntASTNode implements  IntASTMember {
    private List<IntASTExpression> children;

    public IntASTMethod(IntASTNode parent, String methodName) {
        super(parent, methodName);
        this.children = new ArrayList<>();
    }

    public IntASTMethod(String methodName) {
        this(null, methodName);
    }

    @Override
    public IntASTNode getChild(int index) {
        // TODO implement method
        return null;
    }

    @Override
    public <T extends IntASTNode> T getChild(int i, Class<? extends T> type) {
        // TODO implement method
        return null;
    }

    @Override
    public <T extends IntASTNode> List<T> getChildren(Class<? extends T> type) {
        // TODO implement method
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

    }

    @Override
    public boolean isLeaf() {
        return false;
    }
}
