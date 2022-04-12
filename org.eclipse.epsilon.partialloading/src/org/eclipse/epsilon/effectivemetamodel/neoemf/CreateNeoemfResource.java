package org.eclipse.epsilon.effectivemetamodel.neoemf;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import fr.inria.atlanmod.neoemf.config.ImmutableConfig;
import fr.inria.atlanmod.neoemf.data.blueprints.neo4j.config.BlueprintsNeo4jConfig;
import fr.inria.atlanmod.neoemf.data.blueprints.util.BlueprintsUriFactory;
import fr.inria.atlanmod.neoemf.data.mapdb.config.MapDbConfig;

public class CreateNeoemfResource {
	
	
	public static void main( String... args ) throws Exception
    {

		ResourceSet xmiResourceSet = new ResourceSetImpl();
		ResourceSet RS = new ResourceSetImpl();
		String metamodel = "src/org/eclipse/epsilon/effectivemetamodel/neoemf/movies.ecore";
		//CreateNeoemfResource create = new CreateNeoemfResource();
		//create.RegisterEcore("src/org/eclipse/epsilon/effectivemetamodel/neoemf/movies.ecore");
		ResourceSet ecoreResourceSet = new ResourceSetImpl();
		ecoreResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		Resource ecoreResource = ecoreResourceSet.createResource(URI.createFileURI(new File(metamodel).getAbsolutePath()));
		try {
			ecoreResource.load(null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (EObject o : ecoreResource.getContents()) {
			EPackage ePackage = (EPackage) o;
			xmiResourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
		}
		
		xmiResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		Resource xmiResource = xmiResourceSet.createResource(URI.createFileURI(new File("src/org/eclipse/epsilon/effectivemetamodel/neoemf/ExampleImdb.xmi").getAbsolutePath()));
		xmiResource.load(null);
		
		String db = "/Users/sorourjahanbin/git/Intelligent-partitioning/org.eclipse.epsilon.partialloading/databases/sampleXMI.graphdb";
		
		Resource persistentResource = RS.createResource(new BlueprintsUriFactory().createLocalUri(db));

		//Map<String, ?> options = new BlueprintsNeo4jConfig().autoSave().cacheContainers().cacheMetaClasses().toMap();

		ImmutableConfig config = new BlueprintsNeo4jConfig().autoSave().cacheContainers().cacheMetaClasses();
		persistentResource.save(config.toMap());
		//persistentResource.unload();
		//persistentResource.getContents().addAll(EcoreUtil.copyAll(xmiResource.getContents()));
		//persistentResource.load(config.toMap());
		persistentResource.getContents().add(EcoreUtil.copy(xmiResource.getContents().get(0)));
		// "regular" save
		persistentResource.save(config.toMap());
    }
	
//	void RegisterEcore(String metamodel) {
//		System.out.println("Registering Ecore:......");
//		
//		
//		System.out.println("Registeration Done!......");
//	}
}
