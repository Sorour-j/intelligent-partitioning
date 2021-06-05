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
import org.eclipse.epsilon.effectivemetamodel.XMIN;
import org.eclipse.epsilon.effectivemetamodel.extraction.EolEffectiveMetamodelComputationVisitor;
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

public class EolXminModelRunConfiguration extends EvlRunConfiguration{
	
	IEolModule module;
	EolStaticAnalyser staticnalyser = new EolStaticAnalyser();
	XMIN xminModel;
	public EolXminModelRunConfiguration(EvlRunConfiguration other) {
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

		
		staticnalyser.getContext().setModelFactory(new SubModelFactory());
		staticnalyser.validate(module);
		if (!staticnalyser.getContext().getModelDeclarations().isEmpty() 
			&& staticnalyser.getContext().getModelDeclarations().get(0).getDriverNameExpression().getName().equals("XMIN"))
			{
				
			xminModel = new EolEffectiveMetamodelComputationVisitor().setExtractor(module, staticnalyser);
//			smartEMFModel.setCalculatedEffectiveMetamodel(true);
//			smartEMFModel.load();
			System.out.println(xminModel);
		//	System.out.println(new EolUnparser().unparse((EolModule)module));
		}
	}
}