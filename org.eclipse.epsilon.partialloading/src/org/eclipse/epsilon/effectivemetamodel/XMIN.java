package org.eclipse.epsilon.effectivemetamodel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.smartsaxparser.EffectiveMetamodelReconciler;
import org.eclipse.epsilon.smartsaxparser.SmartSAXResourceFactory;
import org.eclipse.epsilon.smartsaxparser.SmartSAXXMIResource;

public class XMIN extends EmfModel{

	EffectiveMetamodel effectiveMetamodel;
	HashMap<String, EffectiveMetamodel> effectiveMetamodels = new HashMap<String, EffectiveMetamodel>();
//	ArrayList<String> cons = new ArrayList<String>();
//	int i = 0;
	ResourceSet resourceSet = new ResourceSetImpl();
	Resource resource;
	
	public XMIN()
	{
		effectiveMetamodel = new EffectiveMetamodel();
		effectiveMetamodel.setName(this.name);
//		cons.add("hasTitle");
//		cons.add("hasRate");
//		cons.add("hasName");
	}
	public void setEffectiveMteamodel (HashMap<String, EffectiveMetamodel> efMetamodels) 
	{
		this.effectiveMetamodels = efMetamodels;
	}
	public void setEffectiveMteamodel (EffectiveMetamodel efMetamodel) 
	{
		this.effectiveMetamodel = efMetamodel;
		effectiveMetamodel.setName(this.name);
	}
	public EffectiveMetamodel getEffectiveMteamodel () 
	{
		return effectiveMetamodel;
	}
	public HashMap<String, EffectiveMetamodel> getEffectiveMteamodels () 
	{
		return effectiveMetamodels;
	}
	@Override
	public String toString() {
		return "XMIN Model [name=" + getName() + "]";
	}
	
	public void load(EffectiveMetamodel efMetamodel){
		this.effectiveMetamodel = efMetamodel;
		effectiveMetamodel.setName(this.name);
		effectiveMetamodel.setNsuri(PROPERTY_METAMODEL_URI);
		load();
	}
	
	public void loadResource() {
		
//		EffectiveMetamodel context = new EffectiveMetamodel(this.name,PROPERTY_METAMODEL_URI);
		EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(metamodelUris.get(0).toString());
//		if (i == 0)
//			context.addToAllOfKind("Movie");
//		else
//			context.addToAllOfKind("Movie");
		resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
		try {
			determinePackagesFrom(resourceSet);
		} catch (EolModelLoadingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new SmartSAXResourceFactory());
		resource = resourceSet.createResource(modelUri);
		this.setResource(resource);
		
//		EffectiveMetamodelReconciler effectiveMetamodelReconciler = new EffectiveMetamodelReconciler();
//		effectiveMetamodelReconciler.addPackages(resourceSet.getPackageRegistry().values());
//		effectiveMetamodelReconciler.addEffectiveMetamodel(context);
//		effectiveMetamodelReconciler.reconcile();
//		Map<String, Object> loadOptions = new HashMap<String, Object>();
//		loadOptions.put(SmartSAXXMIResource.OPTION_EFFECTIVE_METAMODEL_RECONCILER, effectiveMetamodelReconciler);
//		loadOptions.put(SmartSAXXMIResource.OPTION_LOAD_ALL_ATTRIBUTES, false);
//		loadOptions.put(SmartSAXXMIResource.OPTION_RECONCILE_EFFECTIVE_METAMODELS, true);
//		try {
//			resource.load(loadOptions);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	@Override
	public void load(){
//		if (effectiveMetamodel.getIsCalculated() == false)
//			return;
		//System.gc();
		if (effectiveMetamodels.isEmpty() && effectiveMetamodel.getIsCalculated() == false)
			return;
//		else
//			setEffectiveMteamodel(effectiveMetamodels.get(cons.get(i)));
//		
	//	System.out.println("Cons : " + cons.get(i));
//		effectiveMetamodel.setNsuri(metamodelUris.get(0).toString());
//		ResourceSet resourceSet = new ResourceSetImpl();
//		EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(metamodelUris.get(0).toString());
//		resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
//		try {
//			determinePackagesFrom(resourceSet);
//		} catch (EolModelLoadingException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new SmartSAXResourceFactory());
//		Resource resource = resourceSet.createResource(modelUri);
//		this.setResource(resource);
		long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		EffectiveMetamodelReconciler effectiveMetamodelReconciler = new EffectiveMetamodelReconciler();
		effectiveMetamodelReconciler.addPackages(resourceSet.getPackageRegistry().values());
		effectiveMetamodelReconciler.addEffectiveMetamodel(effectiveMetamodel);
		long st = System.nanoTime();
		effectiveMetamodelReconciler.reconcile();
		long en = System.nanoTime();
		System.out.println("Reconcile  : "+ (long)((en-st)/ 1000000));
		Map<String, Object> loadOptions = new HashMap<String, Object>();
		loadOptions.put(SmartSAXXMIResource.OPTION_EFFECTIVE_METAMODEL_RECONCILER, effectiveMetamodelReconciler);
		loadOptions.put(SmartSAXXMIResource.OPTION_LOAD_ALL_ATTRIBUTES, false);
		loadOptions.put(SmartSAXXMIResource.OPTION_RECONCILE_EFFECTIVE_METAMODELS, true);

		long startTime =0;
		try {
			 startTime = System.nanoTime();
			resource.load(loadOptions);
			
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
		for (EObject o : resource.getContents()) {
			
				System.out.println("Obj____" + o.getClass());
				for (EObject ob : o.eContents()) {
				System.out.println("Cont_____" + ob.getClass());
				for (EObject obj : ob.eContents()) 
					System.out.println("Cont_____" + obj.getClass());
				}
			}
//		i++;
	}
}
