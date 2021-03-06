package org.eclipse.epsilon.smartsaxparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.xmi.FeatureNotFoundException;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.ConfigurationCache;
import org.eclipse.emf.ecore.xmi.impl.SAXXMIHandler;
import org.eclipse.emf.ecore.xml.type.SimpleAnyType;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.eclipse.emf.ecore.xml.type.util.XMLTypeUtil;

public class SmartSAXXMIHandler extends SAXXMIHandler{
	
	protected HashMap<String, HashMap<EClass, ArrayList<EStructuralFeature>>> traversalPlans = new HashMap<String, HashMap<EClass, ArrayList<EStructuralFeature>>>();
	protected HashMap<String, HashMap<EClass, ArrayList<EStructuralFeature>>> actualObjectsAndFeaturesToLoad = new HashMap<String, HashMap<EClass, ArrayList<EStructuralFeature>>>();
	protected HashMap<String, HashMap<EClass, ArrayList<EStructuralFeature>>> typesToLoad = new HashMap<String, HashMap<EClass, ArrayList<EStructuralFeature>>>();

 	protected HashMap<EClass, EObject> cache = new HashMap<EClass, EObject>();
 	
 	protected boolean handlingFeature = false;
 	
 	protected ArrayList<Integer> extentIndexToDelete = new ArrayList<Integer>(); 
	
	protected EClass traversal_currentClass;
	protected ArrayList<EStructuralFeature> traversal_currentFeatures = new ArrayList<EStructuralFeature>();
	
	protected boolean shouldHalt = false;
	protected String currentName = "";
	protected int currentElementsSize = -1;
	
	public boolean loadAllAttributes = true;
	public long time = 0;
	public boolean handleFeature;
	
	@Override
	public void endDocument() {
		
		System.out.println("Time new loop : " + time);
		traversal_currentFeatures.clear();
		traversal_currentFeatures = null;
		traversalPlans.clear();
		traversalPlans = null;
		actualObjectsAndFeaturesToLoad.clear();
		actualObjectsAndFeaturesToLoad = null;
		cache.clear();
		cache = null;
		objects.clear();
		objects = null;
		elements.clear();
		elements = null;
		mixedTargets.clear();
		mixedTargets = null;
		super.endDocument();
	}
	
	public void setTypesToLoad(
			HashMap<String, HashMap<EClass, ArrayList<EStructuralFeature>>> typesToLoad) {
		this.typesToLoad = typesToLoad;
	}
	
	public void setObjectsAndRefNamesToVisit(
			HashMap<String, HashMap<EClass, ArrayList<EStructuralFeature>>> objectsAndRefNamesToVisit) {
		this.traversalPlans = objectsAndRefNamesToVisit;
	}
	
	public void setActualObjectsToLoad(
			HashMap<String, HashMap<EClass, ArrayList<EStructuralFeature>>> actualObjectsToLoad) {
		this.actualObjectsAndFeaturesToLoad = actualObjectsToLoad;
	}
	
	public SmartSAXXMIHandler(XMLResource xmiResource, XMLHelper helper,
			Map<?, ?> options) {
		super(xmiResource, helper, options);
	}
	
	public void setLoadAllAttributes(boolean loadAllAttributes) {
		this.loadAllAttributes = loadAllAttributes;
	}
	
	public boolean cached(EClass eClass)
	{
		return cache.containsKey(eClass);
	}
	
	public EObject getCache(EClass eClass)
	{
		return cache.get(eClass);
	}
	
	public void insertCache(EClass eClass, EObject eObject)
	{
		if (!cache.containsKey(eClass)) {
			cache.put(eClass, eObject);	
		}
	}
	
