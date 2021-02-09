package intermediate;

/**
 * Intermediate AST node that represents a package or class import.
 */
public class IntASTImport extends AbstractIntASTNode implements IntASTTerminal {
    public IntASTImport(IntASTNode parent, String importName) {
        super(parent, importName);
    }

    public IntASTImport(String importName) {
        super(importName);
    }
}
