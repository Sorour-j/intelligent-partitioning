package org.eclipse.epsilon.effectivemetamodel.example.Standalone;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

public class ProduceXmiID {

	public static ResourceSet ecoreResourceSet = new ResourceSetImpl();
	public static ResourceSet xmiResourceSet = new ResourceSetImpl();
	public static XMIResource resource;
	public static void main(String[] args) throws Exception {
		 
	ecoreResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
	Resource ecoreResource = ecoreResourceSet
			.createResource(URI.createFileURI(new File("src/org/eclipse/epsilon/effectivemetamodel/example/Standalone/psl.ecore").getAbsolutePath()));
	try {
		ecoreResource.load(null);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	xmiResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl() {
		@Override
		public XMIResourceImpl createResource(URI uri) {
			return new XMIResourceImpl(uri) {
				@Override
				protected boolean useUUIDs() {
					return true;
				}
			};
		}
	});
	
	//xmiResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
	for (EObject o : ecoreResource.getContents()) {
		EPackage ePackage = (EPackage) o;
		xmiResourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
	}
	System.out.println("Registeration Done!......");

	resource = (XMIResource) xmiResourceSet.createResource(URI.createFileURI("src/org/eclipse/epsilon/effectivemetamodel/example/Standalone/Project.xmi"));
	resource.load(null);
	
	
//	resourceWithIDs.getContents().addAll(EcoreUtil.copyAll(resource.getContents()));
	
	final Map<Object, Object> saveOptions = resource.getDefaultSaveOptions();
	saveOptions.put(XMIResource.OPTION_SCHEMA_LOCATION,Boolean.TRUE);
	saveOptions.put(XMIResource.OPTION_ENCODING,"UTF-8");
	saveOptions.put(XMIResource.OPTION_USE_XMI_TYPE, Boolean.TRUE);
	saveOptions.put(XMIResource.OPTION_SAVE_TYPE_INFORMATION,Boolean.TRUE);
	
	//resource.save(System.out,saveOptions);
	resource.save(saveOptions);
	
}
}