	@Override
	@Deprecated
	protected EObject createObjectFromFactory(EFactory factory, String typeName) {
		EClass eClass = (EClass) factory.getEPackage().getEClassifier(typeName);
		
		//if the an instance of the class should be created
		if (shouldCreateAllObjectForClass(eClass)) {
			//prepare newObject
		    EObject newObject = null;
		    //if factory != null
		    if (factory != null)
		    {
		    	//create object
		    	newObject = helper.createObject(factory, typeName);

		    	//if object is not null, handle attributes and things
		    	if (newObject != null)
		    	{
		    		if (disableNotify)
		    			newObject.eSetDeliver(false);

		    		handleObjectAttribs(newObject);
		    	}
		    }
		//    if (!extent.contains(newObject))
//		    if (newObject.getClass().getName().equals("InfixExpression") && objects.peekEObject().getClass().getName().equals("IfStatement") )
//	    		System.out.println();
		   if (!handlingFeature || !extent.contains(objects.peekEObject())) {
		    	extent.add(newObject);	
//		    	if (extent.size() > 71)
//		    		System.out.print("Hi");
		    }
		    	
		    return newObject;
		    
		}
		
		else if (shouldCreateObjectForReference(eClass) && handlingFeature) {
			
			//prepare newObject
		    EObject newObject = null;
		    //if factory != null
		    if (factory != null)
		    {
		    	//create object
		    	newObject = helper.createObject(factory, typeName);

		    	//if object is not null, handle attributes and things
		    	if (newObject != null)
		    	{
		    		if (disableNotify)
		    			newObject.eSetDeliver(false);

		    		handleObjectAttribs(newObject);
		    	}
		    }
		    
		    //if (!handlingFeature) {
		//    if (!extent.contains(newObject))
	
//		    if (newObject.getClass().getName().equals("InfixExpression") && objects.peekEObject().getClass().getName().equals("IfStatement") )
//	    		System.out.println();
		    if (!handlingFeature || !extent.contains(objects.peekEObject())) {
		    	extent.add(newObject);
//		    	if (extent.size() > 71)
//		    		System.out.print("Hi");
		    }
			//}
		    
		    return newObject;

		}
		else {
			EObject newObject = null;
			EClass class1 = (EClass) factory.getEPackage().getEClassifier(typeName);
			if (cached(class1)) {
				newObject = getCache(class1);
			}
			else {
				if (factory != null) {
					newObject = helper.createObject(factory, typeName);
					insertCache(class1, newObject);	
				}
			}
		    return newObject;
		}
	}
	

		
	
	@Override
	protected void handleObjectAttribs(EObject obj) {
    if (attribs != null)
    {
      InternalEObject internalEObject = (InternalEObject)obj;
      for (int i = 0, size = attribs.getLength(); i < size; ++i)
      {
        String name = attribs.getQName(i);
        if (name.equals(ID_ATTRIB))
        {
          xmlResource.setID(internalEObject, attribs.getValue(i));
        }
        else if (name.equals(hrefAttribute) && (!recordUnknownFeature || types.peek() != UNKNOWN_FEATURE_TYPE || obj.eClass() != anyType))
        {
          handleProxy(internalEObject, attribs.getValue(i));
        }
        else if (isNamespaceAware)
        {
          String namespace = attribs.getURI(i);
          if (!ExtendedMetaData.XSI_URI.equals(namespace) && !notFeatures.contains(name))
          {
            setAttribValue(obj, name, attribs.getValue(i));
          }
        }
        else if (!name.startsWith(XMLResource.XML_NS) && !notFeatures.contains(name))
        {
          setAttribValue(obj, name, attribs.getValue(i));
        }
      }
    }
  }

