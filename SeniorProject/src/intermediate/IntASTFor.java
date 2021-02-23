package intermediate;
import java.util.List;

public class IntASTFor extends AbstractIntASTBranchNode implements IntASTStatement {
    public IntASTFor() {
        super("");
    }

    @Override
    public void addChild(IntASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof IntASTForControl) {
            // adding the for control. ensure one does not
            // already exist
            if (super.children.isEmpty() ||
                    !(super.children.get(0) instanceof IntASTForControl)) {
                // no existing for control
                child.setParent(this);
                super.children.add(0, child);
            } else {
                // this for already has its for control
                throw new IllegalArgumentException("Cannot have more than for control per for loop");
            }
        } else if (child instanceof IntASTStatement) {
            // adding the for loop's body. ensure one does not
            // already exist
            if (super.children.isEmpty() ||
                    !(super.children.get(super.children.size()-1) instanceof IntASTStatement)) {
                // no existing loop body
                child.setParent(this);
                super.children.add(child);
            } else {
                // this for loop already has its body
                throw new IllegalArgumentException("Cannot have more than body per for loop");
            }
        } else {
            throw new IllegalArgumentException("IntASTFor does not support children of type \""
                    + child.getClass().getName() + "\"");
        }
    }

    public IntASTForControl getForControl() {
        return getChild(0, IntASTForControl.class);
    }

    public List<IntASTStatement> getStatement() {
        return getChildren(IntASTStatement.class);
    }
    
    public IntASTStatement getStatement(int i) {
            return getChild(i, IntASTStatement.class);
        }

}
