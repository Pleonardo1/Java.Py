package intermediate;

import java.util.List;

public class IntASTDo extends AbstractIntASTBranchNode implements IntASTStatement {
    public IntASTDo() {super("");}

    @Override
    public void addChild(IntASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof IntASTParExpression) {
            // parExpression should follow a statement
            // TODO parExpression logic
            child.setParent(this);
            super.children.add(child);
        } else if (child instanceof IntASTStatement) {
            // first child should be an Statement
            // TODO statement logic
            child.setParent(this);
            super.children.add(child);
        } else {
            throw new IllegalArgumentException("IntASTDo does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
    
    public IntASTStatement getStatement(int i) {
        return getChild(i, IntASTStatement.class);
    }
    
    public List<IntASTStatement> getStatement() {
        return getChildren(IntASTStatement.class);
    }
    
    public IntASTParExpression getParExpression() {
        return getChild(0, IntASTParExpression.class);
    }
    
    public IntASTStatement getStatementNotParExpression() {
        return getChild(0, IntASTStatement.class, IntASTParExpression.class);
    }
}