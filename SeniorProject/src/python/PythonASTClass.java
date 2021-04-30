package python;
import java.util.List;

public class PythonASTClass extends AbstractPythonASTBranchNode {
    
    public PythonASTClass () {
        super("");
    }
    
    @Override
    public void addChild(PythonASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof PythonASTTerminal) {
            child.setParent(this);
            super.children.add(child);
        } else if (child instanceof PythonASTArgList) {

                child.setParent(this);
                super.children.add(child);

        } else if (child instanceof PythonASTSuite) {

                  child.setParent(this);
                  super.children.add(child);

        } else {
              throw new IllegalArgumentException("IntASTClass does not support children of type \""
                      + child.getClass().getName() + "\"");
        }
    }
}