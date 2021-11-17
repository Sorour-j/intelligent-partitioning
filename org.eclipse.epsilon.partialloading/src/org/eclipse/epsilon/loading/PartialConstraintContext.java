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
			//long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			model = ((XMIN)(context.getModelRepository().getModels().get(0)));
			model.loadResource();
			ExecutorFactory executorFactory = context.getExecutorFactory();
//			model.load(model.getEffectiveMteamodels().get(constraint.getName()));
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
//			System.out.println("Free memory After Running : " + Runtime.getRuntime().freeMemory()/ 1000000);
//			//long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
//			//long memory = (long) ((endMemory - startMemory) / 1000000);
//			//System.out.println("Cons Contex " + memory);
//			System.gc();
			//endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			//memory = (long) ((endMemory - startMemory) / 1000000);
			//System.out.println("Use After gc() " + memory);
//			System.out.println("Free memory After gc() : " + Runtime.getRuntime().freeMemory()/ 1000000);
			
		}
	}
}
