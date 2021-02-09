package intermediate;

/**
 * An interface used to show that an Intermediate AST Node
 * is a node that can be a member of a class body. The only
 * additional functionality being able to set whether the
 * member is static.
 */
public interface IntASTMember extends IntASTNode {
    boolean isStatic();

    void setStatic(boolean isStatic);
}
