import intermediate.IntASTNode;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import java.io.FileWriter;
import java.io.IOException;

import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        CharStream cs = CharStreams.fromFileName("SeniorProject/src/MyNum.java");
        JavaLexer javaLexer = new JavaLexer(cs);
        CommonTokenStream token = new CommonTokenStream(javaLexer);
        JavaParser parser = new JavaParser(token);
        ParseTree tree = parser.compilationUnit();

        System.out.println("Intermediate tree conversion:");
        toIntermediate(tree);

        MyVisitor visitor = new MyVisitor();
        visitor.visit(tree);

        /*  We created a visitor object in order to
            access the array list that appends the
            relevant node */
        MyVisitor visit = new MyVisitor();
        List<String> pyCode = visit.pyList;

        /*  Instantiating the class */
        pyCode.add("\n\n");
        pyCode.add("m"); pyCode.add("="); pyCode.add("MyNum()");
        pyCode.add("\n"); pyCode.add("m.main()");

        FileWriter myWriter = new FileWriter("SeniorProject/WebApp/Output.txt");
        myWriter.write(printPy(pyCode));
        myWriter.close();
    }


    //  Current method for formatting the Python code
    public static String printPy (List<String> myPy) {
        String output = "";

        for (int i = 0; i < myPy.size(); i++) {

            // we reach a colon, print : and start new line
            if(myPy.get(i).equals(":")) {
                output += myPy.get(i) + "\n";

            // we reach a semi-colon, start new line
            } else if (myPy.get(i).equals(";")) {
                output += "\n";

            // print current arraylist element
            }else {
                output += " " + myPy.get(i);
            }
        }

        return output;
    }

    public static IntASTNode toIntermediate(ParseTree javaAST) {
        // convert to intermediate
        JavaToIntermediate intermediate = new JavaToIntermediate();
        IntASTNode out = intermediate.visit(javaAST);
        printIntermediate(out, "");
        return out;
    }

    public static void printIntermediate(IntASTNode node, String indent) {
        System.out.println(indent + node.getClass().getSimpleName() + ": " + node.getText());
        indent += "  ";
        for (IntASTNode child : node.getChildren(IntASTNode.class)) {
            printIntermediate(child, indent);
        }
    }
}
