package org.eclipse.epsilon.effectivemetamodel.neoemf;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodel;
import org.eclipse.epsilon.effectivemetamodel.SubModelFactory;
import org.eclipse.epsilon.effectivemetamodel.extraction.EolEffectiveMetamodelComputationVisitor;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.eol.staticanalyser.EolStaticAnalyser;

public class RepoModel {
    
	public static void main(String[] args) throws Exception {
		
		//Register Meta-model
		String metamodel = "/Users/sorourjahanbin/git/Intelligent-partitioning/org.eclipse.epsilon.partialloading/src/org/eclipse/epsilon/effectivemetamodel/neoemf/JDTAST.ecore";
		
		ResourceSet resourceSet = new ResourceSetImpl();
		ResourceSet ecoreResourceSet = new ResourceSetImpl();
		ecoreResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		Resource ecoreResource = ecoreResourceSet.
				createResource(URI.createFileURI(new File(metamodel).getAbsolutePath()));
		try {
			ecoreResource.load(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (EObject o : ecoreResource.getContents()) {
			EPackage ePackage = (EPackage) o;
			resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
			EPackage.Registry.INSTANCE.put(ePackage.getNsURI(), ePackage);
		}
		
		NeoEMFModel model = new NeoEMFModel();
		model.setName("JDTAST");
		
		StringProperties modelProperties = new StringProperties();
		modelProperties.setProperty(NeoEMFModel.PROPERTY_NAME, "JDTAST");
	//	modelProperties.setProperty(NeoEMFModel.PROPERTY_METAMODEL_URI,"http://www.eclipse.org/MoDisco/Java/0.2.incubation/java");
		modelProperties.setProperty("type", "NeoEMF");
		
		EolModule module = new EolModule();
		module.parse(new File("/Users/sorourjahanbin/git/mainandstaticanalysis/org.eclipse.epsilon.neo4j/src/org/eclipse/epsilon/neo4j/benchmarks/grabats.eol"));
			//	+ "v.name.println(\"Name: \");}");
		
//		EolStaticAnalyser staticAnalyser = new EolStaticAnalyser();
//		staticAnalyser.getContext().setModelFactory(new SubModelFactory());
//		staticAnalyser.validate(module);
		long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		System.out.println("**** Free Memory Before exe ****");
		System.out.println((startMemory/ 1000000) + " MB");
//		EffectiveMetamodel ef = new EffectiveMetamodel("graph", "http://atlanmod.org/neoemf/tutorial");
//		ef = new EolEffectiveMetamodelComputationVisitor().setExtractor(module, staticAnalyser);
//		
//		model.setEffectiveMteamodel(ef);
		model.load();
//		long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
//		long memory = (long) ((endMemory - startMemory) / 1000000);

		module.getContext().getModelRepository().addModel(model);
		module.execute();
		long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	    long memory = (long) ((endMemory - startMemory) / 1000000);
		System.out.println("**** Memory After exe ****");
		System.out.println(memory + " MB");
	}
}
