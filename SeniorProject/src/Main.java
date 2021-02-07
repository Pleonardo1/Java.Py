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

        List<String> myTokens = new ArrayList<>();
        List<String> myLiterals = new ArrayList<>();

        //Prints each token classifier and literal
        for (Token t : token.getTokens()) {
            myTokens.add(JavaLexer.VOCABULARY.getSymbolicName(t.getType()));
            myLiterals.add(t.getText());
        }

        MyVisitor visit = new MyVisitor();
        List<String> pyCode = visit.newTree;

        //ADDING THE INIT FUNCTION
        pyCode.add("\nif");
        pyCode.add("__name__");
        pyCode.add("==");
        pyCode.add("\"__main__\"");
        pyCode.add(":");
        pyCode.add("main()");

        printPy(pyCode);
    }

    public static void printPy (List<String> myPy) {

        for (int i = 0; i < myPy.size(); i++) {

            if (i == 0) {
                System.out.print("\n" + myPy.get(i));
            } else {
                if(myPy.get(i).equals(":")) {
                    System.out.print(myPy.get(i) + "\n\t");
                } else {
                    System.out.print(" " + myPy.get(i));
                }
            }
        }
    }

}
