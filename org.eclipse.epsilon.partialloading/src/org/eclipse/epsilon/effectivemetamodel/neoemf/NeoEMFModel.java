package org.eclipse.epsilon.effectivemetamodel.neoemf;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodel;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.smartsaxparser.EffectiveMetamodelReconciler;

import fr.inria.atlanmod.neoemf.data.blueprints.neo4j.config.BlueprintsNeo4jConfig;
import fr.inria.atlanmod.neoemf.data.blueprints.util.BlueprintsUriFactory;
import fr.inria.atlanmod.neoemf.resource.DefaultPersistentResource;

public class NeoEMFModel extends EmfModel {

	protected EffectiveMetamodel effectiveMetamodel = null;
	ResourceSet resourceSet = new ResourceSetImpl();
	Resource resource;
	boolean partial = true;
	protected HashMap<Object, ?> values = new HashMap<Object, Object>();
	protected ArrayList<Object> cache = new ArrayList<Object>();
	protected EffectiveMetamodelReconciler effectiveMetamodelReconciler = new EffectiveMetamodelReconciler();

	@Override
	public String toString() {
		return "NeoEMF Model [name=" + getName() + "]";
	}
	
	@Override
	public void load() throws EolModelLoadingException {

		loadResource();
		try {
			resource.load(new BlueprintsNeo4jConfig().cacheFeatures().toMap());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadResource() {

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

		String db = "/Users/sorourjahanbin/git/mainandstaticanalysis/org.eclipse.epsilon.neo4j/neoemfDB/Grabats-set0.graphdb";
		
		resource = resourceSet.getResource(new BlueprintsUriFactory().createLocalUri(db), readOnLoad);
		this.setResource(resource);
		
	}

}