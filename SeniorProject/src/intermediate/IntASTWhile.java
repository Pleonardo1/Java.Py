package intermediate;
import java.util.List;

public class IntASTWhile extends AbstractIntASTBranchNode implements IntASTStatement {
    public IntASTWhile() {super("");}

    @Override
    public void addChild(IntASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof IntASTParExpression) {
            child.setParent(this);
            super.children.add(child);
        } else if (child instanceof IntASTStatement) {
            child.setParent(this);
            super.children.add(child);
        } else {
            throw new IllegalArgumentException("IntASTWhile does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }

    public IntASTExpression getExpression(int i) {
        return getChild(i, IntASTExpression.class);
    }

    public List<IntASTExpression> getExpressions() {
        return getChildren(IntASTExpression.class);
    }

    public IntASTStatement getStatementNotExpression(int i) {
        return getChild(i, IntASTStatement.class, IntASTExpression.class);
    }

    public List<IntASTStatement> getStatementsNotExpressions() {
        return getChildren(IntASTStatement.class, IntASTExpression.class);
    }

}
