package intermediate;

import java.util.List;

/**
 * Intermediate AST node that represents a set of method parameters.
 * May be used to represent the formal parameters of a method
 * declaration or the input parameters of a method call.
 */
public class IntASTMethodParameters extends AbstractIntASTBranchNode {
    public IntASTMethodParameters() {
        super("");
    }

    @Override
    public void addChild(IntASTNode child) {
        // check what kind of child is being added
        if (child == null) {
            return;
        } else if (child instanceof IntASTIdentifier) {
            // adding a method parameter
            child.setParent(this);
            if (super.children.isEmpty() ||
                    !super.children.get(super.children.size()-1).getText().equals("...")) {
                super.children.add(child);
            } else {
                super.children.add(super.children.size()-1, child);
            }
        } else if (child instanceof IntASTOperator && child.getText().equals("...")) {
            // adding an ellipsis (for variable arguments)
            // ensure one does not already exist
            if (super.children.isEmpty() || !super.children.get(super.children.size()-1).getText().equals("...")) {
                child.setParent(this);
                super.children.add(child);
            } else {
                throw new IllegalArgumentException("Cannot have more than one ellipsis in a set of method parameters");
            }
        } else {
            throw new IllegalArgumentException("intermediate.IntASTMethodParameters does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }
    
    public boolean isVarargs() {
        return getOperator() != null;
    }
    
    public IntASTIdentifier getIdentifier(int i) {
        return getChild(i, IntASTIdentifier.class);
    }
    
    public List<IntASTIdentifier> getIdentifier() {
        return getChildren(IntASTIdentifier.class);
    }
    
    public IntASTOperator getOperator() {
        return getChild(0, IntASTOperator.class);
    }
}
