package python;

public class PythonASTTfpdef extends AbstractPythonASTBranchNode {
    public PythonASTTfpdef() {
        super("");
    }
    
    public void addChild(PythonASTNode child) {
        if (child == null) {
            return;
        
        } else if (child instanceof PythonASTTerminal) {
            child.setParent(this);
            super.children.add(child);
            
        } else if (child instanceof PythonASTExpression) {
            child.setParent(this);
            super.children.add(child);
            
        } else {
            throw new IllegalArgumentException("PythonASTTfpdef does not support children of type \""
                                + child.getClass().getName() + "\"");
        }
    
    }
}