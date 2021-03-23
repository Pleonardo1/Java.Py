package python;
import java.util.List;

public class PythonASTSuite extends AbstractPythonASTBranchNode {
    public PythonASTSuite () {
        super("");
    }

    public void addChild(PythonASTNode child) {
        if(child == null) {
            return;
        } else if (child instanceof PythonASTSimpleStatement) {
            //Reached simple Statment
            if (super.children.isEmpty()) {
                child.setParent(this);
                super.children.add(child);
            } else {
                throw new IllegalArgumentException("Cannot have more than 1 child with a child of type PythonASTSimpleStatement");
            }
        } else if (child instanceof PythonASTTerminal.PythonASTNewline
                || child instanceof PythonASTTerminal.PythonASTIndent
                || child instanceof PythonASTTerminal.PythonASTDedent
                || child instanceof PythonASTStatement) {
            // ensure we aren't mixing rule variations
            if (getChildCount(PythonASTSimpleStatement.class) != 0) {
                throw new IllegalArgumentException("Cannot have more than 1 child with a child of type PythonASTSimpleStatement");
            }
            // verify the child constraints
            if (child instanceof PythonASTTerminal.PythonASTNewline) {
                // adding a newline; ensure we only get 1
                if (getChildCount(PythonASTTerminal.PythonASTNewline.class) == 0) {
                    child.setParent(this);
                    super.children.add(child);
                } else {
                    throw new IllegalArgumentException("Cannot have more than 1 child of type PythonASTNewline");
                }
            } else if (child instanceof PythonASTTerminal.PythonASTIndent) {
                // adding an indent; ensure we only get 1
                if (getChildCount(PythonASTTerminal.PythonASTIndent.class) == 0) {
                    child.setParent(this);
                    super.children.add(child);
                } else {
                    throw new IllegalArgumentException("Cannot have more than 1 child of type PythonASTIndent");
                }
            } else if (child instanceof PythonASTTerminal.PythonASTDedent) {
                // adding a dedent; ensure we only get 1
                if (getChildCount(PythonASTTerminal.PythonASTDedent.class) == 0) {
                    child.setParent(this);
                    super.children.add(child);
                } else {
                    throw new IllegalArgumentException("Cannot have more than 1 child of type PythonASTDedent");
                }
            } else {
                // adding a statement
                child.setParent(this);
                super.children.add(child);
            }
        } else {
            throw new IllegalArgumentException("PythonASTSuite does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
}