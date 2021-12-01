package org.eclipse.epsilon.smartsaxparser;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.impl.XMILoadImpl;
import org.xml.sax.helpers.DefaultHandler;

public class SmartSAXXMILoadImpl extends XMILoadImpl{
	public boolean loadAllAttributes = true;
	
	protected HashMap<String, HashMap<EClass, ArrayList<EStructuralFeature>>> objectsAndRefNamesToVisit = new HashMap<String, HashMap<EClass,ArrayList<EStructuralFeature>>>();
	protected HashMap<String, HashMap<EClass, ArrayList<EStructuralFeature>>> actualObjectsToLoad = new HashMap<String, HashMap<EClass,ArrayList<EStructuralFeature>>>();
	protected HashMap<String, HashMap<EClass, ArrayList<EStructuralFeature>>> typesToLoad = new HashMap<String, HashMap<EClass,ArrayList<EStructuralFeature>>>();

	public void clearCollections()
	{
		objectsAndRefNamesToVisit.clear();
		objectsAndRefNamesToVisit = null;
		actualObjectsToLoad.clear();
		actualObjectsToLoad = null;
	}
	
	public void setObjectsAndRefNamesToVisit(
			HashMap<String, HashMap<EClass, ArrayList<EStructuralFeature>>> objectsAndRefNamesToVisit) {
		this.objectsAndRefNamesToVisit = objectsAndRefNamesToVisit;
	}

	public void setActualObjectsToLoad(
			HashMap<String, HashMap<EClass, ArrayList<EStructuralFeature>>> actualObjectsToLoad) {
		this.actualObjectsToLoad = actualObjectsToLoad;
	}
	
	public void setTypesToLoad(
			HashMap<String, HashMap<EClass, ArrayList<EStructuralFeature>>> typesToLoad) {
		this.typesToLoad = typesToLoad;
	}
	
	public SmartSAXXMILoadImpl(XMLHelper helper) {
		super(helper);
	}
	
	public void setLoadAllAttributes(boolean loadAllAttributes) {
		this.loadAllAttributes = loadAllAttributes;
	}
	
	@Override
	protected DefaultHandler makeDefaultHandler() {
		SmartSAXXMIHandler handler = new SmartSAXXMIHandler(resource, helper, options); 
		handler.setLoadAllAttributes(loadAllAttributes);

		handler.setObjectsAndRefNamesToVisit(objectsAndRefNamesToVisit);
		handler.setActualObjectsToLoad(actualObjectsToLoad);
		handler.setTypesToLoad(typesToLoad);
		return handler; 
		
	}

}
