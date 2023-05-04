package org.eclipse.epsilon.smartsaxparser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.epsilon.effectivemetamodel.EffectiveFeature;
import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodel;
import org.eclipse.epsilon.effectivemetamodel.EffectiveType;
import org.eclipse.epsilon.eol.m3.Reference;

public class EffectiveMetamodelReconciler {

	//effective metamodels
	protected ArrayList<EffectiveMetamodel> effectiveMetamodels = new ArrayList<EffectiveMetamodel>();
	
	//epackages
	protected ArrayList<EPackage> packages = new ArrayList<EPackage>();
	
	protected HashMap<String, HashMap<EClass, ArrayList<EStructuralFeature>>> actualObjectsAndFeaturesToLoad = new HashMap<String, HashMap<EClass,ArrayList<EStructuralFeature>>>();
	protected HashMap<String, HashMap<EClass, ArrayList<EStructuralFeature>>> typesToLoad = new HashMap<String, HashMap<EClass,ArrayList<EStructuralFeature>>>();
	protected HashMap<String, ArrayList<String>> metamodelClasses = new HashMap<String, ArrayList<String>>();
	public ArrayList<EffectiveMetamodel> getEffectiveMetamodels() {
		return effectiveMetamodels;
	}
	
	public void addEffectiveMetamodel(EffectiveMetamodel effectiveMetamodel)
	{
		effectiveMetamodels.add(effectiveMetamodel);
	}
	
	public void addEffectiveMetamodels(ArrayList<EffectiveMetamodel> effectiveMetamodels)
	{
		this.effectiveMetamodels.addAll(effectiveMetamodels);
	}
	
	public void addPackage(EPackage ePackage)
	{
		packages.add(ePackage);
	}
	
	public void addPackages(Collection<?> packages)
	{
		this.packages.addAll((Collection<? extends EPackage>) packages);
	}
	
	public HashMap<String, HashMap<EClass, ArrayList<EStructuralFeature>>> getActualObjectsToLoad() {
		return actualObjectsAndFeaturesToLoad;
	}
	
//	public HashMap<String, HashMap<EClass, ArrayList<EStructuralFeature>>> getTypesToLoad() {
//		//return typesToLoad;
//	}
	
