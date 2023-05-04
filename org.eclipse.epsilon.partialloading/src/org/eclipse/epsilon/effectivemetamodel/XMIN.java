package org.eclipse.epsilon.effectivemetamodel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.emc.emf.xmi.PartialXMILoadConfiguration;
import org.eclipse.epsilon.emc.emf.xmi.PartialXMIResourceFactory;
import org.eclipse.epsilon.evl.dom.Constraint;
import org.eclipse.epsilon.smartsaxparser.EffectiveMetamodelReconciler;
import org.eclipse.epsilon.xmin.partitioning.PartitioningHandler;

public class XMIN extends EmfModel {

	public static final String OPTION_PARTIAL_LOADING_CONFIGURATION = "PARTIAL_LOADING_CONFIGURATION";
	EffectiveMetamodel effectiveMetamodel;
	// Name of Constraint, Effective meta-model
		protected LinkedHashMap<Set<Constraint>, EffectiveMetamodel> effectiveMetamodels = new LinkedHashMap<Set<Constraint>, EffectiveMetamodel>();

	//HashMap<String, EffectiveMetamodel> effectiveMetamodels = new HashMap<String, EffectiveMetamodel>();
	ResourceSet resourceSet = new ResourceSetImpl();
	Resource resource = new XMIResourceImpl();
	ResourceSet metamodelResource = new ResourceSetImpl();
	EPackage pkg;
	
	protected PartitioningHandler partitionHandler;
	
	public EffectiveMetamodelReconciler effectiveMetamodelReconciler = new EffectiveMetamodelReconciler();
	protected HashMap<EClass, ArrayList<EStructuralFeature>> actualObjectsAndFeaturesToLoad = new HashMap<EClass, ArrayList<EStructuralFeature>>();
	protected HashMap<EClass, ArrayList<EStructuralFeature>> objectsToLoad = new HashMap<EClass, ArrayList<EStructuralFeature>>();
	
	public XMIN() {
		
	}

	@Override
	public String toString() {
		return "XMIN Model [name=" + getName() + "]";
	}

	public void load(EffectiveMetamodel efMetamodel) {
		effectiveMetamodel = new EffectiveMetamodel();
		//effectiveMetamodel.setName(this.name);
		this.effectiveMetamodel = efMetamodel;
		effectiveMetamodel.setName(this.name);
		effectiveMetamodel.setNsuri(metamodelUris.get(0).toString());
		
		load();
	}

	public void setMetamodelResource(ResourceSet resourceSet) {
		this.metamodelResource = resourceSet;
	}

	@Override
	public void load() {
		
		if (effectiveMetamodel == null && effectiveMetamodels.isEmpty())
			return;

		createResource(this.getMetamodelUris().get(0));
		if (effectiveMetamodels.isEmpty() && effectiveMetamodel.getIsCalculated() == false)
			return;

		this.setResource(resource);

		PartialXMILoadConfiguration configuration = new PartialXMILoadConfiguration();

		 if (!effectiveMetamodelReconciler.getActualObjectsToLoad().isEmpty())
			for (EClass eclass : objectsToLoad.keySet()) {
				configuration.addAllOfKind(eclass);
				for (EStructuralFeature ef : objectsToLoad.get(eclass)) {
					configuration.addFeature(eclass, ef);
				}
			}

		HashMap<String, Object> loadOptions = new HashMap<>();
		loadOptions.put(OPTION_PARTIAL_LOADING_CONFIGURATION, configuration);
		loadOptions.put(XMIResource.OPTION_DEFER_IDREF_RESOLUTION,true);
	//	System.out.println("**** Resource Objects ****");
	//	System.out.println(resource.getContents().size());
		long startTime = 0;
		try {
			startTime = System.nanoTime();
			resource.load(loadOptions);
			//resource.save(System.out, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	//	System.out.println(resource.getContents().size());
	}

	public void createResource(String packageName) {

		// if (this.effectiveMetamodelReconciler.getPackage(packageName) != null)
		resource = new XMIResourceImpl();
		effectiveMetamodelReconciler = new EffectiveMetamodelReconciler();
		this.effectiveMetamodelReconciler.addPackages(metamodelResource.getPackageRegistry().values());
		this.effectiveMetamodelReconciler.addEffectiveMetamodel(getEffectiveMteamodel());
		this.effectiveMetamodelReconciler.reconcile();
		objectsToLoad.clear();

		if (!effectiveMetamodelReconciler.getActualObjectsToLoad().isEmpty()) {
			actualObjectsAndFeaturesToLoad = effectiveMetamodelReconciler.getActualObjectsToLoad().get(packageName);
			objectsToLoad.putAll(effectiveMetamodelReconciler.getActualObjectsToLoad().get(packageName));
		}
		if (!effectiveMetamodelReconciler.getTypesToLoad().isEmpty())
			objectsToLoad.putAll(effectiveMetamodelReconciler.getTypesToLoad().get(packageName));

		pkg = effectiveMetamodelReconciler.getPackage(packageName);
	//	factory = pkg.getEFactoryInstance();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new PartialXMIResourceFactory());
		resourceSet.getPackageRegistry().put(pkg.getNsURI(), pkg);
		resource = resourceSet.createResource(URI.createFileURI(this.getModelFile()));
	}
	
	public void setEffectiveMteamodels(LinkedHashMap<Constraint, EffectiveMetamodel> efmetamodels, boolean isPartitioned) {
		effectiveMetamodels = new LinkedHashMap<Set<Constraint>, EffectiveMetamodel>();
		Collection<Object> pkgs = metamodelResource.getPackageRegistry().values();
		pkg = (EPackage) pkgs.iterator().next();
		if (isPartitioned) {
			partitionHandler = new PartitioningHandler(efmetamodels,pkg);
			effectiveMetamodels.putAll(partitionHandler.getPartitions());
		}
		else {
			for (Entry<Constraint, EffectiveMetamodel> en : efmetamodels.entrySet()) {
					Set<Constraint> cons = new HashSet<Constraint>();
					cons.add(en.getKey());
					effectiveMetamodels.put(cons, en.getValue());
			}
		}
	}

	public void setEffectiveMteamodel(EffectiveMetamodel efMetamodel) {
		this.effectiveMetamodel = efMetamodel;
		effectiveMetamodel.setName(this.name);
	}

	public EffectiveMetamodel getEffectiveMteamodel() {
		return effectiveMetamodel;
	}
	public EffectiveMetamodel getEffectiveMteamodel(Constraint c) {
		for (Set<Constraint> con : effectiveMetamodels.keySet()) {
			if (con.contains(c)) {
				return effectiveMetamodels.get(con);
			}
		}
		return null;
	}
	public HashMap<Set<Constraint>, EffectiveMetamodel> getEffectiveMteamodels() {
		return effectiveMetamodels;
	}
	public PartitioningHandler getPartitioningHandler() {
		return partitionHandler;
	}
}
