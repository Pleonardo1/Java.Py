package intermediate;

import java.util.Collections;
import java.util.List;

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
    default <T extends IntASTNode> T getChild(int i, Class<? extends T> type, Class<? extends T> ...exclude) {
        throw new UnsupportedOperationException();
    }

    @Override
    default List<IntASTNode> getChildren() {
        return Collections.emptyList();
    }

    @Override
    default <T extends IntASTNode> List<T> getChildren(Class<? extends T> type, Class<? extends T> ...exclude) {
        return Collections.emptyList();
    }

    @Override
    default int getChildCount() {
        return 0;
    }

    @Override
    default <T extends IntASTNode> int getChildCount(Class<? extends T> type, Class<? extends T> ...exclude) {
        return 0;
    }

    @Override
    default void addChild(IntASTNode child) {
        throw new UnsupportedOperationException();
    }
}
