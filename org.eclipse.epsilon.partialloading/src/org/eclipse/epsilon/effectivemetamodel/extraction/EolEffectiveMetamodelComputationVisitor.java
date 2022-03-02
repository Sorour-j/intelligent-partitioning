package org.eclipse.epsilon.effectivemetamodel.extraction;

import java.util.ArrayList;

import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodel;
import org.eclipse.epsilon.effectivemetamodel.EffectiveType;
import org.eclipse.epsilon.effectivemetamodel.XMIN;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.eol.IEolModule;
import org.eclipse.epsilon.eol.m3.Attribute;
import org.eclipse.epsilon.eol.m3.MetaClass;
import org.eclipse.epsilon.eol.m3.Reference;
import org.eclipse.epsilon.eol.m3.StructuralFeature;
import org.eclipse.epsilon.eol.dom.AbortStatement;
import org.eclipse.epsilon.eol.dom.AndOperatorExpression;
import org.eclipse.epsilon.eol.dom.AnnotationBlock;
import org.eclipse.epsilon.eol.dom.AssignmentStatement;
import org.eclipse.epsilon.eol.dom.BooleanLiteral;
import org.eclipse.epsilon.eol.dom.BreakStatement;
import org.eclipse.epsilon.eol.dom.Case;
import org.eclipse.epsilon.eol.dom.CollectionLiteralExpression;
import org.eclipse.epsilon.eol.dom.ComplexOperationCallExpression;
import org.eclipse.epsilon.eol.dom.ContinueStatement;
import org.eclipse.epsilon.eol.dom.DeleteStatement;
import org.eclipse.epsilon.eol.dom.DivOperatorExpression;
import org.eclipse.epsilon.eol.dom.DoubleEqualsOperatorExpression;
import org.eclipse.epsilon.eol.dom.ElvisOperatorExpression;
import org.eclipse.epsilon.eol.dom.EnumerationLiteralExpression;
import org.eclipse.epsilon.eol.dom.EqualsOperatorExpression;
import org.eclipse.epsilon.eol.dom.ExecutableAnnotation;
import org.eclipse.epsilon.eol.dom.ExecutableBlock;
import org.eclipse.epsilon.eol.dom.Expression;
import org.eclipse.epsilon.eol.dom.ExpressionInBrackets;
import org.eclipse.epsilon.eol.dom.ExpressionStatement;
import org.eclipse.epsilon.eol.dom.FirstOrderOperationCallExpression;
import org.eclipse.epsilon.eol.dom.ForStatement;
import org.eclipse.epsilon.eol.dom.GreaterEqualOperatorExpression;
import org.eclipse.epsilon.eol.dom.GreaterThanOperatorExpression;
import org.eclipse.epsilon.eol.dom.IEolVisitor;
import org.eclipse.epsilon.eol.dom.IfStatement;
import org.eclipse.epsilon.eol.dom.ImpliesOperatorExpression;
import org.eclipse.epsilon.eol.dom.Import;
import org.eclipse.epsilon.eol.dom.IntegerLiteral;
import org.eclipse.epsilon.eol.dom.ItemSelectorExpression;
import org.eclipse.epsilon.eol.dom.LessEqualOperatorExpression;
import org.eclipse.epsilon.eol.dom.LessThanOperatorExpression;
import org.eclipse.epsilon.eol.dom.MapLiteralExpression;
import org.eclipse.epsilon.eol.dom.MinusOperatorExpression;
import org.eclipse.epsilon.eol.dom.ModelDeclaration;
import org.eclipse.epsilon.eol.dom.ModelDeclarationParameter;
import org.eclipse.epsilon.eol.dom.NameExpression;
import org.eclipse.epsilon.eol.dom.NegativeOperatorExpression;
import org.eclipse.epsilon.eol.dom.NewInstanceExpression;
import org.eclipse.epsilon.eol.dom.NotEqualsOperatorExpression;
import org.eclipse.epsilon.eol.dom.NotOperatorExpression;
import org.eclipse.epsilon.eol.dom.Operation;
import org.eclipse.epsilon.eol.dom.OperationCallExpression;
import org.eclipse.epsilon.eol.dom.OperatorExpression;
import org.eclipse.epsilon.eol.dom.OrOperatorExpression;
import org.eclipse.epsilon.eol.dom.Parameter;
import org.eclipse.epsilon.eol.dom.PlusOperatorExpression;
import org.eclipse.epsilon.eol.dom.PostfixOperatorExpression;
import org.eclipse.epsilon.eol.dom.PropertyCallExpression;
import org.eclipse.epsilon.eol.dom.RealLiteral;
import org.eclipse.epsilon.eol.dom.ReturnStatement;
import org.eclipse.epsilon.eol.dom.SimpleAnnotation;
import org.eclipse.epsilon.eol.dom.StatementBlock;
import org.eclipse.epsilon.eol.dom.StringLiteral;
import org.eclipse.epsilon.eol.dom.SwitchStatement;
import org.eclipse.epsilon.eol.dom.TernaryExpression;
import org.eclipse.epsilon.eol.dom.ThrowStatement;
import org.eclipse.epsilon.eol.dom.TimesOperatorExpression;
import org.eclipse.epsilon.eol.dom.TransactionStatement;
import org.eclipse.epsilon.eol.dom.TypeExpression;
import org.eclipse.epsilon.eol.dom.VariableDeclaration;
import org.eclipse.epsilon.eol.dom.WhileStatement;
import org.eclipse.epsilon.eol.dom.XorOperatorExpression;
import org.eclipse.epsilon.eol.exceptions.models.EolModelNotFoundException;
import org.eclipse.epsilon.eol.staticanalyser.EolStaticAnalyser;
import org.eclipse.epsilon.eol.types.EolAnyType;
import org.eclipse.epsilon.eol.types.EolCollectionType;
import org.eclipse.epsilon.eol.types.EolModelElementType;
import org.eclipse.epsilon.eol.types.EolType;

