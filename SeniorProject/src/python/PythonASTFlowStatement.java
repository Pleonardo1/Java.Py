package python;

public class PythonASTFlowStatement extends AbstractPythonASTBranchNode {
    public PythonASTFlowStatement() {
        super("");
    }

    @Override
    public void addChild(PythonASTNode child) {
        if (child == null) {
            return;
            //unsure if there needs to be a new function for each item
        } else if (child instanceof PythonASTTerminal) {
            if (child.getText().equals("break")
                    || child.getText().equals("continue")
                    || child.getText().equals("return")
                    || child.getText().equals("raise")) {
                child.setParent(this);
                super.children.add(child);
            } else {
                throw new IllegalArgumentException("PythonASTFlowStatement only accepts the terminal children" +
                        " ‘break’, 'continue', 'return', 'raise' ; " + child.getText());
            }
        } else if (child instanceof PythonASTTest) {
            child.setParent(this);
            super.children.add(child);
        } else {
            throw new IllegalArgumentException("PythonASTImportStatement does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
}
