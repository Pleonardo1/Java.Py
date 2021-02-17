package intermediate;

/**
 * Intermediate AST node that represents one of a class's inherited
 * classes. Since Python supports multiple inheritance and Java
 * has interfaces as a form of multiple inheritance, there may
 * be more that one of these node in a single class's inheritance
 * clause.
 */
public class IntASTInherit extends AbstractIntASTNode implements IntASTTerminal {
    public IntASTInherit(String inheritName) {
        super(inheritName);
    }
}
