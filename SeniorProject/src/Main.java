import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import java.io.IOException;


public class Main {

    public static void main(String[] args) throws IOException {

        CharStream cs = CharStreams.fromFileName("./src/MyNum.Java");
        JavaLexer javaLexer = new JavaLexer(cs);
        CommonTokenStream token = new CommonTokenStream(javaLexer);
        JavaParser parser = new JavaParser(token);
        ParseTree tree = parser.compilationUnit();

        MyVisitor visitor = new MyVisitor();
        visitor.visit(tree);

        //Prints each token classifier and literal
        /*for (Token t : token.getTokens()) {
            System.out.printf("%-20s %s\n", JavaLexer.VOCABULARY.getSymbolicName(t.getType()), t.getText());
        }*/

        String[] pycode = transpile(token);

        // Need to find someway to account for spacing
        for (int i = 0; i < pycode.length; i++) {
            System.out.print(pycode[i]);
        }

    }

    public static String[] transpile(CommonTokenStream myTokens) {

        //Array of the token vocab terms
        String[] tokens = new String[myTokens.getNumberOfOnChannelTokens()];
        //Array of parsed literals
        String[] literals = new String[tokens.length];

        // # of tokens
        int streamLength = myTokens.getNumberOfOnChannelTokens();
        int i = 0;

        // Initializes values for the tokens and literals array
        for (Token t : myTokens.getTokens()) {
            tokens[i] = JavaLexer.VOCABULARY.getSymbolicName(t.getType());
            literals[i] = t.getText();
            i++;
        }

        // Trial transpilation of the literals using the vocab term
        for (int j = 0; j < tokens.length; j++) {
            //Left Brace counter for indentation
            int numOfLbrace = 0;
            String[] mainMethod = {"public", "static", "void", "main",
                                    "(", "String", "[", "]", "args", ")"};

            System.out.println("THIS IS TOKENS[" + j + "]: " + tokens[j] + " ");
            switch(tokens[j]) {

                // Only alters literal array for Main method
                /*case "PUBLIC":
                    if (isSubArray(literals, mainMethod)) {
                        literals[j] = "def main()";
                    }*/
                case "LBRACE":
                    numOfLbrace += 1;
                    tokens[j] = "SEMI";
                    // Need someway to update the # of indents
                    literals[j] = ":\n" + stringMultiply("\t", numOfLbrace);
                    break;
                case "INT":
                case "FLOAT":
                case "LONG":
                    literals[j] = "var";
                    break;
                // Termination of a statement so we start a new line
                case "SEMI":
                    literals[j] = "\n";
                    break;
                case "RBRACE":
                case "EOF":
                    literals[j] = "";
                    break;
            }
        }

        //Attempting to manipulate spacing
        for (int k = 0; k < tokens.length - 1; k++) {
            if (tokens[k+1].equals("LPAREN") || tokens[k + 1].equals("RPAREN")
                    || tokens[k+1].equals("RBRACK") || tokens[k+1].equals("SEMI")
                    || tokens[k].equals("LPAREN")) {
                continue;
            } else {
                literals[k] += " ";
            }
        }
        return literals;
    }

    // Intended to help with indentation
    // Not working properly as of now
    public static String stringMultiply(String s, int n){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < n; i++){
            sb.append(s);
        }
        return sb.toString();
    }

    // Useful in converting long code literals such as PSVM(S[]A)
    public static boolean isSubArray (String[] first, String[] second) {
        int i = 0;
        int j = 0;

        //Iterate through both arrays simultaneously
        while (i < first.length && j < second.length) {

            if (first[i] == first[j]) {
                i++;
                j++;

                //once the j makes it to the end it must be a sub array
                if (j == second.length - 1) {
                    return true;
                }
            //Iterate i and reset J
            } else {
                i = i - j + 1;
                j = 0;
            }
        }
        return false;
    }

}
