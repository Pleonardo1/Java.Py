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
            child.setParent(this);
            super.children.add(child);

        } else if (child instanceof PythonASTTerminal.PythonASTIndent) {
            // Indent is reached
            child.setParent(this);
            super.children.add(child);

        } else if (child instanceof PythonASTStatement) {
            // Can have one or more statements in a suite
            child.setParent(this);
            super.children.add(child);

        } else if (child instanceof PythonASTTerminal.PythonASTDedent) {
            // Detent terminates end of a suit
             child.setParent(this);
             super.children.add(child);

        } else {
            throw new IllegalArgumentException("IntASTBlock does not support children of type \""
                   + child.getClass().getName() + "\"");
        }
    }
}