	public void reconcile()
	{
		long start = System.nanoTime();
		//for each epackage, add to 'actualObjectToLoad' considering 
		for(EPackage ePackage: packages)
		{
			
			//for each eclassifier
			for(EClassifier eClassifier: ePackage.getEClassifiers())
			{
				
				//if eclassifier is a eclass
				if (eClassifier instanceof EClass) {
					
					//if the class is an actual eclass to load, add to the map
					if (actualObjectToLoad(ePackage, (EClass) eClassifier)) {
						addActualObjectToLoad((EClass) eClassifier);
					}
					
					
					if (typesToLoad(ePackage, (EClass) eClassifier)) {
						addTypesToLoad((EClass) eClassifier);
					}
					
					EClass eClass = (EClass) eClassifier;
				}
			}
		}
		long end = System.nanoTime();
	//	System.out.println("Loop One = " + (long)(end-start)/1000000);
		
		start = System.nanoTime();
		
//		for(EPackage ePackage: packages)
//		{
//			for(EClassifier eClassifier: ePackage.getEClassifiers())
//			{
//				if (eClassifier instanceof EClass) {
//					EClass leClass = (EClass) eClassifier;
//					
//					if (actualObjectToLoad(ePackage, (EClass) eClassifier)) {
//						
//						for(EReference eReference: leClass.getEAllReferences())
//						{
//							if( actualObjectsAndFeaturesToLoad.get(ePackage.getName()).get(eClassifier.getName()).contains(eReference.getName()))
//							{
//								EClass eType = (EClass) eReference.getEType();
//							
//								if (eReference.isContainment()) {
//									addTypesToLoad(eType);
//									for(EClassifier eclass: ePackage.getEClassifiers())
//									{
//										if (eclass instanceof EClass) {
//											EClass subclass = (EClass) eclass;
//											if (subclass.getEAllSuperTypes().contains(eType))
//												addTypesToLoad(subclass);
//										}
//									}
//								}
//								else
//								{
//									addActualObjectToLoad(eType);
//									
//									for(EClassifier eclass: ePackage.getEClassifiers())
//										{
//											if (eclass instanceof EClass) {
//												EClass subclass = (EClass) eclass;
//												if (subclass.getEAllSuperTypes().contains(eType))
//													addActualObjectToLoad(subclass);
//											}
//										}
//								}
//							//	By Sorour
////								for(EClassifier eclass: ePackage.getEClassifiers())
////								{
////									if (eclass instanceof EClass) {
////										EClass subclass = (EClass) eclass;
////										if (subclass.getEAllSuperTypes().contains(eType))
////											addTypesToLoad(subclass);
////									}
////								}
//							}
//						}
//					}
//					
//					if (typesToLoad(ePackage, leClass)) {
//						for(EReference eReference: leClass.getEAllReferences())
//						{
//							EClass eType = (EClass) eReference.getEType();
//							if (typesToLoad!=null &&typesToLoad.get(ePackage.getName())!= null &&typesToLoad.get(ePackage.getName()).get(eClassifier.getName())!=null)
//								if(typesToLoad.get(ePackage.getName()).get(eClassifier.getName()).contains(eReference.getName()))
//								{
//						
//									if (eReference.isContainment()) {
//										addTypesToLoad(eType);
//										for(EClassifier eclass: ePackage.getEClassifiers())
//										{
//											if (eclass instanceof EClass) {
//												EClass subclass = (EClass) eclass;
//												if (subclass.getEAllSuperTypes().contains(eType))
//													addTypesToLoad(subclass);
//											}
//										}
//									}
//									else
//									{
//										addActualObjectToLoad(eType);
//										for(EClassifier eclass: ePackage.getEClassifiers())
//										{
//											if (eclass instanceof EClass) {
//												EClass subclass = (EClass) eclass;
//												if (subclass.getEAllSuperTypes().contains(eType))
//													addActualObjectToLoad(subclass);
//											}
//										}
//									}
//										
//								
//						
//								//By Sorour
////								for(EClassifier eclass: ePackage.getEClassifiers())
////								{
////									if (eclass instanceof EClass) {
////										EClass subclass = (EClass) eclass;
////										if (subclass.getEAllSuperTypes().contains(eType))
////											addTypesToLoad(subclass);
////									}
////								}
//								}
//						}
//						}
//				}
//			}
//	}
		end = System.nanoTime();
	//	System.out.println("Loop Two = " + (long)(end-start)/1000000);
		
	/*	for(XMIN em: effectiveMetamodels)
		{
			
			for(EffectiveType et: em.getAllOfKind())
			{
				ArrayList<String> features = actualObjectsAndFeaturesToLoad.get(em.getName()).get(et.getName());
				for(EffectiveFeature ef: et.getAttributes())
				{
					if (!features.contains(ef.getName())) {
						features.add(ef.getName());
					}
				}
				for(EffectiveFeature ef: et.getReferences())
				{
					if (!features.contains(ef.getName())) {
						features.add(ef.getName());
					}
				}
			}
			
			for(EffectiveType et: em.getAllOfType())
			{
				ArrayList<String> features = actualObjectsAndFeaturesToLoad.get(em.getName()).get(et.getName());
				for(EffectiveFeature ef: et.getAttributes())
				{
					if (!features.contains(ef.getName())) {
						features.add(ef.getName());
					}
				}
				for(EffectiveFeature ef: et.getReferences())
				{
					if (!features.contains(ef.getName())) {
						features.add(ef.getName());
					}
				}
			}
			
			for(EffectiveType et: em.getTypes())
			{
				if (typesToLoad.containsKey(et.getName())) {
					ArrayList<String> features = typesToLoad.get(em.getName()).get(et.getName());
					for(EffectiveFeature ef: et.getAttributes())
					{
						if (!features.contains(ef.getName())) {
							features.add(ef.getName());
						}
					}
				for(EffectiveFeature ef: et.getReferences())
				{
					if (!(features.contains(ef.getName()))) {
						features.add(ef.getName());
					}
				}
				}
			}
		}*/
	}

	
	//returns true if the eclass is an actual object to load
	public boolean actualObjectToLoad(EPackage ePackage, EClass eClass)
	{
		//for each effective metamodel in the container
		for(EffectiveMetamodel em: effectiveMetamodels)
		{
			//if em's name is equal to epack's name
			//if (em.getName().equalsIgnoreCase(ePackage.getName())) {
				
				//for each type in all of kind
				for(EffectiveType et: em.getAllOfKind())
				{
					//get the element name
					String elementName = et.getName();
					//if the element's name is equal to the eclass's name, return true
					if (elementName.equals(eClass.getName())) {
						return true;
					}
					
					//get the eclass by name
				/*	EClass kind = (EClass) ePackage.getEClassifier(elementName);
					
					//if the eclass's super types contains the type also return true
					if(eClass.getEAllSuperTypes().contains(kind))
					{
						return true;
					}*/
					/*By Sorour*/
//				    if (eClass.getEAllSuperTypes().contains(kind))
//						return true;
					/**/
				}
				
				//for each type in all of type
				for(EffectiveType et: em.getAllOfType())
				{
					//get name
					String elementName = et.getName();
					//if name equals, return true
					if (elementName.equals(eClass.getName())) {
						return true;
					}
				}
			//}
		}
		return false;
	}
	
