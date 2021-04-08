import intermediate.*;
import python.*;

import java.util.ArrayList;
import java.util.List;

public class IntermediateToPython {

    private List<PythonASTFunction> staticMethods = new ArrayList<>();

    public PythonASTFileInput visitCompilationUnit(IntASTCompilationUnit ctx) {
        PythonASTFileInput root = new PythonASTFileInput();

        root.addChild(new PythonASTTerminal("import sys"));
        root.addChild(new PythonASTTerminal.PythonASTNewline());

        // convert the imports
        for (IntASTImport node : ctx.getImportDeclaration()) {
            root.addChild(visitImport(node));
        }
        // convert the class declarations
        for (IntASTClass node : ctx.getClassDeclaration()) {
            root.addChild(visitClass(node));
        }

        PythonASTFunction main = null;

        // add the static methods
        for (PythonASTFunction func : this.staticMethods) {
            PythonASTStatement stmt = new PythonASTStatement();
            PythonASTCompoundStatement comp = new PythonASTCompoundStatement();
            if (func.isMain()) {
                main = func;
            } else {
                comp.addChild(func);
                stmt.addChild(comp);

                root.addChild(stmt);
            }
        }

        if (main != null) {
            /*
            if __name__ == "__main__":
                /args/ = sys.argv
                ...
             */
            PythonASTIfStatement ifStmt = new PythonASTIfStatement();
            PythonASTBinaryExpression bin = new PythonASTBinaryExpression();

            // define the if condition
            PythonASTAtomExpression atomExpression = new PythonASTAtomExpression();
            PythonASTAtom atom = new PythonASTAtom();

            atom.addChild(new PythonASTTerminal("__name__"));
            atomExpression.addChild(atom);
            bin.addChild(atomExpression);

            bin.addChild(new PythonASTTerminal("=="));

            atomExpression = new PythonASTAtomExpression();
            atom = new PythonASTAtom();

            atom.addChild(new PythonASTTerminal("\"__main__\""));
            atomExpression.addChild(atom);

            bin.addChild(atomExpression);

            // add the condition to the if statement
            ifStmt.addChild(new PythonASTTerminal("if"));
            ifStmt.addChild(bin);
            ifStmt.addChild(new PythonASTTerminal(":"));

                /*
                if __name__ == "__main__":
                 */

            // get the main argument name
            PythonASTParametersList params = main.getChild(0, PythonASTParametersList.class);
            PythonASTTfpdef tfpdef = params.getChild(0, PythonASTTfpdef.class);
            PythonASTTerminal name = tfpdef.getChild(0, PythonASTTerminal.class);
            if (name.getText().startsWith("*")) {
                name.setText(name.getText().substring(1));
            }

            // assign sys.argv to the main argument name
            bin = new PythonASTBinaryExpression();
            atomExpression = new PythonASTAtomExpression();
            atom = new PythonASTAtom();

            atom.addChild(name);
            atomExpression.addChild(atom);
            bin.addChild(atomExpression);

            bin.addChild(new PythonASTTerminal("="));

            atomExpression = new PythonASTAtomExpression();
            atom = new PythonASTAtom();

            atom.addChild(new PythonASTTerminal("sys.argv"));
            atomExpression.addChild(atom);
            bin.addChild(atomExpression);

            // add "/args/ = sys.argv" to a suite object
            PythonASTSuite suite = new PythonASTSuite();
            PythonASTStatement stmt = new PythonASTStatement();
            PythonASTSimpleStatement simple = new PythonASTSimpleStatement();
            PythonASTSmallStatement small = new PythonASTSmallStatement();

            small.addChild(bin);
            simple.addChild(small);
            simple.addChild(new PythonASTTerminal.PythonASTNewline());
            stmt.addChild(simple);

            suite.addChild(new PythonASTTerminal.PythonASTNewline());
            suite.addChild(new PythonASTTerminal.PythonASTIndent());
            suite.addChild(stmt);

            // add the remainder of the main method to the suite
            for (PythonASTNode node : main.getChild(0, PythonASTSuite.class).getChildren(PythonASTStatement.class)) {
                suite.addChild(node);
            }

            suite.addChild(new PythonASTTerminal.PythonASTDedent());

            // add the suite to the if statement
            ifStmt.addChild(suite);

            // add the if statement to a statement
            stmt = new PythonASTStatement();
            PythonASTCompoundStatement comp = new PythonASTCompoundStatement();

            comp.addChild(ifStmt);
            stmt.addChild(comp);

            root.addChild(stmt);
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

    public PythonASTStatement visitClass(IntASTClass ctx) {
        PythonASTStatement root = new PythonASTStatement();
        PythonASTCompoundStatement comp = new PythonASTCompoundStatement();
        PythonASTClass cls = new PythonASTClass();

        cls.addChild(new PythonASTTerminal("class"));
        cls.addChild(new PythonASTTerminal(ctx.getText()));

        // class name (argList):
        if (ctx.getTypeList() != null) {
            cls.addChild(new PythonASTTerminal("("));
            cls.addChild(visitTypeList(ctx.getTypeList()));
            cls.addChild(new PythonASTTerminal(")"));
        }

        cls.addChild(new PythonASTTerminal(":"));

        cls.addChild(visitClassBody(ctx.getClassBody()));

        comp.addChild(cls);
        root.addChild(comp);

        return root;
    }

    public PythonASTArgList visitTypeList(IntASTTypeList ctx) {
        List<IntASTIdentifier> types = ctx.getIdentifier();
        if (types.isEmpty()) {
            return null;
        }
        PythonASTArgList root = new PythonASTArgList();
        PythonASTArgument arg;

        //Can have single argument or more
        for (int i = 0; i < types.size() - 1; i++) {
            arg = new PythonASTArgument();
            arg.addChild(visitTerminal(types.get(i)));
            root.addChild(arg);
            root.addChild(new PythonASTTerminal(","));
        }
        arg = new PythonASTArgument();
        arg.addChild(visitTerminal(types.get(types.size()-1)));
        root.addChild(arg);
        return root;
    }

    public PythonASTSuite visitClassBody(IntASTClassBody ctx) {
        PythonASTSuite root = new PythonASTSuite();

        root.addChild(new PythonASTTerminal.PythonASTIndent());
        root.addChild(new PythonASTTerminal.PythonASTNewline());

        for (IntASTMember node : ctx.getMember()) {
            root.addChild(visitMember(node));
        }

        root.addChild(new PythonASTTerminal.PythonASTDedent());

        return root;
    }

    public PythonASTNode visitMember(IntASTMember ctx) {
        PythonASTStatement root = new PythonASTStatement();

        if (ctx instanceof IntASTBlock) {
            return visitBlock((IntASTBlock) ctx);

        } else if (ctx instanceof IntASTClass) {
            return visitClass((IntASTClass) ctx);

        } else if (ctx instanceof IntASTField) {
            root.addChild(visitField((IntASTField) ctx));

        } else if (ctx instanceof IntASTConstructor) {
            PythonASTCompoundStatement stmt = new PythonASTCompoundStatement();
            stmt.addChild(visitConstructor((IntASTConstructor) ctx));
            root.addChild(stmt);

        } else if (ctx instanceof IntASTMethod) {
            PythonASTCompoundStatement stmt = new PythonASTCompoundStatement();
            stmt.addChild(visitMethod((IntASTMethod) ctx));
            root.addChild(stmt);

        } else {
            throw new IllegalArgumentException("Unknown IntASTMember type: " + ctx.getClass().getSimpleName());
        }

        return root;
    }

    public PythonASTSuite visitBlock(IntASTBlock ctx) {
        PythonASTSuite root = new PythonASTSuite();
        List<IntASTStatement> stmts = ctx.getStatement();

        root.addChild(new PythonASTTerminal.PythonASTIndent());
        root.addChild(new PythonASTTerminal.PythonASTNewline());

        if (stmts.isEmpty()) {
            PythonASTSimpleStatement simple = new PythonASTSimpleStatement();
            PythonASTSmallStatement small = new PythonASTSmallStatement();
            PythonASTStatement stmt = new PythonASTStatement();

            small.addChild(new PythonASTPassStatement());

            simple.addChild(small);
            simple.addChild(new PythonASTTerminal.PythonASTNewline());

            stmt.addChild(simple);

            root.addChild(stmt);
        } else {
            for (IntASTStatement stmt : stmts) {
                root.addChild(visitStatement(stmt));
            }
        }

        root.addChild(new PythonASTTerminal.PythonASTDedent());

        return root;
    }

    public PythonASTSimpleStatement visitField(IntASTField ctx) {
        IntASTStatement stmt = ctx.getStatement();
        if (stmt == null) {
            return null;
        } else if (stmt instanceof IntASTExpressionList) {
            List<IntASTExpression> exprs = ((IntASTExpressionList) stmt).getExpression();
            if (exprs.isEmpty()) {
                return null;
            }
            PythonASTSimpleStatement root = new PythonASTSimpleStatement();
            PythonASTSmallStatement small;

            for (int i = 0; i < exprs.size() - 1; i++) {
                small = new PythonASTSmallStatement();
                small.addChild(visitExpression(exprs.get(i)));

                root.addChild(small);
                root.addChild(new PythonASTTerminal(";"));
            }
            small = new PythonASTSmallStatement();
            small.addChild(visitExpression(exprs.get(exprs.size() - 1)));

            root.addChild(small);
            root.addChild(new PythonASTTerminal.PythonASTNewline());

            return root;
        } else if (stmt instanceof IntASTExpression) {
            PythonASTSimpleStatement root = new PythonASTSimpleStatement();
            root.addChild(visitExpression((IntASTExpression) stmt));
            return root;
        } else {
            throw new IllegalArgumentException("Invalid field declaration type: " + stmt.getClass().getSimpleName());
        }
    }

    public PythonASTFunction visitMethod(IntASTMethod ctx) {
        PythonASTFunction root = new PythonASTFunction();

        root.addChild(new PythonASTTerminal("def"));
        root.addChild(new PythonASTTerminal(ctx.getText()));

        root.addChild(new PythonASTTerminal("("));

        if (!ctx.isStatic()) {
            root.addChild(new PythonASTTerminal("self"));
            if (ctx.getMethodParameters().getChildCount() != 0) {
                root.addChild(new PythonASTTerminal(","));
            }
        }

        root.addChild(visitMethodParameters(ctx.getMethodParameters()));

        root.addChild(new PythonASTTerminal(")"));

        root.addChild(new PythonASTTerminal(":"));

        if (ctx.getBlock() != null) {
            PythonASTSuite suite = visitBlock(ctx.getBlock());
            // add a newline to aesthetically separate the method definitions
            suite.addChild(new PythonASTTerminal.PythonASTNewline());
            root.addChild(suite);
        }

        if (ctx.isStatic()) {
            this.staticMethods.add(root);
            if (ctx.isMain()) {
                root.setMain(true);
            }
            return null;
        } else {
            return root;
        }
    }

    public PythonASTFunction visitConstructor (IntASTConstructor ctx) {
        // delegate to visitMethod
        IntASTMethod root = new IntASTMethod("__init__");
        root.addChild(ctx.getMethodParameters());
        root.addChild(ctx.getBlock());
        return visitMethod(root);
    }

    public PythonASTNode visitStatement(IntASTStatement ctx) {
        if (ctx instanceof IntASTStatementExpression) {
            return visitStatementExpression((IntASTStatementExpression) ctx);
        } else if (ctx instanceof IntASTExpressionList) {
            PythonASTStatement root = new PythonASTStatement();
            PythonASTSimpleStatement simple = new PythonASTSimpleStatement();
            PythonASTSmallStatement small;
            List<IntASTExpression> exprs = ((IntASTExpressionList) ctx).getExpression();

            if (exprs.isEmpty()) {
                return null;
            }

            for (int i = 0; i < exprs.size() - 1; i++) {
                small = new PythonASTSmallStatement();
                small.addChild(visitExpression(exprs.get(i)));
                simple.addChild(small);
                simple.addChild(new PythonASTTerminal(";"));
            }
            small = new PythonASTSmallStatement();
            small.addChild(visitExpression(exprs.get(exprs.size() - 1)));
            simple.addChild(small);
            simple.addChild(new PythonASTTerminal.PythonASTNewline());

            root.addChild(simple);

            return root;
        } else if (ctx instanceof IntASTExpression || ctx instanceof IntASTControl) {
            PythonASTStatement root = new PythonASTStatement();
            PythonASTSimpleStatement simple = new PythonASTSimpleStatement();
            PythonASTSmallStatement small = new PythonASTSmallStatement();

            if (ctx instanceof IntASTExpression) {
                small.addChild(visitExpression((IntASTExpression) ctx));
            } else {
                small.addChild(visitControl((IntASTControl) ctx));
            }
            simple.addChild(small);
            simple.addChild(new PythonASTTerminal.PythonASTNewline());
            root.addChild(simple);
            return root;
        } else if (ctx instanceof IntASTBlock) {
            return visitBlock((IntASTBlock) ctx);
        } else {
            PythonASTStatement root = new PythonASTStatement();
            PythonASTCompoundStatement comp = new PythonASTCompoundStatement();
            comp.addChild(visitCompoundStatement(ctx));
            root.addChild(comp);
            return root;
        }
    }

    public PythonASTNode visitCompoundStatement(IntASTStatement ctx) {
        if (ctx instanceof IntASTIf) {
            return visitIf((IntASTIf) ctx);

        } else if (ctx instanceof IntASTWhile) {
            return visitWhile((IntASTWhile) ctx);

        } else if (ctx instanceof IntASTDo) {
            return visitDo((IntASTDo) ctx);

        } else  if (ctx instanceof IntASTFor) {
            return visitFor((IntASTFor) ctx);

        } else if (ctx instanceof IntASTTry) {
            return visitTry((IntASTTry) ctx);

        } else if (ctx instanceof IntASTMethod) {
            return visitMethod((IntASTMethod) ctx);

        } else if (ctx instanceof IntASTClass) {
            return visitClass((IntASTClass) ctx);

        } else {
            throw new IllegalArgumentException("Invalid Compound Statement type: " + ctx.getClass().getName());
        }
    }



    public PythonASTParametersList visitMethodParameters(IntASTMethodParameters ctx) {
        PythonASTParametersList root = new PythonASTParametersList();
        List<IntASTIdentifier> params = ctx.getIdentifier();
        PythonASTTfpdef tfpdef;

        if (params.size() == 0) {
            return root;
        }

        for(int i = 0; i < params.size() - 1; i++) {
            tfpdef = new PythonASTTfpdef();
            tfpdef.addChild(visitTerminal(params.get(i)));
            root.addChild(tfpdef);
            root.addChild(new PythonASTTerminal(","));
        }

        tfpdef = new PythonASTTfpdef();
        tfpdef.addChild(visitTerminal(params.get(params.size()-1)));
        root.addChild(tfpdef);

        // define as varargs
        if (ctx.isVarargs()) {
            tfpdef.getChild(0).setText("*" + tfpdef.getChild(0).getText());
        }

        return root;
    }

    public PythonASTExpression visitExpression (IntASTExpression ctx) {
        if (ctx instanceof IntASTTernaryExpression) {
            return visitTernaryExpression((IntASTTernaryExpression) ctx);
        } else if (ctx instanceof IntASTBinaryExpression) {
            return visitBinaryExpression((IntASTBinaryExpression) ctx);
        } else if (ctx instanceof IntASTUnaryExpression) {
            return visitUnaryExpression((IntASTUnaryExpression) ctx);
        } else if (ctx instanceof IntASTParExpression) {
            return visitParExpression((IntASTParExpression) ctx);
        } else if (ctx instanceof IntASTArrayInit) {
            return visitArrayInit((IntASTArrayInit) ctx);
//        } else if (ctx instanceof IntASTTypeList) {
//            return visitTypeList((IntASTTypeList) ctx);
//        } else if (ctx instanceof IntASTExpressionList) {
//            return visitExpressionList((IntASTExpressionList) ctx);
        } else if (ctx instanceof IntASTCastExpression) {
            return visitCastExpression((IntASTCastExpression) ctx);
        } else if (ctx instanceof IntASTNewExpression) {
            return visitNewExpression((IntASTNewExpression) ctx);
        } else if (ctx instanceof IntASTMethodCall) {
            return visitMethodCall((IntASTMethodCall) ctx);
        } else if (ctx instanceof IntASTAssert) {
            return visitAssert((IntASTAssert) ctx);
        } else if (ctx instanceof IntASTTerminal) {
            PythonASTAtomExpression atomExpression = new PythonASTAtomExpression();
            PythonASTAtom atom = new PythonASTAtom();

            atom.addChild(visitTerminal((IntASTTerminal) ctx));
            atomExpression.addChild(atom);

            return atomExpression;
        } else {
            throw new IllegalArgumentException("Unknown IntASTExpression type: " + ctx.getClass().getSimpleName());
        }
    }

    public PythonASTTernaryExpression visitTernaryExpression(IntASTTernaryExpression ctx) {
        PythonASTTernaryExpression root = new PythonASTTernaryExpression();
        List<IntASTExpression> exprs = ctx.getExpressionNotOperator();

        root.addChild(visitExpression(exprs.get(0)));
        root.addChild(new PythonASTTerminal("if"));
        root.addChild(visitExpression(exprs.get(1)));
        root.addChild(new PythonASTTerminal("else"));
        root.addChild(visitExpression(exprs.get(2)));

        return root;
    }

    public PythonASTBinaryExpression visitBinaryExpression(IntASTBinaryExpression ctx) {
        PythonASTBinaryExpression root = new PythonASTBinaryExpression();
        List<IntASTExpression> exprs = ctx.getExpressionNotOperator();

        root.addChild(visitExpression(exprs.get(0)));
        root.addChild(visitTerminal(ctx.getOperator()));
        root.addChild(visitExpression(exprs.get(1)));

        return root;
    }

    public PythonASTExpression visitUnaryExpression(IntASTUnaryExpression ctx) {
        PythonASTExpression root;

        if (ctx.getOperator().getText().equals("++")) {
            root = new PythonASTBinaryExpression();
            root.addChild(visitExpression(ctx.getExpressionNotOperator()));
            root.addChild(new PythonASTTerminal("+="));

            PythonASTAtomExpression atomExpression = new PythonASTAtomExpression();
            PythonASTAtom atom = new PythonASTAtom();

            atom.addChild(new PythonASTTerminal("1"));
            atomExpression.addChild(atom);

            root.addChild(atomExpression);
        } else if (ctx.getOperator().getText().equals("--")) {
            root = new PythonASTBinaryExpression();
            root.addChild(visitExpression(ctx.getExpressionNotOperator()));
            root.addChild(new PythonASTTerminal("-="));

            PythonASTAtomExpression atomExpression = new PythonASTAtomExpression();
            PythonASTAtom atom = new PythonASTAtom();

            atom.addChild(new PythonASTTerminal("1"));
            atomExpression.addChild(atom);

            root.addChild(atomExpression);
        } else
            // determine if the expression is prefix or postfix
        if (ctx.getChild(0) instanceof IntASTOperator) {
            // prefix
            root = new PythonASTUnaryExpression();
            root.addChild(visitTerminal(ctx.getOperator()));
            root.addChild(visitExpression(ctx.getExpressionNotOperator()));
        } else {
            // postfix
            root = new PythonASTUnaryExpression();
            root.addChild(visitExpression(ctx.getExpressionNotOperator()));
            root.addChild(visitTerminal(ctx.getOperator()));
        }

        return root;
    }

    public PythonASTAtomExpression visitParExpression(IntASTParExpression ctx) {
        PythonASTAtomExpression root = new PythonASTAtomExpression();
        PythonASTAtom atom = new PythonASTAtom();

        atom.addChild(new PythonASTTerminal("("));
        atom.addChild(visitExpression(ctx.getExpression()));
        atom.addChild(new PythonASTTerminal(")"));
        root.addChild(atom);

        return root;
    }

    public PythonASTAtomExpression visitNewExpression(IntASTNewExpression ctx) {
        PythonASTAtomExpression atomExpression = new PythonASTAtomExpression();
        //PythonASTTrailer trailer = new PythonASTTrailer();

        // essentially just a method call with the object's name as the method name
        if (ctx.getChild(0) instanceof IntASTMethodCall) {
            atomExpression.addChild(visitMethodCall((IntASTMethodCall) ctx.getChild(0)));
        } else {
            atomExpression.addChild(visitArrayInit((IntASTArrayInit) ctx.getChild(0)));
        }

        return atomExpression;
    }

    public PythonASTFlowStatement visitControl(IntASTControl ctx) {
        PythonASTFlowStatement flow = new PythonASTFlowStatement();

        flow.addChild(new PythonASTTerminal(ctx.getText()));
        if (ctx.getExpression() != null) {
            flow.addChild(visitExpression(ctx.getExpression()));
        }

        return flow;
    }

    public PythonASTIfStatement visitIf(IntASTIf ctx) {
        PythonASTIfStatement root = new PythonASTIfStatement();
        List<IntASTStatement> stmt_list = ctx.getStatementNotParExpression();

        root.addChild(new PythonASTTerminal("if"));
        root.addChild(visitParExpression(ctx.getParExpression()));
        root.addChild(new PythonASTTerminal(":"));

        // get if body
        // ensure we get a node of type PythonASTSuite
        if (stmt_list.get(0) instanceof IntASTBlock) {
            root.addChild(visitBlock((IntASTBlock) stmt_list.get(0)));
        } else {
            PythonASTSuite suite = new PythonASTSuite();
            suite.addChild(visitStatement(stmt_list.get(0)));
            root.addChild(suite);
        }

        // get else and convert else-if to elif
        while (stmt_list.size() > 1) {
            // have an else clause, check if its an else-if
            if (stmt_list.get(1) instanceof IntASTIf) {
                // have an else-if, so add to root as elif
                ctx = (IntASTIf) stmt_list.get(1);
                stmt_list = ctx.getStatementNotParExpression();

                root.addChild(new PythonASTTerminal("elif"));
                root.addChild(visitParExpression(ctx.getParExpression()));
                root.addChild(new PythonASTTerminal(":"));

                // get elif body
                // ensure we get a node of type PythonASTSuite
                if (stmt_list.get(0) instanceof IntASTBlock) {
                    root.addChild(visitBlock((IntASTBlock) stmt_list.get(0)));
                } else {
                    PythonASTSuite suite = new PythonASTSuite();
                    suite.addChild(visitStatement(stmt_list.get(0)));
                    root.addChild(suite);
                }

                // restart loop to check for further else clauses
            } else {
                // have an else, so add to root
                root.addChild(new PythonASTTerminal("else"));
                root.addChild(new PythonASTTerminal(":"));

                // get else body
                // ensure we get a node of type PythonASTSuite
                if (stmt_list.get(1) instanceof IntASTBlock) {
                    root.addChild(visitBlock((IntASTBlock) stmt_list.get(1)));
                } else {
                    PythonASTSuite suite = new PythonASTSuite();
                    suite.addChild(visitStatement(stmt_list.get(1)));
                    root.addChild(suite);
                }
                break;
            }
        }

        return root;
    }

    public PythonASTTryStatement visitTry (IntASTTry ctx) {
        PythonASTExceptClause except;
        PythonASTTryStatement root = new PythonASTTryStatement();
        List<IntASTCatchClause> catch_clauses = ctx.getCatches().getCatchClause();
        
        //Try Clause
        root.addChild(new PythonASTTerminal("try"));
        root.addChild(new PythonASTTerminal(":"));
        
        root.addChild(visitBlock(ctx.getBlock(0)));
        

        //Except Clause
        if (ctx.getChildCount() == 3 || !ctx.hasFinally()) {
            for (IntASTCatchClause clause : catch_clauses) {
                // get the except clause header from the catches clause
                root.addChild(visitCatchClause(clause));
                root.addChild(new PythonASTTerminal(":"));
                // add the except clause body
                root.addChild(visitBlock(clause.getBlock()));
            }
        }
        
        if (ctx.hasFinally()) {
            //Finally
            root.addChild(new PythonASTTerminal("finally"));
            root.addChild(new PythonASTTerminal(":"));
            root.addChild(visitBlock(ctx.getBlock(ctx.getChildCount(IntASTBlock.class) - 1)));
        }

        return root;
    }

    public PythonASTExceptClause visitCatchClause(IntASTCatchClause ctx) {
        PythonASTExceptClause root = new PythonASTExceptClause();
        PythonASTArgList types = visitTypeList(ctx.getTypeList());

        root.addChild(new PythonASTTerminal("except"));

        // add type(s) to the except clause
        if (types.getChildCount() == 1) {
            // only 1 type
            root.addChild(types.getChild(0));
        } else {
            // more than 1 type, so add as atom expression (parenthesized expression)
            PythonASTAtomExpression atomExpression = new PythonASTAtomExpression();
            PythonASTAtom atom = new PythonASTAtom();

            atom.addChild(new PythonASTTerminal("("));
            atom.addChild(types);
            atom.addChild(new PythonASTTerminal(")"));

            atomExpression.addChild(atom);

            root.addChild(atomExpression);
        }

        // add "as var" to the except clause
        root.addChild(new PythonASTTerminal("as"));
        root.addChild(visitTerminal(ctx.getIdentifier()));

        return root;
    }

    public PythonASTWhileStatement visitWhile (IntASTWhile ctx) {
        PythonASTWhileStatement root = new PythonASTWhileStatement();
        IntASTStatement stmt = ctx.getStatementNotParExpression();

        root.addChild(new PythonASTTerminal("while"));
        root.addChild(visitParExpression(ctx.getParExpression()));
        root.addChild(new PythonASTTerminal(":"));

        // ensure we get a node of type PythonASTSuite
        if (stmt instanceof IntASTBlock) {
            root.addChild(visitBlock((IntASTBlock) stmt));
        } else {
            PythonASTSuite suite = new PythonASTSuite();
            suite.addChild(visitStatement(stmt));
            root.addChild(suite);
        }

        return root;
    }

    private int doWhileFirstLoopId = 0;

    public PythonASTWhileStatement visitDo (IntASTDo ctx) {

        /*
        Possible do-while work-around

        do {
            ...
        } while (condition);

         |
         V

        boolean __isFirstLoop = true;
        while (__isFirstLoop || (condition)) {
            __isFirstLoop = false;
            ...
        }

         |
         V

        __is_first_loop = True
        while __is_first_loop or (condition):
            __is_first_loop = False
            ...
         */

        // handle nested do-while loops
        String firstLoop = "__is_first_loop_" + this.doWhileFirstLoopId++;

        PythonASTWhileStatement root = new PythonASTWhileStatement();
        PythonASTSimpleStatement simple_stmt = new PythonASTSimpleStatement();
        PythonASTSmallStatement small_stmt = new PythonASTSmallStatement();
        PythonASTBinaryExpression bin_expr = new PythonASTBinaryExpression();

        /*
         __is_first_loop_XX = True
         */
        bin_expr.addChild(new PythonASTTerminal(firstLoop));
        bin_expr.addChild(new PythonASTTerminal("="));
        bin_expr.addChild(new PythonASTTerminal("True"));

        small_stmt.addChild(bin_expr);

        simple_stmt.addChild(small_stmt);
        simple_stmt.addChild(new PythonASTTerminal.PythonASTNewline());

        root.addChild(simple_stmt);

        /*
         __is_first_loop_XX = True
         while __is_first_loop_XX or (condition):
         */
        bin_expr = new PythonASTBinaryExpression();

        bin_expr.addChild(new PythonASTTerminal(firstLoop));
        bin_expr.addChild(new PythonASTTerminal("or"));
        bin_expr.addChild(visitParExpression(ctx.getParExpression()));

        root.addChild(new PythonASTTerminal("while"));
        root.addChild(bin_expr);
        root.addChild(new PythonASTTerminal(":"));

        /*
         __is_first_loop_XX = True
         while __is_first_loop_XX or (condition):
             __is_first_loop_XX = False
         */
        PythonASTSuite suite = new PythonASTSuite();

        simple_stmt = new PythonASTSimpleStatement();
        small_stmt = new PythonASTSmallStatement();
        bin_expr = new PythonASTBinaryExpression();

        bin_expr.addChild(new PythonASTTerminal(firstLoop));
        bin_expr.addChild(new PythonASTTerminal("="));
        bin_expr.addChild(new PythonASTTerminal("False"));

        small_stmt.addChild(bin_expr);

        simple_stmt.addChild(small_stmt);
        simple_stmt.addChild(new PythonASTTerminal.PythonASTNewline());

        PythonASTStatement stmt = new PythonASTStatement();
        stmt.addChild(simple_stmt);

        suite.addChild(new PythonASTTerminal.PythonASTNewline());
        suite.addChild(new PythonASTTerminal.PythonASTIndent());
        suite.addChild(stmt);

        /*
         __is_first_loop_XX = True
         while __is_first_loop_XX or (condition):
             __is_first_loop_XX = False
             ...
         */
        PythonASTNode tmp = visitStatement(ctx.getStatementNotParExpression());
        if (tmp instanceof PythonASTSuite) {
            for (PythonASTStatement node : tmp.getChildren(PythonASTStatement.class)) {
                suite.addChild(node);
            }
        } else {
            suite.addChild(tmp);
        }

        suite.addChild(new PythonASTTerminal.PythonASTDedent());
        root.addChild(suite);

        // reset nested loop counter
        this.doWhileFirstLoopId--;

        return root;
    }

    public PythonASTAssertStatement visitAssert(IntASTAssert ctx) {
        PythonASTAssertStatement root = new PythonASTAssertStatement();

        root.addChild(new PythonASTTerminal("assert"));
        root.addChild(visitExpression(ctx.getExpression(0)));
        if (ctx.getChildCount() == 2) {
            root.addChild(new PythonASTTerminal(","));
            root.addChild(visitExpression(ctx.getExpression(1)));
        }

        return root;
    }

    private static boolean isBasicForControl(IntASTForControl ctx) {
        // enhanced for control is not basic
        if (ctx.isEnhanced()) {
            return false;
        }
        // requires all 3 components to be a basic for loop
        if (ctx.getChildCount() != 3) {
            return false;
        }
        // check whether the forInit is a single binary expression
        if (!(ctx.getChild(0) instanceof IntASTExpressionList &&
                ctx.getChild(0).getChildCount() == 1 &&
                ctx.getChild(0).getChild(0) instanceof IntASTBinaryExpression)) {
            return false;
        }
        IntASTBinaryExpression init = (IntASTBinaryExpression) ctx.getChild(0).getChild(0);

        // ensure the forInit is an assignment statement
        if (!init.getOperator().getText().equals("=")) {
            return false;
        }

        // get the loop variable name
        String loopVar = init.getChild(0).getText();

        // check whether the loop condition is a single binary expression
        if (!(ctx.getChild(1) instanceof IntASTBinaryExpression)) {
            return false;
        }
        IntASTBinaryExpression cond = (IntASTBinaryExpression) ctx.getChild(1);

        // check whether the condition is a simple less-than or greater-than comparison
        if (!cond.getOperator().getText().equals("<") && !cond.getOperator().getText().equals(">")) {
            return false;
        }

        // ensure the comparison is being done with respect to the loop variable
        if (!((cond.getChild(0) instanceof IntASTIdentifier && cond.getChild(0).getText().equals(loopVar)) ||
                (cond.getChild(2) instanceof IntASTIdentifier && cond.getChild(2).getText().equals(loopVar)))) {
            return false;
        }

        // check whether the forUpdate is a single expression
        if (!(ctx.getChild(2) instanceof IntASTExpressionList && ctx.getChild(2).getChildCount() == 1)) {
            return false;
        }

        // check whether the forUpdate is a binary or unary expression
        if (ctx.getChild(2).getChild(0) instanceof IntASTUnaryExpression) {
            // have a unary expression, ensure it's plus or minus
            IntASTUnaryExpression forUpdate = (IntASTUnaryExpression) ctx.getChild(2).getChild(0);

            if (!forUpdate.getOperator().getText().equals("++") && !forUpdate.getOperator().getText().equals("--")) {
                return false;
            }

            // ensure the correct variable is being updated
            if (!(forUpdate.getExpressionNotOperator() instanceof IntASTIdentifier &&
                    forUpdate.getExpressionNotOperator().getText().equals(loopVar))) {
                return false;
            }
        } else {
            // have a binary expression, ensure its addition or subtraction
            IntASTBinaryExpression forUpdate = (IntASTBinaryExpression) ctx.getChild(2).getChild(0);

            if (!forUpdate.getOperator().getText().equals("+=") && !forUpdate.getOperator().getText().equals("-=")) {
                return false;
            }

            // ensure the correct variable is being updated
            if (!(forUpdate.getExpressionNotOperator(0) instanceof IntASTIdentifier &&
                    forUpdate.getExpressionNotOperator(0).getText().equals(loopVar))) {
                return false;
            }
        }

        // we SHOULD have a basic for loop at this point
        return true;
    }

    public PythonASTForStatement visitFor(IntASTFor ctx) {
        PythonASTForStatement root = new PythonASTForStatement();
        PythonASTExpressionList expr_list = new PythonASTExpressionList();

        IntASTForControl control = ctx.getForControl();
        List <IntASTIdentifier> identifiers = control.getIdentifier();
        List <IntASTExpression> exprs = control.getExpression();

        // Terminal 'for'
        root.addChild(new PythonASTTerminal("for"));

        if (control.isEnhanced()) {
            PythonASTAtomExpression atomExpression = new PythonASTAtomExpression();
            PythonASTAtom atom = new PythonASTAtom();

            /* Don't need to account for range() or len() for enhanced
            since you won't have the format --> (char letter : {a, b, c}) in java */

            // ExpressionList
            atom.addChild(new PythonASTTerminal(identifiers.get(0).getText()));
            atomExpression.addChild(atom);
            expr_list.addChild(atomExpression);
            root.addChild(expr_list);

            // Terminal 'in'
            root.addChild(new PythonASTTerminal("in"));

            //TestList
            expr_list = new PythonASTExpressionList();
            expr_list.addChild(visitExpression(exprs.get(2)));
            root.addChild(expr_list);

        } else {
            // Child 1: Expression List
            // For Init
            /*
            for (i = X; i < Y; i++)
             */

            if (isBasicForControl(control)) {
                PythonASTAtomExpression atomExpression = new PythonASTAtomExpression();
                PythonASTAtom atom = new PythonASTAtom();

                atom.addChild(new PythonASTTerminal(control.getChild(0).getChild(0).getChild(0).getText()));
                atomExpression.addChild(atom);
                root.addChild(atomExpression);
                root.addChild(new PythonASTTerminal("in"));

                // Range Function
                root.addChild(new PythonASTTerminal("range"));
                root.addChild(new PythonASTTerminal("("));

                // Start conditions
                atomExpression = new PythonASTAtomExpression();
                atom = new PythonASTAtom();

                atom.addChild(new PythonASTTerminal(control.getChild(0).getChild(0).getChild(2).getText()));
                atomExpression.addChild(atom);
                root.addChild(atomExpression);

                root.addChild(new PythonASTTerminal(","));

                // stop conditions
                atomExpression = new PythonASTAtomExpression();
                atom = new PythonASTAtom();

                atom.addChild(new PythonASTTerminal(control.getChild(1).getChild(2).getText()));
                atomExpression.addChild(atom);
                root.addChild(atomExpression);

                root.addChild(new PythonASTTerminal(","));

                // Iterate Condition
                atomExpression = new PythonASTAtomExpression();
                atom = new PythonASTAtom();
                if (((IntASTUnaryExpression) control.getChild(2).getChild(0)).getOperator().getText().equals("++")) {
                    atom.addChild(new PythonASTTerminal("1"));

                } else if (((IntASTUnaryExpression) control.getChild(2).getChild(0)).getOperator().getText().equals("--") ) {
                    atom.addChild(new PythonASTTerminal("-1"));

                } else {
                    throw new IllegalArgumentException("Invalid Iteration");
                }
                atomExpression.addChild(atom);
                root.addChild(atomExpression);

                // End Range Function
                root.addChild(new PythonASTTerminal(")"));

            } else {
                /*
                TODO Fallback for-loop conversion

                for (expr1; expr2; expr3) {
                  ...
                }

                  |
                  V

                expr1;
                while (expr2) {
                   try {
                      ...
                   } finally {
                      expr3;
                   }
                }
                 */
            }
        }

        //Add suite Identifier & get suite statements
        root.addChild(new PythonASTTerminal(":"));
        IntASTStatement stmt = ctx.getStatement();

        if (stmt instanceof IntASTBlock) {
            root.addChild(visitBlock((IntASTBlock) stmt));
        } else {
            PythonASTSuite suite = new PythonASTSuite();
            suite.addChild(visitStatement(stmt));
            root.addChild(suite);
       }

        return root;
    }


    public PythonASTAtomExpression visitStatementExpression(IntASTStatementExpression ctx) {
        PythonASTAtomExpression root = new PythonASTAtomExpression();

        List <IntASTExpression> exprs = ctx.getExpression();

        // Primary -> Atom
        if (exprs.get(0) instanceof IntASTMethodCall) {
            root.addChild(visitMethodCall((IntASTMethodCall) exprs.get(0)));
        } else if (exprs.get(0) instanceof IntASTParExpression) {
            root.addChild(visitParExpression((IntASTParExpression) exprs.get(0)).getChild(0));
        } else if (exprs.get(0) instanceof IntASTNewExpression) {
            root.addChild(visitNewExpression((IntASTNewExpression) exprs.get(0)));
        } else {
            PythonASTAtom atom = new PythonASTAtom();

            atom.addChild(visitTerminal((IntASTTerminal) exprs.get(0)));
            root.addChild(atom);
        }

        // Selector -> Trailer
        for (int i = 1; i < exprs.size(); i++) {
            if (exprs.get(0) instanceof IntASTMethodCall) {
                root.addChild(visitMethodCall((IntASTMethodCall) exprs.get(0)));
            } else if (exprs.get(0) instanceof IntASTParExpression) {
                root.addChild(visitParExpression((IntASTParExpression) exprs.get(0)).getChild(0));
            } else {
                PythonASTTrailer atom = new PythonASTTrailer();

                atom.addChild(visitTerminal((IntASTTerminal) exprs.get(0)));
                root.addChild(atom);
            }
        }

        return root;
    }

    public PythonASTAtomExpression visitCastExpression (IntASTCastExpression ctx) {
        PythonASTAtomExpression root = new PythonASTAtomExpression();
        PythonASTAtom atom = new PythonASTAtom();
        PythonASTTrailer trailer = new PythonASTTrailer();

        atom.addChild(visitTerminal((IntASTTerminal) ctx.getChild(0)));
        trailer.addChild(new PythonASTTerminal("("));
        trailer.addChild(visitExpression((IntASTExpression) ctx.getChild(1)));
        trailer.addChild(new PythonASTTerminal(")"));

        root.addChild(atom);
        root.addChild(trailer);

        return root;
    }

    public PythonASTAtomExpression visitMethodCall(IntASTMethodCall ctx) {
        PythonASTAtomExpression root = new PythonASTAtomExpression();
        PythonASTAtom atom = new PythonASTAtom();
        PythonASTTrailer trail = new PythonASTTrailer();

        atom.addChild(visitTerminal(ctx.getIdentifier()));

        trail.addChild(new PythonASTTerminal("("));
        trail.addChild(visitExpressionList(ctx.getExpressionList()));
        trail.addChild(new PythonASTTerminal(")"));

        root.addChild(atom);
        root.addChild(trail);

        if (ctx.getIdentifier().getText().equals("System.out.print")) {
            trail.getChild(1).addChild(new PythonASTTerminal(","));

            PythonASTBinaryExpression bin = new PythonASTBinaryExpression();
            PythonASTAtomExpression atomExpr = new PythonASTAtomExpression();
            atom = new PythonASTAtom();

            atom.addChild(new PythonASTTerminal("end"));
            atomExpr.addChild(atom);

            bin.addChild(atomExpr);
            bin.addChild(new PythonASTTerminal("="));

            atomExpr = new PythonASTAtomExpression();
            atom = new PythonASTAtom();

            atom.addChild(new PythonASTTerminal("\"\""));
            atomExpr.addChild(atom);

            bin.addChild(atomExpr);

            trail.getChild(1).addChild(bin);
        }

        return root;
    }

    public PythonASTExpressionList visitExpressionList(IntASTExpressionList ctx) {
        PythonASTExpressionList root = new PythonASTExpressionList();
        List<IntASTExpression> exprs = ctx.getExpression();

        if (!exprs.isEmpty()) {
            root.addChild(visitExpression(exprs.get(0)));

            for (int i = 1; i < exprs.size(); i++) {
                root.addChild(new PythonASTTerminal(","));
                root.addChild(visitExpression(exprs.get(i)));
            }
        }

        return root;
    }

    public PythonASTAtomExpression visitArrayInit(IntASTArrayInit ctx) {
        PythonASTAtomExpression root = new PythonASTAtomExpression();
        PythonASTAtom atom = new PythonASTAtom();
        PythonASTExpressionList exprs = new PythonASTExpressionList();

        atom.addChild(new PythonASTTerminal ("["));
        if (ctx.getChildCount() != 0) {
            for (int i = 0; i < ctx.getChildCount() - 1; i++) {
                if (ctx.getChild(i) instanceof IntASTExpression) {
                    exprs.addChild(visitExpression((IntASTExpression) ctx.getChild(i)));
                } else {
                    exprs.addChild(visitArrayInit((IntASTArrayInit) ctx.getChild(i)));
                }
                exprs.addChild(new PythonASTTerminal(","));
            }
            if (ctx.getChild(ctx.getChildCount() - 1) instanceof IntASTExpression) {
                exprs.addChild(visitExpression((IntASTExpression) ctx.getChild(ctx.getChildCount() - 1)));
            } else {
                exprs.addChild(visitArrayInit((IntASTArrayInit) ctx.getChild(ctx.getChildCount() - 1)));
            }
        }

        atom.addChild(exprs);
        atom.addChild(new PythonASTTerminal ("]"));

        root.addChild(atom);

        return root;
    }

    public PythonASTTerminal visitTerminal(IntASTTerminal ctx) {
        if (ctx instanceof IntASTOperator) {
            return visitOperator((IntASTOperator) ctx);

        } else if (ctx instanceof IntASTIdentifier) {
            return visitIdentifier((IntASTIdentifier) ctx);
        } else if (ctx instanceof IntASTLiteral) {
            return visitLiteral((IntASTLiteral) ctx);
        } else {
            return new PythonASTTerminal(ctx.getText());
        }
    }

    public PythonASTTerminal visitIdentifier(IntASTIdentifier ctx) {
        return convertIdentifier(ctx.getText());
    }

    private static PythonASTTerminal convertIdentifier(String id) {
        switch (id) {
            case "double":
                return new PythonASTTerminal("float");
            case "String":
                return new PythonASTTerminal("str");
            case "byte":
            case "short":
            case "long":
                return new PythonASTTerminal("int");
            case "char":
                return new PythonASTTerminal("chr");
            case "System.out.println":
            case "System.out.print":
                return new PythonASTTerminal("print");
            case "System.out.printf":
                return new PythonASTTerminal("printf");
            default:
                // check for "this" identifier
                StringBuilder str = new StringBuilder();
                String[] split = id.split("\\.");
                for (int i = 0; i < split.length; i++) {
                    if (split[i].equals("this")) {
                        str.append("self");
                    } else {
                        str.append(split[i]);
                    }
                    if (i < split.length - 1) {
                        str.append('.');
                    }
                }
                return new PythonASTTerminal(str.toString());
        }
    }

    public PythonASTTerminal visitOperator(IntASTOperator ctx) {
        switch (ctx.getText()) {
            case "||":
                return new PythonASTTerminal("or");
            case "&&":
                return new PythonASTTerminal("and");
            case "instanceof":
                return new PythonASTTerminal("is");

            default:
                return new PythonASTTerminal(ctx.getText());
        }
    }

    public PythonASTTerminal visitLiteral(IntASTLiteral ctx) {
        switch (ctx.getText()) {
            case "true":
                return new PythonASTTerminal("True");
            case "false":
                return new PythonASTTerminal("False");
            default:
                return new PythonASTTerminal(ctx.getText());
        }
    }
}
