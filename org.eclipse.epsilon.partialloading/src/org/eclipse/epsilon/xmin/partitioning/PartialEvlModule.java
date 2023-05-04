package org.eclipse.epsilon.xmin.partitioning;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.epsilon.common.module.ModuleElement;
import org.eclipse.epsilon.common.parse.AST;
import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodel;
import org.eclipse.epsilon.effectivemetamodel.XMIN;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.execute.ExecutorFactory;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.eol.models.ModelRepository;
import org.eclipse.epsilon.evl.EvlModule;
import org.eclipse.epsilon.evl.dom.Constraint;
import org.eclipse.epsilon.evl.dom.ConstraintContext;
import org.eclipse.epsilon.evl.execute.context.IEvlContext;

public class PartialEvlModule extends EvlModule {

	private boolean isPartitioned = false;

	public PartialEvlModule() {
		super();
	}

//	@Override
//	public ModuleElement adapt(AST cst, ModuleElement parentAst) {
//		ModuleElement me = super.adapt(cst, parentAst);
//
//		if (me instanceof ConstraintContext)
//			return new PartialConstraintContext();
//		else
//			return me;
//	}

	public boolean isPartitioned() {
		return isPartitioned;
	}

	public void setIsPartitioned(boolean isPartitioned) {
		this.isPartitioned = isPartitioned;
	}
	@Override
	protected void checkConstraints() throws EolRuntimeException {
		IEvlContext context = getContext();
		IModel model = context.getModelRepository().getModels().get(0);
		
		XMIN xminModel = (XMIN) model;
		HashMap<Set<Constraint>, EffectiveMetamodel> partitionSets = xminModel.getEffectiveMteamodels();
		// Set<Constraint> executedConstraints = new HashSet<Constraint>();
		ExecutorFactory executorFactory = context.getExecutorFactory();

		if (!isPartitioned && partitionSets.isEmpty()) {
			
			
			EffectiveMetamodel efModel = xminModel.getEffectiveMteamodel();
			long timeBefore = System.currentTimeMillis();
			xminModel.load(efModel);
			long timeAfter = System.currentTimeMillis();
			System.out.println("Loading time: " + (timeAfter - timeBefore) + " ms");
			
			timeBefore = System.currentTimeMillis();
			//for (Constraint c : neo4jModel.getPartitioningHandler().getConstraints(efModel)) {
			for (Constraint constraint : constraints) {
				
				ConstraintContext conContext = constraint.getConstraintContext();
				Collection<?> modelElements = conContext.getAllOfSourceKind(context);
				for (Object modelElement : modelElements) {
					if (conContext.appliesTo(modelElement, context)) {
						executorFactory.execute(constraint, context, modelElement);
					}
				}
				// executedConstraints.add(c);
			}
			timeAfter = System.currentTimeMillis();
			System.out.println("Execution time: " + (timeAfter - timeBefore) + " ms \n");
			// if there is no constraints to execute, this stage can be skipped. How?!
			//((XMIN) model).getResource().unload();
			System.gc();
		}
		else {
			
		for (Set<Constraint> sets : partitionSets.keySet()) {
			System.out.println(sets);
			
				//EffectiveMetamodel efModel = neo4jModel.getPartitioningHandler().getEffectivemetamodel(constraint);
				EffectiveMetamodel efModel = partitionSets.get(sets);
				long timeBefore = System.currentTimeMillis();
				xminModel.load(efModel);
				long timeAfter = System.currentTimeMillis();
				System.out.println("Loading time: " + (timeAfter - timeBefore) + " ms");
				
				timeBefore = System.currentTimeMillis();
				//for (Constraint c : neo4jModel.getPartitioningHandler().getConstraints(efModel)) {
				for (Constraint constraint : sets) {
					
					ConstraintContext conContext = constraint.getConstraintContext();
					Collection<?> modelElements = conContext.getAllOfSourceKind(context);
					for (Object modelElement : modelElements) {
						if (conContext.appliesTo(modelElement, context)) {
							executorFactory.execute(constraint, context, modelElement);
						}
					}
					// executedConstraints.add(c);
				}
				timeAfter = System.currentTimeMillis();
				System.out.println("Execution time: " + (timeAfter - timeBefore) + " ms \n");
				// if there is no constraints to execute, this stage can be skipped. How?!
				((XMIN) model).getResource().unload();
				System.gc();
			}
		}
	}
}
