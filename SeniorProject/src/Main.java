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
        //CharStream cs = CharStreams.fromFileName("./test.txt");
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

        Transpile pycode = new Transpile(myTokens, myLiterals);

        List<String> literals = pycode.transpiler();
        System.out.println();

        // Need to find someway to account for spacing
        for (int i = 0; i < literals.size(); i++) {
            System.out.print(literals.get(i));
        }

    }

}
