package org.eclipse.epsilon.effectivemetamodel.extraction;

import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodel;
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

public class EvlEffectiveMetamodelComputationVisitor extends EolEffectiveMetamodelComputationVisitor
		implements IEvlVisitor {

	protected EvlStaticAnalyser staticAnalyser = null;
	protected EvlModule module;

	public EffectiveMetamodel setExtractor(EvlModule module, EvlStaticAnalyser staticAnalyser) {

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
		for (ConstraintContext cc : module.getConstraintContexts()) {
			cc.accept(this);
		}
		for (Constraint c : module.getConstraints()) {
			c.accept(this);
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
	public void visit(ConstraintContext constraintContext) {
		EolModelElementType target;

		if (constraintContext.getTypeExpression() != null) {
			target = (EolModelElementType) staticAnalyser.getResolvedType(constraintContext.getTypeExpression());
			efmetamodel.addToAllOfKind(target.getName());

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
		constraintContext.getTypeExpression().accept(this);
		if (constraintContext.getGuardBlock() != null)
			constraintContext.getGuardBlock().accept(this);
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