public class EolEffectiveMetamodelComputationVisitor implements IEolVisitor {


	EffectiveMetamodel efmetamodel = new EffectiveMetamodel();
	EolModule module;
	EolStaticAnalyser staticanalyser = new EolStaticAnalyser();

	public EffectiveMetamodel setExtractor(IEolModule imodule, EolStaticAnalyser staticAnalyser) {

		EolModule module = (EolModule) imodule;
		this.module = module;
		this.staticanalyser = staticAnalyser;
		
//		try {
//			xminModel = (XMIN) module.getContext().getModelRepository().getModelByName(staticAnalyser.getContext().getModelDeclarations().get(0).getModel().getName());
//		} catch (EolModelNotFoundException e) {
//			// TODO Auto-generated catch block
//			xminModel = (XMIN) staticAnalyser.getContext().getModelDeclarations().get(0).getModel();
//		}
//		efmetamodel = new EffectiveMetamodel(xminModel.getName(), xminModel.getMetamodelUris().get(0));
		extractor();
		iterate();
		efmetamodel.setIsCalculated(true);
//		xminModel.setEffectiveMteamodel(efmetamodel);
		return efmetamodel;
	}

	public void iterate() {
		String modelVersion1, modelVersion2 = null;
		modelVersion1 = effectiveMetamodelConvertor(efmetamodel);
		extractor();
		modelVersion2 = effectiveMetamodelConvertor(efmetamodel);
		while (!modelVersion1.equals(modelVersion2)) {
			modelVersion1 = modelVersion2;
			extractor();
			modelVersion2 = effectiveMetamodelConvertor(efmetamodel);
		}
	}

	public void extractor() {
		if (module.getMain() != null)
			module.getMain().accept(this);
		module.getDeclaredOperations().forEach(o -> o.accept(this));
	}

	@Override
	public void visit(AbortStatement abortStatement) {
		// TODO Auto-generated method stub
	}

