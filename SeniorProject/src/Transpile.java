import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;

public class Transpile {

    List<String> tokens;
    List<String> literals;

    public Transpile(List<String> A, List<String> B) {
        this.tokens = new ArrayList<>(A);
        this.literals = new ArrayList<>(B);
    }

    // Temporary logic to transpile sample code
    public List<String> transpiler() {

        //Attempt to remove main method from array list
        /* ArrayList<String> psvm = new ArrayList<String>() {{
            add("public"); //1
            add("Static"); //2
            add("void");   //3
            add("main");   //4
            add("(");      //5
            add("String"); //6
            add("[");      //7
            add("]");      //8
            add("args");   //9
            add(")");      //10
        }};

        int psvmindex= literals.indexOf("public");
        if (literals.contains("public")){
            literals.subList(psvmindex, psvmindex + 9).clear();
        } */

        // Trial transpilation of the literals using the vocab term
        for (int i = 0; i < tokens.size(); i++) {
            //Left Brace counter for indentation
            int numOfLbrace = 0;

            //System.out.println("THIS IS TOKENS[" + j + "]: " + tokens[j] + " ");
            switch(tokens.get(i)) {

                // Only alters literal array for Main method
                case "LBRACE":
                    numOfLbrace += 1;
                    //tokens.indexOf(j).equals("SEMI");
                    // Need someway to update the # of indents
                    literals.set(i, ":\n" + stringMultiply("\t", numOfLbrace));
                    break;
                case "INT":
                case "FLOAT":
                case "LONG":
                    literals.set(i, "var");
                    break;
                // Termination of a statement so we start a new line
                case "SEMI":
                    literals.set(i, "\n");
                    break;
                case "RBRACE":
                case "EOF":
                    literals.set(i, "");
                    break;
            }
        }


        //Attempting to manipulate spacing
        for (int k = 0; k < tokens.size() - 1; k++) {
            if (tokens.get(k+1).equals("LPAREN") || tokens.get(k+1).equals("RPAREN")
                    || tokens.get(k+1).equals("RBRACK") || tokens.get(k+1).equals("SEMI")
                    || tokens.get(k).equals("LPAREN")) {
                continue;
            } else {
                //literals.add(k, " ");
            }
        }
        return literals;
    }


    // Intended to help with indentation
    // Not working properly as of now
    public String stringMultiply(String s, int n){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < n; i++){
            sb.append(s);
        }
        return sb.toString();
    }


    // Useful in converting long code literals such as PSVM(S[]A)
    public boolean isSubArray (String[] first, String[] second) {
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