	@Override
	protected void setAttribValue(EObject object, String name, String value) {
	    int index = name.indexOf(':', 0);

	    // We use null here instead of "" because an attribute without a prefix is considered to have the null target namespace...
	    String prefix = null;
	    String localName = name;
	    if (index != -1)
	    {
	      prefix    = name.substring(0, index);
	      localName = name.substring(index + 1);
	    }
	    EStructuralFeature feature = getFeature(object, prefix, localName, false);
	    if (feature == null)
	    {
	      handleUnknownFeature(prefix, localName, false, object, value);
	    }
	    else
	    {
	      int kind = helper.getFeatureKind(feature);

	      if (kind == XMLHelper.DATATYPE_SINGLE || kind == XMLHelper.DATATYPE_IS_MANY)
	      {
	    	  if (loadAllAttributes) {
	    		  setFeatureValue(object, feature, value, -2);	
	    	  }
	    	  else {
		    	  if (shouldHandleFeature(object, feature)) {
		    		  setFeatureValue(object, feature, value, -2);	
		    	  }
		    	  else {
					if (handlingFeature && shouldHandleFeatureForType(object, feature)) {
						setFeatureValue(object, feature, value, -2);
					}
				}
			}
	        
	      }
	      else
	      {
	    	  if (shouldHandleFeature(object, feature)) {
	  	        	setValueFromId(object, (EReference)feature, value);
			}
	    	  else {
					if (handlingFeature && shouldHandleFeatureForType(object, feature)) {
						
						if (((EReference)feature).isContainment())
							setFeatureValue(object, feature, value, -2);
						else
							setValueFromId(object, (EReference)feature, value);
					}
				}
	      }
	    }
	  }
	
	
	@Override
		public void startElement(String uri, String localName, String name) {

		if (shouldHalt) {
		    elements.push(name);
		}
		else {
			if (objects.size() == 0) {
				super.startElement(uri, localName, name);	
			}
			else {
				EObject peekObject = objects.peekEObject();
				EClass leClass = peekObject.eClass();
				//leClass the class that should proceed and name is the feature
				if (shouldProceed(leClass, name)) {
						super.startElement(uri, localName, name);
				}
				else {
					halt(name);
				}
			}
		}
	}
	
		
	@Override
	public void endElement(String uri, String localName, String name) {
		
		if (shouldHalt && currentElementsSize != -1) {
			if (elements.size() >= currentElementsSize) {
				if (name.equals(currentName) && elements.size() == currentElementsSize) {
					shouldHalt = false;
					elements.pop();	
				}
				else {
					elements.pop();	
				}
			}
			else
			{
				shouldHalt = false;	
			}
		}
		else
		{
			super.endElement(uri, localName, name);	
		}
	}
	
	public void halt(String name)
	{
		currentElementsSize = elements.size();
		currentName = name;
		shouldHalt = true;
	}
		