	public boolean typesToLoad(EPackage ePackage, EClass eClass)
	{
		//for each effective metamodel in the container
		for(EffectiveMetamodel em: effectiveMetamodels)
		{
			//if em's name is equal to epack's name
				for(EffectiveType et: em.getTypes())
				{
					//get name
					String elementName = et.getName();
					//if name equals, return true
					if (elementName.equals(eClass.getName())) {
						return true;
					}
					
					//get the eclass by name
					EClass kind = (EClass) ePackage.getEClassifier(elementName);
					
					//if the eclass's super types contains the type also return true
					if(eClass.getEAllSuperTypes().contains(kind))
					{
						return true;
					}
					
				}
		//	}
		}
		return false;
	}
	
	public void addActualObjectToLoad(EClass eClass)
	{
		//get the epackage name
		String epackage = eClass.getEPackage().getNsURI();
		
		//get the submap with the epackage name
		HashMap<EClass, ArrayList<EStructuralFeature>> subMap = actualObjectsAndFeaturesToLoad.get(epackage);
		//HashMap<EClass, ArrayList<EStructuralFeature>> types = typesToLoad.get(epackage);
		ArrayList<EStructuralFeature> refs = new ArrayList<EStructuralFeature>();
				
		//if sub map is null
		if (subMap == null) {
			
			//create new sub map
			subMap = new HashMap<EClass, ArrayList<EStructuralFeature>>();
			
			//create new refs for the map
			refs = getFeaturesForClassToLoad(eClass);
			
			//add the ref to the sub map
			subMap.put(eClass, refs);
			
			//add the sub map to objectsAndRefNamesToVisit
			actualObjectsAndFeaturesToLoad.put(epackage, subMap);
		}
		else
		{
			//if sub map is not null, get the refs by class name
			refs = subMap.get(eClass);

			//if refs is null, create new refs and add the ref and then add to sub map
			if (refs == null) {
				//get refs from allOfKind or allOfType
				refs = getFeaturesForClassToLoad(eClass);
	
				subMap.put(eClass, refs);
			}
		}
	}
	
