package org.eclipse.epsilon.emc.neo4j.example.standalone;

import java.io.File;
import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodel;
import org.eclipse.epsilon.effectivemetamodel.extraction.EolEffectiveMetamodelComputationVisitor;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.emc.neo4j.Neo4jModel;
import org.eclipse.epsilon.emc.neo4j.SubModelFactory;
import org.eclipse.epsilon.eol.IEolModule;
import org.eclipse.epsilon.eol.launch.EolRunConfiguration;
import org.eclipse.epsilon.eol.staticanalyser.EolStaticAnalyser;

public class EolNeoEMFRunConfiguration extends EolRunConfiguration {

	IEolModule module;
	EolStaticAnalyser staticanalyser = new EolStaticAnalyser();
	Neo4jModel model;
	EffectiveMetamodel efMetamodel = new EffectiveMetamodel();
	
	public EolNeoEMFRunConfiguration(EolRunConfiguration other) {
		super(other);
		module = super.getModule();
	}

	@Override
	public void preExecute() throws Exception {
		
		super.preExecute();
		String metamodel = "src/org/eclipse/epsilon/emc/neo4j/example/standalone/Java.ecore";
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

		model = (Neo4jModel)module.getContext().getModelRepository().getModels().get(0);
		//model.setMetamodelFileUri(URI.createFileURI(metamodel));
		staticanalyser.getContext().setModelFactory(new SubModelFactory());
		staticanalyser.validate(module);
		
		efMetamodel = new EolEffectiveMetamodelComputationVisitor().setExtractor(module, staticanalyser);
		model.setEffectiveMteamodel(efMetamodel);
		model.effectiveMetamodelReconciler.addPackages(resourceSet.getPackageRegistry().values());
		model.effectiveMetamodelReconciler.addEffectiveMetamodel(efMetamodel);
		model.effectiveMetamodelReconciler.reconcile();

		model.load();
		
		Resource resource = model.getResource();
		InMemoryEmfModel emfModel = new InMemoryEmfModel("javaMM",resource);
		emfModel.setName("javaMM");
		//emfModel.setMetamodelFile(metamodel);
		//emfModel.setMetamodelUri("http://www.eclipse.org/MoDisco/Java/0.2.incubation/java");
		module.getContext().getModelRepository().removeModel(model);
		module.getContext().getModelRepository().addModel(emfModel);
		//emfModel.getResource().save(System.out,null);
	}

}
