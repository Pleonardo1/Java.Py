
/**
 * A dummy interface used to show that as Intermediate AST Node
 * is a node that represents a terminal token such as an identifier,
 * a numeric constant, or an operator.
 */
public interface IntASTTerminal extends IntASTExpression {
    @Override
    default boolean isLeaf() {
        return true;
    }

    @Override
    default IntASTNode getChild(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    default int getChildCount() {
        return 0;
    }

    @Override
    default void addChild(IntASTNode child) {
        throw new UnsupportedOperationException();
    }
}
