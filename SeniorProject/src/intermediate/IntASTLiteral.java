package intermediate;

/**
 * Intermediate AST node that represents a literal value such
 * as a string literal or a numeric constant.
 */
public class IntASTLiteral extends AbstractIntASTNode implements IntASTTerminal {
    public IntASTLiteral(String text) {
        super(text);
    }
}
