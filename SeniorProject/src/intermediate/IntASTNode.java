package intermediate;

import java.util.List;

/**
 * Interface defining the outline of an Intermediate AST node.
 */
public interface IntASTNode {
    /**
     * Get this node's parent. If this node is the root node then
     * {@code null} is returned.
     *
     * @return This node's parent
     */
    IntASTNode getParent();

    /**
     * Set's this node's parent. Does not make any changes to the
     * new or old parent nodes.
     *
     * @param parent The new parent node
     */
    void setParent(IntASTNode parent);

    /**
     * Get the indicated child of this node. Depending on the
     * implementation, an invalid index may return {@code null}
     * or throw an exception.
     *
     * @param index The index of the child to return
     * @return The indicated child of this node
     */
    IntASTNode getChild(int index);

    /**
     * Gets the {@code i}-th child that is a subtype of {@code T},
     * if such a child exists. If no such child exists, {@code null}
     * is returned.
     *
     * @param i The index of the child to retrieve
     * @param type The {@code Class} object representation of {@code T}
     * @param <T> The supertype of the target child
     * @return The {@code i}-th child of type {@code T}
     */
    <T extends IntASTNode> T getChild(int i, Class<? extends T> type);

    /**
     * Returns a list of all children of supertype {@code T}. If no
     * such children exist, an empty list is returned.
     *
     * @param type The {@code Class} object representation of {@code T}
     * @param <T> The supertype of the target children
     * @return A list of all children of type {@code T}
     */
    <T extends IntASTNode> List<T> getChildren(Class<? extends T> type);

    /**
     * Returns the number of children to this node.
     *
     * @return The number of children to this node
     */
    int getChildCount();

    /**
     * Returns the number of children of supertype {@code T}.
     *
     * @param type The {@code Class} object representation of {@code T}
     * @param <T> The supertype of the target children
     * @return A list of all children of type {@code T}
     */
    <T extends IntASTNode> int getChildCount(Class<? extends T> type);

    /**
     * Adds a node to this node's children. Node classes that do
     * not support having children will throw an
     * {@code UnsupportedOperationException}.
     *
     * @param child The child to add to this node
     */
    void addChild(IntASTNode child);

    /**
     * Get a {@code String} representation of this node. Not all
     * nodes have a {@code String} representation.
     *
     * @return A {@code String} representation of this node, or an empty {@code String} if none exists
     */
    String getText();

    /**
     * Sets the {@code String} representation of this node.
     *
     * @param text The new {@code String} representation of this node
     */
    void setText(String text);

    /**
     * Returns {@code true} if this node is the root of the tree,
     * {@code false} otherwise.
     *
     * @return Whether this node is the root of a tree
     */
    boolean isRoot();

    /**
     * Returns {@code true} if this node is a leaf of a tree,
     * {@code false} otherwise.
     *
     * @return Whether this node is a leaf of a tree
     */
    boolean isLeaf();
}
