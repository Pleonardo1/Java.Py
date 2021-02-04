import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

public class MyVisitor extends JavaBaseVisitor<Object>{


    private JavaParser.PrimitiveTypeContext ctx;

    @Override public Object visitCompilationUnit(JavaParser.CompilationUnitContext ctx) {
        return visitChildren(ctx);
    }

    @Override public Object visitPackageDeclaration(JavaParser.PackageDeclarationContext ctx) {
        return visitChildren(ctx);
    }

    @Override public Object visitPrimitiveType(JavaParser.PrimitiveTypeContext ctx) {
        System.out.println("\n" + ctx.getText() + " ");
        return "var";
    }

}
