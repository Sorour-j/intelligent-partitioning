package org.eclipse.epsilon.loading;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.epsilon.effectivemetamodel.XMIN;
import org.eclipse.epsilon.effectivemetamodel.extraction.EvlEffectiveMetamodelComputationVisitor;
import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodelComputationVisitor;
import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodelExtractor;
import org.eclipse.epsilon.effectivemetamodel.SubModelFactory;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.eol.IEolModule;
import org.eclipse.epsilon.eol.dt.launching.EpsilonLaunchConfigurationDelegateListener;
import org.eclipse.epsilon.eol.staticanalyser.EolStaticAnalyser;
import org.eclipse.epsilon.evl.EvlModule;
import org.eclipse.epsilon.evl.staticanalyser.EvlStaticAnalyser;

public class EffectiveMetamodelInjector implements EpsilonLaunchConfigurationDelegateListener{

	@Override
	public void aboutToParse(ILaunchConfiguration configuration, String mode, ILaunch launch,
			IProgressMonitor progressMonitor, IEolModule module) throws CoreException {
		// TODO Auto-generated method stub
	}

	@Override
	public void aboutToExecute(ILaunchConfiguration configuration, String mode, ILaunch launch,
			IProgressMonitor progressMonitor, IEolModule module) throws Exception {
		
		XMIN smartEMFModel = null;
		
		if (module instanceof EvlModule) {
			
			EvlStaticAnalyser staticanalyser = new EvlStaticAnalyser();
			staticanalyser.getContext().setModelFactory(new SubModelFactory());
			new EvlStaticAnalyser().validate(module);
			if (!staticanalyser.getContext().getModelDeclarations().isEmpty() && 
				 staticanalyser.getContext().getModelDeclarations().get(0).getDriverNameExpression().getName().equals("XMIN")){
					//ArrayList<SmartEMF> effectiveMetamodels = new ArrayList<SmartEMF>();		
					smartEMFModel = new EvlEffectiveMetamodelComputationVisitor(staticanalyser).preValidate(module);
					//module = (IEolModule) module;
					}
		}
		else if (module instanceof EolModule) {
			EolStaticAnalyser staticanalyser = new EolStaticAnalyser();
			staticanalyser.getContext().setModelFactory(new SubModelFactory());
			new EolStaticAnalyser().validate(module);
		//	if (module.getMain() == null) return;
			if (!staticanalyser.getContext().getModelDeclarations().isEmpty() && 
					 staticanalyser.getContext().getModelDeclarations().get(0).getDriverNameExpression().getName().equals("XMIN")){
					 smartEMFModel = new EffectiveMetamodelExtractor().geteffectiveMetamodel(module);
						
				}
		}
		smartEMFModel.load();
		System.out.println(smartEMFModel);
}
	@Override
	public void executed(ILaunchConfiguration configuration, String mode, ILaunch launch,
			IProgressMonitor progressMonitor, IEolModule module, Object result) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