	@Override
	public void visit(AndOperatorExpression andOperatorExpression) {
		visitOperatorExpression(andOperatorExpression);
	}

	@Override
	public void visit(DeleteStatement deleteStatement) {
		deleteStatement.getExpression().accept(this);
	}

	@Override
	public void visit(AnnotationBlock annotationBlock) {
		// TODO Auto-generated method stub
	}

	@Override
	public void visit(AssignmentStatement assignmentStatement) {
		assignmentStatement.getTargetExpression().accept(this);
		assignmentStatement.getValueExpression().accept(this);
	}

	@Override
	public void visit(BooleanLiteral booleanLiteral) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(BreakStatement breakStatement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Case case_) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(CollectionLiteralExpression<?> collectionLiteralExpression) {
		collectionLiteralExpression.getParameterExpressions().get(0).accept(this);
	}

	@Override
	public void visit(ComplexOperationCallExpression complexOperationCallExpression) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ContinueStatement continueStatement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(DivOperatorExpression divOperatorExpression) {
		visitOperatorExpression(divOperatorExpression);
	}

	@Override
	public void visit(DoubleEqualsOperatorExpression doubleEqualsOperatorExpression) {
		visitOperatorExpression(doubleEqualsOperatorExpression);
	}

	@Override
	public void visit(ElvisOperatorExpression elvisOperatorExpression) {
		visitOperatorExpression(elvisOperatorExpression);
	}

	@Override
	public void visit(EnumerationLiteralExpression enumerationLiteralExpression) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(EqualsOperatorExpression equalsOperatorExpression) {
		visitOperatorExpression(equalsOperatorExpression);
	}

	@Override
	public void visit(ExecutableAnnotation executableAnnotation) {
		executableAnnotation.getExpression().accept(this);
	}

	@Override
	public void visit(ExecutableBlock<?> executableBlock) {
		Object body = executableBlock.getBody();
		if (body instanceof StatementBlock) {
			((StatementBlock) body).accept(this);
		}
		else if (body instanceof Expression) {
			((Expression) body).accept(this);
		}
	}

	@Override
	public void visit(ExpressionInBrackets expressionInBrackets) {
		expressionInBrackets.getExpression().accept(this);
	}

	@Override
	public void visit(ExpressionStatement expressionStatement) {
		expressionStatement.getExpression().accept(this);
	}

	@Override
	public void visit(FirstOrderOperationCallExpression firstOrderOperationCallExpression) {
		firstOrderOperationCallExpression.getTargetExpression().accept(this);
		Expression expression = firstOrderOperationCallExpression.getExpressions().get(0);
		expression.accept(this);
	}

	@Override
	public void visit(ForStatement forStatement) {
		forStatement.getIteratedExpression().accept(this);
		forStatement.getIteratorParameter().accept(this);
		forStatement.getBodyStatementBlock().accept(this);
	}

	@Override
	public void visit(GreaterEqualOperatorExpression greaterEqualOperatorExpression) {
		visitOperatorExpression(greaterEqualOperatorExpression);
	}

	@Override
	public void visit(GreaterThanOperatorExpression greaterThanOperatorExpression) {
		visitOperatorExpression(greaterThanOperatorExpression);
	}

	@Override
	public void visit(IfStatement ifStatement) {
		ifStatement.getConditionExpression().accept(this);
		ifStatement.getThenStatementBlock().accept(this);
		if (ifStatement.getElseStatementBlock() != null) {
			ifStatement.getElseStatementBlock().accept(this);
		}
	}

	@Override
	public void visit(ImpliesOperatorExpression impliesOperatorExpression) {
		visitOperatorExpression(impliesOperatorExpression);
	}

