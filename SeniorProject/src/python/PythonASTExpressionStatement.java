package python;

public class PythonASTExpressionStatement extends AbstractPythonASTBranchNode {
    public PythonASTExpressionStatement() {
        super("");
    }

    @Override
    public void addChild(PythonASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof PythonASTExpressionList
                || child instanceof PythonASTExpression) {
            child.setParent(this);
            super.children.add(child);
        } else if (child instanceof PythonASTTerminal) {
            switch (child.getText()) {
                case ":":
                case "=":
                case "+=":
                case "-=":
                case "*=":
                case "@=":
                case "/=":
                case "%=":
                case "&=":
                case "|=":
                case "^=":
                case "\\=":
                case "<<=":
                case ">>=":
                case "**=":
                    child.setParent(this);
                    super.children.add(child);
                    break;
                default:
                    throw new IllegalArgumentException("PythonASTExpressionStatement does not " +
                            "accept the terminal child " + child.getText());
            }
        } else {
            throw new IllegalArgumentException("PythonASTExpressionStatement does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
}
