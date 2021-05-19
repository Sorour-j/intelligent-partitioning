package org.eclipse.epsilon.effectivemetamodel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.SmartSaxParser.EffectiveMetamodelReconciler;
import org.eclipse.epsilon.SmartSaxParser.SmartSAXResourceFactory;
import org.eclipse.epsilon.SmartSaxParser.SmartSAXXMIResource;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.emc.emf.EmfPropertySetter;
import org.eclipse.epsilon.eol.dom.ExpressionStatement;
import org.eclipse.epsilon.eol.dom.NameExpression;
import org.eclipse.epsilon.eol.dom.OperationCallExpression;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;

public class XMIN extends EmfModel{

	protected String name;
	protected String nsuri;
	protected String path;
	protected boolean CalculatedEffectiveMetamodel = false; // To understand that effective metamodel is calculated or not 
	protected ArrayList<EffectiveType> allOfType = new ArrayList<EffectiveType>();
	protected ArrayList<EffectiveType> allOfKind = new ArrayList<EffectiveType>();
	
	protected ArrayList<EffectiveType> types = new ArrayList<EffectiveType>();
	
	public XMIN()
	{
	}
	
	@Override
	public String toString() {
		return "XMIN Model [name=" + getName() + "]";
	}
	public void setCalculatedEffectiveMetamodel(boolean set) {
		this.CalculatedEffectiveMetamodel = set;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	
	public XMIN(String name, String nsuri)
	{
		this.name = name;
		this.nsuri = nsuri;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getNsuri() {
		return nsuri;
	}
	
	public ArrayList<EffectiveType> getAllOfType() {
		return allOfType;
	}
	
	public ArrayList<EffectiveType> getAllOfKind() {
		return allOfKind;
	}
	
//	public boolean removeFromTypes(String modelElement) {
//		
//		EffectiveType me = new EffectiveType(modelElement);
//		
//		for(EffectiveType et: types)
//		{
//			if (et.getName().equals(modelElement)) {
//				types.remove(me);
//				return true;
//			}
//		}
//		return false;
//	}
	
	/*Add elements to effective meta-model references*/
	/* In all methods, if the element already exists, it just returned*/
	
	public void addAttributeToAll(String modelElement) {
		for(EffectiveType et: allOfKind)
			addAttributeToEffectiveType(et,modelElement);
		for(EffectiveType et: allOfType)
			addAttributeToEffectiveType(et,modelElement);
		for(EffectiveType et: types)
			addAttributeToEffectiveType(et,modelElement);
	}
	
	public EffectiveType addToAllOfKind(String modelElement)
	{
		for(EffectiveType et: allOfKind){
			if (et.getName().equals(modelElement)) {
				return et;
			}
		}
		if (allOfTypeContains(modelElement)) {
			EffectiveType et = addToAllOfKind(getFromAllOfType(modelElement));
			removeFromTypes(modelElement);
			return et;
		}
		else if(typesContains(modelElement)) {
			EffectiveType et = addToAllOfKind(getFromTypes(modelElement));
			removeFromTypes(modelElement);
			return et;
		}
		
		EffectiveType et = new EffectiveType(modelElement);
		et.setEffectiveMetamodel(this);
		allOfKind.add(et);
		return et;
	}
	public EffectiveType addToAllOfKind(EffectiveType et)
	{
		for(EffectiveType t: allOfKind){
			if (t.getName().equals(et.getName())) {
				return t;
			}
		}
		et.setEffectiveMetamodel(this);
		allOfKind.add(et);
		return et;
	}
	public EffectiveType addToAllOfType(String modelElement)
	
	{
		for(EffectiveType et: allOfType){
			if (et.getName().equals(modelElement)) {
				et.increaseUsage();
				return et;
			}
		}
		
		EffectiveType et = new EffectiveType(modelElement);
		et.setEffectiveMetamodel(this);
		allOfType.add(et);
		return et;
	}
	
	public EffectiveType addToTypes(String modelElement)
	
	{
		for(EffectiveType et: types)
		{
			if (et.getName().equals(modelElement)) {
				return et;
			}
		}
		EffectiveType et = new EffectiveType(modelElement);
		et.setEffectiveMetamodel(this);
		types.add(et);
		return et;
	}
	
	/*Get methods for all references of effective meta-model*/
	public ArrayList<EffectiveType> getTypes() {
		return types;
	}
	
	/*Methods for adding new feature to effective types (where-ever they are!)*/
	public EffectiveFeature addAttributeToEffectiveType(EffectiveType effectiveType, String attribute){
//		EffectiveType effectiveType = getFromAllOfKind(elementName);
//		if (effectiveType == null) {
//			effectiveType = getFromAllOfType(elementName);
//			if (effectiveType == null) 
//				effectiveType = getFromTypes(elementName);
//		}
		if (effectiveType != null) {
			EffectiveFeature effectiveFeature = new EffectiveFeature(attribute);
			for (EffectiveFeature ef : effectiveType.getAttributes())
				if (ef.getName().equals(attribute))
					return effectiveFeature;
			effectiveType.getAttributes().add(effectiveFeature);
			return effectiveFeature;
		}
		return null;
	}
	
	public EffectiveFeature addReferenceToEffectiveType(String elementName, String reference)
	{
		EffectiveType effectiveType = getFromAllOfKind(elementName);
		if (effectiveType == null) {
			effectiveType = getFromAllOfType(elementName);
			if (effectiveType == null) 
				effectiveType = getFromTypes(elementName);
		}
		if (effectiveType != null) {
			EffectiveFeature effectiveFeature = new EffectiveFeature(reference);
			for (EffectiveFeature ef : effectiveType.getReferences())
				if (ef.getName().equals(reference))
					return effectiveFeature;
			effectiveType.getReferences().add(effectiveFeature);
			return effectiveFeature;
		}
		return null;
	}
	
	public boolean removeFromTypes(String elementName)
	{
		EffectiveType effectiveType = getFromTypes(elementName);
		if (effectiveType != null) {
			return getTypes().remove(effectiveType);
		}
		return false;
	}
	public boolean removeFromAllOfType(String elementName)
	{
		EffectiveType effectiveType = getFromAllOfType(elementName);
		if (effectiveType != null) {
			return getTypes().remove(effectiveType);
		}
		return false;
	}
//	
//	public EffectiveFeature addReferenceToAllOfKind(String elementName, String reference)
//	{
//		EffectiveType effectiveType = getFromAllOfKind(elementName);
//		if (effectiveType != null) {
//			EffectiveFeature effectiveFeature = new EffectiveFeature(reference);
//			effectiveType.getReferences().add(effectiveFeature);
//			return effectiveFeature;
//		}
//		return null;
//	}
//	
//	public EffectiveFeature addReferenceToAllOfType(String elementName, String reference)
//	{
//		EffectiveType effectiveType = getFromAllOfType(elementName);
//		if (effectiveType != null) {
//			EffectiveFeature effectiveFeature = new EffectiveFeature(reference);
//			effectiveType.getReferences().add(effectiveFeature);
//			return effectiveFeature;
//		}
//		return null;
//	}
//	
//	public EffectiveFeature addAttributeToAllOfType(String elementName, String attribute)
//	{
//		EffectiveType effectiveType = getFromAllOfType(elementName);
//		if (effectiveType != null) {
//			EffectiveFeature effectiveFeature = new EffectiveFeature(attribute);
//			effectiveType.getAttributes().add(effectiveFeature);
//			return effectiveFeature;
//		}
//		return null;
//	}
//	public EffectiveFeature addAttributeToTypes(String elementName, String attribute)
//	{
//		EffectiveType effectiveType = getFromTypes(elementName);
//		if (effectiveType != null) {
//			EffectiveFeature effectiveFeature = new EffectiveFeature(attribute);
//			effectiveType.getAttributes().add(effectiveFeature);
//			return effectiveFeature;
//		}
//		return null;
//	}
//	public EffectiveFeature addAttributeToAllOfKind(String elementName, String attribute)
//	{
//		EffectiveType effectiveType = getFromAllOfKind(elementName);
//		if (effectiveType != null) {
//			EffectiveFeature effectiveFeature = new EffectiveFeature(attribute);
//			effectiveType.getAttributes().add(effectiveFeature);
//			return effectiveFeature;
//		}
//		return null;
//	}
	

	public ArrayList<EffectiveType> getAllOfEffectiveTypes()
	{
		ArrayList<EffectiveType> classes = new ArrayList<EffectiveType>();
		classes.addAll(getAllOfKind());
		classes.addAll(getAllOfType());
		classes.addAll(getTypes());
		return classes;
	}
	/*Get elements from allOfType*/
	public EffectiveType getFromAllOfType(String elementName)
	{
		for(EffectiveType ef: allOfType)
		{
			if (ef.getName().equals(elementName)) {
				return ef;
			}
		}
		return null;
	}
	/*Get elements from types*/
	public EffectiveType getFromTypes(String elementName)
	{
		for(EffectiveType ef: types)
		{
			if (ef.getName().equals(elementName)) {
				return ef;
			}
		}
		return null;
	}
	/*Get elements from allOfKind*/
	public EffectiveType getFromAllOfKind(String elementName)
	{
		for(EffectiveType ef: allOfKind)
		{
			if (ef.getName().equals(elementName)) {
				return ef;
			}
		}
		return null;
	}
	
	/*Check if an element is exists in allOfType*/
	public boolean allOfTypeContains(String modelElement)
	{
		for(EffectiveType ef: allOfType)
		{
			if (ef.getName().equals(modelElement)) {
				return true;
			}
		}
		return false;
	}
	/*Check if an element is exists in allOfKind*/
	public boolean allOfKindContains(String modelElement)
	{
		for(EffectiveType ef: allOfKind)
		{
			if (ef.getName().equals(modelElement)) {
				return true;
			}
		}
		return false;
	}
	/*Check if an element is exists in types*/
	public boolean typesContains(String modelElement)
	{
		for(EffectiveType ef: types){
			if (ef.getName().equals(modelElement)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void load(){
		
		if (CalculatedEffectiveMetamodel == false)
			return;
		
		ResourceSet resourceSet = new ResourceSetImpl();
		EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(metamodelUris.get(0).toString());
		resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
		try {
			determinePackagesFrom(resourceSet);
		} catch (EolModelLoadingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new SmartSAXResourceFactory());
		Resource resource = resourceSet.createResource(modelUri);
		this.setResource(resource);
		System.gc();
					
		long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		EffectiveMetamodelReconciler effectiveMetamodelReconciler = new EffectiveMetamodelReconciler();
		effectiveMetamodelReconciler.addPackages(resourceSet.getPackageRegistry().values());
		effectiveMetamodelReconciler.addEffectiveMetamodel(this);
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
		//System.out.println("End : "+ endMemory);
		long memory = (long) ((endMemory - startMemory) / 1000000);
		System.out.println("**** Memory ****");
		System.out.println((memory) + " MB");
		System.out.println("**** Loaded Objects ****");
		System.out.println(resource.getContents().size());
		
	}
}
