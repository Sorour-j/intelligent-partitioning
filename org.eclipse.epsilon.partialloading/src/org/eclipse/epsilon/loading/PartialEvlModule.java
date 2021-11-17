package org.eclipse.epsilon.loading;

import java.util.HashMap;

import org.eclipse.epsilon.common.module.ModuleElement;
import org.eclipse.epsilon.common.parse.AST;
import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodel;
import org.eclipse.epsilon.evl.EvlModule;
import org.eclipse.epsilon.evl.dom.ConstraintContext;

public class PartialEvlModule extends EvlModule {

	//protected List<PartialConstraintContext> constraintContexts;
	//protected final ArrayList<PartialConstraintContext> declaredConstraintContexts = new ArrayList<>(0);
	HashMap<String, EffectiveMetamodel> effectiveMetamodels = new HashMap<String, EffectiveMetamodel>();
	
	public PartialEvlModule(){
		super();
	}
	
//	@Override
//	public void build(AST cst, IModule module) {
//		super.build(cst, module);
//		for (ConstraintContext c : getDeclaredConstraintContexts()) {
//			constraintContexts.add((PartialConstraintContext)c);
//		}
//	}
	
	@Override
	public ModuleElement adapt(AST cst, ModuleElement parentAst) {
		ModuleElement me = super.adapt(cst, parentAst);
		
		if (me instanceof ConstraintContext)
			return new PartialConstraintContext();
			else
				return me;
		//return super.adapt(cst, parentAst);
	}
}
