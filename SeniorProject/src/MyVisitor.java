public class MyVisitor extends JavaBaseVisitor<Object>{
    @Override public Object visitCompilationUnit(JavaParser.CompilationUnitContext ctx) {
        System.out.println();
        System.out.println("Success!");
        return visitChildren(ctx);
    }

    @Override public Object visitPackageDeclaration(JavaParser.PackageDeclarationContext ctx) {
        return visitChildren(ctx);
    }

}
