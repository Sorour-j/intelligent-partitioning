package org.eclipse.epsilon.effectivemetamodel.neoemf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodel;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.smartsaxparser.EffectiveMetamodelReconciler;

import fr.inria.atlanmod.neoemf.config.ImmutableConfig;
import fr.inria.atlanmod.neoemf.data.blueprints.neo4j.config.BlueprintsNeo4jConfig;
import fr.inria.atlanmod.neoemf.data.blueprints.util.BlueprintsUriFactory;
import fr.inria.atlanmod.neoemf.data.mapdb.config.MapDbConfig;
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

	public void loadResource() {

		EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage("http://www.eclipse.org/MoDisco/Java/0.2.incubation/java");
		resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
		try {
			determinePackagesFrom(resourceSet);
		} catch (EolModelLoadingException e1) {
			e1.printStackTrace();
		}
		String db = "/Users/sorourjahanbin/git/Intelligent-partitioning/org.eclipse.epsilon.partialloading/databases/myGraph.graphdb";
		db = "/Users/sorourjahanbin/git/mainandstaticanalysis/org.eclipse.epsilon.neo4j/target/ExampleMovieDB";
		// resource = resourceSet.createResource(new
		// MapDbUriFactory().createLocalUri("/Users/sorourjahanbin/git/Intelligent-partitioning/org.eclipse.epsilon.partialloading/src/org/eclipse/epsilon/effectivemetamodel/neoemf/databases/myGraph.mapdb"));
		// resource = resourceSet.getResource(new MapDbUriFactory().createLocalUri(db),
		// readOnLoad);
		resource = resourceSet.getResource(new BlueprintsUriFactory().createLocalUri(db), readOnLoad);
		this.setResource(resource);
		
	}

	@Override
	public void load() throws EolModelLoadingException {

		
		loadResource();
		try {
			resource.load(new BlueprintsNeo4jConfig().cacheFeatures().toMap());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		if (effectiveMetamodel == null)
//			return;
//		write();
//		if(partial)
//		partialLoad();
//		for (EClass cls : effectiveMetamodelReconciler.getActualObjectsToLoad().get(effectiveMetamodel.getName())
//				.keySet()) {
//			try {
//				Collection<EObject> objcs= getAllOfKindFromModel(cls.getName());
//				((DefaultPersistentResource)resource).allInstancesOf(cls);
//				System.out.println("Objects: " + objcs);
//			} catch (EolModelElementTypeNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
	}

	public void setEffectiveMteamodel(EffectiveMetamodel efMetamodel) {
		this.effectiveMetamodel = efMetamodel;
		effectiveMetamodel.setName(this.name);
	}

	public void write() {
		GraphFactory factory = GraphFactory.eINSTANCE;
		Graph graph = factory.createGraph();

		for (int i = 0; i < 10; i++) {
			Vertice v1 = factory.createVertice();
			v1.setLabel("Vertice " + i + "a");
			v1.setName("Name " + i);
			Vertice v2 = factory.createVertice();
			v2.setLabel("Vertice " + i + "b");
			v2.setName("Name " + i);

			graph.getVertices().add(v1);
			graph.getVertices().add(v2);
		}

		resource.getContents().add(graph);

		ImmutableConfig config = new MapDbConfig().cacheContainers().withLists().cacheMetaClasses();

		try {
			resource.save(config.toMap());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void partialLoad() {

		// HashMap<EClass, ArrayList<EStructuralFeature>> objectsToLoad;
		effectiveMetamodelReconciler.addPackages(resourceSet.getPackageRegistry().values());
		effectiveMetamodelReconciler.addEffectiveMetamodel(effectiveMetamodel);
		effectiveMetamodelReconciler.reconcile();
		
		HashMap<Object, ArrayList<EStructuralFeature>> objectsToLoad = new HashMap<Object, ArrayList<EStructuralFeature>>();
	//	HashMap<Object, HashMap<EStructuralFeature, String>> featureValues = new HashMap<Object, HashMap<EStructuralFeature, String>>();
		
//		objectsToLoad.putAll(effectiveMetamodelReconciler.getActualObjectsToLoad().get(effectiveMetamodel.getName()));
		for (EClass cls : effectiveMetamodelReconciler.getActualObjectsToLoad().get(effectiveMetamodel.getName())
				.keySet()) {

			for (EObject obj : ((DefaultPersistentResource)resource).allInstancesOf(cls)) {
				ArrayList<EStructuralFeature> features = effectiveMetamodelReconciler.getActualObjectsToLoad()
						.get(effectiveMetamodel.getName()).get(cls);
				objectsToLoad.put(obj, features);
				
			}
		ArrayList< Object> objs = new ArrayList<Object>();	
		objs.addAll(((DefaultPersistentResource)resource).eStore().efficientGet(objectsToLoad));
		
			//	if (obj.eClass().getName().equals(cls.getName())) {
//					for (EStructuralFeature ef : effectiveMetamodelReconciler.getActualObjectsToLoad()
//							.get(effectiveMetamodel.getName()).get(cls)) {
						
						
						
//						try {
//							EObject o = (EObject) obj.eGet(ef);
//							addToCache(ef.toString(), o);
//							//kindCache.put(ef, obj.eGet(ef));
//						} catch (EolModelElementTypeNotFoundException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
						//kindCache.put(ef,obj.eGet(ef));
						//Object o = obj.eGet(ef);
				//	cache.addAll(((DefaultPersistentResource)resource).eStore().efficientGet((InternalEObject)obj, features));
						//cache.add(((DefaultPersistentResource)resource).eStore().get((InternalEObject)obj, ef, ef.getFeatureID()));
				System.out.println("Partial NeoEMF Done!");
					}
				//}
			//}
		//}
		
	}

	public void lazyLoad() {

		System.out.println(resource.getContents().size());
		Graph g = (Graph) resource.getContents().get(0);
		for (Vertice each : g.getVertices()) {
			System.out.println(each.getLabel());
		}
		System.out.print("NeoEMF  Done!");
	}
}

//long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
//	write();
//if (partial)
//	partialLoad();

//long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
//long memory = (long) ((endMemory - startMemory) / 1000000);
//System.out.println("**** Memory ****");
//System.out.println((memory) + " MB");