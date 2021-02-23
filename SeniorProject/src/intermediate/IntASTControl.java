package intermediate;

/**
 * Intermediate AST node that represents a control statement
 * such as return, break or continue.
 */
public class IntASTControl extends AbstractIntASTBranchNode implements IntASTStatement {
    public IntASTControl(String text) {
        super(text);
    }

    @Override
    public void addChild(IntASTNode child) {
        if (child == null) {
            return;
        } else if (!(child instanceof IntASTExpression) &&
                !(child instanceof IntASTIdentifier)) {
            throw new IllegalArgumentException("IntASTControl does not support children of type \""
                    + child.getClass().getName() + "\"");
        } else if (super.text.equals("return") || super.text.equals("throw")) {
            // have a return statement, so child must be an expression
            if (child instanceof IntASTExpression) {
                // ensure only one expression is stored
                if (super.children.isEmpty()) {
                    child.setParent(this);
                    super.children.add(child);
                } else {
                    throw new IllegalArgumentException("A " + super.text + " statement can only have one statement");
                }
            } else {
                throw new IllegalArgumentException("IntASTControl does not support children of type \""
                        + child.getClass().getName() + "\" when representing a " + super.text + " statement");
            }
        } else if (super.text.equals("break") || super.text.equals("continue")) {
            // have a break or continue statement, so child must be an identifier
            if (child instanceof IntASTIdentifier) {
                // ensure only one identifier is stored
                if (super.children.isEmpty()) {
                    child.setParent(this);
                    super.children.add(child);
                } else {
                    throw new IllegalArgumentException("A " + super.text + "statement can only have one target label");
                }
            } else {
                throw new IllegalArgumentException("IntASTControl does not support children of type \""
                        + child.getClass().getName() + "\" when representing a " + super.text + " statement");
            }
        } else {
            throw new IllegalStateException("Unknown control statement type: " + super.text);
        }
    }

    public IntASTExpression getExpression() {
        return getChild(0, IntASTExpression.class);
    }
    
    public IntASTIdentifier getIdentifier() {
        return getChild(0, IntASTIdentifier.class);
    }
    
    public IntASTExpression getExpressionNotIdentifier() {
        return getChild(0, IntASTExpression.class, IntASTIdentifier.class);
    }
}
