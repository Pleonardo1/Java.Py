package python;

import intermediate.IntASTNode;

public class PythonASTArgList extends AbstractPythonASTBranchNode {

    public PythonASTArgList () {
        super("");
    }

    @Override
    public void addChild(PythonASTNode child) {
        // ArgList is one or multiple arguments
        if (child == null) {
            return;
        } else if (child instanceof PythonASTArgument) {
            //Arguments can be made up of "test" (identifier) assignments w/optional for loops (line 257 py grammar)
            child.setParent(this);
            super.children.add(child);
        } else if(child instanceof PythonASTArgList) {
            //Series of arguments
            for (PythonASTNode node : child.getChildren()) {
                addChild(node);
            }
            ((PythonASTArgList) child).children.clear();
        } else {
            throw new IllegalArgumentException("PythonASTArgList does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
}
