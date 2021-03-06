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

public class EffectiveMetamodelReconciler {

	//effective metamodels
	protected ArrayList<EffectiveMetamodel> effectiveMetamodels = new ArrayList<EffectiveMetamodel>();
	
	//epackages
	protected ArrayList<EPackage> packages = new ArrayList<EPackage>();
	
	protected HashMap<String, HashMap<EClass, ArrayList<EStructuralFeature>>> traversalPlans = new HashMap<String, HashMap<EClass,ArrayList<EStructuralFeature>>>();
	protected HashMap<String, HashMap<EClass, ArrayList<EStructuralFeature>>> actualObjectsAndFeaturesToLoad = new HashMap<String, HashMap<EClass,ArrayList<EStructuralFeature>>>();
	protected HashMap<String, HashMap<EClass, ArrayList<EStructuralFeature>>> typesToLoad = new HashMap<String, HashMap<EClass,ArrayList<EStructuralFeature>>>();
	protected HashMap<String, ArrayList<String>> metamodelClasses = new HashMap<String, ArrayList<String>>();
	protected HashMap<String, ArrayList<String>> placeHolderObjects = new HashMap<String, ArrayList<String>>();
	
	//visited EClasses
	protected ArrayList<EClass> visitedClasses = new ArrayList<EClass>();

	
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
	
	public HashMap<String, HashMap<EClass, ArrayList<EStructuralFeature>>> getObjectsAndRefNamesToVisit() {
		return traversalPlans;
	}
	
	public HashMap<String, HashMap<EClass, ArrayList<EStructuralFeature>>> getActualObjectsToLoad() {
		return actualObjectsAndFeaturesToLoad;
	}
	
	public HashMap<String, HashMap<EClass, ArrayList<EStructuralFeature>>> getTypesToLoad() {
		return typesToLoad;
	}
	
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
					
					//cast to eClass

					EClass eClass = (EClass) eClassifier;
					
					//clear visited class
					visitedClasses.clear();
					
