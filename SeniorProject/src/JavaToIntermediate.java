import intermediate.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

// TODO Determine a method of tracking whether a variable reference is local or global

/**
 * Uses the Java parse tree defined by Java.g4 and created by
 * the class {@link JavaParser} to build an Intermediate AST.
 * Since Java and Python have many structural similarities
 * (if-else statements, for loops, while loops, methods, classes,
 * etc.), much of the construction of the Intermediate AST
 * consists of simplifying the Java parse tree and cutting out
 * Java elements that don't exist in Python such as visibility
 * modifiers and variable type declarations.
 */
public class JavaToIntermediate extends JavaBaseVisitor<IntASTNode> {

    /**
     * Top-level tree traversal. The node returned from this
     * represents an entire Java source file that has been stripped
     * of most of the elements of the Java language that don't
     * exist in Python, such as visibility modifiers and variable
     * type declarations.
     *
     * @param ctx The root node of the Java parse tree to traverse
     * @return The Intermediate AST root node
     */
    @Override
    public IntASTCompilationUnit visitCompilationUnit(JavaParser.CompilationUnitContext ctx) {
        IntASTCompilationUnit root = new IntASTCompilationUnit();
        // convert the package declaration
        if (ctx.packageDeclaration() != null) {
            root.addChild(visitPackageDeclaration(ctx.packageDeclaration()));
        }
        // convert the import declaration(s)
        for (JavaParser.ImportDeclarationContext importCtx : ctx.importDeclaration()) {
            root.addChild(visitImportDeclaration(importCtx));
        }
        // convert the type declaration(s)
        for (JavaParser.TypeDeclarationContext typeCtx : ctx.typeDeclaration()) {
            root.addChild(visitTypeDeclaration(typeCtx));
        }
        // return the compilation unit node
        return root;
        // this ignores annotations
    }

    /**
     * Convert a Java package declaration to the Intermediate AST.
     * Simply take the package name and store it in another node.
     * No conversion is necessary here.
     */
    @Override
    public IntASTPackage visitPackageDeclaration(JavaParser.PackageDeclarationContext ctx) {
        return new IntASTPackage(ctx.qualifiedName().getText());
    }

    /**
     * Convert a Java import declaration to the Intermediate AST.
     * Take the qualified name of the import and store it in a new
     * node. The Java parse tree does not include wildcards ".*" in
     * qualified names, so ensure that does not get lost in the
     * conversion.
     * <p>
     *     <b>Currently static imports are not being converted.</b>
     * </p>
     */
    @Override
    public IntASTImport visitImportDeclaration(JavaParser.ImportDeclarationContext ctx) {
        if (ctx.STATIC() != null) {
            // TODO Figure out how to convert Java static imports to a Python equivalent
            throw new UnsupportedOperationException("Static imports currently not supported");
        }
        String name = ctx.qualifiedName().getText();
        if (ctx.MUL() != null) {
            // ensure wildcard imports are handled
            name += ".*";
        }
        return new IntASTImport(name);
    }

    /**
     * Convert a Java type declaration to the Intermediate AST.
     * This node delegates directly to deciding whether the type is
     * a Class or an Interface.
     */
    @Override
    public IntASTClass visitTypeDeclaration(JavaParser.TypeDeclarationContext ctx) {
        if (ctx.classOrInterfaceDeclaration() == null) {
            // TODO add interface support
            return null;
        }
        return visitClassOrInterfaceDeclaration(ctx.classOrInterfaceDeclaration());
    }

    @Override
    public IntASTTypeList visitTypeList(JavaParser.TypeListContext ctx) {
        IntASTTypeList root = new IntASTTypeList();
        for (JavaParser.TypeContext type : ctx.type()) {
            root.addChild(visitType(type));
        }
        return root;
    }

    @Override
    public IntASTIdentifier visitType(JavaParser.TypeContext ctx) {
        // TODO implement type specification conversion (for stuff like type checking and casting)
        IntASTIdentifier root;
        if (ctx.classOrInterfaceType() != null) {
            root = visitClassOrInterfaceType(ctx.classOrInterfaceType());
        } else {
            root = visitPrimitiveType(ctx.primitiveType());
        }
        if (!ctx.LBRACK().isEmpty()) {
            StringBuilder name = new StringBuilder(root.getText());
            for (int i = ctx.LBRACK().size(); i > 0; i--) {
                name.append("[]");
            }
            root.setText(name.toString());
        }
        return root;
    }

    @Override
    public IntASTIdentifier visitClassOrInterfaceType(JavaParser.ClassOrInterfaceTypeContext ctx) {
        return null;
    }

    /**
     * Convert a Java Class or a Java Interface to the
     * Intermediate AST. Classes and Interfaces are treated
     * almost identically (even though they are converted
     * separately due to how the Java parse tree is structured)
     * with the main difference being that Interfaces are
     * automatically set as being abstract.
     */
    @Override
    public IntASTClass visitClassOrInterfaceDeclaration(JavaParser.ClassOrInterfaceDeclarationContext ctx) {
        IntASTClass root;
        if (ctx.classDeclaration() != null) {
            root = visitClassDeclaration(ctx.classDeclaration());
            // check whether the class is abstract
            for (JavaParser.ClassOrInterfaceModifierContext mod : ctx.classOrInterfaceModifiers().classOrInterfaceModifier()) {
                if (mod.ABSTRACT() != null) {
                    root.setAbstract(true);
                }
            }
        } else {
            root = visitInterfaceDeclaration(ctx.interfaceDeclaration());
            // interfaces are automatically abstract
            root.setAbstract(true);
        }
        return root;
    }

