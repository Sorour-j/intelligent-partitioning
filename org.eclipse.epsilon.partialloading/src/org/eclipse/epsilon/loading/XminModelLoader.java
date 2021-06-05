package org.eclipse.epsilon.loading;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.epsilon.effectivemetamodel.XMIN;
import org.eclipse.epsilon.effectivemetamodel.extraction.EolEffectiveMetamodelComputationVisitor;
import org.eclipse.epsilon.effectivemetamodel.extraction.EvlEffectiveMetamodelComputationVisitor;
import org.eclipse.epsilon.effectivemetamodel.SubModelFactory;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.eol.IEolModule;
import org.eclipse.epsilon.eol.dt.launching.EpsilonLaunchConfigurationDelegateListener;
import org.eclipse.epsilon.eol.staticanalyser.EolStaticAnalyser;
import org.eclipse.epsilon.evl.EvlModule;
import org.eclipse.epsilon.evl.staticanalyser.EvlStaticAnalyser;

public class XminModelLoader implements EpsilonLaunchConfigurationDelegateListener{

	@Override
	public void aboutToParse(ILaunchConfiguration configuration, String mode, ILaunch launch,
			IProgressMonitor progressMonitor, IEolModule module) throws CoreException {
		// TODO Auto-generated method stub
	}

	@Override
	public void aboutToExecute(ILaunchConfiguration configuration, String mode, ILaunch launch,
			IProgressMonitor progressMonitor, IEolModule module) throws Exception {
		
		XMIN xminModel = null;
		
		if (module instanceof EvlModule) {
			
			EvlStaticAnalyser staticanalyser = new EvlStaticAnalyser();
			staticanalyser.getContext().setModelFactory(new SubModelFactory());
			 staticanalyser.validate(module);
			if (!staticanalyser.getContext().getModelDeclarations().isEmpty() && 
				 staticanalyser.getContext().getModelDeclarations().get(0).getDriverNameExpression().getName().equals("XMIN")){
					 xminModel = new EvlEffectiveMetamodelComputationVisitor().setExtractor(module,staticanalyser);
			}
		}
		else if (module instanceof EolModule) {
			EolStaticAnalyser staticanalyser = new EolStaticAnalyser();
			staticanalyser.getContext().setModelFactory(new SubModelFactory());
			staticanalyser.validate(module);
			if (!staticanalyser.getContext().getModelDeclarations().isEmpty() && 
					 staticanalyser.getContext().getModelDeclarations().get(0).getDriverNameExpression().getName().equals("XMIN")){
					 xminModel = new EolEffectiveMetamodelComputationVisitor().setExtractor(module,staticanalyser);	
				}
		}
		xminModel.load();
		System.out.println(xminModel);
}
	@Override
	public void executed(ILaunchConfiguration configuration, String mode, ILaunch launch,
			IProgressMonitor progressMonitor, IEolModule module, Object result) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
