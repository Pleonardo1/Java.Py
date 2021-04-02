package python;

import java.util.List;

public class PythonASTBinaryExpression extends AbstractPythonASTBranchNode implements PythonASTExpression {
    public PythonASTBinaryExpression() {
        super("");
    }

    @Override
    public void addChild(PythonASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof PythonASTExpression || child instanceof PythonASTTerminal) {
            // can only have two expression, at index 0 and index 2
            // can only have one terminal, at index 1
            if (super.children.size() < 3) {
                child.setParent(this);
                super.children.add(child);
            } else {
                // child violates condition of having only 2 expressions and 1 terminal
                throw new IllegalArgumentException("A binary expression can only have 3 children: 2 expressions and 1 terminal");
            }
        } else {
            throw new IllegalArgumentException("PythonASTBinaryStatement does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
}