	public boolean shouldHandleFeature(EObject object, EStructuralFeature feature)
	{
		EClass eClass = object.eClass();
		String packageName = eClass.getEPackage().getName();
		HashMap<EClass, ArrayList<EStructuralFeature>> subMap = actualObjectsAndFeaturesToLoad.get(packageName);
		if (subMap != null) {
			ArrayList<EStructuralFeature> features = subMap.get(eClass);
			if (features != null && (features.contains(feature) || features.contains("*"))) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}
	
	public boolean shouldHandleFeatureForType(EObject object, EStructuralFeature feature)
	{
		EClass eClass = object.eClass();
		String packageName = eClass.getEPackage().getName();
		HashMap<EClass, ArrayList<EStructuralFeature>> subMap = typesToLoad.get(packageName);
		if (subMap != null) {
			ArrayList<EStructuralFeature> features = subMap.get(eClass);
			if (features != null && (features.contains(feature) || features.contains("*"))) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}
	
	
	public boolean shouldCreateAllObjectForClass(EClass eClass)
	{
		String epackage = eClass.getEPackage().getName();
		HashMap<EClass, ArrayList<EStructuralFeature>> subMap = actualObjectsAndFeaturesToLoad.get(epackage);
		if (subMap == null) {
			return false;
		}
		
		if (subMap.keySet().contains(eClass)) {
			return true;
		}
		return false;
	}
	
	public boolean shouldCreateObjectForReference(EClass eClass)
	{
		String epackage = eClass.getEPackage().getName();
		
		HashMap<EClass, ArrayList<EStructuralFeature>> subMap = typesToLoad.get(epackage);
		if (subMap == null) {
			return false;
		}
		
		if (subMap.keySet().contains(eClass)) {
			return true;
		}
		return false;
	}
		
	
	public boolean shouldProceed(EClass eClass, String name)
	{
		//if current class is not null and current class is equal to the class in question
		if (traversal_currentClass != null && traversal_currentClass.getName().equals(eClass.getName())) {
			
			if (traversal_currentFeatures.contains(name)) {
				return true;
			}
			
			if (traversal_currentFeatures.size() == 0) {
				return true;
			}
		}
		else {
			//set the current class to the eclass under question
			traversal_currentClass = eClass;
			//get epackage string
			String epackage = eClass.getEPackage().getName();
			//get classname string
			String className = eClass.getName();
			//get subMap
			HashMap<EClass, ArrayList<EStructuralFeature>> subMap = traversalPlans.get(epackage);
			//if submap is not null
			if (subMap != null) {
				//get the features of the submap
				 ArrayList<EStructuralFeature> features = subMap.get(eClass);
				 //if features is not null
				 if (features != null) {
					//set the current features as features for caching 
					traversal_currentFeatures = features;
					//if features contains the feature name
					if (features.contains(name)) {
						return true;
					}
					//if features size is 0, should return true, too
					if (features.size() == 0) {
						return true;
					}
				}
			 }

		}
		 return false;
		 
	}
	
	@Override
	protected void handleFeature(String prefix, String name) {
		
	    EObject peekObject = objects.peekEObject();
//	    if (peekObject.getClass().getName().equals("IfStatement"))
//	    	System.out.println();
	    // This happens when processing an element with simple content that has elements content even though it shouldn't.
	    //
	    if (peekObject == null)
	    {
	      types.push(ERROR_TYPE);
	      error
	        (new FeatureNotFoundException
	          (name,
	           null,
	           getLocation(),
	           getLineNumber(),
	           getColumnNumber()));
	      return;
	    }
	    
	    
	    EStructuralFeature feature = getFeature(peekObject, prefix, name, true);
	    
	    if (feature != null)
	    {
	    	if (shouldHandleFeature(peekObject, feature) || shouldHandleFeatureForType(peekObject, feature)) {
				handlingFeature = true;
			}
	    	else {
				handlingFeature = false;
			}
	    	
	      int kind = helper.getFeatureKind(feature);
	      if (kind == XMLHelper.DATATYPE_SINGLE || kind == XMLHelper.DATATYPE_IS_MANY)
	      {
	        objects.push(null);
	        mixedTargets.push(null);
	        types.push(feature);
	        if (!isNull())
	        {
	          text = new StringBuffer();
	        }
	      }
	      else if (extendedMetaData != null)
	      {
	        EReference eReference = (EReference)feature;
	        boolean isContainment = eReference.isContainment();
	        if (!isContainment && !eReference.isResolveProxies() && extendedMetaData.getFeatureKind(feature) != ExtendedMetaData.UNSPECIFIED_FEATURE)
	        {
	          isIDREF = true;
	          objects.push(null);
	          mixedTargets.push(null);
	          types.push(feature);
	          text = new StringBuffer();
	        }
	        else
	        {
	          createObject(peekObject, feature);
	          EObject childObject = objects.peekEObject();
	          if (childObject != null)
	          {
	            if (isContainment)
	            {
	              EStructuralFeature simpleFeature = extendedMetaData.getSimpleFeature(childObject.eClass());
	              if (simpleFeature != null)
	              {
	                isSimpleFeature = true;
	                isIDREF = simpleFeature instanceof EReference;
	                objects.push(null);
	                mixedTargets.push(null);
	                types.push(simpleFeature);
	                text = new StringBuffer();
	              }
	            }
	            else if (!childObject.eIsProxy())
	            {
	              text = new StringBuffer();
	            }
	          }
	        }
	      }
	      else
	      {
	    		  createObject(peekObject, feature);
	       
//	        long st = System.nanoTime();
//	        // managing duplication
//	        if (!peekObject.eContents().isEmpty())
//	        	for (EObject o : peekObject.eContents()) {
//	        		if (extent.contains(o))	{
//	        			if (xmlResource.getIDToEObjectMap().containsValue(o)){
//	        				//when a object is deleted from extent, it will be deleted from xmlresource as well!
//	        				// So we add it again by referring to a same object which is in content of another object
//	        				String id = xmlResource.getEObjectToIDMap().get(o);
//	        				extent.removeAll(peekObject.eContents());
//	        				extent.remove(o);
//	        				xmlResource.setID(o, id);	
//	        			//	break;
//	        			}
//	        		}
//	        	}
//	        long en = System.nanoTime();
//			time = time + ((long)((en-st)/ 1000000));
	      }
	    }
	    else
	    {
	      // Try to get a general-content feature.
	      // Use a pattern that's not possible any other way.
	      //
	      if (xmlMap != null && (feature = getFeature(peekObject, null, "", true)) != null)
	      {

	        EFactory eFactory = getFactoryForPrefix(prefix);

	        // This is for the case for a local unqualified element that has been bound.
	        //
	        if (eFactory == null)
	        {
	          eFactory = feature.getEContainingClass().getEPackage().getEFactoryInstance();
	        }

	        EObject newObject = null;
	        if (useNewMethods)
	        {
	          newObject = createObject(eFactory, helper.getType(eFactory, name), false);
	        }
	        else
	        {
	            newObject = createObjectFromFactory(eFactory, name);
	        }
	        newObject = validateCreateObjectFromFactory(eFactory, name, newObject, feature);
	        if (newObject != null)
	        {
	          setFeatureValue(peekObject, feature, newObject);
	        }
	        processObject(newObject);
	      }
	      else
	      {
	        // This handles the case of a substitution group.
	        //
	        if (xmlMap != null)
	        {
	          EFactory eFactory = getFactoryForPrefix(prefix);
	          EObject newObject = createObjectFromFactory(eFactory, name);
	          validateCreateObjectFromFactory(eFactory, name, newObject);
	          if (newObject != null)
	          {
	            for (EReference eReference : peekObject.eClass().getEAllReferences())
	            {
	              if (eReference.getEType().isInstance(newObject))
	              {
	                setFeatureValue(peekObject, eReference, newObject);
	                processObject(newObject);
	                return;
	              }
	            }
	          }
	        }

	        handleUnknownFeature(prefix, name, true, peekObject, null);
	      }
	    }
	  }

	
	
	
	@Override
	protected EObject createDocumentRoot(String prefix, String uri,
			String name, EFactory eFactory, boolean top) {

	    if (extendedMetaData != null && eFactory != null)
	    {
	      EPackage ePackage = eFactory.getEPackage();
	      EClass eClass = null;
	      if (useConfigurationCache)
	      {
	        eClass = ConfigurationCache.INSTANCE.getDocumentRoot(ePackage);
	        if (eClass == null)
	        {
	          eClass = extendedMetaData.getDocumentRoot(ePackage);
	          ConfigurationCache.INSTANCE.putDocumentRoot(ePackage, eClass);
	        }
	      }
	      else
	      {
	        eClass = extendedMetaData.getDocumentRoot(ePackage);
	      }
	      if (eClass != null)
	      {
	        // EATM Kind of hacky.
	        String typeName = extendedMetaData.getName(eClass);
	        @SuppressWarnings("deprecation") EObject newObject =
	          useNewMethods ?
	            createObject(eFactory, eClass, true) :
	              helper.createObject(eFactory, typeName);
	        validateCreateObjectFromFactory(eFactory, typeName, newObject);
	        if (top)
	        {
	          if (suppressDocumentRoot)
	          {
	            // Set up a deferred extent so the document root we create definitely will not be added to the resource.
	            //
	            List<EObject> oldDeferredExtent = deferredExtent;
	            try
	            {
	              deferredExtent = new ArrayList<EObject>();
	              processTopObject(newObject);
	            }
	            finally
	            {
	              deferredExtent = oldDeferredExtent;
	            }
	            handleFeature(prefix, name);

	            // Remove the document root's information from the top of the stacks.
	            //
	            objects.remove(0);
	            mixedTargets.remove(0);
	            types.remove(0);

	            // Process the new root object if any.
	            //
	            EObject peekObject = objects.peek();
	            if (peekObject == null)
	            {
	              // There's an EObject on the stack already.
	              //
	              if (objects.size() > 1)
	              {
	                // Excise the new root from the document root.
	                //
	                EcoreUtil.remove(peekObject = objects.get(0));
	              }
	              else
	              {
	                // If there is no root object, we're dealing with an EAttribute feature instead of an EReference feature.
	                // So create an instance of simple any type and prepare it to handle the text content.
	                //
	                SimpleAnyType simpleAnyType = (SimpleAnyType)EcoreUtil.create(anySimpleType);
	                simpleAnyType.setInstanceType(((EAttribute)types.peek()).getEAttributeType());
	                objects.set(0, simpleAnyType);
	                types.set(0, XMLTypePackage.Literals.SIMPLE_ANY_TYPE__RAW_VALUE);
	                mixedTargets.set(0, simpleAnyType.getMixed());
	                peekObject = simpleAnyType;
	              }
	            }
	            else
	            {
	              // Excise the new root from the document root.
	              //
	              EcoreUtil.remove(peekObject);
	            }
	            // Do the extent processing that should have been done for the root but was actualljy done for the document root.
	            //
	            if (deferredExtent != null)
	            {
	              deferredExtent.add(peekObject);
	            }
	            else
	            {
	            	//comment
	              //extent.addUnique(peekObject);
	            }

	            // The new root object is the actual new object since all sign of the document root will now have disappeared.
	            //
	            newObject = peekObject;
	          }
	          else
	          {
	            processTopObject(newObject);
	            handleFeature(prefix, name);
	          }
	        }
	        return newObject;
	      }
	    }
	    return null;
	  
	}
	
	  protected void setFeatureValue(EObject object, EStructuralFeature feature, Object value)
	  {
		
		  if (shouldHandleFeature(object, feature)) {
			  setFeatureValue(object, feature, value, -1);	
		}
		  else {
//			if (handlingFeature && shouldHandleFeatureForType(object, feature.getName())) {
//				setFeatureValue(object, feature, value, -1);
//			}
			  if (shouldHandleFeatureForType(object, feature)) {
					setFeatureValue(object, feature, value, -1);
				}
		}
	  }

	
	@Override
	protected void processTopObject(EObject object) {
	    if (object != null)
	    {
	      if (deferredExtent != null)
	      {
	        deferredExtent.add(object);
	      }
	      else
	      {
//	    	  extentIndexToDelete.add(extent.size());
//	    	  extent.addUnique(object);
	      }

	      if (extendedMetaData != null && !mixedTargets.isEmpty())
	      {
	        FeatureMap featureMap = mixedTargets.pop();
	        EStructuralFeature target = extendedMetaData.getMixedFeature(object.eClass());
	        if (target != null)
	        {
	          FeatureMap otherFeatureMap = (FeatureMap)object.eGet(target);
	          for (FeatureMap.Entry entry : new ArrayList<FeatureMap.Entry>(featureMap))
	          {
	            // Ignore a whitespace only text entry at the beginning.
	            //
	            if (entry.getEStructuralFeature() !=  XMLTypePackage.Literals.XML_TYPE_DOCUMENT_ROOT__TEXT ||
	                  !"".equals(XMLTypeUtil.normalize(entry.getValue().toString(), true)))
	            {
	              otherFeatureMap.add(entry.getEStructuralFeature(), entry.getValue());
	            }
	          }
	        }
	        text = null;
	      }
	    }

		processObject(object);
	}
}
