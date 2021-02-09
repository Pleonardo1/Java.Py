import intermediate.*;
import org.antlr.v4.runtime.tree.TerminalNode;

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
            return null;
        }
        return visitClassOrInterfaceDeclaration(ctx.classOrInterfaceDeclaration());
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
        if (ctx.type() != null) {
            root.addChild(new IntASTInherit(ctx.type().classOrInterfaceType().Identifier(0).getText()));
        }
        // get the inherited interfaces
        if (ctx.typeList() != null && ctx.typeList().type() != null) {
            for (JavaParser.TypeContext type : ctx.typeList().type()) {
                root.addChild(new IntASTInherit(type.classOrInterfaceType().Identifier(0).getText()));
            }
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
            for (JavaParser.TypeContext type : ctx.typeList().type()) {
                root.addChild(new IntASTInherit(type.classOrInterfaceType().Identifier(0).getText()));
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
        return null;
    }

    @Override
    public IntASTStatement visitFieldDeclaration(JavaParser.FieldDeclarationContext ctx) {
        return null;
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

    @Override
    public IntASTBlock visitBlock(JavaParser.BlockContext ctx) {
        return null;
    }
}
