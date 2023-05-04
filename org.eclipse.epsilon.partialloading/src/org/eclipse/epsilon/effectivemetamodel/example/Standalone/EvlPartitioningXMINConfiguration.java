package org.eclipse.epsilon.effectivemetamodel.example.Standalone;

import java.io.File;
import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodel;
import org.eclipse.epsilon.effectivemetamodel.XMIN;
import org.eclipse.epsilon.effectivemetamodel.extraction.EvlEffectiveMetamodelComputationVisitor;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.evl.EvlModule;
import org.eclipse.epsilon.evl.IEvlModule;
import org.eclipse.epsilon.evl.launch.EvlRunConfiguration;
import org.eclipse.epsilon.evl.staticanalyser.EvlStaticAnalyser;

public class EvlPartitioningXMINConfiguration extends EvlRunConfiguration{

	IEvlModule module;
	EvlStaticAnalyser staticanalyser = new EvlStaticAnalyser();
	XMIN model;
	EffectiveMetamodel efMetamodel = new EffectiveMetamodel();

	public EvlPartitioningXMINConfiguration(EvlRunConfiguration other) {
		super(other);
		module = getModule();
	}

	@Override
	public void preExecute() throws Exception {
		
		super.preExecute();
		model = (XMIN)module.getContext().getModelRepository().getModels().get(0);
		String metamodel = model.getMetamodelFiles().get(0);
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
			resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
			EPackage.Registry.INSTANCE.put(ePackage.getNsURI(), ePackage);
		}

		long startTime = System.nanoTime();
		long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		staticanalyser.getContext().setModelFactory(new SubModelFactory());
		staticanalyser.validate(module);
		
		efMetamodel = new EvlEffectiveMetamodelComputationVisitor().setExtractor((EvlModule)module, staticanalyser);

		model.setEffectiveMteamodel(efMetamodel);
		model.setMetamodelResource(resourceSet);
		
		long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		
		long endTime = System.nanoTime();;
		long duration = (endTime - startTime); // divide by 1000000 to get milliseconds.
		System.out.println("**** Loading Time ****");
		System.out.println(duration/1000000  + " milliseconds");
		
		System.out.println("Loading Memory: " + (long)((endMemory - startMemory)/(1024*1024)) + "MB");
		Resource resource = model.getResource();

		InMemoryEmfModel emfModel = new InMemoryEmfModel(model.getName(),resource);
		emfModel.setName(model.getName());
		model.close();
		
		module.getContext().getModelRepository().removeModel(model);
		module.getContext().getModelRepository().addModel(emfModel);
		
	}
}
