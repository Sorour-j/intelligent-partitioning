package org.eclipse.epsilon.effectivemetamodel.neoemf;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import fr.inria.atlanmod.neoemf.config.ImmutableConfig;
import fr.inria.atlanmod.neoemf.data.blueprints.neo4j.config.BlueprintsNeo4jConfig;
import fr.inria.atlanmod.neoemf.data.mapdb.config.MapDbConfig;

public class NeoEMFDataset {

	ResourceSet xmiResourceSet = new ResourceSetImpl();
	ResourceSet ecoreResourceSet = new ResourceSetImpl();
	Resource resource = null;
	ArrayList<String> className = new ArrayList<String>();

	public static void main(final String[] args) throws IOException {
		NeoEMFDataset db = new NeoEMFDataset();
		db.RegisterEcore("movies.ecore");
		db.loadXmi("ExampleIMDB.xmi");
		db.write();
	}

	public void write() {
		
		ImmutableConfig config = new BlueprintsNeo4jConfig();

		try {
			resource.save(config.toMap());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void RegisterEcore(String metamodel) {
		System.out.println("Registering Ecore:......");
		ecoreResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		Resource ecoreResource = ecoreResourceSet
				.createResource(URI.createFileURI(new File(metamodel).getAbsolutePath()));
		try {
			ecoreResource.load(null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (EObject o : ecoreResource.getContents()) {
			EPackage ePackage = (EPackage) o;
			xmiResourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
			for (EClassifier cls : ePackage.getEClassifiers()) {
				className.add(cls.getName());
			}
		}
		System.out.println("Registeration Done!......");
	}

	void loadXmi(String model) {
		System.out.println("Start loading xmi:......");
		xmiResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		resource = xmiResourceSet.createResource(URI.createFileURI(new File(model).getAbsolutePath()));
		try {
			resource.load(null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Loading done!......");
	}
}
