package org.eclipse.epsilon.effectivemetamodel.extraction;

import java.util.HashMap;

import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodel;
import org.eclipse.epsilon.effectivemetamodel.XMIN;
import org.eclipse.epsilon.eol.dom.ExecutableBlock;
import org.eclipse.epsilon.eol.dom.Expression;
import org.eclipse.epsilon.eol.dom.StatementBlock;
import org.eclipse.epsilon.eol.types.EolModelElementType;
import org.eclipse.epsilon.etl.dom.IEtlVisitor;
import org.eclipse.epsilon.etl.dom.TransformationRule;

public class EtlPartitioningEffectiveMetamodelComputationVisitor extends EtlEffectiveMetamodelComputationVisitor implements IEtlVisitor {
	
	
	//What about Pre and Post?
	HashMap<TransformationRule, EffectiveMetamodel> ruleModels = new HashMap<TransformationRule, EffectiveMetamodel>();
	
	@Override
	public void visit(TransformationRule transformationRule) {
		EolModelElementType target;
		efmetamodel.clear();
		
		if (transformationRule.getSourceParameter().getTypeExpression()!= null) {
			target = (EolModelElementType) staticAnalyser.getResolvedType(transformationRule.getSourceParameter().getTypeExpression());
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
		ruleModels.put(transformationRule, efmetamodel);
	}

}
