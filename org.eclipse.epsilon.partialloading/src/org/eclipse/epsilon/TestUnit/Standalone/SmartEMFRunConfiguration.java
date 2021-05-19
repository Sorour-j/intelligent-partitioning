package org.eclipse.epsilon.TestUnit.Standalone;

import java.io.File;
import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodelExtractor;
import org.eclipse.epsilon.effectivemetamodel.EvlEffectiveMetamodelExtractor;
import org.eclipse.epsilon.effectivemetamodel.XMIN;
import org.eclipse.epsilon.effectivemetamodel.SubModelFactory;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.eol.IEolModule;
import org.eclipse.epsilon.eol.launch.EolRunConfiguration;
import org.eclipse.epsilon.eol.launch.EolRunConfiguration.Builder;
import org.eclipse.epsilon.eol.parse.EolUnparser;
import org.eclipse.epsilon.eol.staticanalyser.EolStaticAnalyser;
import org.eclipse.epsilon.evl.EvlModule;
import org.eclipse.epsilon.evl.launch.EvlRunConfiguration;
import org.eclipse.epsilon.evl.staticanalyser.EvlStaticAnalyser;

public class SmartEMFRunConfiguration extends EvlRunConfiguration{
	
	IEolModule module;
	
	public SmartEMFRunConfiguration(EvlRunConfiguration other) {
		super(other);
		module = super.getModule();
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void preExecute() throws Exception {
		super.preExecute();
		
		String metamodel = "src/org/eclipse/epsilon/TestUnit/Standalone/Java.ecore";
		ResourceSet resourceSet = new ResourceSetImpl();
		ResourceSet ecoreResourceSet = new ResourceSetImpl();
		ecoreResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		Resource ecoreResource = ecoreResourceSet.
				createResource(URI.createFileURI(new File(metamodel).getAbsolutePath()));
		try {
			ecoreResource.load(null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (EObject o : ecoreResource.getContents()) {
			EPackage ePackage = (EPackage) o;
			System.out.println("JAva MM :" + o.eContents().size());
			resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
			EPackage.Registry.INSTANCE.put(ePackage.getNsURI(), ePackage);
		}	//	Resource resource = resourceSet.createResource(URI.createFileURI(new File(model).getAbsolutePath()));

		module.getCompilationContext().setModelFactory(new SubModelFactory());
		if (module instanceof EvlModule)
			new EvlStaticAnalyser().validate(module);
		else
			new EolStaticAnalyser().validate(module);
			
		if (!module.getCompilationContext().getModelDeclarations().isEmpty() 
			&& module.getCompilationContext().getModelDeclarations().get(0).getDriverNameExpression().getName().equals("XMIN"))
			{
				
				
			
			//ArrayList<SmartEMF> effectiveMetamodels = new ArrayList<SmartEMF>();
			XMIN smartEMFModel = null;
			if (module instanceof EvlModule)
				smartEMFModel = new EffectiveMetamodelExtractor().geteffectiveMetamodel(module);
			else {
				if (module.getMain() == null) return;
			smartEMFModel = new EffectiveMetamodelExtractor().geteffectiveMetamodel(module);
			}
//			smartEMFModel.setCalculatedEffectiveMetamodel(true);
//			smartEMFModel.load();
			System.out.println(smartEMFModel);
		//	System.out.println(new EolUnparser().unparse((EolModule)module));
		}
	}
}
