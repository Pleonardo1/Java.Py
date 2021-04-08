import python.*;

import java.io.FileWriter;
import java.io.IOException;

public class FormatPy {

    private static String indent = "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t";

    int indentCount = 0;
    PythonASTNode pyTree;
    boolean needIndent = false;

    public FormatPy (PythonASTNode pyTree) {
        this.pyTree = pyTree;
    }

    public void output(FileWriter out) throws IOException {
        output(out, this.pyTree);
    }

    private void output(FileWriter out, PythonASTNode node) throws IOException {
        if (node instanceof PythonASTTerminal) {

            if (node instanceof PythonASTTerminal.PythonASTIndent) {
                indentCount++;

            } else if (node instanceof PythonASTTerminal.PythonASTNewline) {
                out.append("\n");
                needIndent = true;

            } else if (node instanceof PythonASTTerminal.PythonASTDedent) {
                indentCount--;

            } else {
                if (needIndent) {
                    out.append(indent.substring(0, indentCount));
                    needIndent = false;
                }
                out.append(node.getText());

                switch (node.getText()) {
                    case "(":
                    case ")":
                    case "[":
                    case ":":
                        break;

                    default:
                        out.append(" ");
                }

            }

        } else {
            for (PythonASTNode child : node.getChildren()) {
                output(out, child);
            }
        }
    }

}