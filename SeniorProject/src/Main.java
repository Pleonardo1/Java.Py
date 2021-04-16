import intermediate.IntASTCompilationUnit;
import intermediate.IntASTNode;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.*;
import java.util.Iterator;
import java.util.stream.Collectors;

import python.*;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;



public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {

        // Java Tools
        Label java_label = new Label("Java Code");
        TextArea javaArea = new TextArea();
        Button convert = new Button("Convert");

        // Python Tools
        Label py_label = new Label("Python Code");
        TextArea pythonArea = new TextArea();
        Button copy = new Button("Copy");

        // Button Handlers
        convert.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                ObservableList<CharSequence> paragraph = javaArea.getParagraphs();
                Iterator<CharSequence> iter = paragraph.iterator();
                try
                {
                    BufferedWriter input = new BufferedWriter(new FileWriter(new File("SeniorProject/src/Input.txt")));
                    while(iter.hasNext())
                    {
                        CharSequence seq = iter.next();
                        input.append(seq);
                        input.newLine();
                    }
                    input.flush();
                    input.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }

                // Generate Trees and Output
                treeGeneration();

                try {
                    BufferedReader output = new BufferedReader(new FileReader("SeniorProject/src/Output.txt"));
                    StringBuilder sb = new StringBuilder();
                    String line = output.readLine();

                    while (line != null) {
                        sb.append(line);
                        sb.append(System.lineSeparator());
                        line = output.readLine();
                    }
                    String everything = sb.toString();
                    output.close();

                    pythonArea.setText(everything);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // TODO: Copy to Clipboard Button
        copy.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

            }
        });

        // Scene Order
        GridPane gp = new GridPane();
        gp.setHgap(10);
        gp.setVgap(10);

        //Java Box
        gp.add(java_label, 1, 1, 1, 1);
        gp.add(javaArea, 1, 2, 1, 5);
        gp.add(convert, 1, 7, 1, 1);

        //Python Box
        gp.add(py_label, 1, 9, 1, 1);
        gp.add(pythonArea, 1, 10, 1, 5);
        gp.add(copy, 1, 15, 1, 1);

        primaryStage.setTitle("Java.Py");
        primaryStage.setScene(new Scene(gp, 1000, 600));
        primaryStage.show();
    }

    public static void treeGeneration() {
        // TODO CALL TO CONVERT OUTPUT
        CharStream cs = null;
        try {
            cs = CharStreams.fromFileName("SeniorProject/src/Input.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        JavaLexer javaLexer = new JavaLexer(cs);
        CommonTokenStream token = new CommonTokenStream(javaLexer);
        JavaParser parser = new JavaParser(token);
        ParseTree tree = parser.compilationUnit();

        System.out.println("Intermediate tree conversion:");
        IntASTNode intNode = toIntermediate(tree);
        System.out.println("\n\n");

        System.out.println("Python tree conversion:");
        try {
            FormatPy pythonNode = intermediateToPython((IntASTCompilationUnit) intNode);
        } catch (IOException e) {
            e.printStackTrace();
        }

        MyVisitor visitor = new MyVisitor();
        visitor.visit(tree);
    }

    public static void main(String[] args) throws IOException {
        launch(args);
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