					//visit EClass
					planTraversal(eClass);
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
			if (em.getName().equalsIgnoreCase(ePackage.getName())) {
				
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
					EClass kind = (EClass) ePackage.getEClassifier(elementName);
					
					//if the eclass's super types contains the type also return true
//					if(eClass.getESuperTypes().contains(kind))
//					{
//						return true;
//					}
					/*By Sorour*/
				    if (eClass.getEAllSuperTypes().contains(kind))
						return true;
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
			}
		}
		return false;
	}
	
	public boolean typesToLoad(EPackage ePackage, EClass eClass)
	{
		//for each effective metamodel in the container
		for(EffectiveMetamodel em: effectiveMetamodels)
		{
			//if em's name is equal to epack's name
			if (em.getName().equalsIgnoreCase(ePackage.getName())) {
				
				//for each type in all of type
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
//					if(eClass.getESuperTypes().contains(kind))
//					{
//						return true;
//					}
					// The subclasses of effective types should be loaded as well
					
					/*By Sorour*/
					 if (eClass.getEAllSuperTypes().contains(kind))
						return true;
					/**/
				}
			}
		}
		return false;
	}
	
	public void addActualObjectToLoad(EClass eClass)
	{
		//get the epackage name
		String epackage = eClass.getEPackage().getName();
		
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
		String epackage = eClass.getEPackage().getName();
		
		//get the submap with the epackage name
		HashMap<EClass, ArrayList<EStructuralFeature>> subMap = typesToLoad.get(epackage);
	//	HashMap<EClass, ArrayList<EStructuralFeature>> Actual = actualObjectsAndFeaturesToLoad.get(epackage);
		ArrayList<EStructuralFeature> refs = getFeaturesForTypeToLoad(eClass);
		
		//if sub map is null and eClass doesn't exist in ActualObjToLoad
		
		if (subMap == null) {
				//create new sub map
				subMap = new HashMap<EClass, ArrayList<EStructuralFeature>>();
				
					//add the ref to the sub map
					subMap.put(eClass, refs);
			
					//add the sub map to objectsAndRefNamesToVisit
					typesToLoad.put(epackage, subMap);
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
			if (em.getName().equals(ePackage.getName())) {
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
					EClass kind = (EClass) ePackage.getEClassifier(et.getName());
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
					}
				}
			}
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
			if (em.getName().equals(ePackage.getName())) {
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
			}
		}
		return result;
	}

	public void planTraversal(EClass eClass)
	{
		//add this class to the visited
		visitedClasses.add(EcoreUtil.copy(eClass));
		
		//if this one is a live class, should addRef()
		if (liveClass(eClass.getEPackage(), eClass.getName())) {
			//add class to objectsAndRefNamesToVisit
			addToTraversalPlan(eClass, null);
			//add to placeholder if necessary
			insertPlaceHolderOjbects(eClass.getEPackage(), eClass);
		}
		
		//for each reference
		for(EReference eReference: eClass.getEAllReferences())
		{
			//if the etype of the reference has not been visited
			if (!visitedEClass((EClass) eReference.getEType())) {
				//visit the etype
				planTraversal((EClass) eReference.getEType());
			}
			
			//if is live reference
			if (liveReference(eReference)) {
				//add class and reference to objectsAndRefNamesToVisit
				addToTraversalPlan(eClass, eReference);
				//insert placeholder if necessary
				insertPlaceHolderOjbects(eClass.getEPackage(), eClass);
			}
		}
		
		//for every eclassifier
		for(EClassifier every: eClass.getEPackage().getEClassifiers())
		{
			if (every instanceof EClass) {
				EClass theClass = (EClass) every;
				
				if (theClass.getEAllSuperTypes().contains(eClass)) {
					for(EReference eReference: theClass.getEAllReferences())
					{
						if (!visitedEClass((EClass) eReference.getEType())) {
							planTraversal((EClass) eReference.getEType());
						}
						
						if (liveReference(eReference)) {
							addToTraversalPlan(theClass, eReference);
							insertPlaceHolderOjbects(theClass.getEPackage(), theClass);
						}
					}
				}
			}
		}
	}

	
	//determines if a class is live class, this is used to generate the traversal path
	public boolean liveClass(EPackage ePackage, String className)
	{
		//for each effective metamodel
		for(EffectiveMetamodel em: effectiveMetamodels)
		{
			//get the package first
			if (em.getName().equalsIgnoreCase(ePackage.getName())) {
				
				//for all of kinds
				for(EffectiveType et: em.getAllOfKind())
				{
					//the element name
					String elementName = et.getName();
					//if name equals return true
					if (className.equals(elementName)) {
						return true;
					}
					
					//get the eclass for the mec
					EClass kind = (EClass) ePackage.getEClassifier(elementName);
					//get the eclass for the current class under question
					EClass actual = (EClass) ePackage.getEClassifier(className);
					//if the current class under question is a sub class of the mec, should return true
					if(actual.getEAllSuperTypes().contains(kind))
					{
						return true;
					}
					//if the current class under question is a super class of the mec, should also return true
					if (kind.getEAllSuperTypes().contains(actual)) 
					{
						return true;
					}
				}
				
				for(EffectiveType et: em.getAllOfType())
				{
					//the element n ame
					String elementName = et.getName();
					//if name equals return true
					if (className.equals(elementName)) {
						return true;
					}
					
					//get the eclass for the mec
					EClass type = (EClass) ePackage.getEClassifier(elementName);
					//get the eclass for the class under question
					EClass actual = (EClass) ePackage.getEClassifier(className);
					//if the class under question is a super class of the mec, should return true
					if (type.getEAllSuperTypes() != null && type.getEAllSuperTypes().contains(actual)) 
					{
						return true;
					}
				}
				
				for(EffectiveType et: em.getTypes())
				{
					//the element n ame
					String elementName = et.getName();
					//if name equals return true
					if (className.equals(elementName)) {
						return true;
					}
					
					//get the eclass for the mec
					EClass type = (EClass) ePackage.getEClassifier(elementName);
					//get the eclass for the class under question
					EClass actual = (EClass) ePackage.getEClassifier(className);
					//if the class under question is a super class of the mec, should return true
					if (type.getEAllSuperTypes() != null && type.getEAllSuperTypes().contains(actual)) 
					{
						return true;
					}
				}
				
				ArrayList<String> placeHolders = placeHolderObjects.get(em.getName());
				
				if (placeHolders != null) {
					for(String t : placeHolderObjects.get(em.getName()))
					{
						if (t.equals(className)) {
							return true;
						}
						
						//get the eclass for the mec
						EClass type = (EClass) ePackage.getEClassifier(t);
						//get the eclass for the class under question
						EClass actual = (EClass) ePackage.getEClassifier(className);
						//if the class under question is a super class of the mec, should return true
						if (type.getEAllSuperTypes().contains(actual)) 
						{
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	//add classes and references to visit and fill up the objectsAndRefNamesToVisit map
	public void addToTraversalPlan(EClass eClass, EReference eReference)
	{
		//get the epackage name
		String epackage = eClass.getEPackage().getName();
		//get the submap with the epackage name
		HashMap<EClass, ArrayList<EStructuralFeature>> subMap = traversalPlans.get(epackage);
		//if sub map is null
		if (subMap == null) {
			//create new sub map
			subMap = new HashMap<EClass, ArrayList<EStructuralFeature>>();
			//create new refs for the map
			ArrayList<EStructuralFeature> refs = new ArrayList<EStructuralFeature>();
			//if eReference is not null
			if (eReference != null) {
				//add the eReference to the ref
				refs.add(eReference);
			}
			//add the ref to the sub map
			subMap.put(eClass, refs);
			//add the sub map to objectsAndRefNamesToVisit
			traversalPlans.put(epackage, subMap);
		}
		else {
			//if sub map is not null, get the refs by class name
			ArrayList<EStructuralFeature> refs = subMap.get(eClass);

			//if refs is null, create new refs and add the ref and then add to sub map
			if (refs == null) {
				refs = new ArrayList<EStructuralFeature>();
				if(eReference != null)
				{
					refs.add(eReference);
				}
				subMap.put(eClass, refs);
			}
			//if ref is not null, add the ref
			else {
				if (eReference != null) {
					if (!refs.contains(eReference)) {
						refs.add(eReference);	
					}
				}
			}
		}
	}

	
	
	public void insertPlaceHolderOjbects(EPackage ePackage, EClass eClass)
	{
		//inserted 
		boolean inserted = false;
		//for each effective metamodel
		for(EffectiveMetamodel em: effectiveMetamodels)
		{
			//get the matching package
			if (em.getName().equals(ePackage.getName())) {
				
				//for each type in all of kind
				for(EffectiveType et: em.getAllOfKind())
				{
					//if types match, return
					if (et.getName().equals(eClass.getName())) {
						inserted = true;
						return;
					}
					
					//if types match return
					EClass kind = (EClass) ePackage.getEClassifier(et.getName());
					for(EClass superClass: eClass.getEAllSuperTypes())
					{
						if (kind.getName().equals(superClass.getName())) {
							inserted = true;
							return;
						}
					}
				}
				
				//for each type in all of type
				for(EffectiveType et: em.getAllOfType())
				{
					//if types match, return
					if (et.getName().equals(eClass.getName())) {
						inserted = true;
						return;
					}
				}
				
				ArrayList<String> placeHolders = placeHolderObjects.get(em.getName());
				
				if (placeHolders != null) {
					for(String t : placeHolderObjects.get(em.getName()))
					{
						if (t.equals(eClass.getName())) {
							inserted = true;
							return;
						}
					}
				}
				
				
				
				//if not inserted, add to types
				if (!inserted) {
					inserted = true;
					addPlaceHolderObject(ePackage.getName(), eClass.getName());
					return;
				}
			}
		}
		//if not inserted, create effective metamodel and add to types
		if (!inserted) {
			addPlaceHolderObject(ePackage.getName(), eClass.getName());
			EffectiveMetamodel newEffectiveMetamodel = new EffectiveMetamodel();
			newEffectiveMetamodel.setName(ePackage.getName());
			effectiveMetamodels.add(newEffectiveMetamodel);
		}
	}
	
	public void addPlaceHolderObject(String ePackage, String type)
	{
		if (placeHolderObjects.containsKey(ePackage)) {
			ArrayList<String> metamodel = placeHolderObjects.get(ePackage);
			if (metamodel.contains(type)) {
				return;
			}
			else {
				metamodel.add(type);
			}
		}
		else {
			ArrayList<String> metamodel = new ArrayList<String>();
			metamodel.add(type);
			placeHolderObjects.put(ePackage, metamodel);
		}
	}

	//test to see if the class has been visited
	public boolean visitedEClass(EClass eClass)
	{
		for(EClass clazz: visitedClasses)
		{
			if (clazz.getName().equals(eClass.getName())) {
				return true;
			}
		}
		return false;
	}


	//returns true if the reference is live
	public boolean liveReference(EReference eReference)
	{
		//if reference is containment, we are not looking into non-containment references
		if(eReference.isContainment())
		{
			//get the etype
			EClassifier eClassifier = eReference.getEType();
			EClass etype = (EClass) eClassifier;
			
			//if etype is a live class, return true
			if (liveClass(etype.getEPackage(), etype.getName())) {
				return true;
			}
			
			return false;
		}
		return false;
		
	}
	
	public void print(String packageName) {
		System.out.println("AllOfKind:");
		for (EClass cls : getActualObjectsToLoad().get(packageName).keySet()) {
			System.out.print(cls.getName() + " : ");
			for (EStructuralFeature f : getActualObjectsToLoad().get(packageName).get(cls))
				System.out.println(" feature = " + f.getName());
		}
		
		if (!getTypesToLoad().isEmpty()) {
			System.out.println("AllOfType:");
		for (EClass cls : getTypesToLoad().get(packageName).keySet()) {
			System.out.print(cls.getName() + " : ");
			for (EStructuralFeature f : getTypesToLoad().get(packageName).get(cls))
				System.out.println(" feature = " + f.getName());
		}
		}
	}
	
	public EPackage getPackage(String packageName) {
		for (EPackage pkg : packages)
			if (pkg.getName().equals(packageName))
				return pkg;
		return null;
	}
}