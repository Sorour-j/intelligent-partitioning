package org.eclipse.epsilon.effectivemetamodel.extraction;

import org.eclipse.epsilon.effectivemetamodel.XMIN;
import org.eclipse.epsilon.eol.dom.ExecutableBlock;
import org.eclipse.epsilon.eol.dom.Expression;
import org.eclipse.epsilon.eol.dom.StatementBlock;
import org.eclipse.epsilon.eol.exceptions.models.EolModelNotFoundException;
import org.eclipse.epsilon.eol.types.EolModelElementType;
import org.eclipse.epsilon.erl.dom.Post;
import org.eclipse.epsilon.erl.dom.Pre;
import org.eclipse.epsilon.etl.EtlModule;
import org.eclipse.epsilon.etl.dom.IEtlVisitor;
import org.eclipse.epsilon.etl.dom.TransformationRule;
import org.eclipse.epsilon.etl.staticanalyser.EtlStaticAnalyser;
import org.eclipse.epsilon.evl.EvlModule;
import org.eclipse.epsilon.evl.dom.Constraint;
import org.eclipse.epsilon.evl.dom.ConstraintContext;
import org.eclipse.epsilon.evl.staticanalyser.EvlStaticAnalyser;

public class EtlEffectiveMetamodelComputationVisitor extends EolEffectiveMetamodelComputationVisitor implements IEtlVisitor {

	EtlModule module;
	EtlStaticAnalyser staticAnalyser = new EtlStaticAnalyser();

	public XMIN setExtractor(EtlModule module , EtlStaticAnalyser staticAnalyser) {
		
		this.module = module;
		this.staticAnalyser = staticAnalyser;
		try {
			xminModel = (XMIN) module.getContext().getModelRepository().getModelByName(staticAnalyser.getContext().getModelDeclarations().get(0).getModel().getName());
		} catch (EolModelNotFoundException e) {
			// TODO Auto-generated catch block
			xminModel = (XMIN) staticAnalyser.getContext().getModelDeclarations().get(0).getModel();
		}
		extractor();
		iterate();
		xminModel.setIsCalculated(true);
		return xminModel;
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
		modelVersion1 = effectiveMetamodelConvertor(xminModel);
		extractor();
		modelVersion2 = effectiveMetamodelConvertor(xminModel);
		while (!modelVersion1.equals(modelVersion2)) {
			modelVersion1 = modelVersion2;
			extractor();
			modelVersion2 = effectiveMetamodelConvertor(xminModel);
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
			xminModel.addToAllOfKind(target.getName());

			if (xminModel.allOfTypeContains(target.getTypeName())) {

				xminModel.addToAllOfKind(xminModel.getFromAllOfType(target.getName()));
				xminModel.removeFromAllOfType(target.getName());

			}
			// If the element is already under EM's types reference
			if (xminModel.typesContains(target.getTypeName())) {

				xminModel.addToAllOfKind(xminModel.getFromTypes(target.getName()));
				xminModel.removeFromTypes(target.getName());
			} else {
				// not already under the EM's allOfKind or allOfType references
				xminModel.addToAllOfKind(target.getTypeName());
				loadFeatures(target, xminModel.getFromAllOfKind(target.getName()));
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

}
