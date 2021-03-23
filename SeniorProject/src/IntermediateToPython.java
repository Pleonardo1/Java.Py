import intermediate.*;
import python.*;

public class IntermediateToPython {
    public PythonASTFileInput visitCompilationUnit(IntASTCompilationUnit ctx) {
        PythonASTFileInput root = new PythonASTFileInput();

        // convert the imports
        for (IntASTImport node : ctx.getImportDeclaration()) {
            root.addChild(visitImport(node));
        }
        // convert the class declarations
        for (IntASTClass node : ctx.getClassDeclaration()) {
            // TODO continue visitors from here
        }

        return root;
    }

    public PythonASTImportStatement visitImport(IntASTImport ctx) {
        PythonASTImportStatement root = new PythonASTImportStatement();

        String name = ctx.getText();
        String packageName = name.substring(0, name.lastIndexOf("."));
        String className = name.substring(name.lastIndexOf(".") + 1);

        // "import java.util.*" -> "from java.util import *"
        root.addChild(new PythonASTTerminal("from"));
        root.addChild(new PythonASTTerminal(packageName));
        root.addChild(new PythonASTTerminal("import"));
        root.addChild(new PythonASTTerminal(className));

        return root;
    }
}
