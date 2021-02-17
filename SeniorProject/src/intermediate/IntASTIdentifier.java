package intermediate;

/**
 * Intermediate AST node that represents a single identifier,
 * such as a variable name or a method call.
 */
public class IntASTIdentifier extends AbstractIntASTNode implements IntASTTerminal {
    public IntASTIdentifier(String name) {
        super(name);
    }
}
