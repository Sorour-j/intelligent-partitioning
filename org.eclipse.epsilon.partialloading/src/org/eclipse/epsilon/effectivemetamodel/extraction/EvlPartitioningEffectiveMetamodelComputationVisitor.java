package org.eclipse.epsilon.effectivemetamodel.extraction;

import java.util.HashMap;

import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodel;
import org.eclipse.epsilon.eol.types.EolModelElementType;
import org.eclipse.epsilon.evl.EvlModule;
import org.eclipse.epsilon.evl.dom.Constraint;
import org.eclipse.epsilon.evl.dom.ConstraintContext;
import org.eclipse.epsilon.evl.dom.Fix;
import org.eclipse.epsilon.evl.staticanalyser.EvlStaticAnalyser;

public class EvlPartitioningEffectiveMetamodelComputationVisitor extends EvlEffectiveMetamodelComputationVisitor {

	protected HashMap<String, EffectiveMetamodel> constraintsMetamodel = new HashMap<String, EffectiveMetamodel>();
	
	//protected EvlStaticAnalyser staticAnalyser = null;
	//protected EvlModule module = new EvlModule();
	
	
public HashMap<String, EffectiveMetamodel> preExtractor(EvlModule module , EvlStaticAnalyser staticAnalyser) {
		
		this.module = module;
		this.staticAnalyser = staticAnalyser;
		extractor();
	//	iterate();
	//	efmetamodel.setIsCalculated(true);
		return constraintsMetamodel;
	}

	@Override
	public void visit(ConstraintContext constraintContext) {
		EolModelElementType target;
		
		for (Constraint c : constraintContext.getConstraints())
		{
			efmetamodel = new EffectiveMetamodel();
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
			c.accept(this);
		}
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
		
		efmetamodel.setIsCalculated(true);
		//efmetamodel.copyEffectiveMetamodel(conEfMM);
		constraintsMetamodel.put(constraint.getName(), efmetamodel);
		//efmetamodel.clear();
	}
	
	public HashMap<String, EffectiveMetamodel> getMetamodels() {
		return constraintsMetamodel;
	}

	
}
