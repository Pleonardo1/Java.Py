package python;

public class PythonASTArgument extends AbstractPythonASTBranchNode{

    public PythonASTArgument () {super("");}

    public void addChild(PythonASTNode child) {

        if (child == null) {
            return;
        } else if (child instanceof PythonASTType) {

        } else if (child instanceof PythonASTFor) {

        } else {

        }

    }
}
