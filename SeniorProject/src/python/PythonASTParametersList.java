package python;

public class PythonASTParametersList extends AbstractPythonASTBranchNode {
    public PythonASTParametersList() {
        super("");
    }

    @Override
    public void addChild(PythonASTNode child){
        if (child == null) {
            return;

        } else if (child instanceof PythonASTTfpdef) {
            child.setParent(this);
            super.children.add(child);

        } else if (child instanceof PythonASTTerminal) {
            if (child.getText().equals(",")
                || child.getText().equals("=")) {
                child.setParent(this);
                super.children.add(child);
            } else {
                throw new IllegalArgumentException("PythonASTParametersListStatement only accepts the terminal children" +
                        " ',' and '='; " + child.getText());
            }
        } else if (child instanceof PythonASTExpression) {
            child.setParent(this);
            super.children.add(child);
        } else {
            throw new IllegalArgumentException("PythonASTParametersList does not support children of type \""
                     + child.getClass().getName() + "\"");
        }

    }
 }