	public void addTypesToLoad(EClass eClass)
	{
		//get the epackage name
		String epackage = eClass.getEPackage().getNsURI();
		
		//get the submap with the epackage name
		HashMap<EClass, ArrayList<EStructuralFeature>> subMap = actualObjectsAndFeaturesToLoad.get(epackage);
	//	HashMap<EClass, ArrayList<EStructuralFeature>> Actual = actualObjectsAndFeaturesToLoad.get(epackage);
		ArrayList<EStructuralFeature> refs = new ArrayList<EStructuralFeature>();
		
		//if sub map is null and eClass doesn't exist in ActualObjToLoad
		
		if (subMap == null) {
				//create new sub map
				subMap = new HashMap<EClass, ArrayList<EStructuralFeature>>();
				
				refs = getFeaturesForTypeToLoad(eClass);
					//add the ref to the sub map
					subMap.put(eClass, refs);
			
					//add the sub map to objectsAndRefNamesToVisit
					actualObjectsAndFeaturesToLoad.put(epackage, subMap);
				}
		else
		{	
			//if sub map is not null, get the refs by class name
			refs = subMap.get(eClass);
			//if refs is null, create new refs and add the ref and then add to sub map
			if (refs == null) {
				
				//the features should be loaded accprding to effective metamdel
				refs = getFeaturesForTypeToLoad(eClass);
				subMap.put(eClass, refs);
			}
			
		}
		//If this eclass exists as ActualObjectToLoad then merge features in ActualObjtToLoad
		//Merge or not?
		
//		if (Actual != null && Actual.containsKey(eClass.getName())) {
//				for (String feature : Actual.get(eClass.getName()))
//						if (refs.contains(feature))
//							refs.remove(feature);
//			
//						refs.addAll(Actual.get(eClass.getName()));
//						Actual.put(eClass.getName(), refs);
//						subMap.remove(eClass.getName());
//				}
}

	public ArrayList<EStructuralFeature> getFeaturesForTypeToLoad(EClass eClass)
	{
		//get the package
		EPackage ePackage = eClass.getEPackage();
		//prepare the result
		ArrayList<EStructuralFeature> result = new ArrayList<EStructuralFeature>();
		
		//for all model containers
		for(EffectiveMetamodel em: effectiveMetamodels)
		{
			//if the container is the container needed
		//	if (em.getName().equals(ePackage.getName())) {
				for(EffectiveType et: em.getTypes())
				{
					//if class name equals, add all references and attributes
					if (eClass.getName().equals(et.getName())) {
						for(EffectiveFeature ef: et.getAttributes())
						{
							//result.add(ef.getName());
							if (!result.contains(eClass.getEStructuralFeature(ef.getName())))
							result.add(eClass.getEStructuralFeature(ef.getName()));
						}
						for(EffectiveFeature ef: et.getReferences())
						{
							//result.add(ef.getName());
							if (!result.contains(eClass.getEStructuralFeature(ef.getName())))
							result.add(eClass.getEStructuralFeature(ef.getName()));
						}
						//break loop2;
					}
					
					//if eclass is a sub class of the kind, add all attributes and references
				/*	EClass kind = (EClass) ePackage.getEClassifier(et.getName());
					if (eClass.getEAllSuperTypes().contains(kind)) {
						for(EffectiveFeature ef: et.getAttributes())
						{
							//add by Sorour
							if (!result.contains(eClass.getEStructuralFeature(ef.getName())))
									//result.add(ef.getName());
								result.add(eClass.getEStructuralFeature(ef.getName()));
						}
						for(EffectiveFeature ef: et.getReferences())
						{
							if (!result.contains(eClass.getEStructuralFeature(ef.getName())))
								//result.add(ef.getName());
								result.add(eClass.getEStructuralFeature(ef.getName()));
						}
						//break loop1;
					}*/
				}
		//	}
		}
		return result;
	}
	
