package org.eclipse.epsilon.loading;

import java.util.HashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.epsilon.effectivemetamodel.XMIN;
import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodel;
import org.eclipse.epsilon.effectivemetamodel.extraction.EolEffectiveMetamodelComputationVisitor;
import org.eclipse.epsilon.effectivemetamodel.extraction.EvlEffectiveMetamodelComputationVisitor;
import org.eclipse.epsilon.effectivemetamodel.extraction.EvlPartitioningEffectiveMetamodelComputationVisitor;
import org.eclipse.epsilon.emc.emfmysql.EmfMySqlModel;
import org.eclipse.epsilon.effectivemetamodel.SubModelFactory;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.eol.IEolModule;
import org.eclipse.epsilon.eol.dt.launching.EpsilonLaunchConfigurationDelegateListener;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.eol.staticanalyser.EolStaticAnalyser;
import org.eclipse.epsilon.evl.EvlModule;
import org.eclipse.epsilon.evl.staticanalyser.EvlStaticAnalyser;

public class XminModelLoader implements EpsilonLaunchConfigurationDelegateListener{

	@Override
	public void aboutToParse(ILaunchConfiguration configuration, String mode, ILaunch launch,
			IProgressMonitor progressMonitor, IEolModule module) throws CoreException {
	}

	@Override
	public void aboutToExecute(ILaunchConfiguration configuration, String mode, ILaunch launch,
			IProgressMonitor progressMonitor, IEolModule module) throws Exception {
		
		boolean partitioning = true;
		IModel model = module.getContext().getModelRepository().getModels().get(0);
		if (!(model instanceof XMIN) && !(model instanceof EmfMySqlModel))
				return;

		EffectiveMetamodel efMetamodel = new EffectiveMetamodel();
		HashMap<String, EffectiveMetamodel> efMetamodels = new HashMap<String, EffectiveMetamodel>();
		
		if (module instanceof EvlModule && partitioning) {
			
			EvlStaticAnalyser staticanalyser = new EvlStaticAnalyser();
			staticanalyser.getContext().setModelFactory(new SubModelFactory());
			 staticanalyser.validate(module);
			if (!staticanalyser.getContext().getModelDeclarations().isEmpty() && 
				 staticanalyser.getContext().getModelDeclarations().get(0).getDriverNameExpression().getName().equals("XMIN")){
				efMetamodels = new EvlPartitioningEffectiveMetamodelComputationVisitor().preExtractor((EvlModule)module, staticanalyser);
			}
		}

		else if (module instanceof EvlModule) {
			
			EvlStaticAnalyser staticanalyser = new EvlStaticAnalyser();
			staticanalyser.getContext().setModelFactory(new SubModelFactory());
			 staticanalyser.validate(module);
			if (!staticanalyser.getContext().getModelDeclarations().isEmpty() && 
				 staticanalyser.getContext().getModelDeclarations().get(0).getDriverNameExpression().getName().equals("XMIN")){
					 efMetamodel = new EvlEffectiveMetamodelComputationVisitor().setExtractor((EvlModule)module, staticanalyser);
			}
		}
		else if (module instanceof EolModule) {
			EolStaticAnalyser staticanalyser = new EolStaticAnalyser();
			staticanalyser.getContext().setModelFactory(new SubModelFactory());
			staticanalyser.validate(module);
			if (!staticanalyser.getContext().getModelDeclarations().isEmpty()) { //&& 
				//	staticanalyser.getContext().getModelDeclarations().get(0).getDriverNameExpression().getName().equals("XMIN")){
					efMetamodel = new EolEffectiveMetamodelComputationVisitor().setExtractor(module,staticanalyser);	
				}
		}
		
		//((XMIN)model).setEffectiveMteamodel(efMetamodels);	
		System.out.println(model);
}
	@Override
	public void executed(ILaunchConfiguration configuration, String mode, ILaunch launch,
			IProgressMonitor progressMonitor, IEolModule module, Object result) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
