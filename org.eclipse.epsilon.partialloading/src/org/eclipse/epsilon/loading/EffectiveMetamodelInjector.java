package org.eclipse.epsilon.loading;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLContentHandlerImpl.XMI;
import org.eclipse.epsilon.common.module.ModuleElement;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.effectivemetamodel.XMIN;
import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodelComputationVisitor;
import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodelExtraction;
import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodelExtractor;
import org.eclipse.epsilon.effectivemetamodel.EffectiveType;
import org.eclipse.epsilon.effectivemetamodel.EvlEffectiveMetamodelExtractor;
import org.eclipse.epsilon.effectivemetamodel.SubModelFactory;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.eol.IEolModule;
import org.eclipse.epsilon.eol.compile.m3.Attribute;
import org.eclipse.epsilon.eol.compile.m3.MetaClass;
import org.eclipse.epsilon.eol.compile.m3.Reference;
import org.eclipse.epsilon.eol.compile.m3.StructuralFeature;
import org.eclipse.epsilon.eol.dom.ExpressionStatement;
import org.eclipse.epsilon.eol.dom.ModelDeclaration;
import org.eclipse.epsilon.eol.dom.NameExpression;
import org.eclipse.epsilon.eol.dom.NewInstanceExpression;
import org.eclipse.epsilon.eol.dom.OperationCallExpression;
import org.eclipse.epsilon.eol.dom.PropertyCallExpression;
import org.eclipse.epsilon.eol.dom.StringLiteral;
import org.eclipse.epsilon.eol.dom.TypeExpression;
import org.eclipse.epsilon.eol.dom.VariableDeclaration;
import org.eclipse.epsilon.eol.dt.launching.EolLaunchConfigurationTabGroup;
import org.eclipse.epsilon.eol.dt.launching.EpsilonLaunchConfigurationDelegateListener;
import org.eclipse.epsilon.eol.execute.context.IEolContext;
import org.eclipse.epsilon.eol.launch.EolRunConfiguration;
import org.eclipse.epsilon.eol.parse.EolUnparser;
import org.eclipse.epsilon.eol.staticanalyser.EolStaticAnalyser;
import org.eclipse.epsilon.eol.types.EolCollectionType;
import org.eclipse.epsilon.eol.types.EolModelElementType;
import org.eclipse.epsilon.evl.EvlModule;
import org.eclipse.epsilon.evl.dom.ConstraintContext;
import org.eclipse.epsilon.evl.parse.Evl_EvlParserRules.context_return;
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
		
		
		module.getCompilationContext().setModelFactory(new SubModelFactory());
		if (module instanceof EvlModule)
			new EvlStaticAnalyser().validate(module);
		else {
			new EolStaticAnalyser().validate(module);
			if (module.getMain() == null) return;
		}
		
	if (!module.getCompilationContext().getModelDeclarations().isEmpty() 
		&& module.getCompilationContext().getModelDeclarations().get(0).getDriverNameExpression().getName().equals("XMIN"))
		{
		//ArrayList<SmartEMF> effectiveMetamodels = new ArrayList<SmartEMF>();
		XMIN smartEMFModel;
		if (module instanceof EvlModule) {
			
			smartEMFModel = new EffectiveMetamodelComputationVisitor().preValidate(module);
			module = (IEolModule) module;
		}
		else
		smartEMFModel = new EffectiveMetamodelExtractor().geteffectiveMetamodel(module);
		
		smartEMFModel.load();
	//	smartEMFModel.load();
		
		System.out.println(smartEMFModel);
	//	System.out.println(new EolUnparser().unparse((EolModule)module));
}
	}
		
	@Override
	public void executed(ILaunchConfiguration configuration, String mode, ILaunch launch,
			IProgressMonitor progressMonitor, IEolModule module, Object result) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
