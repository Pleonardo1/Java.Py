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

    public IntASTParExpression getParExpression() {
        return getChild(0, IntASTParExpression.class);
    }

    public IntASTStatement getStatementNotParExpression() {
        return getChild(0, IntASTStatement.class, IntASTParExpression.class);
    }

    public List<IntASTStatement> getStatements() {
        return getChildren(IntASTStatement.class);
    }

}
