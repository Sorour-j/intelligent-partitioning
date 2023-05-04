package org.eclipse.epsilon.xmin.partitioning;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

public class XMIIDGenerator {
	public static void main(String[] args) throws Exception {
		String metamodel = "/Users/sorourjahanbin/git/Intelligent-partitioning/org.eclipse.epsilon.partialloading/model/componentLanguage.ecore";
		String model = "/Users/sorourjahanbin/git/Intelligent-partitioning/org.eclipse.epsilon.partialloading/model/component-4.2M.xmi";
		ResourceSet resourceSet = new ResourceSetImpl();
		ResourceSet ecoreResourceSet = new ResourceSetImpl();
		ecoreResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		Resource ecoreResource = ecoreResourceSet.
				createResource(URI.createFileURI(new File(metamodel).getAbsolutePath()));
		try {
			ecoreResource.load(null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (EObject o : ecoreResource.getContents()) {
			EPackage ePackage = (EPackage) o;
			resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
			EPackage.Registry.INSTANCE.put(ePackage.getNsURI(), ePackage);
		}
	//ResourceSet resourceSet = new ResourceSetImpl();
	resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl(){
		@Override
    	public Resource createResource(URI uri) {
    		return new XMIResourceImpl(uri){
    			@Override
    			protected boolean useUUIDs() {
    				return true;
    			}	    			
       		};
    	}
	});
Map<Object, Object> defaultLoadOptions = resourceSet.getLoadOptions();		
defaultLoadOptions.put(XMLResource.OPTION_RECORD_UNKNOWN_FEATURE,Boolean.TRUE);
 
defaultLoadOptions.put(XMLResource.OPTION_RECORD_ANY_TYPE_NAMESPACE_DECLARATIONS, Boolean.TRUE);
    
XMIResource inputResource = (XMIResource) resourceSet.getResource(URI.createFileURI(model),true);
try {
/*-Loading model from xmi file */
inputResource.load(defaultLoadOptions);
                       
	} catch (IOException e) {
		e.printStackTrace();
	}
/*-Saving model back to the same xmi file */
    try {
    	inputResource.save(defaultLoadOptions);
	} catch (IOException e) {
		e.printStackTrace();
	}
}
}
