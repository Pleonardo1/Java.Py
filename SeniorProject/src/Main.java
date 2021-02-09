import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        CharStream cs = CharStreams.fromFileName("./src/MyNum.Java");
        JavaLexer javaLexer = new JavaLexer(cs);
        CommonTokenStream token = new CommonTokenStream(javaLexer);
        JavaParser parser = new JavaParser(token);
        ParseTree tree = parser.compilationUnit();

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

        // Call to formatting/output function
        printPy(pyCode);
    }

    /*
        Current method for formatting and
        printing the Python code
     */
    public static void printPy (List<String> myPy) {

        for (int i = 0; i < myPy.size(); i++) {

            // Print the first element on new line
            if (i == 0) {
                System.out.print("\n" + myPy.get(i));

            } else {

                // we reach a colon, print : and start new line
                if(myPy.get(i).equals(":")) {
                    System.out.print(myPy.get(i) + "\n");

                // we reach a semi-colon, start new line
                } else if (myPy.get(i).equals(";")) {
                    System.out.print("\n");

                // print current arraylist element
                }else {
                    System.out.print(" " + myPy.get(i));
                }
            }
        }
    }
}
