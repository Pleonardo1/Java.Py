package intermediate;

/**
 * Intermediate AST node that represents an expression
 * that is a single statement, such as
 * <pre>
 *     int i = 0;
 * </pre>
 * or
 * <pre>
 *     x = y + z;
 * </pre>
 */
public class IntASTStatementExpression extends AbstractIntASTBranchNode implements IntASTStatement, IntASTExpression {
    public IntASTStatementExpression() {
        super("");
    }

    @Override
    public void addChild(IntASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof IntASTStatementExpression) {
            // instead of simply adding the IntASTStatementExpression
            // as a new child, instead move all of the
            // IntASTStatementExpression's children to this node
            for (IntASTNode node : ((IntASTStatementExpression)child).children) {
                addChild(node);
            }
            ((IntASTStatementExpression)child).children.clear();
        } else if (child instanceof IntASTExpression) {
            // adding a new part of the expression
            child.setParent(this);
            super.children.add(child);
        } else {
            throw new IllegalArgumentException("IntASTStatementExpression does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
}
