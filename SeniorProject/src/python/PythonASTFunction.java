package python;

public class PythonASTFunction extends AbstractPythonASTBranchNode {
    private boolean isMain = false;
    
    public PythonASTFunction() {
        super("");
    }

    @Override
    public void addChild(PythonASTNode child) {
        if (child == null) {
            return;
        } else if (child.getText().equals("def") || child.getText().equals(":")) {
            child.setParent(this);
            super.children.add(child);
        } else if (child instanceof PythonASTSuite
                || child instanceof PythonASTTerminal
                || child instanceof PythonASTParametersList) {
            child.setParent(this);
            super.children.add(child);
        } else {
            throw new IllegalArgumentException("PythonASTFunctionStatement does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
    
    public boolean isMain() {
        return this.isMain;
    }
    
    public void setMain(boolean isMain) {
        this.isMain = isMain;
    }
}

