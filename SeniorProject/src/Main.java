import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        CharStream cs = CharStreams.fromFileName("./src/HelloWorld.Java");
        JavaLexer javaLexer = new JavaLexer(cs);
        CommonTokenStream token = new CommonTokenStream(javaLexer);
        JavaParser parser = new JavaParser(token);
        ParseTree tree = parser.compilationUnit();

        MyVisitor visitor = new MyVisitor();
        visitor.visit(tree);

        System.out.println(tree.getText());

    }
}
