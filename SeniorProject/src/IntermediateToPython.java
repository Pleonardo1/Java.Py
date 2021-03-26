import intermediate.*;
import python.*;

import java.util.List;

public class IntermediateToPython {
    public PythonASTFileInput visitCompilationUnit(IntASTCompilationUnit ctx) {
        PythonASTFileInput root = new PythonASTFileInput();

        // convert the imports
        for (IntASTImport node : ctx.getImportDeclaration()) {
            root.addChild(visitImport(node));
        }
        // convert the class declarations
        for (IntASTClass node : ctx.getClassDeclaration()) {
            root.addChild(visitClass(node));
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

    public PythonASTClass visitClass(IntASTClass ctx) {
        PythonASTClass root = new PythonASTClass(ctx.getText());

        root.addChild(new PythonASTTerminal("class"));
        root.addChild(new PythonASTTerminal(ctx.getText()));

        // class name (argList):
        if (ctx.getTypeList() != null) {
            root.addChild(new PythonASTTerminal("("));
            root.addChild(visitTypeList(ctx.getTypeList()));
            root.addChild(new PythonASTTerminal(")"));
        }

        root.addChild(new PythonASTTerminal(":"));

        root.addChild(visitClassBody(ctx.getClassBody()));

        return root;
    }

    public PythonASTArgList visitTypeList(IntASTTypeList ctx) {
        List<IntASTIdentifier> types = ctx.getIdentifier();
        if (types.isEmpty()) {
            return null;
        }
        PythonASTArgList root = new PythonASTArgList();

        //Can have single argument or more
        for (int i = 0; i < types.size() - 1; i++) {
            root.addChild(visitTerminal(types.get(i)));
            root.addChild(new PythonASTTerminal(","));
        }
        root.addChild(visitTerminal(types.get(types.size()-1)));
        return root;
    }

    public PythonASTSuite visitClassBody(IntASTClassBody ctx) {
        PythonASTSuite root = new PythonASTSuite();

        root.addChild(new PythonASTTerminal.PythonASTNewline());
        root.addChild(new PythonASTTerminal.PythonASTIndent());

        for (IntASTMember node : ctx.getMember()) {
            root.addChild(visitMember(node));
        }

        root.addChild(new PythonASTTerminal.PythonASTDedent());

        return root;
    }

    public PythonASTNode visitMember(IntASTMember ctx) {

        if (ctx instanceof IntASTBlock) {
            return visitBlock((IntASTBlock) ctx);

        } else if (ctx instanceof IntASTClass) {
            return visitClass((IntASTClass) ctx);

        } else if (ctx instanceof IntASTField) {
            return visitField((IntASTField) ctx);

        } else if (ctx instanceof IntASTConstructor) {
            return visitConstructor((IntASTConstructor) ctx);

        } else if (ctx instanceof IntASTMethod) {
            return visitMethod((IntASTMethod) ctx);
        } else {
            throw new IllegalArgumentException("Unknown IntASTMember type: " + ctx.getClass().getSimpleName());
        }
    }

    public PythonASTSuite visitBlock(IntASTBlock ctx) {
        PythonASTSuite root = new PythonASTSuite();
        List<IntASTStatement> stmts = ctx.getStatement();

        root.addChild(new PythonASTTerminal.PythonASTNewline());
        root.addChild(new PythonASTTerminal.PythonASTIndent());

        if (stmts.isEmpty()) {
            PythonASTSimpleStatement simple = new PythonASTSimpleStatement();
            PythonASTSmallStatement small = new PythonASTSmallStatement();

            small.addChild(new PythonASTPassStatement());

            simple.addChild(small);
            simple.addChild(new PythonASTTerminal.PythonASTNewline());

            root.addChild(simple);
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

            for (IntASTExpression expr : exprs) {
                root.addChild(visitExpression(expr));
                root.addChild(new PythonASTTerminal(";"));
            }
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
        }

        if (ctx.getMethodParameters() != null) {
            if (!ctx.isStatic()) {
                root.addChild(new PythonASTTerminal(","));
            }
            root.addChild(visitMethodParameters(ctx.getMethodParameters()));
        }

        root.addChild(new PythonASTTerminal(")"));


        root.addChild(new PythonASTTerminal(":"));

        if (ctx.getBlock() != null) {
            root.addChild(visitBlock(ctx.getBlock()));
        }
        return root;
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
            root.addChild(visitCompoundStatement(ctx));
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
            
        /*} else if (ctx instanceof IntASTCatches) {
            return visitCatches((IntASTCatches) ctx); */

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

        for(int i = 0; i < params.size() - 1; i++) {
            tfpdef = new PythonASTTfpdef();
            tfpdef.addChild(visitTerminal(params.get(i)));
            root.addChild(tfpdef);
            root.addChild(new PythonASTTerminal(","));
        }

        tfpdef = new PythonASTTfpdef();
        tfpdef.addChild(visitTerminal(params.get(params.size()-1)));
        root.addChild(tfpdef);

        return root;
    }

    public PythonASTNode visitExpression(IntASTExpression ctx) {
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
        } else if (ctx instanceof IntASTTypeList) {
            return visitTypeList((IntASTTypeList) ctx);
        } else if (ctx instanceof IntASTExpressionList) {
            return visitExpressionList((IntASTExpressionList) ctx);
        } else if (ctx instanceof IntASTCastExpression) {
            return visitCastExpression((IntASTCastExpression) ctx);
        } else if (ctx instanceof IntASTMethodCall) {
            return visitMethodCall((IntASTMethodCall) ctx);
        } else if (ctx instanceof IntASTAssert) {
            return visitAssert((IntASTAssert) ctx);
        } else if (ctx instanceof IntASTTerminal) {
            return visitTerminal((IntASTTerminal) ctx);
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

    public PythonASTUnaryExpression visitUnaryExpression(IntASTUnaryExpression ctx) {
        PythonASTUnaryExpression root = new PythonASTUnaryExpression();

        // determine if the expression is prefix or postfix
        if (ctx.getChild(0) instanceof IntASTOperator) {
            // prefix
            root.addChild(visitTerminal(ctx.getOperator()));
            root.addChild(visitExpression(ctx.getExpressionNotOperator()));
        } else {
            // postfix
            root.addChild(visitExpression(ctx.getExpressionNotOperator()));
            root.addChild(visitTerminal(ctx.getOperator()));
        }

        return root;
    }

    public PythonASTAtomExpression visitParExpression(IntASTParExpression ctx) {
        PythonASTAtomExpression root = new PythonASTAtomExpression();
        PythonASTAtom atom = new PythonASTAtom();

        atom.addChild(visitExpression(ctx.getExpression()));
        root.addChild(atom);

        return root;
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
            root.addChild(visitBlock(ctx.getBlock(ctx.getChildCount() - 1)));
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
        PythonASTSimpleStatement stmt = new PythonASTSimpleStatement();
        PythonASTSmallStatement small_stmt = new PythonASTSmallStatement();
        PythonASTBinaryExpression bin_expr = new PythonASTBinaryExpression();

        /*
         __is_first_loop_XX = True
         */
        bin_expr.addChild(new PythonASTTerminal(firstLoop));
        bin_expr.addChild(new PythonASTTerminal("="));
        bin_expr.addChild(new PythonASTTerminal("True"));

        small_stmt.addChild(bin_expr);

        stmt.addChild(small_stmt);
        stmt.addChild(new PythonASTTerminal.PythonASTNewline());

        root.addChild(stmt);

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

        stmt = new PythonASTSimpleStatement();
        small_stmt = new PythonASTSmallStatement();
        bin_expr = new PythonASTBinaryExpression();

        bin_expr.addChild(new PythonASTTerminal(firstLoop));
        bin_expr.addChild(new PythonASTTerminal("="));
        bin_expr.addChild(new PythonASTTerminal("False"));

        small_stmt.addChild(bin_expr);

        stmt.addChild(small_stmt);
        stmt.addChild(new PythonASTTerminal.PythonASTNewline());

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

    public PythonASTTerminal visitTerminal(IntASTTerminal ctx) {
        if (ctx instanceof IntASTOperator) {
            return visitOperator((IntASTOperator) ctx);
        } else {
            return new PythonASTTerminal(ctx.getText());
        }
    }

    public PythonASTTerminal visitOperator(IntASTOperator ctx) {
        switch (ctx.getText()) {
            case "||":
                return new PythonASTTerminal("or");
            case "&&":
                return new PythonASTTerminal("and");

            default:
                return new PythonASTTerminal(ctx.getText());
        }
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
}
