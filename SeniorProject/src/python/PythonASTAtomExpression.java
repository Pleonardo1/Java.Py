package python;

public class PythonASTAtomExpression extends AbstractPythonASTBranchNode implements PythonASTExpression {
    public PythonASTAtomExpression() {
        super("");
    }

    @Override
    public void addChild(PythonASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof PythonASTAtom) {
            if (getChildCount(PythonASTAtom.class) == 0) {
                child.setParent(this);
                super.children.add(child);
            } else {
                throw new IllegalArgumentException("Cannot have more than 1 child of type PythonASTAtom");
            }
        } else if (child instanceof PythonASTTrailer) {
            child.setParent(this);
            super.children.add(child);
        } else {
            throw new IllegalArgumentException("PythonASTAtomExpression does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
}
