package org.eclipse.epsilon.xmin.partitioning;

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
import org.eclipse.epsilon.effectivemetamodel.extraction.PartitioningEffectiveMetamodelVisitor;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.evl.EvlModule;
import org.eclipse.epsilon.evl.IEvlModule;
import org.eclipse.epsilon.evl.launch.EvlRunConfiguration;
import org.eclipse.epsilon.evl.staticanalyser.EvlStaticAnalyser;

public class EvlPartitioningXMINRunConfiguration extends EvlRunConfiguration{

	PartialEvlModule module;
	EvlStaticAnalyser staticanalyser = new EvlStaticAnalyser();
	XMIN model;
	EffectiveMetamodel efMetamodel = new EffectiveMetamodel();

	public EvlPartitioningXMINRunConfiguration(EvlRunConfiguration other) {
		super(other);
		module = (PartialEvlModule)getModule();
	}

	@Override
	public void preExecute() throws Exception {
		
		super.preExecute();
		module.setIsPartitioned(false);
		
		//which model?
		model = (XMIN)module.getContext().getModelRepository().getModels().get(0);
		//Register meta-model
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
		long timeA = System.currentTimeMillis();
		//analyse the program and resolve the types
		staticanalyser.getContext().setModelFactory(new SubModelFactory());
		staticanalyser.validate(module);
		long timeB = System.currentTimeMillis();
		System.out.println("Analysis time: " + (timeB - timeA) + " ms");
		
		timeA = System.currentTimeMillis();
		model.setMetamodelResource(resourceSet);
		//compute all effective meta-models >> each constraint : one meta-model
		model.setEffectiveMteamodels(new PartitioningEffectiveMetamodelVisitor().setpartitionExtractor((PartialEvlModule)module, staticanalyser), module.isPartitioned());
	//	model.setEffectiveMteamodel(new EvlEffectiveMetamodelComputationVisitor().setExtractor((PartialEvlModule)module, staticanalyser));
		timeB = System.currentTimeMillis();
		System.out.println("Effective metamodel extraction time: " + (timeB - timeA) + " ms");
		staticanalyser.postValidate(module);
	}
	
	@Override
	public void postExecute() throws Exception {
		super.postExecute();
		model.close();
	}
}