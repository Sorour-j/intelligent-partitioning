package org.eclipse.epsilon.loading;

import java.util.Collection;

import org.eclipse.epsilon.effectivemetamodel.XMIN;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.execute.ExecutorFactory;
import org.eclipse.epsilon.evl.dom.Constraint;
import org.eclipse.epsilon.evl.dom.ConstraintContext;
import org.eclipse.epsilon.evl.execute.context.IEvlContext;

public class PartialConstraintContext extends ConstraintContext{

	@Override
	public void execute(Collection<Constraint> constraintsToCheck, IEvlContext context) throws EolRuntimeException {
		
		
		if (!isLazy(context)) {
			
			XMIN model = new XMIN();
			model = ((XMIN)(context.getModelRepository().getModels().get(0)));
			model.loadResource();
			ExecutorFactory executorFactory = context.getExecutorFactory();
			
			for (Constraint constraint : constraintsToCheck) {
				
				System.out.println("Free memory Before loading : " + Runtime.getRuntime().freeMemory()/ 1000000);
				model.load(model.getEffectiveMteamodels().get(constraint.getName()));
				System.out.println("Free memory After loading : " + Runtime.getRuntime().freeMemory()/ 1000000);
				
				for (Object modelElement : getAllOfSourceKind(context)) {
					if (appliesTo(modelElement, context)) {
						executorFactory.execute(constraint, context, modelElement);
					}
				}
				
				System.gc();
				System.out.println("Free memory After gc() : " + Runtime.getRuntime().freeMemory()/ 1000000);
			}	
		}
	}
}
