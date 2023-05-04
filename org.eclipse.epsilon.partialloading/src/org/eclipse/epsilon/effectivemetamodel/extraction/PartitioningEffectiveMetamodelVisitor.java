package org.eclipse.epsilon.effectivemetamodel.extraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodel;
import org.eclipse.epsilon.eol.dom.Expression;
import org.eclipse.epsilon.eol.dom.OperationCallExpression;
import org.eclipse.epsilon.eol.dom.StringLiteral;
import org.eclipse.epsilon.evl.EvlModule;
import org.eclipse.epsilon.evl.dom.Constraint;
import org.eclipse.epsilon.evl.staticanalyser.EvlStaticAnalyser;

public class PartitioningEffectiveMetamodelVisitor extends EvlEffectiveMetamodelComputationVisitor {
	
	LinkedHashMap<Constraint, EffectiveMetamodel>  effectivemetamodels = new LinkedHashMap<Constraint, EffectiveMetamodel> ();
	ArrayList<EffectiveMetamodel> efmodels = new ArrayList<EffectiveMetamodel>();
	
	public LinkedHashMap<Constraint, EffectiveMetamodel> setpartitionExtractor(EvlModule module, EvlStaticAnalyser staticAnalyser) {
		this.module = module;
		this.staticAnalyser = staticAnalyser;
		extractor();
		return effectivemetamodels;
	}
	
	@Override
	public void extractor() {
	
		for (Constraint c : module.getConstraints()) {
			
			Set<String> names = new HashSet<String>();
			efmetamodel = new EffectiveMetamodel();
			
			c.getConstraintContext().accept(this);
			c.accept(this);
			
			efmetamodel.setName(c.getName());
			names.add(c.getName());
			efmodels.add(efmetamodel);
			efmetamodel.setIsCalculated(true);
			effectivemetamodels.put(c, efmetamodel);
		}
	}
	@Override
	public void visit(OperationCallExpression operationCallExpression) {
		super.visit(operationCallExpression);
		//if (module.getDeclaredOperations().contains(operationCallExpression.getNameExpression().getName()))
			module.getOperations().getOperation(operationCallExpression.getNameExpression().getName()).accept(this);
		//I am not sure about this condition?!
		//if (staticanalyser.getResolvedType(operationCallExpression.getTargetExpression()) instanceof EolModelElementType) {
			if (operationCallExpression.getNameExpression().getName().equals("satisfies")) {
				for (Expression parameterExpression : operationCallExpression.getParameterExpressions()) {
						parameterExpression.accept(this);
						String parameter = ((StringLiteral)parameterExpression).getValue();
						for (Constraint cc : module.getConstraints()) {
							if ((cc.getName()).equals(parameter)) {
								cc.accept(this);
								break;
							}
						}
				}
		//	}
		}
	}
}
