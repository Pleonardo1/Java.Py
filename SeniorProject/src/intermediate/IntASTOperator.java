package intermediate;

/**
 * Intermediate AST node that represents a single operator,
 * such as '+', '=', or '!'.
 */
public class IntASTOperator extends AbstractIntASTNode implements IntASTTerminal {
    public IntASTOperator(String text) {
        super(text);
    }
}
