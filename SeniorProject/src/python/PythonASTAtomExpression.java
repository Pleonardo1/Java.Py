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
        } else if (child instanceof PythonASTAtomExpression) {
            if (super.children.isEmpty()) {
                for (PythonASTNode node : child.getChildren()) {
                    addChild(node);
                }
            } else {
                PythonASTAtom atom = (PythonASTAtom) child.getChild(0);
                PythonASTTrailer trail = new PythonASTTrailer();
                // determine how to convert the atom child into a trailer
                if (!atom.getChild(0).getText().equals("(") &&
                        !atom.getChild(0).getText().equals("[")) {
                    // need a period first
                    trail.addChild(new PythonASTTerminal("."));
                }
                for (PythonASTNode node : atom.getChildren()) {
                    trail.addChild(node);
                }

                // add the new trailer to this atom expression
                addChild(trail);
                // add the original atom expression's trailers to this atom expression
                for (PythonASTTrailer node : child.getChildren(PythonASTTrailer.class)) {
                    addChild(node);
                }

                // clear the original atom expression's children
                ((PythonASTAtomExpression)child).children.clear();
            }
        } else {
            throw new IllegalArgumentException("PythonASTAtomExpression does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
}