	@Override
	public void visit(Import import_) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IntegerLiteral integerLiteral) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ItemSelectorExpression itemSelectorExpression) {
		itemSelectorExpression.getTargetExpression().accept(this);
		itemSelectorExpression.getIndexExpression().accept(this);
	}

	@Override
	public void visit(LessEqualOperatorExpression lessEqualOperatorExpression) {
		visitOperatorExpression(lessEqualOperatorExpression);
	}

	@Override
	public void visit(LessThanOperatorExpression lessThanOperatorExpression) {
		visitOperatorExpression(lessThanOperatorExpression);
	}

	@Override
	public void visit(MapLiteralExpression<?, ?> mapLiteralExpression) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(MinusOperatorExpression minusOperatorExpression) {
		visitOperatorExpression(minusOperatorExpression);
	}

	@Override
	public void visit(ModelDeclaration modelDeclaration) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ModelDeclarationParameter modelDeclarationParameter) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(NameExpression nameExpression) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(NegativeOperatorExpression negativeOperatorExpression) {
		visitOperatorExpression(negativeOperatorExpression);
	}

	@Override
	public void visit(NewInstanceExpression newInstanceExpression) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(NotEqualsOperatorExpression notEqualsOperatorExpression) {
		visitOperatorExpression(notEqualsOperatorExpression);
	}

	@Override
	public void visit(NotOperatorExpression notOperatorExpression) {
		visitOperatorExpression(notOperatorExpression);
	}

	public void visitOperatorExpression(OperatorExpression operatorExpression) {
		operatorExpression.getFirstOperand().accept(this);
		if (operatorExpression.getSecondOperand() != null)
			operatorExpression.getSecondOperand().accept(this);
	}

	@Override
	public void visit(Operation operation) {

		operation.getBody().accept(this);
		if (operation.getReturnTypeExpression() != null)
			operation.getReturnTypeExpression().accept(this);
	}

	@Override
	public void visit(OperationCallExpression operationCallExpression) {
		EolModelElementType target;
		if (operationCallExpression.getTargetExpression() != null) {
			operationCallExpression.getTargetExpression().accept(this);
		}

		for (Expression parameterExpression : operationCallExpression.getParameterExpressions()) {
			parameterExpression.accept(this);
		}

		if (!operationCallExpression.isContextless()) {

			if (staticanalyser.getResolvedType(operationCallExpression.getTargetExpression()) instanceof EolModelElementType) {

				target = (EolModelElementType) staticanalyser.getResolvedType(operationCallExpression.getTargetExpression());

				if (operationCallExpression.getNameExpression().getName().equals("all")
						|| operationCallExpression.getNameExpression().getName().equals("allOfKind")
						|| operationCallExpression.getNameExpression().getName().equals("allInstances")) {

					// If the element is already under EM's allOfType reference
					if (efmetamodel.allOfTypeContains(target.getTypeName())) {

						efmetamodel.addToAllOfKind(efmetamodel.getFromAllOfType(target.getName()));
						efmetamodel.removeFromAllOfType(target.getName());
					}
					// If the element is already under EM's types reference
					if (efmetamodel.typesContains(target.getTypeName())) {

						efmetamodel.addToAllOfKind(efmetamodel.getFromTypes(target.getName()));
						efmetamodel.removeFromTypes(target.getName());
					} else {
						// not already under the EM's allOfKind or allOfType references
						efmetamodel.addToAllOfKind(target.getTypeName());
						loadFeatures(target, efmetamodel.getFromAllOfKind(target.getName()));
					}

				} else if (operationCallExpression.getNameExpression().getName().equals("allOfType")) {
					if (!efmetamodel.allOfKindContains(target.getTypeName())
							&& !efmetamodel.allOfTypeContains(target.getTypeName())) {
						efmetamodel.addToAllOfType(target.getTypeName());
						loadFeatures(target, efmetamodel.getFromAllOfType(target.getName()));
					}
				}
			}
		}
	}

	@Override
	public void visit(OrOperatorExpression orOperatorExpression) {
		visitOperatorExpression(orOperatorExpression);
	}

	@Override
	public void visit(Parameter parameter) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(PlusOperatorExpression plusOperatorExpression) {
		visitOperatorExpression(plusOperatorExpression);
	}

	@Override
	public void visit(PostfixOperatorExpression postfixOperatorExpression) {
		visitOperatorExpression(postfixOperatorExpression);
	}

	@Override
	public void visit(PropertyCallExpression propertyCallExpression) {

		propertyCallExpression.getTargetExpression().accept(this);
		EolType resolvedType = staticanalyser.getResolvedType(propertyCallExpression.getTargetExpression());

		if (resolvedType instanceof EolModelElementType) {
			ProprtyCallExpresionHandler(propertyCallExpression, ((EolModelElementType) resolvedType));
		}
		
		else if (resolvedType instanceof EolCollectionType
				&& ((EolCollectionType) resolvedType).getContentType() instanceof EolModelElementType) 
				{
			ProprtyCallExpresionHandler(propertyCallExpression, ((EolModelElementType) ((EolCollectionType) resolvedType).getContentType()));
		}
		
		else if (resolvedType instanceof EolAnyType) {
			String nameexpr = propertyCallExpression.getNameExpression().getName();
			for (EffectiveType ef : efmetamodel.getAllOfEffectiveTypes()) {
				for (StructuralFeature feature : ef.getTraversalAttributes()) {
					if (feature.getName().equals(nameexpr)) {
						ef.addToAttributes(nameexpr);
					}
				}
				for (StructuralFeature feature : ef.getTraversalReferences()) {
					if (feature.getName().equals(nameexpr)) {
						ef.addToReferences(nameexpr);
					}
				}
			}
		}
	}

	@Override
	public void visit(RealLiteral realLiteral) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ReturnStatement returnStatement) {
		if (returnStatement.getReturnedExpression() != null)
			returnStatement.getReturnedExpression().accept(this);
	}

	@Override
	public void visit(SimpleAnnotation simpleAnnotation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(StatementBlock statementBlock) {
		statementBlock.getStatements().forEach(s -> s.accept(this));
	}

	@Override
	public void visit(StringLiteral stringLiteral) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(SwitchStatement switchStatement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(TernaryExpression ternaryExpression) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ThrowStatement throwStatement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(TimesOperatorExpression timesOperatorExpression) {
		visitOperatorExpression(timesOperatorExpression);
	}

	@Override
	public void visit(TransactionStatement transactionStatement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(TypeExpression typeExpression) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(VariableDeclaration variableDeclaration) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(WhileStatement whileStatement) {
		whileStatement.getConditionExpression().accept(this);
		whileStatement.getBodyStatementBlock().accept(this);
	}

	@Override
	public void visit(XorOperatorExpression xorOperatorExpression) {
		visitOperatorExpression(xorOperatorExpression);
	}

	public void ProprtyCallExpresionHandler(PropertyCallExpression pro, EolModelElementType resolvedType) {

		EolModelElementType target;
		EffectiveType effectiveType;
		ArrayList<StructuralFeature> features = new ArrayList<StructuralFeature>();
		target = resolvedType;

		if (pro.getNameExpression().getName().equals("all")
				|| pro.getNameExpression().getName().equals("allInstances")) {
			// Like AllofKind Algorithm

			// If the element is already under EM's allOfType reference
			if (efmetamodel.allOfTypeContains(target.getTypeName())) {

				efmetamodel.addToAllOfKind(efmetamodel.getFromAllOfType(target.getName()));
				efmetamodel.removeFromAllOfType(target.getName());
			}
			// If the element is already under EM's types reference
			if (efmetamodel.typesContains(target.getTypeName())) {

				efmetamodel.addToAllOfKind(efmetamodel.getFromTypes(target.getName()));
				efmetamodel.removeFromTypes(target.getName());
			} else {

				// not already under the EM's allOfKind or allOfType references
				efmetamodel.addToAllOfKind(target.getTypeName());
				loadFeatures(target, efmetamodel.getFromAllOfKind(target.getName()));
			}
		}
		// not already under the EM's types, allOfKind or allOfType references
		else {
			effectiveType = new EffectiveType(target.getTypeName());
			effectiveType.setEffectiveMetamodel(efmetamodel);

			if (efmetamodel.allOfKindContains(effectiveType.getName()))
				effectiveType = efmetamodel.getFromAllOfKind(effectiveType.getName());

			else if (efmetamodel.allOfTypeContains(effectiveType.getName()))
				effectiveType = efmetamodel.getFromAllOfType(effectiveType.getName());

			else if (efmetamodel.typesContains(effectiveType.getName()))
				effectiveType = efmetamodel.getFromTypes(effectiveType.getName());

			else {
				// add target.getTypeName() under EM's types reference;
				effectiveType = efmetamodel.addToTypes(effectiveType.getName());
				loadFeatures(target, efmetamodel.getFromTypes(target.getName()));
			}
//			if (!target.getMetaClass().getAllStructuralFeatures().isEmpty()) {
//				features.addAll(target.getMetaClass().getAllStructuralFeatures());
//			}
//
//			for (MetaClass metaclass : target.getMetaClass().getSuperTypes()) {
//				features.addAll(metaclass.getAllStructuralFeatures());
//			}

			features.addAll(effectiveType.getTraversalAttributes());
			features.addAll(effectiveType.getTraversalReferences());

			for (StructuralFeature sf : features) {
				if (sf instanceof Attribute) {
					if (sf.getName().equals(pro.getNameExpression().getName())
							&& !effectiveType.containsAttribute(sf.getName()))
						effectiveType.addToAttributes(sf.getName());
//					else
//						effectiveType.addToTraversalAttributes(sf);

				} else if (sf instanceof Reference) {
					if (sf.getName().equals(pro.getNameExpression().getName())
							&& !effectiveType.containsReference(sf.getName())) {
						effectiveType.addToReferences(sf.getName());

						//I add isContaiment in Reference and set it in EmfModelMetamodel class
						if (!((Reference) sf).isContainment()) {
							efmetamodel.addToAllOfKind(sf.getType().getName());
							loadFeatures((EolModelElementType)(sf.getType()), efmetamodel.getFromAllOfKind(sf.getType().getName()));
						}
					}
//					else
//							effectiveType.addToTraversalReferences(sf);
				}
			}
		}
	}

	public String effectiveMetamodelConvertor(EffectiveMetamodel model) {
		String efMetamodelString = new String();
		for (EffectiveType type : model.getAllOfKind()) {
			efMetamodelString += "AllofKind" + type.getName() + "-";
			efMetamodelString += "Attributes" + type.getAttributes() + "-";
			efMetamodelString += "references" + type.getReferences() + "-";
		}
		for (EffectiveType type : model.getAllOfType()) {
			efMetamodelString += "AllofType" + type + "-";
			efMetamodelString += "Attributes" + type.getAttributes() + "-";
			efMetamodelString += "references" + type.getReferences() + "-";
		}
		for (EffectiveType type : model.getTypes()) {
			efMetamodelString += "AllofType" + type + "-";
			efMetamodelString += "Attributes" + type.getAttributes() + "-";
			efMetamodelString += "references" + type.getReferences() + "-";
		}
		return efMetamodelString;
	}

	public void loadFeatures(EolModelElementType type, EffectiveType efType) {

		ArrayList<StructuralFeature> features = new ArrayList<StructuralFeature>();
		if (!type.getMetaClass().getAllStructuralFeatures().isEmpty()) {
			features.addAll(type.getMetaClass().getAllStructuralFeatures());
		}

		for (MetaClass metaclass : type.getMetaClass().getSuperTypes()) {
			features.addAll(metaclass.getAllStructuralFeatures());
		}
		for (StructuralFeature sf : features) {

			if (sf instanceof Attribute) {
				efType.addToTraversalAttributes(sf);

			} else if (sf instanceof Reference) {
				efType.addToTraversalReferences(sf);
			}
		}
	}
}
