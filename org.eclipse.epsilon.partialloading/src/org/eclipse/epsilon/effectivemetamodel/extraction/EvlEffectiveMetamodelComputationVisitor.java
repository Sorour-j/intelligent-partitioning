package org.eclipse.epsilon.effectivemetamodel.extraction;

import org.eclipse.epsilon.effectivemetamodel.XMIN;
import org.eclipse.epsilon.eol.exceptions.models.EolModelNotFoundException;
import org.eclipse.epsilon.eol.types.EolModelElementType;
import org.eclipse.epsilon.erl.dom.Post;
import org.eclipse.epsilon.erl.dom.Pre;
import org.eclipse.epsilon.evl.EvlModule;
import org.eclipse.epsilon.evl.dom.Constraint;
import org.eclipse.epsilon.evl.dom.ConstraintContext;
import org.eclipse.epsilon.evl.dom.Fix;
import org.eclipse.epsilon.evl.dom.IEvlVisitor;
import org.eclipse.epsilon.evl.staticanalyser.EvlStaticAnalyser;

public class EvlEffectiveMetamodelComputationVisitor extends EolEffectiveMetamodelComputationVisitor implements IEvlVisitor {

	protected EvlStaticAnalyser staticAnalyser = null;
	protected EvlModule module = new EvlModule();
	
	
	public XMIN setExtractor(EvlModule module , EvlStaticAnalyser staticAnalyser) {
		
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
		
		for (Pre p : module.getPre()) {
			p.accept(this);
		}

		for (ConstraintContext cc : module.getConstraintContexts()) {
			cc.accept(this);
		}
		
		module.getDeclaredOperations().forEach(o -> o.accept(this));
		
		for (Post post : module.getPost()) {
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
	public void visit(ConstraintContext constraintContext) {
		EolModelElementType target;
		if (constraintContext.getTypeExpression() != null) {
			target = (EolModelElementType) staticAnalyser.getResolvedType(constraintContext.getTypeExpression());
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
		constraintContext.getTypeExpression().accept(this);
		for (Constraint c : constraintContext.getConstraints())
			c.accept(this);
	}

	@Override
	public void visit(Constraint constraint) {
		if (constraint.getGuardBlock() != null)
			constraint.getGuardBlock().accept(this);

		if (constraint.getCheckBlock() != null)
			constraint.getCheckBlock().accept(this);

		if (constraint.getMessageBlock() != null)
			constraint.getMessageBlock().accept(this);

		for (Fix f : constraint.getFixes()) {
			if (f.getBodyBlock() != null)
				f.getBodyBlock().accept(this);

			if (f.getGuardBlock() != null)
				f.getGuardBlock().accept(this);

			if (f.getTitleBlock() != null)
				f.getTitleBlock().accept(this);
		}
	}

	@Override
	public void visit(Fix fix) {
		// TODO Auto-generated method stub
	}

}
