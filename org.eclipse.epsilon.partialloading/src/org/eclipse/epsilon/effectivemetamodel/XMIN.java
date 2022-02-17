package org.eclipse.epsilon.effectivemetamodel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.emc.emf.xmi.PartialXMILoadConfiguration;
import org.eclipse.epsilon.emc.emf.xmi.PartialXMIResourceFactory;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.smartsaxparser.EffectiveMetamodelReconciler;
import org.eclipse.epsilon.smartsaxparser.SmartSAXResourceFactory;
import org.eclipse.epsilon.smartsaxparser.SmartSAXXMIResource;

public class XMIN extends EmfModel {

	public static final String OPTION_PARTIAL_LOADING_CONFIGURATION = "PARTIAL_LOADING_CONFIGURATION";
	EffectiveMetamodel effectiveMetamodel;
	HashMap<String, EffectiveMetamodel> effectiveMetamodels = new HashMap<String, EffectiveMetamodel>();
	ResourceSet resourceSet = new ResourceSetImpl();
	Resource resource;

	public XMIN() {
		effectiveMetamodel = new EffectiveMetamodel();
		effectiveMetamodel.setName(this.name);
	}

	@Override
	public String toString() {
		return "XMIN Model [name=" + getName() + "]";
	}

	public void load(EffectiveMetamodel efMetamodel) {
		this.effectiveMetamodel = efMetamodel;
		effectiveMetamodel.setName(this.name);
		effectiveMetamodel.setNsuri(metamodelUris.get(0).toString());
		load();
	}

	public void loadResource() {

		EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(metamodelUris.get(0).toString());
		resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
		try {
			determinePackagesFrom(resourceSet);
		} catch (EolModelLoadingException e1) {
			e1.printStackTrace();
		}
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new PartialXMIResourceFactory());
		resource = resourceSet.createResource(modelUri);
		this.setResource(resource);
	}

	@Override
	public void load() {
		if (effectiveMetamodels.isEmpty() && effectiveMetamodel.getIsCalculated() == false)
			return;
		long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		EffectiveMetamodelReconciler effectiveMetamodelReconciler = new EffectiveMetamodelReconciler();
		effectiveMetamodelReconciler.addPackages(resourceSet.getPackageRegistry().values());
		effectiveMetamodelReconciler.addEffectiveMetamodel(effectiveMetamodel);
		long st = System.nanoTime();
		effectiveMetamodelReconciler.reconcile();
		long en = System.nanoTime();
		System.out.println("Reconcile  : " + (long) ((en - st) / 1000000));
		effectiveMetamodelReconciler.print("EnergyProvider");
		PartialXMILoadConfiguration configuration = new PartialXMILoadConfiguration();

		if (!effectiveMetamodelReconciler.getActualObjectsToLoad().isEmpty())
			for (EClass eclass : effectiveMetamodelReconciler.getActualObjectsToLoad().get(effectiveMetamodel.getName())
					.keySet()) {
				configuration.addAllOfKind(eclass);
				for (EStructuralFeature ef : effectiveMetamodelReconciler.getActualObjectsToLoad()
						.get(effectiveMetamodel.getName()).get(eclass)) {
					configuration.addFeature(eclass, ef);
				}
			}

		if (!effectiveMetamodelReconciler.getTypesToLoad().isEmpty())
			for (EClass eclass : effectiveMetamodelReconciler.getTypesToLoad().get(effectiveMetamodel.getName())
					.keySet()) {
				configuration.addAllOfType(eclass);
				for (EStructuralFeature ef : effectiveMetamodelReconciler.getTypesToLoad()
						.get(effectiveMetamodel.getName()).get(eclass))
					configuration.addFeature(eclass, ef);
			}

		HashMap<String, Object> loadOptions = new HashMap<>();
		loadOptions.put(OPTION_PARTIAL_LOADING_CONFIGURATION, configuration);

		long startTime = 0;
		try {
			startTime = System.nanoTime();
			resource.load(loadOptions);
			//resource.save(System.out, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		long endTime = System.nanoTime();
		long duration = (endTime - startTime); // divide by 1000000 to get milliseconds.
		System.out.println("**** Time ****");
		System.out.println(duration / 1000000 + " milliseconds");

		long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		long memory = (long) ((endMemory - startMemory) / 1000000);
		System.out.println("**** Memory ****");
		System.out.println((memory) + " MB");
		System.out.println("**** Loaded Objects ****");
		System.out.println(resource.getContents().size());
	}

	public void setEffectiveMteamodel(HashMap<String, EffectiveMetamodel> efMetamodels) {
		this.effectiveMetamodels = efMetamodels;
	}

	public void setEffectiveMteamodel(EffectiveMetamodel efMetamodel) {
		this.effectiveMetamodel = efMetamodel;
		effectiveMetamodel.setName(this.name);
	}

	public EffectiveMetamodel getEffectiveMteamodel() {
		return effectiveMetamodel;
	}

	public HashMap<String, EffectiveMetamodel> getEffectiveMteamodels() {
		return effectiveMetamodels;
	}
	
}