	public ArrayList<EStructuralFeature> getFeaturesForClassToLoad(EClass eClass)
	{
		//get the package
		EPackage ePackage = eClass.getEPackage();
		//prepare the result
		ArrayList<EStructuralFeature> result = new ArrayList<EStructuralFeature>();
		
		//for all model containers
		for(EffectiveMetamodel em: effectiveMetamodels)
		{
			//if the container is the container needed
		//	if (em.getName().equals(ePackage.getName())) {
				//for elements all of kind
				//loop1:
				for(EffectiveType et: em.getAllOfKind())
				{
					//if class name equals, add all attributes and references
					if (eClass.getName().equals(et.getName())) {
						for(EffectiveFeature ef: et.getAttributes())
						{
							//result.add(ef.getName());
							if (!result.contains(eClass.getEStructuralFeature(ef.getName())))
								result.add(eClass.getEStructuralFeature(ef.getName()));
						}
						for(EffectiveFeature ef: et.getReferences())
						{
							//result.add(ef.getName());
							if (!result.contains(eClass.getEStructuralFeature(ef.getName())))
								result.add(eClass.getEStructuralFeature(ef.getName()));
						}
						//break loop1;
					}
					
					//if eclass is a sub class of the kind, add all attributes and references
					EClass kind = (EClass) ePackage.getEClassifier(et.getName());
					if (eClass.getEAllSuperTypes().contains(kind)) {
						for(EffectiveFeature ef: et.getAttributes())
						{
							//Add recently
							if (!result.contains(eClass.getEStructuralFeature(ef.getName())))
								//result.add(ef.getName());
								result.add(eClass.getEStructuralFeature(ef.getName()));
						}
						for(EffectiveFeature ef: et.getReferences())
						{
							//Add recently
							if (!result.contains(eClass.getEStructuralFeature(ef.getName())))
								//result.add(ef.getName());
								result.add(eClass.getEStructuralFeature(ef.getName()));
						}
						//break loop1;
					}
				}
				
				//for elements all of type
				//loop2:
				for(EffectiveType et: em.getAllOfType())
				{
					//if class name equals, add all references and attributes
					if (eClass.getName().equals(et.getName())) {
						for(EffectiveFeature ef: et.getAttributes())
						{
							//result.add(ef.getName());
							result.add(eClass.getEStructuralFeature(ef.getName()));
						}
						for(EffectiveFeature ef: et.getReferences())
						{
							//result.add(ef.getName());
							result.add(eClass.getEStructuralFeature(ef.getName()));
						}
						//break loop2;
					}
				}
		//	}
		}
		return result;
	}
	
	public void print(String packageName) {
		System.out.println("AllOfKind:");
		for (EClass cls : getActualObjectsToLoad().get(packageName).keySet()) {
			System.out.print(cls.getName() + " : ");
			for (EStructuralFeature f : getActualObjectsToLoad().get(packageName).get(cls))
				System.out.println(" feature = " + f.getName());
		}
		
//		if (!getTypesToLoad().isEmpty()) {
//			System.out.println("AllOfType:");
//		for (EClass cls : getTypesToLoad().get(packageName).keySet()) {
//			System.out.print(cls.getName() + " : ");
//			for (EStructuralFeature f : getTypesToLoad().get(packageName).get(cls))
//				System.out.println(" feature = " + f.getName());
//		}
//		}
	}
	
	public EPackage getPackage(String packageName) {
		for (EPackage pkg : packages)
			if (pkg.getNsURI().equals(packageName))
				return pkg;
		return null;
	}
//	public HashMap<String, HashMap<EClass, ArrayList<EStructuralFeature>>> getActualObjectsToLoad() {
//		return actualObjectsAndFeaturesToLoad;
//	}
	public HashMap<String, HashMap<EClass, ArrayList<EStructuralFeature>>> getTypesToLoad() {
		return typesToLoad;
	}
}