import intermediate.IntASTCompilationUnit;
import intermediate.IntASTNode;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import java.io.FileWriter;
import java.io.IOException;
import python.*;

public class Main {

    public static void main(String[] args) throws IOException {

        CharStream cs = CharStreams.fromFileName("SeniorProject/src/MyNum.java");
        JavaLexer javaLexer = new JavaLexer(cs);
        CommonTokenStream token = new CommonTokenStream(javaLexer);
        JavaParser parser = new JavaParser(token);
        ParseTree tree = parser.compilationUnit();

        System.out.println("Intermediate tree conversion:");
        IntASTNode intNode = toIntermediate(tree);
        System.out.println("\n\n");

        System.out.println("Python tree conversion:");
        FormatPy pythonNode = intermediateToPython((IntASTCompilationUnit) intNode);

        MyVisitor visitor = new MyVisitor();
        visitor.visit(tree);

    }

    public static IntASTNode toIntermediate(ParseTree javaAST) {
        // convert to intermediate
        JavaToIntermediate intermediate = new JavaToIntermediate();
        IntASTNode out = intermediate.visit(javaAST);
        printIntermediate(out, "");
        return out;
    }

    public static FormatPy intermediateToPython(IntASTCompilationUnit root) throws IOException {

        // convert  intermediate to python
        IntermediateToPython python = new IntermediateToPython();
        PythonASTNode out = python.visitCompilationUnit(root);
        FormatPy myPy = new FormatPy(out);
        
        FileWriter myWriter = new FileWriter("SeniorProject/src/Output.txt");
        myPy.output(myWriter);
        myWriter.close();
        
        printPythonTree(out, "");
        return myPy;
    }

    public static void printIntermediate(IntASTNode node, String indent) {
        System.out.println(indent + node.getClass().getSimpleName() + ": " + node.getText());
        indent += "  ";
        for (IntASTNode child : node.getChildren()) {
            printIntermediate(child, indent);
        }
    }

    public static void printPythonTree(PythonASTNode node, String indent) {
        System.out.println(indent + node.getClass().getSimpleName() + ": " + node.getText());
        indent += "  ";
        for (PythonASTNode child : node.getChildren()) {
            printPythonTree(child, indent);
        }
    }
}
