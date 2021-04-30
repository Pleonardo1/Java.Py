import intermediate.IntASTCompilationUnit;
import intermediate.IntASTNode;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.image.Image;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import java.io.*;
import java.util.Iterator;
import python.*;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {

        // Java Tools
        Label java_label = new Label("Java Code");
        java_label.setFont(Font.font("Arial", FontWeight.BOLD, 15));

        TextArea javaArea = new TextArea();
        Button import_ = new Button("Import");
        Button convert = new Button("Convert");
        Button clear = new Button("Clear");

        javaArea.setPrefHeight(525);
        javaArea.setStyle("-fx-control-inner-background:#454545");

        import_.setPrefWidth(70);
        convert.setPrefWidth(70);
        clear.setPrefWidth(70);

        FileChooser fileChooser1 = new FileChooser();
        fileChooser1.setTitle("Open Image");
        fileChooser1.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Java Files", "*.java"));

        // Python Tools
        Label py_label = new Label("Python Code");
        py_label.setFont(Font.font("Arial", FontWeight.BOLD, 15));

        TextArea pythonArea = new TextArea();
        Button copy = new Button("Copy");
        Button save = new Button("Save");

        pythonArea.setPrefHeight(525);
        pythonArea.setStyle("-fx-control-inner-background:#454545");

        copy.setPrefWidth(70);
        save.setPrefWidth(70);

        FileChooser fileChooser2 = new FileChooser();
        fileChooser2.setTitle("Save");
        fileChooser2.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Python Files", "*.py"));

        // Import button
        import_.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                pythonArea.setText("");

                //Opening a dialog box
                File f = fileChooser1.showOpenDialog(primaryStage);
                read(javaArea, f.toString());
            }});

        clear.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                javaArea.setText("");
                pythonArea.setText("");
            }});


        // Save button
        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //Opening a dialog box
                File f = fileChooser2.showSaveDialog(primaryStage);
                String py_code = pythonArea.getText();

                try {
                    FileWriter fw = new FileWriter(f);
                    fw.write(py_code);
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        // Convert Button
        convert.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Writes the Java Text Area Code to the Input file used for parsing
                write(javaArea, "SeniorProject/src/Input.txt");
                
                // Check if the Java File is empty and if not generate the parse tree
                File java_file = new File("SeniorProject/src/Input.txt");
                isEmptyFile(java_file);
                treeGeneration();
                
                // Reads the python output file and writes it to the Python Text Area
                read(pythonArea, "SeniorProject/src/Output.txt");
            }
        });

        // Create Clipboard for Copying Code
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();

        // Copy Button
        copy.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                content.putString(pythonArea.getText());
                clipboard.setContent(content);
            }
        });

        // Project Logo
        Image image = new Image("File:SeniorProject/src/JavaPy2.png");
        ImageView view = new ImageView(image);
        view.setPreserveRatio(true);
        view.setFitHeight(90);

        // Scene Order
        GridPane gp = new GridPane();
        gp.setHgap(10);
        gp.setVgap(10);

        BorderPane bp = new BorderPane();
        bp.setStyle("-fx-background: rgb(50,50,50);");
        bp.setTop(view);
        bp.setMargin(view, new Insets(10, 0, 20, 10));

        //Java Item Positioning
        gp.add(java_label, 1, 1, 1, 1);
        gp.add(javaArea, 1, 2, 1, 5);
        gp.add(import_, 2, 2, 1, 1);
        gp.add(convert, 2, 3, 1, 1);
        gp.add(clear, 2, 4, 1, 1);

        //Python Item Positioning
        gp.add(py_label, 4, 1, 1, 1);
        gp.add(pythonArea, 4, 2, 1, 5);
        gp.add(copy, 5, 2, 1, 1);
        gp.add(save, 5, 3, 1, 1);

        bp.setCenter(gp);

        primaryStage.setTitle("Java.Py");
        primaryStage.setScene(new Scene(bp, 1160, 700));
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    public static void isEmptyFile(File file) {
        if (file.length() <= 2) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("The file you are converting is empty!");
            alert.showAndWait();
        }
    }

    public static void write (TextArea area, String path) {
        ObservableList<CharSequence> paragraph = area.getParagraphs();
        Iterator<CharSequence> iter = paragraph.iterator();
        try
        {
            BufferedWriter input = new BufferedWriter(new FileWriter(new File(path)));
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
    }

    public static void read (TextArea area, String path) {
        try {
            BufferedReader output = new BufferedReader(new FileReader(path));
            StringBuilder sb = new StringBuilder();
            String line = output.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = output.readLine();
            }
            String everything = sb.toString();
            output.close();

            area.setText(everything);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void treeGeneration() {
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

        //System.out.println("Intermediate tree conversion:");
        IntASTNode intNode = toIntermediate(tree);
        //System.out.println("\n\n");

        //System.out.println("Python tree conversion:");
        try {
            FormatPy pythonNode = intermediateToPython((IntASTCompilationUnit) intNode);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException {
        launch(args);
    }

    public static IntASTNode toIntermediate(ParseTree javaAST) {
        // convert to intermediate
        JavaToIntermediate intermediate = new JavaToIntermediate();
        IntASTNode out = intermediate.visit(javaAST);
        //printIntermediate(out, "");
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
        
        //printPythonTree(out, "");
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