    @Override
    public IntASTClass visitClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
        if (ctx.normalClassDeclaration() != null) {
            return visitNormalClassDeclaration(ctx.normalClassDeclaration());
        } else {
            // TODO Handle Enums somehow
            throw new UnsupportedOperationException("Enums not supported");
        }
    }

    @Override
    public IntASTClass visitNormalClassDeclaration(JavaParser.NormalClassDeclarationContext ctx) {
        IntASTClass root = new IntASTClass(ctx.Identifier().getText());
        // get the inherited class
        IntASTTypeList inherit = new IntASTTypeList();
        if (ctx.type() != null) {
            inherit.addChild(new IntASTIdentifier(ctx.type().classOrInterfaceType().Identifier(0).getText()));
        }
        // get the inherited interfaces
        if (ctx.typeList() != null) {
            inherit.addChild(visitTypeList(ctx.typeList()));
        }
        // add the inherited types
        if (inherit.getChildCount() != 0) {
            root.addChild(inherit);
        }
        // get the class body
        root.addChild(visitClassBody(ctx.classBody()));
        // check whether the class is abstract
        return root;
        // ignores generic-type arguments (the ones in "<>")
    }

    @Override
    public IntASTClassBody visitClassBody(JavaParser.ClassBodyContext ctx) {
        IntASTClassBody root = new IntASTClassBody();
        // collect the declarations within the class body
        if (ctx.classBodyDeclaration() != null) {
            for (JavaParser.ClassBodyDeclarationContext decl : ctx.classBodyDeclaration()) {
                root.addChild(visitClassBodyDeclaration(decl));
            }
        }
        return root;
    }

    @Override
    public IntASTMember visitClassBodyDeclaration(JavaParser.ClassBodyDeclarationContext ctx) {
        if (ctx.block() != null) {
            IntASTBlock root = visitBlock(ctx.block());
            if (ctx.STATIC() != null) {
                root.setStatic(true);
            }
            return root;
        } else if (ctx.memberDecl() != null) {
            IntASTMember root = visitMemberDecl(ctx.memberDecl());
            // check whether the member is abstract
            // only applies to methods
            if (root instanceof IntASTMethod) {
                for (JavaParser.ModifierContext mod : ctx.modifiers().modifier()) {
                    if (mod.ABSTRACT() != null) {
                        ((IntASTMethod) root).setAbstract(true);
                    }
                }
            }
            return root;
            // ignores all other modifiers on member declarations
        } else {
            return null;
        }
    }

    @Override
    public IntASTMember visitMemberDecl(JavaParser.MemberDeclContext ctx) {
        // branch out into the different types of members
        if (ctx.genericMethodOrConstructorDecl() != null) {
            return visitGenericMethodOrConstructorDecl(ctx.genericMethodOrConstructorDecl());
        } else if (ctx.memberDeclaration() != null) {
            return visitMemberDeclaration(ctx.memberDeclaration());
        } else if (ctx.voidMethodDeclaratorRest() != null) {
            // non-generic void-return method
            IntASTMethod root = new IntASTMethod(ctx.Identifier().getText());
            JavaParser.VoidMethodDeclaratorRestContext voidCtx = ctx.voidMethodDeclaratorRest();
            root.addChild(visitFormalParameters(voidCtx.formalParameters()));
            if (voidCtx.methodBody() != null) {
                root.addChild(visitMethodBody(voidCtx.methodBody()));
            }
            return root;
        } else if (ctx.constructorDeclaratorRest() != null) {
            // non-generic constructor
            IntASTConstructor root = new IntASTConstructor(ctx.Identifier().getText());
            JavaParser.ConstructorDeclaratorRestContext constCtx = ctx.constructorDeclaratorRest();
            root.addChild(visitFormalParameters(constCtx.formalParameters()));
            root.addChild(visitConstructorBody(constCtx.constructorBody()));
            return root;
        } else if (ctx.interfaceDeclaration() != null) {
            return visitInterfaceDeclaration(ctx.interfaceDeclaration());
        } else {
            return visitClassDeclaration(ctx.classDeclaration());
        }
    }

    @Override
    public IntASTMember visitGenericMethodOrConstructorDecl(JavaParser.GenericMethodOrConstructorDeclContext ctx) {
        // ignore the generic arguments
        // TODO Decide whether generic arguments can be converted into Python in some form
        return visitGenericMethodOrConstructorRest(ctx.genericMethodOrConstructorRest());
    }

    @Override
    public IntASTMember visitGenericMethodOrConstructorRest(JavaParser.GenericMethodOrConstructorRestContext ctx) {
        if (ctx.methodDeclaratorRest() != null) {
            // ignore method return type
            IntASTMethod root = new IntASTMethod(ctx.Identifier().getText());
            JavaParser.MethodDeclaratorRestContext methCtx = ctx.methodDeclaratorRest();
            root.addChild(visitFormalParameters(methCtx.formalParameters()));
            if (methCtx.methodBody() != null) {
                root.addChild(visitMethodBody(methCtx.methodBody()));
            }
            return root;
        } else {
            IntASTConstructor root = new IntASTConstructor(ctx.Identifier().getText());
            JavaParser.ConstructorDeclaratorRestContext constCtx = ctx.constructorDeclaratorRest();
            root.addChild(visitFormalParameters(constCtx.formalParameters()));
            root.addChild(visitConstructorBody(constCtx.constructorBody()));
            return root;
        }
    }

    @Override
    public IntASTMember visitMemberDeclaration(JavaParser.MemberDeclarationContext ctx) {
        // ignore member type
        if (ctx.methodDeclaration() != null) {
            return visitMethodDeclaration(ctx.methodDeclaration());
        } else {
            return visitFieldDeclaration(ctx.fieldDeclaration());
        }
    }

    @Override
    public IntASTMethod visitMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        IntASTMethod root = new IntASTMethod(ctx.Identifier().getText());
        JavaParser.MethodDeclaratorRestContext methCtx = ctx.methodDeclaratorRest();
        root.addChild(visitFormalParameters(methCtx.formalParameters()));
        if (methCtx.methodBody() != null) {
            root.addChild(visitMethodBody(methCtx.methodBody()));
        }
        return root;
    }

    @Override
    public IntASTMethodParameters visitFormalParameters(JavaParser.FormalParametersContext ctx) {
        IntASTMethodParameters root = new IntASTMethodParameters();
        // the formalParameterDecls rule is recursive
        JavaParser.FormalParameterDeclsContext param = ctx.formalParameterDecls();
        while (param != null) {
            JavaParser.FormalParameterDeclsRestContext rest = param.formalParameterDeclsRest();
            root.addChild(new IntASTIdentifier(rest.variableDeclaratorId().Identifier().getText()));
            if (rest.ELLIPSIS() != null) {
                root.addChild(new IntASTOperator("..."));
            }
            // get the next parameter
            param = rest.formalParameterDecls();
        }
        return root;
    }

    @Override
    public IntASTBlock visitMethodBody(JavaParser.MethodBodyContext ctx) {
        // delegate directly to visitBlock, since that is
        // the entirety of the methodBody rule
        return visitBlock(ctx.block());
    }

    @Override
    public IntASTMember visitFieldDeclaration(JavaParser.FieldDeclarationContext ctx) {
        IntASTField root = new IntASTField();
        root.addChild(visitVariableDeclarators(ctx.variableDeclarators()));
        return root;
    }

    @Override
    public IntASTBlock visitBlock(JavaParser.BlockContext ctx) {
        IntASTBlock root = new IntASTBlock();
        for (JavaParser.BlockStatementContext block : ctx.blockStatement()) {
            root.addChild(visitBlockStatement(block));
        }
        return root;
    }

    @Override
    public IntASTStatement visitBlockStatement(JavaParser.BlockStatementContext ctx) {
        if (ctx.localVariableDeclarationStatement() != null) {
            // variable declaration
            return visitLocalVariableDeclarationStatement(ctx.localVariableDeclarationStatement());
        } else if (ctx.classOrInterfaceDeclaration() != null) {
            // class/interface declaration
            return visitClassOrInterfaceDeclaration(ctx.classOrInterfaceDeclaration());
        } else {
            // regular statement
            return visitStatement(ctx.statement());
        }
    }

    @Override
    public IntASTExpression visitLocalVariableDeclarationStatement(JavaParser.LocalVariableDeclarationStatementContext ctx) {
        // filter out simple variable declarations while leaving assignments
        return visitLocalVariableDeclaration(ctx.localVariableDeclaration());
    }

    @Override
    public IntASTExpression visitLocalVariableDeclaration(JavaParser.LocalVariableDeclarationContext ctx) {
        // ignore variable modifiers and type
        return visitVariableDeclarators(ctx.variableDeclarators());
    }

    @Override
    public IntASTExpression visitVariableDeclarators(JavaParser.VariableDeclaratorsContext ctx) {
        IntASTExpressionList root = new IntASTExpressionList();
        List<JavaParser.VariableDeclaratorContext> list = ctx.variableDeclarator();
        int i;
        // get the first variable declaration with assignment
        for (i = 0; i < list.size(); i++) {
            if (list.get(i).ASSIGN() != null) {
                root.addChild(visitVariableDeclarator(list.get(i)));
                break;
            }
        }
        // get any remaining variable declarations with assignment
        for (i++; i < list.size(); i++) {
            if (list.get(i).ASSIGN() != null) {
                root.addChild(visitVariableDeclarator(list.get(i)));
            }
        }
        // only return the node if variable declarations with assignment were found
        if (root.getChildCount() != 0) {
            return root;
        } else {
            return null;
        }
    }

    @Override
    public IntASTExpression visitVariableDeclarator(JavaParser.VariableDeclaratorContext ctx) {
        if (ctx.ASSIGN() != null) {
            IntASTBinaryExpression root = new IntASTBinaryExpression();
            root.addChild(visitVariableDeclaratorId(ctx.variableDeclaratorId()));
            root.addChild(new IntASTOperator("="));
            root.addChild(visitVariableInitializer(ctx.variableInitializer()));
            return root;
        } else {
            return null;
        }
    }

    @Override
    public IntASTNode visitVariableDeclaratorId(JavaParser.VariableDeclaratorIdContext ctx) {
        // get variable name with array dimensions
        StringBuilder out = new StringBuilder(ctx.Identifier().getText());
        for (int i = 0; i < ctx.LBRACK().size(); i++) {
            out.append("[]");
        }
        return new IntASTIdentifier(out.toString());
    }

    @Override
    public IntASTNode visitVariableInitializer(JavaParser.VariableInitializerContext ctx) {
        if (ctx.arrayInitializer() != null) {
            return visitArrayInitializer(ctx.arrayInitializer());
        } else {
            return visitExpression(ctx.expression());
        }
    }

    @Override
    public IntASTArrayInit visitArrayInitializer(JavaParser.ArrayInitializerContext ctx) {

        IntASTArrayInit root = new IntASTArrayInit();

        List<JavaParser.VariableInitializerContext> list = ctx.variableInitializer();

        for (JavaParser.VariableInitializerContext initContext : list) {
                root.addChild(visitVariableInitializer(initContext));
        }

        return root;
    }

    @Override
    public IntASTStatement visitStatement(JavaParser.StatementContext ctx) {

        // go down the list of this rule's possibilities
        if (ctx.TRY() != null) {
            // try block or try-with-resources block
            // TODO determine why try only works if checked before ctx.block()
            IntASTTry root = new IntASTTry();
            root.addChild(visitBlock(ctx.block()));

            if (ctx.catches() != null) {
                root.addChild(visitCatches(ctx.catches()));
            }
            if (ctx.finallyBlock() != null) {
                root.addChild(visitFinallyBlock(ctx.finallyBlock()));
            }
            return root;

        } else if (ctx.block() != null) {
            // block statement
            return visitBlock(ctx.block());
        } else if (ctx.ASSERT() != null) {

            IntASTAssert root = new IntASTAssert();

            List<JavaParser.ExpressionContext> list = ctx.expression();

            for (JavaParser.ExpressionContext expressContext : list) {
                root.addChild(visitExpression(expressContext));
            }
            return root;

        } else if (ctx.IF() != null) {
            // if statement
            IntASTIf root = new IntASTIf();
            root.addChild(visitParExpression(ctx.parExpression()));

            List<JavaParser.StatementContext> list = ctx.statement();

            for (JavaParser.StatementContext statementContext : list) {
                root.addChild(visitStatement(statementContext));
            }
            return root;

        } else if (ctx.FOR() != null) {
            // for loop
            IntASTFor root = new IntASTFor();
            root.addChild(visitForControl(ctx.forControl()));
            root.addChild(visitStatement(ctx.statement(0)));
            return root;

        } else if (ctx.DO() != null) {
            //Do-While
            IntASTDo root = new IntASTDo();

            List<JavaParser.StatementContext> list = ctx.statement();

            for (JavaParser.StatementContext statementContext : list) {
                root.addChild(visitStatement(statementContext));
            }

            root.addChild(visitParExpression(ctx.parExpression()));
            return root;

        } else if (ctx.WHILE() != null) {
            // while loop
            IntASTWhile root = new IntASTWhile();
            root.addChild(visitParExpression(ctx.parExpression()));

            List<JavaParser.StatementContext> list = ctx.statement();

            for (JavaParser.StatementContext statementContext : list) {
                root.addChild(visitStatement(statementContext));
            }
            return root;


        } else if (ctx.SWITCH() != null) {
            // switch block
            // TODO create IntASTSwitch class
            throw new UnsupportedOperationException();

        } else if (ctx.SYNCHRONIZED() != null) {
            // TODO add support for synchronized blocks? no idea how they'd translate though
            throw new UnsupportedOperationException("synchronized blocks not supported");

        } else if (ctx.RETURN() != null) {
            // return statement
            IntASTControl root = new IntASTControl ("return");

            if (ctx.expression() != null) {
                List<JavaParser.ExpressionContext> list = ctx.expression();

                for (int i = 0; i < list.size(); i++) {
                    root.addChild(visitExpression(ctx.expression(i)));
                }
            }
            return root;

        } else if (ctx.THROW() != null) {
            // throw statement
            IntASTControl root = new IntASTControl("throw");

            List<JavaParser.ExpressionContext> list = ctx.expression();

            for (int i = 0; i < list.size(); i++) {
                root.addChild(visitExpression(ctx.expression(i)));
            }
            //TODO Implement try for method handled throws vs try-catch throws
            return root;

        } else if (ctx.BREAK() != null) {
            // loop break statement
            return new IntASTControl("break");

        } else if (ctx.CONTINUE() != null) {
            // loop continue statement
            return new IntASTControl("continue");

        } else if (ctx.statementExpression() != null) {
            // ordinary statement
            return visitStatementExpression(ctx.statementExpression());
        } else if (ctx.COLON() != null) {
            // label declaration
            // TODO add support for label declarations? no idea how they'd translate into Python though
            throw new UnsupportedOperationException("jump labels not supported");
        } else {
            // empty statement. literally just a semi-colon
            return null;
        }
    }

    @Override
    public IntASTBlock visitFinallyBlock (JavaParser.FinallyBlockContext ctx) {
        return visitBlock(ctx.block());
    }

    @Override
    public IntASTCatches visitCatches (JavaParser.CatchesContext ctx) {
        IntASTCatches root = new IntASTCatches();
        List<JavaParser.CatchClauseContext> list = ctx.catchClause();

        for (JavaParser.CatchClauseContext catchClauseContext : list) {
            root.addChild(visitCatchClause(catchClauseContext));
        }

        return root;
    }

    @Override
    public IntASTCatchClause visitCatchClause (JavaParser.CatchClauseContext ctx) {
        IntASTCatchClause root = new IntASTCatchClause();

        root.addChild(visitCatchType(ctx.catchType()));
        root.addChild(new IntASTIdentifier(ctx.Identifier().getText()));
        root.addChild(visitBlock(ctx.block()));

        return root;
    }

    // TODO Complete catchType & qualifiedName functionality
    @Override
    public IntASTTypeList visitCatchType (JavaParser.CatchTypeContext ctx) {

        IntASTTypeList root = new IntASTTypeList();
        List <JavaParser.QualifiedNameContext> list = ctx.qualifiedName();

        for (JavaParser.QualifiedNameContext qualName : list) {
            root.addChild(visitQualifiedName(qualName));
        }

        return root;
    }

    @Override
    public IntASTIdentifier visitQualifiedName (JavaParser.QualifiedNameContext ctx) {
        StringBuilder name = new StringBuilder(ctx.Identifier(0).getText());

        for (int i = 1; i < ctx.Identifier().size(); i++) {
            name.append(".");
            name.append(ctx.Identifier(i).getText());
        }

        return new IntASTIdentifier(name.toString());
    }


    @Override
    public IntASTStatement visitStatementExpression(JavaParser.StatementExpressionContext ctx) {
        return visitExpression(ctx.expression());
    }

    @Override
    public IntASTStatement visitExpression(JavaParser.ExpressionContext ctx) {
        // parse down the expression rule tree
        IntASTStatement root;
        if (ctx.assignmentOperator() != null) {
            root = new IntASTBinaryExpression();
            root.addChild(visitConditionalExpression(ctx.conditionalExpression()));
            root.addChild(new IntASTOperator(ctx.assignmentOperator().getText()));
            root.addChild(visitExpression(ctx.expression()));
        } else {
            root = visitConditionalExpression(ctx.conditionalExpression());
        }
        return root;
    }

    @Override
    public IntASTStatement visitConditionalExpression(JavaParser.ConditionalExpressionContext ctx) {
        // ternary conditional expression
        IntASTStatement root;
        if (ctx.QUESTION() != null) {
            root = new IntASTTernaryExpression();
            root.addChild(visitConditionalOrExpression(ctx.conditionalOrExpression()));
            root.addChild(new IntASTOperator("?"));
            root.addChild(visitExpression(ctx.expression()));
            root.addChild(new IntASTOperator(":"));
            root.addChild(visitConditionalExpression(ctx.conditionalExpression()));
        } else {
            root = visitConditionalOrExpression(ctx.conditionalOrExpression());
        }
        return root;
    }

    @Override
    public IntASTStatement visitConditionalOrExpression(JavaParser.ConditionalOrExpressionContext ctx) {
        List<JavaParser.ConditionalAndExpressionContext> list = ctx.conditionalAndExpression();
        IntASTStatement root;
        if (list.size() > 1) {
            root = new IntASTBinaryExpression();
            root.addChild(visitConditionalAndExpression(list.get(0)));
            IntASTStatement tmpRoot = root;
            int i;
            for (i = 1; i < list.size() - 1; i++) {
                tmpRoot.addChild(new IntASTOperator("||"));
                IntASTStatement tmp = new IntASTBinaryExpression();
                tmp.addChild(visitConditionalAndExpression(list.get(i)));

                tmpRoot.addChild(tmp);
                tmpRoot = tmp;
            }
            tmpRoot.addChild(new IntASTOperator("||"));
            tmpRoot.addChild(visitConditionalAndExpression(list.get(i)));
        } else {
            root = visitConditionalAndExpression(list.get(0));
        }
        return root;
    }

    @Override
    public IntASTStatement visitConditionalAndExpression(JavaParser.ConditionalAndExpressionContext ctx) {
        // old logic in case I broke something
        /*
        List<JavaParser.InclusiveOrExpressionContext> list = ctx.inclusiveOrExpression();
        IntASTStatement root = visitInclusiveOrExpression(list.get(0));
        for (int i = 1; i < list.size(); i++) {
            root.addChild(new IntASTOperator("&&"));
            root.addChild(visitInclusiveOrExpression(list.get(i)));
        }
        return root;
        */
        List<JavaParser.InclusiveOrExpressionContext> list = ctx.inclusiveOrExpression();
        IntASTStatement root;
        if (list.size() > 1) {
            root = new IntASTBinaryExpression();
            root.addChild(visitInclusiveOrExpression(list.get(0)));
            IntASTStatement tmpRoot = root;
            int i;
            for (i = 1; i < list.size() - 1; i++) {
                tmpRoot.addChild(new IntASTOperator("&&"));
                IntASTStatement tmp = new IntASTBinaryExpression();
                tmp.addChild(visitInclusiveOrExpression(list.get(i)));

                tmpRoot.addChild(tmp);
                tmpRoot = tmp;
            }
            tmpRoot.addChild(new IntASTOperator("&&"));
            tmpRoot.addChild(visitInclusiveOrExpression(list.get(i)));
        } else {
            root = visitInclusiveOrExpression(list.get(0));
        }
        return root;
    }

    @Override
    public IntASTStatement visitInclusiveOrExpression(JavaParser.InclusiveOrExpressionContext ctx) {
        List<JavaParser.ExclusiveOrExpressionContext> list = ctx.exclusiveOrExpression();
        IntASTStatement root;
        if (list.size() > 1) {
            root = new IntASTBinaryExpression();
            root.addChild(visitExclusiveOrExpression(list.get(0)));
            IntASTStatement tmpRoot = root;
            int i;
            for (i = 1; i < list.size() - 1; i++) {
                tmpRoot.addChild(new IntASTOperator("|"));
                IntASTStatement tmp = new IntASTBinaryExpression();
                tmp.addChild(visitExclusiveOrExpression(list.get(i)));

                tmpRoot.addChild(tmp);
                tmpRoot = tmp;
            }
            tmpRoot.addChild(new IntASTOperator("|"));
            tmpRoot.addChild(visitExclusiveOrExpression(list.get(i)));
        } else {
            root = visitExclusiveOrExpression(list.get(0));
        }
        return root;
    }

    @Override
    public IntASTStatement visitExclusiveOrExpression(JavaParser.ExclusiveOrExpressionContext ctx) {
        List<JavaParser.AndExpressionContext> list = ctx.andExpression();
        IntASTStatement root;
        if (list.size() > 1) {
            root = new IntASTBinaryExpression();
            root.addChild(visitAndExpression(list.get(0)));
            IntASTStatement tmpRoot = root;
            int i;
            for (i = 1; i < list.size() - 1; i++) {
                tmpRoot.addChild(new IntASTOperator("^"));
                IntASTStatement tmp = new IntASTBinaryExpression();
                tmp.addChild(visitAndExpression(list.get(i)));

                tmpRoot.addChild(tmp);
                tmpRoot = tmp;
            }
            tmpRoot.addChild(new IntASTOperator("^"));
            tmpRoot.addChild(visitAndExpression(list.get(i)));
        } else {
            root = visitAndExpression(list.get(0));
        }
        return root;
    }

    @Override
    public IntASTStatement visitAndExpression(JavaParser.AndExpressionContext ctx) {
        List<JavaParser.EqualityExpressionContext> list = ctx.equalityExpression();
        IntASTStatement root;
        if (list.size() > 1) {
            root = new IntASTBinaryExpression();
            root.addChild(visitEqualityExpression(list.get(0)));
            IntASTStatement tmpRoot = root;
            int i;
            for (i = 1; i < list.size() - 1; i++) {
                tmpRoot.addChild(new IntASTOperator("&"));
                IntASTStatement tmp = new IntASTBinaryExpression();
                tmp.addChild(visitEqualityExpression(list.get(i)));

                tmpRoot.addChild(tmp);
                tmpRoot = tmp;
            }
            tmpRoot.addChild(new IntASTOperator("&"));
            tmpRoot.addChild(visitEqualityExpression(list.get(i)));
        } else {
            root = visitEqualityExpression(list.get(0));
        }
        return root;
    }

    @Override
    public IntASTStatement visitEqualityExpression(JavaParser.EqualityExpressionContext ctx) {
        // old logic in case I broke something
        /*
        List<ParseTree> nodes = ctx.children;
        IntASTStatement root = visitInstanceOfExpression((JavaParser.InstanceOfExpressionContext) nodes.get(0));
        for (int i = 1; i < nodes.size(); i += 2) {
            // get the "==" or "!=" operator
            root.addChild(new IntASTOperator(nodes.get(i).getText()));
            // add the next expression
            root.addChild(visitInstanceOfExpression((JavaParser.InstanceOfExpressionContext) nodes.get(i+1)));
        }
        return root;
         */
        // slightly more complicated since "==" and "!=" are used
        // at the same time here. have to loop through the entire
        // children list in order to convert and preserve the
        // operations
        List<ParseTree> nodes = ctx.children;
        IntASTStatement root;
        if (nodes.size() > 1) {
            root = new IntASTBinaryExpression();
            root.addChild(visitInstanceOfExpression((JavaParser.InstanceOfExpressionContext) nodes.get(0)));
            IntASTStatement tmpRoot = root;
            int i;
            for (i = 1; i < nodes.size() - 2; i += 2) {
                IntASTStatement tmp = new IntASTBinaryExpression();
                // get the "==" or "!=" operator
                tmpRoot.addChild(new IntASTOperator(nodes.get(i).getText()));
                // add the next expression
                tmp.addChild(visitInstanceOfExpression((JavaParser.InstanceOfExpressionContext) nodes.get(i+1)));

                tmpRoot.addChild(tmp);
                tmpRoot = tmp;
            }
            tmpRoot.addChild(new IntASTOperator(nodes.get(i).getText()));
            tmpRoot.addChild(visitInstanceOfExpression((JavaParser.InstanceOfExpressionContext) nodes.get(i+1)));
        } else {
            root = visitInstanceOfExpression((JavaParser.InstanceOfExpressionContext) nodes.get(0));
        }
        return root;
    }

    @Override
    public IntASTStatement visitInstanceOfExpression(JavaParser.InstanceOfExpressionContext ctx) {
        IntASTStatement root;
        if (ctx.INSTANCEOF() != null) {
            root = new IntASTBinaryExpression();
            root.addChild(visitRelationalExpression(ctx.relationalExpression()));
            root.addChild(new IntASTOperator("instanceof"));
            root.addChild(visitType(ctx.type()));
        } else {
            root = visitRelationalExpression(ctx.relationalExpression());
        }
        return root;
    }

    @Override
    public IntASTStatement visitRelationalExpression(JavaParser.RelationalExpressionContext ctx) {
        // old logic in case I broke something
        /*
        List<JavaParser.ShiftExpressionContext> list = ctx.shiftExpression();
        List<JavaParser.RelationalOpContext> ops = ctx.relationalOp();
        IntASTStatement root = visitShiftExpression(list.get(0));
        for (int i = 1; i < list.size(); i++) {
            root.addChild(new IntASTOperator(ops.get(i-1).getText()));
            root.addChild(visitShiftExpression(list.get(i)));
        }
        return root;
         */
        List<JavaParser.ShiftExpressionContext> list = ctx.shiftExpression();
        List<JavaParser.RelationalOpContext> ops = ctx.relationalOp();
        IntASTStatement root;
        if (list.size() > 1) {
            root = new IntASTBinaryExpression();
            root.addChild(visitShiftExpression(list.get(0)));
            IntASTStatement tmpRoot = root;
            int i;
            for (i = 1; i < list.size() - 1; i++) {
                tmpRoot.addChild(new IntASTOperator(ops.get(i-1).getText()));
                IntASTStatement tmp = new IntASTBinaryExpression();
                tmp.addChild(visitShiftExpression(list.get(i)));

                tmpRoot.addChild(tmp);
                tmpRoot = tmp;
            }
            tmpRoot.addChild(new IntASTOperator(ops.get(i-1).getText()));
            tmpRoot.addChild(visitShiftExpression(list.get(i)));
        } else {
            root = visitShiftExpression(list.get(0));
        }
        return root;
    }

    @Override
    public IntASTStatement visitShiftExpression(JavaParser.ShiftExpressionContext ctx) {
        List<JavaParser.AdditiveExpressionContext> list = ctx.additiveExpression();
        List<JavaParser.ShiftOpContext> ops = ctx.shiftOp();
        IntASTStatement root;
        if (list.size() > 1) {
            root = new IntASTBinaryExpression();
            root.addChild(visitAdditiveExpression(list.get(0)));
            IntASTStatement tmpRoot = root;
            int i;
            for (i = 1; i < list.size() - 1; i++) {
                // TODO since Python does not have ">>>" we have to find some way to convert unsigned bitwise right-shift
                tmpRoot.addChild(new IntASTOperator(ops.get(i-1).getText()));
                IntASTStatement tmp = new IntASTBinaryExpression();
                tmp.addChild(visitAdditiveExpression(list.get(i)));

                tmpRoot.addChild(tmp);
                tmpRoot = tmp;
            }
            tmpRoot.addChild(new IntASTOperator(ops.get(i-1).getText()));
            tmpRoot.addChild(visitAdditiveExpression(list.get(i)));
        } else {
            root = visitAdditiveExpression(list.get(0));
        }
        return root;
    }

    @Override
    public IntASTStatement visitAdditiveExpression(JavaParser.AdditiveExpressionContext ctx) {
        // slightly more complicated since "+" and "-" are used
        // at the same time here. have to loop through the entire
        // children list in order to convert and preserve the
        // operations
        List<ParseTree> nodes = ctx.children;
        IntASTStatement root;
        if (nodes.size() > 1) {
            root = new IntASTBinaryExpression();
            root.addChild(visitMultiplicativeExpression((JavaParser.MultiplicativeExpressionContext) nodes.get(0)));
            IntASTStatement tmpRoot = root;
            int i;
            for (i = 1; i < nodes.size() - 2; i += 2) {
                IntASTStatement tmp = new IntASTBinaryExpression();
                // get the "+" or "-" operator
                tmpRoot.addChild(new IntASTOperator(nodes.get(i).getText()));
                // add the next expression
                tmp.addChild(visitMultiplicativeExpression((JavaParser.MultiplicativeExpressionContext) nodes.get(i+1)));

                tmpRoot.addChild(tmp);
                tmpRoot = tmp;
            }
            tmpRoot.addChild(new IntASTOperator(nodes.get(i).getText()));
            tmpRoot.addChild(visitMultiplicativeExpression((JavaParser.MultiplicativeExpressionContext) nodes.get(i+1)));
        } else {
            root = visitMultiplicativeExpression((JavaParser.MultiplicativeExpressionContext) nodes.get(0));
        }
        return root;
    }

    @Override
    public IntASTStatement visitMultiplicativeExpression(JavaParser.MultiplicativeExpressionContext ctx) {
        // slightly more complicated since "*", "/" and "%" are used
        // at the same time here. have to loop through the entire
        // children list in order to convert and preserve the
        // operations
        List<ParseTree> nodes = ctx.children;
        IntASTStatement root;
        if (nodes.size() > 1) {
            root = new IntASTBinaryExpression();
            root.addChild(visitUnaryExpression((JavaParser.UnaryExpressionContext) nodes.get(0)));
            IntASTStatement tmpRoot = root;
            int i;
            for (i = 1; i < nodes.size() - 2; i += 2) {
                IntASTStatement tmp = new IntASTBinaryExpression();
                // get the "*", "/" or "%" operator
                tmpRoot.addChild(new IntASTOperator(nodes.get(i).getText()));
                // add the next expression
                tmp.addChild(visitUnaryExpression((JavaParser.UnaryExpressionContext) nodes.get(i+1)));

                tmpRoot.addChild(tmp);
                tmpRoot = tmp;
            }
            tmpRoot.addChild(new IntASTOperator(nodes.get(i).getText()));
            tmpRoot.addChild(visitUnaryExpression((JavaParser.UnaryExpressionContext) nodes.get(i+1)));
        } else {
            root = visitUnaryExpression((JavaParser.UnaryExpressionContext) nodes.get(0));
        }
        return root;
    }

    @Override
    public IntASTStatement visitUnaryExpression(JavaParser.UnaryExpressionContext ctx) {
        if (ctx.unaryExpressionNotPlusMinus() == null) {
            IntASTUnaryExpression root = new IntASTUnaryExpression();
            if (ctx.ADD() != null) {
                root.addChild(new IntASTOperator("+"));
            } else if (ctx.SUB() != null) {
                root.addChild(new IntASTOperator("-"));
            } else if (ctx.INC() != null) {
                root.addChild(new IntASTOperator("++"));
            } else {
                root.addChild(new IntASTOperator("--"));
            }
            root.addChild(visitUnaryExpression(ctx.unaryExpression()));
            return root;
        } else {
            return visitUnaryExpressionNotPlusMinus(ctx.unaryExpressionNotPlusMinus());
        }
    }

    @Override
    public IntASTStatement visitUnaryExpressionNotPlusMinus(JavaParser.UnaryExpressionNotPlusMinusContext ctx) {
        if (ctx.unaryExpression() != null) {
            IntASTUnaryExpression root = new IntASTUnaryExpression();
            if (ctx.TILDE() != null) {
                root.addChild(new IntASTOperator("~"));
            } else {
                root.addChild(new IntASTOperator("!"));
            }
            root.addChild(visitUnaryExpression(ctx.unaryExpression()));
            return root;
        } else if (ctx.castExpression() != null) {
            return visitCastExpression(ctx.castExpression());
        } else {
            IntASTStatementExpression root = new IntASTStatementExpression();
            IntASTStatement tmp = visitPrimary(ctx.primary());
            StringBuilder name = new StringBuilder();
            if (tmp instanceof IntASTIdentifier) {
                // collect a sequence of identifiers into a single string
                name.append(tmp.getText());
            } else {
                root.addChild(tmp);
            }
            for (JavaParser.SelectorContext select : ctx.selector()) {
                tmp = visitSelector(select);
                if (tmp instanceof IntASTMethodCall) {
                    if (name.length() > 0) {
                        name.append(".");
                        name.append(tmp.getChild(0).getText());
                        IntASTExpressionList exp = ((IntASTMethodCall) tmp).getExpressionList();
                        tmp = new IntASTMethodCall();
                        tmp.addChild(new IntASTIdentifier(name.toString()));
                        tmp.addChild(exp);
                        root.addChild(tmp);
                        name.setLength(0);
                    } else {
                        root.addChild(new IntASTOperator("."));
                        root.addChild(tmp);
                    }
                } else if (tmp instanceof IntASTIdentifier) {
                    name.append(".").append(tmp.getText());
                } else if (tmp instanceof IntASTExpression) {
                    root.addChild(new IntASTOperator("["));
                    root.addChild(tmp);
                    root.addChild(new IntASTOperator("]"));
                } else {
                    throw new UnsupportedOperationException();
                }
            }
            if (name.length() != 0) {
                root.addChild(new IntASTIdentifier(name.toString()));
            }
            if (ctx.INC() != null) {
                root.addChild(new IntASTOperator("++"));
            } else if (ctx.DEC() != null) {
                root.addChild(new IntASTOperator("--"));
            }
            if (root.getChildCount() == 1) {
                return (IntASTStatement) root.getChild(0);
            }
            return root;
        }
    }

    @Override
    public IntASTCastExpression visitCastExpression(JavaParser.CastExpressionContext ctx) {
        IntASTCastExpression root = new IntASTCastExpression();
        if (ctx.primitiveType() != null) {
            root.addChild(visitPrimitiveType(ctx.primitiveType()));
            root.addChild(visitUnaryExpression(ctx.unaryExpression()));
        } else {
            if (ctx.type() != null) {
                root.addChild(visitType(ctx.type()));
            } else {
                root.addChild(visitExpression(ctx.expression()));
            }
            root.addChild(visitUnaryExpressionNotPlusMinus(ctx.unaryExpressionNotPlusMinus()));
        }
        return root;
    }

    @Override
    public IntASTStatement visitPrimary(JavaParser.PrimaryContext ctx) {
        // TODO add primary conversion. this consists of parenthesis, "this" and "super" prefixes, literals, variable names, etc.
        if (ctx.parExpression() != null) {
            return visitParExpression(ctx.parExpression());
        } else if (ctx.THIS() != null && ctx.nonWildcardTypeArguments() == null) {
            if (ctx.arguments() != null) {
                IntASTMethodCall root = new IntASTMethodCall();
                root.addChild(new IntASTIdentifier("this"));
                root.addChild(visitArguments(ctx.arguments()));
                return root;
            } else {
                return new IntASTIdentifier("this");
            }
        } else if (ctx.SUPER() != null) {
            IntASTMethodCall root = new IntASTMethodCall();
            IntASTStatement sup = visitSuperSuffix(ctx.superSuffix());
            if (sup instanceof IntASTMethodCall) {
                root.addChild(new IntASTIdentifier("super." + ((IntASTMethodCall) sup).getIdentifier().getText()));
                root.addChild(((IntASTMethodCall) sup).getExpressionList());
            } else if (sup instanceof IntASTIdentifier) {
                return new IntASTIdentifier("super." + sup.getText());
            } else {
                root.addChild(new IntASTIdentifier("super"));
                root.addChild(sup);
            }
            return root;
        } else if (ctx.literal() != null) {
            return new IntASTLiteral(ctx.literal().getText());
        } else if (ctx.NEW() != null) {
            IntASTNewExpression root = new IntASTNewExpression();
            root.addChild(visitCreator(ctx.creator()));
            return root;
        } else if (ctx.nonWildcardTypeArguments() != null) {
            // TODO add generic-type-prefixed method call?
            throw new UnsupportedOperationException("Explicit generic-type method calls are unsupported");
        } else if (ctx.Identifier() != null && ctx.Identifier().size() > 0) {
            // combine multiple identifiers (separated by ".") into a single identifier
            List<TerminalNode> ids = ctx.Identifier();
            StringBuilder id = new StringBuilder(ids.get(0).getText());
            for (int i = 1; i < ids.size(); i++) {
                id.append('.').append(ids.get(i).getText());
            }
            // get the identifiers' suffix, if one exists
            if (ctx.identifierSuffix() != null) {
                IntASTExpression tmp = visitIdentifierSuffix(ctx.identifierSuffix());
                if (tmp instanceof IntASTExpressionList) {
                    IntASTMethodCall root = new IntASTMethodCall();
                    root.addChild(new IntASTIdentifier(id.toString()));
                    root.addChild(tmp);
                    return root;
                }
            }
            return new IntASTIdentifier(id.toString());
        } else if (ctx.primitiveType() != null) {
            // TODO add ".class" for primitives and primitive arrays?
            throw new UnsupportedOperationException("\".class\" for primitives and primitive arrays are unsupported");
        } else {
            // TODO add ".class" for void type?
            throw new UnsupportedOperationException("\"void.class\" is unsupported");
        }
    }

    @Override
    public IntASTExpression visitSuperSuffix(JavaParser.SuperSuffixContext ctx) {
        if (ctx.Identifier() != null) {
            ;
        }
        return null;
    }

    @Override
    public IntASTStatement visitParExpression(JavaParser.ParExpressionContext ctx) {
        IntASTParExpression root = new IntASTParExpression();
        root.addChild(visitExpression(ctx.expression()));
        return root;
    }

    @Override
    public IntASTStatement visitCreator(JavaParser.CreatorContext ctx) {
        if (ctx.classCreatorRest() != null) {
            IntASTMethodCall root = new IntASTMethodCall();
            root.addChild(visitCreatedName(ctx.createdName()));
            root.addChild(visitClassCreatorRest(ctx.classCreatorRest()));
            return root;
        } else {
            // TODO add array creator conversion
            return null;
        }
    }

    @Override
    public IntASTIdentifier visitCreatedName(JavaParser.CreatedNameContext ctx) {
        if (ctx.primitiveType() == null) {
            List<TerminalNode> lst = ctx.Identifier();
            StringBuilder name = new StringBuilder(lst.get(0).getText());
            for (int i = 1; i < lst.size(); i++) {
                name.append(".").append(lst.get(i).getText());
            }
            return new IntASTIdentifier(name.toString());
        } else {
            return visitPrimitiveType(ctx.primitiveType());
        }
    }

    @Override
    public IntASTIdentifier visitPrimitiveType(JavaParser.PrimitiveTypeContext ctx) {
        return new IntASTIdentifier(ctx.getText());
    }

    @Override
    public IntASTExpressionList visitClassCreatorRest(JavaParser.ClassCreatorRestContext ctx) {
        // TODO add support for anonymous classes?
        return visitArguments(ctx.arguments());
    }

    @Override
    public IntASTExpressionList visitArguments(JavaParser.ArgumentsContext ctx) {
        return ctx.expressionList() != null ? visitExpressionList(ctx.expressionList()) : new IntASTExpressionList();
    }

    @Override
    public IntASTExpression visitIdentifierSuffix(JavaParser.IdentifierSuffixContext ctx) {
        // TODO add identifier suffix conversion (array indexing, ".class", method call parameters, etc.)
        if (ctx.arguments() != null) {
            return visitArguments(ctx.arguments());
        }
        return null;
    }

    @Override
    public IntASTStatement visitSelector(JavaParser.SelectorContext ctx) {
        // TODO add selector conversion. this is not very clear but seems to consist of extensions to the primary rule
        if (ctx.Identifier() != null) {
            if (ctx.arguments() != null) {
                IntASTMethodCall root = new IntASTMethodCall();
                root.addChild(new IntASTIdentifier(ctx.Identifier().getText()));
                root.addChild(visitArguments(ctx.arguments()));
                return root;
            } else {
                return new IntASTIdentifier(ctx.Identifier().getText());
            }
        } else if (ctx.expression() != null) {
            return visitExpression(ctx.expression());
        } else {
            throw new UnsupportedOperationException();
        }
    }


    @Override
    public IntASTForControl visitForControl(JavaParser.ForControlContext ctx) {
        // TODO add ForControl Conversion
        if (ctx.enhancedForControl() != null) {
            return visitEnhancedForControl(ctx.enhancedForControl());

        }else {
            IntASTForControl root = new IntASTForControl();

            // use an empty expression list if no for initializer exists
            root.addChild((ctx.forInit() != null) ? visitForInit(ctx.forInit()) : new IntASTExpressionList());

            // use a "true" literal if no loop condition exists
            root.addChild((ctx.expression() != null) ? visitExpression(ctx.expression()) : new IntASTLiteral("true"));

            // use an empty expression list if no for update exists
            root.addChild((ctx.forUpdate() != null) ? visitForUpdate(ctx.forUpdate()) : new IntASTExpressionList());

            return root;
        }
    }


    @Override
    public IntASTExpression visitForInit(JavaParser.ForInitContext ctx) {
        if (ctx.localVariableDeclaration() != null) {
            return visitLocalVariableDeclaration(ctx.localVariableDeclaration());
        } else {
            return visitExpressionList(ctx.expressionList());
        }
    }

    @Override
    public IntASTExpression visitForUpdate(JavaParser.ForUpdateContext ctx) {
        return visitExpressionList(ctx.expressionList());
    }

    @Override
    public IntASTExpressionList visitExpressionList(JavaParser.ExpressionListContext ctx) {
        List<JavaParser.ExpressionContext> list = ctx.expression();
        IntASTExpressionList root = new IntASTExpressionList();

        for (JavaParser.ExpressionContext expressionContext : list) {
            root.addChild(visitExpression(expressionContext));
        }

        return root;
    }

    @Override
    public IntASTForControl visitEnhancedForControl(JavaParser.EnhancedForControlContext ctx) {

        IntASTForControl root = new IntASTForControl();
        root.addChild(new IntASTIdentifier(ctx.Identifier().getText()));
        root.addChild(new IntASTOperator(":"));
        root.addChild(visitExpression(ctx.expression()));

        return root;
    }




    // TODO eventually finish interface stuff

    @Override
    public IntASTClass visitInterfaceDeclaration(JavaParser.InterfaceDeclarationContext ctx) {
        if (ctx.annotationTypeDeclaration() != null) {
            // TODO Do something about annotations? They don't seem important though
            throw new UnsupportedOperationException("Annotation interfaces not supported");
        }
        return visitNormalInterfaceDeclaration(ctx.normalInterfaceDeclaration());
    }

    @Override
    public IntASTClass visitNormalInterfaceDeclaration(JavaParser.NormalInterfaceDeclarationContext ctx) {
        // treat interfaces the same as classes
        IntASTClass root = new IntASTClass(ctx.Identifier().getText());
        // get the inherited interfaces
        if (ctx.typeList() != null && ctx.typeList().type() != null) {
            IntASTTypeList inherit = new IntASTTypeList();
            for (JavaParser.TypeContext type : ctx.typeList().type()) {
                inherit.addChild(new IntASTIdentifier(type.classOrInterfaceType().Identifier(0).getText()));
            }
            if (inherit.getChildCount() != 0) {
                root.addChild(inherit);
            }
        }
        // get the interface body
        root.addChild(visitInterfaceBody(ctx.interfaceBody()));
        return root;
        // ignores generic-type arguments (the ones in "<>")
    }

    @Override
    public IntASTNode visitInterfaceBody(JavaParser.InterfaceBodyContext ctx) {
        IntASTNode root = new IntASTClassBody();
        // collect the declarations within the interface body
        if (ctx.interfaceBodyDeclaration() != null) {
            for (JavaParser.InterfaceBodyDeclarationContext decl : ctx.interfaceBodyDeclaration()) {
                root.addChild(visitInterfaceBodyDeclaration(decl));
            }
        }
        return root;
    }

    @Override
    public IntASTNode visitInterfaceBodyDeclaration(JavaParser.InterfaceBodyDeclarationContext ctx) {
        if (ctx.interfaceMemberDecl() == null) {
            return null;
        } else {
            return visitInterfaceMemberDecl(ctx.interfaceMemberDecl());
            // ignores modifiers on member declarations
        }
    }

    @Override
    public IntASTMember visitInterfaceMemberDecl(JavaParser.InterfaceMemberDeclContext ctx) {
        // branch out into the different types of members
        if (ctx.interfaceMethodOrFieldDecl() != null) {
            return visitInterfaceMethodOrFieldDecl(ctx.interfaceMethodOrFieldDecl());
        } else if (ctx.interfaceGenericMethodDecl() != null) {
            return visitInterfaceGenericMethodDecl(ctx.interfaceGenericMethodDecl());
        } else if (ctx.voidInterfaceMethodDeclaratorRest() != null) {
            // non-generic void-returning method
            IntASTMethod root = new IntASTMethod(ctx.Identifier().getText());
            root.addChild(visitFormalParameters(ctx.voidInterfaceMethodDeclaratorRest().formalParameters()));
            return root;
        } else if (ctx.interfaceDeclaration() != null) {
            return visitInterfaceDeclaration(ctx.interfaceDeclaration());
        } else {
            return visitClassDeclaration(ctx.classDeclaration());
        }
    }

    @Override
    public IntASTMember visitInterfaceMethodOrFieldDecl(JavaParser.InterfaceMethodOrFieldDeclContext ctx) {
        return null;
    }

    @Override
    public IntASTMethod visitInterfaceGenericMethodDecl(JavaParser.InterfaceGenericMethodDeclContext ctx) {
        return null;
    }

}
