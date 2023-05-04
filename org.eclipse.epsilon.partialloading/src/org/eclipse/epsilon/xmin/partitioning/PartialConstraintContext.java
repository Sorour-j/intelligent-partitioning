package org.eclipse.epsilon.xmin.partitioning;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodel;
import org.eclipse.epsilon.effectivemetamodel.XMIN;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.execute.ExecutorFactory;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.eol.models.ModelRepository;
import org.eclipse.epsilon.evl.dom.Constraint;
import org.eclipse.epsilon.evl.dom.ConstraintContext;
import org.eclipse.epsilon.evl.execute.context.IEvlContext;

public class PartialConstraintContext extends ConstraintContext {

	@Override
	public void execute(Collection<Constraint> constraintsToCheck, IEvlContext context) throws EolRuntimeException {

		if (!isLazy(context)) {

			IModel model = context.getModelRepository().getModels().get(0);
		
			ExecutorFactory executorFactory = context.getExecutorFactory();
			if (((PartialEvlModule) (context.getModule())).isPartitioned())
				executeByPartition(constraintsToCheck, context);
			else {
				for (Constraint constraint : constraintsToCheck) {
					
					System.out.println(constraint.getName());	
					if (model instanceof XMIN) {
						long timeBefore = System.currentTimeMillis();
						((XMIN) model).load(((XMIN) model).getEffectiveMteamodel(constraint));
						long timeAfter = System.currentTimeMillis();
						System.out.println("Loading time: " + (timeAfter - timeBefore) + " ms");
						
				}
					for (Object modelElement : getAllOfSourceKind(context)) {
						if (appliesTo(modelElement, context)) {
							executorFactory.execute(constraint, context, modelElement);
						}
					}
					((XMIN) model).getResource().unload();
					System.gc();
				}
			}
		}
	}

	public void executeByPartition(Collection<Constraint> constraintsToCheck, IEvlContext context)
			throws EolRuntimeException {

		IModel model = context.getModelRepository().getModels().get(0);
		
		XMIN xminModel = (XMIN)model;
		Set<Constraint> executedConstraints = new HashSet<Constraint>();

		ExecutorFactory executorFactory = context.getExecutorFactory();
		
		for (Constraint constraint : constraintsToCheck) {

			if (!executedConstraints.contains(constraint)) {
				
				EffectiveMetamodel efModel = xminModel.getPartitioningHandler().getEffectivemetamodel(constraint);
				long timeBefore = System.currentTimeMillis();
				xminModel.load(efModel);
				long timeAfter = System.currentTimeMillis();
				System.out.println("Loading time: " + (timeAfter - timeBefore) + " ms");
				
				timeBefore = System.currentTimeMillis();
				for (Constraint c : xminModel.getPartitioningHandler().getConstraints(efModel)) {
					System.out.println(c.getName());		
					Collection<?> modelElements = getAllOfSourceKind(context);
					for (Object modelElement : modelElements) {
						if (appliesTo(modelElement, context)) {
							//System.out.print(".");
							executorFactory.execute(c, context, modelElement);
						}
					}
					executedConstraints.add(c);
				}
				timeAfter = System.currentTimeMillis();
				System.out.println("Execution time: " + (timeAfter - timeBefore) + " ms");
				// if there is no constraints to execute, this stage can be skipped. How?!
				System.out.println("Resource Unloaded!");
				((XMIN)model).getResource().unload();
				
				System.gc();
			}
		}
	}
}
