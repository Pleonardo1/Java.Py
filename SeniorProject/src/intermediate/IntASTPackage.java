package intermediate;

/**
 * Intermediate AST node that represents a package declaration.
 */
public class IntASTPackage extends AbstractIntASTNode implements IntASTTerminal {
    public IntASTPackage(IntASTNode parent, String packageName) {
        super(parent, packageName);
    }

    public IntASTPackage(String packageName) {
        super(packageName);
    }
}
