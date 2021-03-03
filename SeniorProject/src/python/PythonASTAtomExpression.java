package python;

public class PythonASTAtomExpression extends AbstractPythonASTBranchNode implements PythonASTExpression {
    public PythonASTAtomExpression() {
        super("");
    }

    @Override
    public void addChild(PythonASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof PythonASTAtom
                || child instanceof PythonASTTrailer) {
            child.setParent(this);
            super.children.add(child);
        } else {
            throw new IllegalArgumentException("PythonASTAtomExpression does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
}
