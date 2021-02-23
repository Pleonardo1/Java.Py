package intermediate;
import java.util.List;

public class IntASTIf extends AbstractIntASTBranchNode implements IntASTStatement {
    public IntASTIf () {super("");}

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
            throw new IllegalArgumentException("IntASTIf does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }

    public IntASTParExpression getParExpression () {
        return getChild(0, IntASTParExpression.class);
    }

    public IntASTStatement getStatement(int i) {
        return getChild(0, IntASTStatement.class);
    }
    
    public List<IntASTStatement> getStatement() {
        return getChildren(IntASTStatement.class);
    }
    
    public IntASTStatement getStatementNotParExpression() {
        return getChild(0, IntASTStatement.class, IntASTParExpression.class);
    }
}

