package org.eclipse.epsilon.effectivemetamodel.extraction;

import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodel;
import org.eclipse.epsilon.effectivemetamodel.EffectiveType;
import org.eclipse.epsilon.eol.dom.ExecutableBlock;
import org.eclipse.epsilon.eol.dom.Expression;
import org.eclipse.epsilon.eol.dom.PropertyCallExpression;
import org.eclipse.epsilon.eol.dom.StatementBlock;
import org.eclipse.epsilon.eol.m3.StructuralFeature;
import org.eclipse.epsilon.eol.types.EolAnyType;
import org.eclipse.epsilon.eol.types.EolCollectionType;
import org.eclipse.epsilon.eol.types.EolModelElementType;
import org.eclipse.epsilon.eol.types.EolType;
import org.eclipse.epsilon.erl.dom.Post;
import org.eclipse.epsilon.erl.dom.Pre;
import org.eclipse.epsilon.etl.EtlModule;
import org.eclipse.epsilon.etl.dom.IEtlVisitor;
import org.eclipse.epsilon.etl.dom.TransformationRule;
import org.eclipse.epsilon.etl.staticanalyser.EtlStaticAnalyser;

public class EtlEffectiveMetamodelComputationVisitor extends EolEffectiveMetamodelComputationVisitor implements IEtlVisitor {

	EtlModule module;
	EtlStaticAnalyser staticAnalyser = new EtlStaticAnalyser();

	public EffectiveMetamodel setExtractor(EtlModule module , EtlStaticAnalyser staticAnalyser) {
		
		this.module = module;
		this.staticAnalyser = staticAnalyser;
		extractor();
		iterate();
		efmetamodel.setIsCalculated(true);
		return efmetamodel;
	}
	
	@Override
	public void extractor() {
		
		for (Pre p : module.getDeclaredPre()) {
			p.accept(this);
		}

		for (TransformationRule tr : module.getTransformationRules()) {
			 tr.accept(this);
		}
		
		module.getDeclaredOperations().forEach(o -> o.accept(this));
		
		for (Post post : module.getDeclaredPost()) {
			post.accept(this);
		}
	}
	
	@Override
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
	@Override
	public void visit(Post post) {
		post.getBody().accept(this);
	}

	@Override
	public void visit(Pre pre) {
		pre.getBody().accept(this);
	}

	@Override
	public void visit(TransformationRule transformationRule) {
		
		EolModelElementType target;
		if (transformationRule.getSourceParameter().getTypeExpression()!= null) {
			target = (EolModelElementType) staticAnalyser.getResolvedType(transformationRule.getSourceParameter().getTypeExpression());
			//efmetamodel.addToAllOfKind(target.getName());
			
			if (efmetamodel.allOfTypeContains(target.getTypeName())) {

				efmetamodel.addToAllOfKind(efmetamodel.getFromAllOfType(target.getTypeName()));
				efmetamodel.removeFromAllOfType(target.getName());

			}
			// If the element is already under EM's types reference
			if (efmetamodel.typesContains(target.getTypeName())) {

				efmetamodel.addToAllOfKind(efmetamodel.getFromTypes(target.getTypeName()));
				efmetamodel.removeFromTypes(target.getName());
			} else {
				// not already under the EM's allOfKind or allOfType references
				efmetamodel.addToAllOfKind(target.getTypeName());
				loadFeatures(target, efmetamodel.getFromAllOfKind(target.getTypeName()));
			}
		}
		transformationRule.getSourceParameter().getTypeExpression().accept(this);
		transformationRule.getBody().accept(this);
		if(transformationRule.getGuard() != null) {
			ExecutableBlock<Boolean> guard = transformationRule.getGuard();
			if(guard.getBody() instanceof StatementBlock) {
				StatementBlock guardBody = ((StatementBlock) guard.getBody());
				guardBody.accept(this);
			}
			else
				((Expression)guard.getBody()).accept(this);
		}
		ExecutableBlock<Void> rule =transformationRule.getBody();
		if(rule != null) {
			StatementBlock ruleBody = (StatementBlock) rule.getBody();
			ruleBody.accept(this);
		}
	}
	
	@Override
	public void visit(PropertyCallExpression propertyCallExpression) {

		propertyCallExpression.getTargetExpression().accept(this);
		EolType resolvedType = staticanalyser.getResolvedType(propertyCallExpression.getTargetExpression());
		
		if (resolvedType instanceof EolModelElementType && ((EolModelElementType) resolvedType).getMetaClass()!= null && efmetamodel.hasEffectiveType(((EolModelElementType) resolvedType).getMetaClass().getName())){
			ProprtyCallExpresionHandler(propertyCallExpression, ((EolModelElementType) resolvedType));
		}
		
		else if (resolvedType instanceof EolCollectionType
				&& ((EolCollectionType) resolvedType).getContentType() instanceof EolModelElementType 
				&& efmetamodel.hasEffectiveType(((EolModelElementType)((EolCollectionType) resolvedType).getContentType()).getMetaClass().getName()))  
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
}
