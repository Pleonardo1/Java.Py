
public class MyVisitor extends JavaBaseVisitor<Object>{

    @Override public Object visitCompilationUnit(JavaParser.CompilationUnitContext ctx) {
        return visitChildren(ctx);
    }

    @Override public Object visitPackageDeclaration(JavaParser.PackageDeclarationContext ctx) {
        return visitChildren(ctx);
    }

    @Override public Object visitPrimitiveType(JavaParser.PrimitiveTypeContext ctx) {
        //System.out.println("\n" + ctx.getText() + " ");
        return "var";
    }

    @Override public Object visitVariableDeclarator(JavaParser.VariableDeclaratorContext ctx) {
        //System.out.println("\n" + ctx.getText() + " ");
        return ctx.getText();
     }

/*
    @Override public Object visitImportDeclaration(JavaParser.ImportDeclarationContext ctx) { return visitChildren(ctx); }

    @Override public Object visitTypeDeclaration(JavaParser.TypeDeclarationContext ctx) { return visitChildren(ctx); }

    @Override public Object visitClassOrInterfaceDeclaration(JavaParser.ClassOrInterfaceDeclarationContext ctx) { return visitChildren(ctx); }

    @Override public Object visitClassOrInterfaceModifiers(JavaParser.ClassOrInterfaceModifiersContext ctx) { return visitChildren(ctx); }

    @Override public Object visitClassOrInterfaceModifier(JavaParser.ClassOrInterfaceModifierContext ctx) { return visitChildren(ctx); }

    @Override public Object visitModifiers(JavaParser.ModifiersContext ctx) { return visitChildren(ctx); }

    @Override public Object visitClassDeclaration(JavaParser.ClassDeclarationContext ctx) { return visitChildren(ctx); }

    @Override public Object visitNormalClassDeclaration(JavaParser.NormalClassDeclarationContext ctx) { return visitChildren(ctx); }

    @Override public Object visitTypeParameters(JavaParser.TypeParametersContext ctx) { return visitChildren(ctx); }

    @Override public Object visitTypeParameter(JavaParser.TypeParameterContext ctx) { return visitChildren(ctx); }

    @Override public Object visitTypeBound(JavaParser.TypeBoundContext ctx) { return visitChildren(ctx); }

    @Override public Object visitEnumDeclaration(JavaParser.EnumDeclarationContext ctx) { return visitChildren(ctx); }

    @Override public Object visitEnumBody(JavaParser.EnumBodyContext ctx) { return visitChildren(ctx); }

    @Override public Object visitEnumConstants(JavaParser.EnumConstantsContext ctx) { return visitChildren(ctx); }

    @Override public Object visitEnumConstant(JavaParser.EnumConstantContext ctx) { return visitChildren(ctx); }

    @Override public Object visitEnumBodyDeclarations(JavaParser.EnumBodyDeclarationsContext ctx) { return visitChildren(ctx); }

    @Override public Object visitInterfaceDeclaration(JavaParser.InterfaceDeclarationContext ctx) { return visitChildren(ctx); }

    @Override public Object visitNormalInterfaceDeclaration(JavaParser.NormalInterfaceDeclarationContext ctx) { return visitChildren(ctx); }

    @Override public Object visitTypeList(JavaParser.TypeListContext ctx) { return visitChildren(ctx); }

    @Override public Object visitClassBody(JavaParser.ClassBodyContext ctx) { return visitChildren(ctx); }

    @Override public Object visitInterfaceBody(JavaParser.InterfaceBodyContext ctx) { return visitChildren(ctx); }

    @Override public Object visitClassBodyDeclaration(JavaParser.ClassBodyDeclarationContext ctx) { return visitChildren(ctx); }

    @Override public Object visitMemberDecl(JavaParser.MemberDeclContext ctx) { return visitChildren(ctx); }

    @Override public Object visitMemberDeclaration(JavaParser.MemberDeclarationContext ctx) { return visitChildren(ctx); }

    @Override public Object visitGenericMethodOrConstructorDecl(JavaParser.GenericMethodOrConstructorDeclContext ctx) { return visitChildren(ctx); }

    @Override public Object visitGenericMethodOrConstructorRest(JavaParser.GenericMethodOrConstructorRestContext ctx) { return visitChildren(ctx); }

    @Override public Object visitMethodDeclaration(JavaParser.MethodDeclarationContext ctx) { return visitChildren(ctx); }

    @Override public Object visitFieldDeclaration(JavaParser.FieldDeclarationContext ctx) { return visitChildren(ctx); }

    @Override public Object visitInterfaceBodyDeclaration(JavaParser.InterfaceBodyDeclarationContext ctx) { return visitChildren(ctx); }

    @Override public Object visitInterfaceMemberDecl(JavaParser.InterfaceMemberDeclContext ctx) { return visitChildren(ctx); }

    @Override public Object visitInterfaceMethodOrFieldDecl(JavaParser.InterfaceMethodOrFieldDeclContext ctx) { return visitChildren(ctx); }

    @Override public Object visitInterfaceMethodOrFieldRest(JavaParser.InterfaceMethodOrFieldRestContext ctx) { return visitChildren(ctx); }

    @Override public Object visitMethodDeclaratorRest(JavaParser.MethodDeclaratorRestContext ctx) { return visitChildren(ctx); }

    @Override public Object visitVoidMethodDeclaratorRest(JavaParser.VoidMethodDeclaratorRestContext ctx) { return visitChildren(ctx); }

    @Override public Object visitInterfaceMethodDeclaratorRest(JavaParser.InterfaceMethodDeclaratorRestContext ctx) { return visitChildren(ctx); }

    @Override public Object visitInterfaceGenericMethodDecl(JavaParser.InterfaceGenericMethodDeclContext ctx) { return visitChildren(ctx); }

    @Override public Object visitVoidInterfaceMethodDeclaratorRest(JavaParser.VoidInterfaceMethodDeclaratorRestContext ctx) { return visitChildren(ctx); }

    @Override public Object visitConstructorDeclaratorRest(JavaParser.ConstructorDeclaratorRestContext ctx) { return visitChildren(ctx); }

    @Override public Object visitConstantDeclarator(JavaParser.ConstantDeclaratorContext ctx) { return visitChildren(ctx); }

    @Override public Object visitVariableDeclarators(JavaParser.VariableDeclaratorsContext ctx) { return visitChildren(ctx); }

    @Override public Object visitVariableDeclarator(JavaParser.VariableDeclaratorContext ctx) { return visitChildren(ctx); }

    @Override public Object visitConstantDeclaratorsRest(JavaParser.ConstantDeclaratorsRestContext ctx) { return visitChildren(ctx); }

    @Override public Object visitConstantDeclaratorRest(JavaParser.ConstantDeclaratorRestContext ctx) { return visitChildren(ctx); }

    @Override public Object visitVariableDeclaratorId(JavaParser.VariableDeclaratorIdContext ctx) { return visitChildren(ctx); }

    @Override public Object visitVariableInitializer(JavaParser.VariableInitializerContext ctx) { return visitChildren(ctx); }

    @Override public Object visitArrayInitializer(JavaParser.ArrayInitializerContext ctx) { return visitChildren(ctx); }

    @Override public Object visitModifier(JavaParser.ModifierContext ctx) { return visitChildren(ctx); }

    @Override public Object visitPackageOrTypeName(JavaParser.PackageOrTypeNameContext ctx) { return visitChildren(ctx); }

    @Override public Object visitEnumConstantName(JavaParser.EnumConstantNameContext ctx) { return visitChildren(ctx); }

    @Override public Object visitTypeName(JavaParser.TypeNameContext ctx) { return visitChildren(ctx); }

    @Override public Object visitType(JavaParser.TypeContext ctx) { return visitChildren(ctx); }

    @Override public Object visitClassOrInterfaceType(JavaParser.ClassOrInterfaceTypeContext ctx) { return visitChildren(ctx); }

    @Override public Object visitVariableModifier(JavaParser.VariableModifierContext ctx) { return visitChildren(ctx); }

    @Override public Object visitTypeArguments(JavaParser.TypeArgumentsContext ctx) { return visitChildren(ctx); }

    @Override public Object visitTypeArgument(JavaParser.TypeArgumentContext ctx) { return visitChildren(ctx); }

    @Override public Object visitQualifiedNameList(JavaParser.QualifiedNameListContext ctx) { return visitChildren(ctx); }

    @Override public Object visitFormalParameters(JavaParser.FormalParametersContext ctx) { return visitChildren(ctx); }

    @Override public Object visitFormalParameterDecls(JavaParser.FormalParameterDeclsContext ctx) { return visitChildren(ctx); }

    @Override public Object visitFormalParameterDeclsRest(JavaParser.FormalParameterDeclsRestContext ctx) { return visitChildren(ctx); }

    @Override public Object visitMethodBody(JavaParser.MethodBodyContext ctx) { return visitChildren(ctx); }

    @Override public Object visitConstructorBody(JavaParser.ConstructorBodyContext ctx) { return visitChildren(ctx); }

    @Override public Object visitQualifiedName(JavaParser.QualifiedNameContext ctx) { return visitChildren(ctx); }

    @Override public Object visitLiteral(JavaParser.LiteralContext ctx) { return visitChildren(ctx); }

    @Override public Object visitAnnotations(JavaParser.AnnotationsContext ctx) { return visitChildren(ctx); }

    @Override public Object visitAnnotation(JavaParser.AnnotationContext ctx) { return visitChildren(ctx); }

    @Override public Object visitAnnotationName(JavaParser.AnnotationNameContext ctx) { return visitChildren(ctx); }

    @Override public Object visitElementValuePairs(JavaParser.ElementValuePairsContext ctx) { return visitChildren(ctx); }

    @Override public Object visitElementValuePair(JavaParser.ElementValuePairContext ctx) { return visitChildren(ctx); }

    @Override public Object visitElementValue(JavaParser.ElementValueContext ctx) { return visitChildren(ctx); }

    @Override public Object visitElementValueArrayInitializer(JavaParser.ElementValueArrayInitializerContext ctx) { return visitChildren(ctx); }

    @Override public Object visitAnnotationTypeDeclaration(JavaParser.AnnotationTypeDeclarationContext ctx) { return visitChildren(ctx); }

    @Override public Object visitAnnotationTypeBody(JavaParser.AnnotationTypeBodyContext ctx) { return visitChildren(ctx); }

    @Override public Object visitAnnotationTypeElementDeclaration(JavaParser.AnnotationTypeElementDeclarationContext ctx) { return visitChildren(ctx); }

    @Override public Object visitAnnotationTypeElementRest(JavaParser.AnnotationTypeElementRestContext ctx) { return visitChildren(ctx); }

    @Override public Object visitAnnotationMethodOrConstantRest(JavaParser.AnnotationMethodOrConstantRestContext ctx) { return visitChildren(ctx); }

    @Override public Object visitAnnotationMethodRest(JavaParser.AnnotationMethodRestContext ctx) { return visitChildren(ctx); }

    @Override public Object visitAnnotationConstantRest(JavaParser.AnnotationConstantRestContext ctx) { return visitChildren(ctx); }

    @Override public Object visitDefaultValue(JavaParser.DefaultValueContext ctx) { return visitChildren(ctx); }

    @Override public Object visitBlock(JavaParser.BlockContext ctx) { return visitChildren(ctx); }

    @Override public Object visitBlockStatement(JavaParser.BlockStatementContext ctx) { return visitChildren(ctx); }

    @Override public Object visitLocalVariableDeclarationStatement(JavaParser.LocalVariableDeclarationStatementContext ctx) { return visitChildren(ctx); }

    @Override public Object visitLocalVariableDeclaration(JavaParser.LocalVariableDeclarationContext ctx) { return visitChildren(ctx); }

    @Override public Object visitVariableModifiers(JavaParser.VariableModifiersContext ctx) { return visitChildren(ctx); }

    @Override public Object visitStatement(JavaParser.StatementContext ctx) { return visitChildren(ctx); }

    @Override public Object visitCatches(JavaParser.CatchesContext ctx) { return visitChildren(ctx); }

    @Override public Object visitCatchClause(JavaParser.CatchClauseContext ctx) { return visitChildren(ctx); }

    @Override public Object visitCatchType(JavaParser.CatchTypeContext ctx) { return visitChildren(ctx); }

    @Override public Object visitFinallyBlock(JavaParser.FinallyBlockContext ctx) { return visitChildren(ctx); }

    @Override public Object visitResourceSpecification(JavaParser.ResourceSpecificationContext ctx) { return visitChildren(ctx); }

    @Override public Object visitResources(JavaParser.ResourcesContext ctx) { return visitChildren(ctx); }

    @Override public Object visitResource(JavaParser.ResourceContext ctx) { return visitChildren(ctx); }

    @Override public Object visitFormalParameter(JavaParser.FormalParameterContext ctx) { return visitChildren(ctx); }

    @Override public Object visitSwitchBlockStatementGroups(JavaParser.SwitchBlockStatementGroupsContext ctx) { return visitChildren(ctx); }

    @Override public Object visitSwitchBlockStatementGroup(JavaParser.SwitchBlockStatementGroupContext ctx) { return visitChildren(ctx); }

    @Override public Object visitSwitchLabel(JavaParser.SwitchLabelContext ctx) { return visitChildren(ctx); }

    @Override public Object visitForControl(JavaParser.ForControlContext ctx) { return visitChildren(ctx); }

    @Override public Object visitForInit(JavaParser.ForInitContext ctx) { return visitChildren(ctx); }

    @Override public Object visitEnhancedForControl(JavaParser.EnhancedForControlContext ctx) { return visitChildren(ctx); }

    @Override public Object visitForUpdate(JavaParser.ForUpdateContext ctx) { return visitChildren(ctx); }

    @Override public Object visitParExpression(JavaParser.ParExpressionContext ctx) { return visitChildren(ctx); }

    @Override public Object visitExpressionList(JavaParser.ExpressionListContext ctx) { return visitChildren(ctx); }

    @Override public Object visitStatementExpression(JavaParser.StatementExpressionContext ctx) { return visitChildren(ctx); }

    @Override public Object visitConstantExpression(JavaParser.ConstantExpressionContext ctx) { return visitChildren(ctx); }

    @Override public Object visitExpression(JavaParser.ExpressionContext ctx) { return visitChildren(ctx); }

    @Override public Object visitAssignmentOperator(JavaParser.AssignmentOperatorContext ctx) { return visitChildren(ctx); }

    @Override public Object visitConditionalExpression(JavaParser.ConditionalExpressionContext ctx) { return visitChildren(ctx); }

    @Override public Object visitConditionalOrExpression(JavaParser.ConditionalOrExpressionContext ctx) { return visitChildren(ctx); }

    @Override public Object visitConditionalAndExpression(JavaParser.ConditionalAndExpressionContext ctx) { return visitChildren(ctx); }

    @Override public Object visitInclusiveOrExpression(JavaParser.InclusiveOrExpressionContext ctx) { return visitChildren(ctx); }

    @Override public Object visitExclusiveOrExpression(JavaParser.ExclusiveOrExpressionContext ctx) { return visitChildren(ctx); }

    @Override public Object visitAndExpression(JavaParser.AndExpressionContext ctx) { return visitChildren(ctx); }

    @Override public Object visitEqualityExpression(JavaParser.EqualityExpressionContext ctx) { return visitChildren(ctx); }

    @Override public Object visitInstanceOfExpression(JavaParser.InstanceOfExpressionContext ctx) { return visitChildren(ctx); }

    @Override public Object visitRelationalExpression(JavaParser.RelationalExpressionContext ctx) { return visitChildren(ctx); }

    @Override public Object visitRelationalOp(JavaParser.RelationalOpContext ctx) { return visitChildren(ctx); }

    @Override public Object visitShiftExpression(JavaParser.ShiftExpressionContext ctx) { return visitChildren(ctx); }

    @Override public Object visitShiftOp(JavaParser.ShiftOpContext ctx) { return visitChildren(ctx); }

    @Override public Object visitAdditiveExpression(JavaParser.AdditiveExpressionContext ctx) { return visitChildren(ctx); }

    @Override public Object visitMultiplicativeExpression(JavaParser.MultiplicativeExpressionContext ctx) { return visitChildren(ctx); }

    @Override public Object visitUnaryExpression(JavaParser.UnaryExpressionContext ctx) { return visitChildren(ctx); }

    @Override public Object visitUnaryExpressionNotPlusMinus(JavaParser.UnaryExpressionNotPlusMinusContext ctx) { return visitChildren(ctx); }

    @Override public Object visitCastExpression(JavaParser.CastExpressionContext ctx) { return visitChildren(ctx); }

    @Override public Object visitPrimary(JavaParser.PrimaryContext ctx) { return visitChildren(ctx); }

    @Override public Object visitIdentifierSuffix(JavaParser.IdentifierSuffixContext ctx) { return visitChildren(ctx); }

    @Override public Object visitCreator(JavaParser.CreatorContext ctx) { return visitChildren(ctx); }

    @Override public Object visitCreatedName(JavaParser.CreatedNameContext ctx) { return visitChildren(ctx); }

    @Override public Object visitInnerCreator(JavaParser.InnerCreatorContext ctx) { return visitChildren(ctx); }

    @Override public Object visitArrayCreatorRest(JavaParser.ArrayCreatorRestContext ctx) { return visitChildren(ctx); }

    @Override public Object visitClassCreatorRest(JavaParser.ClassCreatorRestContext ctx) { return visitChildren(ctx); }

    @Override public Object visitExplicitGenericInvocation(JavaParser.ExplicitGenericInvocationContext ctx) { return visitChildren(ctx); }

    @Override public Object visitNonWildcardTypeArguments(JavaParser.NonWildcardTypeArgumentsContext ctx) { return visitChildren(ctx); }

    @Override public Object visitTypeArgumentsOrDiamond(JavaParser.TypeArgumentsOrDiamondContext ctx) { return visitChildren(ctx); }

    @Override public Object visitNonWildcardTypeArgumentsOrDiamond(JavaParser.NonWildcardTypeArgumentsOrDiamondContext ctx) { return visitChildren(ctx); }

    @Override public Object visitSelector(JavaParser.SelectorContext ctx) { return visitChildren(ctx); }

    @Override public Object visitSuperSuffix(JavaParser.SuperSuffixContext ctx) { return visitChildren(ctx); }

    @Override public Object visitExplicitGenericInvocationSuffix(JavaParser.ExplicitGenericInvocationSuffixContext ctx) { return visitChildren(ctx); }

    @Override public Object visitArguments(JavaParser.ArgumentsContext ctx) { return visitChildren(ctx); } */
}


