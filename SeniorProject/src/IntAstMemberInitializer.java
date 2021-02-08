import java.util.Collections;
import java.util.List;

/**
 * Intermediate AST node that represents a member initializer in a
 * class body. Since Python uses implicit declaration of member
 * variables, only members that are explicitly initialized in the
 * class body are represented here. Member declarations with no
 * initialization are discarded.
 */
public class IntAstMemberInitializer extends AbstractIntASTNode implements IntASTMember {
    private IntASTExpression expression;

    public IntAstMemberInitializer(IntASTNode parent, String text) {
        super(parent, text);
        this.expression = null;
    }

    public IntAstMemberInitializer(String text) {
        this(null, text);
    }

    @Override
    public IntASTNode getChild(int index) {
        if (index == 0) {
            return this.expression;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public <T extends IntASTNode> T getChild(int i, Class<? extends T> type) {
        if (i == 0 && type.isAssignableFrom(IntASTExpression.class)) {
            return type.cast(this.expression);
        } else {
            return null;
        }
    }

    @Override
    public <T extends IntASTNode> List<T> getChildren(Class<? extends T> type) {
        if (type.isAssignableFrom(IntASTExpression.class)) {
            return Collections.singletonList(type.cast(this.expression));
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public int getChildCount() {
        return (this.expression == null ? 0 : 1);
    }

    @Override
    public <T extends IntASTNode> int getChildCount(Class<? extends T> type) {
        return (type.isAssignableFrom(IntASTExpression.class) && this.expression != null) ? 1 : 0;
    }

    @Override
    public void addChild(IntASTNode child) {
        if (this.expression == null && (child instanceof IntASTExpression)) {
            this.expression = (IntASTExpression) child;
        } else {
            throw new IllegalArgumentException("This IntASTMemberInitializer already has its child set");
        }
    }

    @Override
    public boolean isLeaf() {
        return this.expression == null;
    }
}
