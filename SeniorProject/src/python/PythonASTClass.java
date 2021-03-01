package python;
import java.util.List;

public class PythonASTClass extends AbstractPythonASTBranchNode {
    
    public PythonASTClass (String className) {
        super(className);
    }
    
    public void addChild(PythonASTNode child) {
        if (child == null) {
            return;
        } else if (child instanceof PythonASTArgList) {
            if (super.children.isEmpty() ||
                    !(super.children.get(0) instanceof PythonASTArgList)) {
                // no existing inheritance list, so insert at the beginning
                child.setParent(this);
                super.children.add(0, child);
            } else {
                // this class already has an inheritance list defined, so
                // throw an exception
                throw new IllegalArgumentException("Cannot have more than one list of inherited types per class declaration");
        } else if (child instanceof PythonASTSuite) {
              // adding the class's body, so ensure one doesn't
              // already exist
              if (super.children.isEmpty() ||
                      !(super.children.get(super.children.size()-1) instanceof PythonASTSuite)) {
                  // no existing class body, so insert normally
                  child.setParent(this);
                  super.children.add(child);
              } else {
                  // this class already has a body defined, so
                  // throw an exception
                  throw new IllegalArgumentException("Cannot have more than one body per class declaration");
              }
        } else {
              throw new IllegalArgumentException("IntASTClass does not support children of type \""
                      + child.getClass().getName() + "\"");
              }
        
        
    }
}