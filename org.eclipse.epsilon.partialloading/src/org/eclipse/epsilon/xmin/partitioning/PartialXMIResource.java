package org.eclipse.epsilon.xmin.partitioning;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLLoad;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.epsilon.emc.emf.xmi.PartialXMIHelper;
import org.eclipse.epsilon.emc.emf.xmi.PartialXMILoadConfiguration;
import org.eclipse.epsilon.emc.emf.xmi.PartialXMIResourceFactory;

public class PartialXMIResource extends XMIResourceImpl {
	
	public static final String OPTION_PARTIAL_LOADING_CONFIGURATION = "PARTIAL_LOADING_CONFIGURATION";
	
	protected PartialXMILoadConfiguration configuration;
	
	public PartialXMIResource(URI uri) {
		super(uri);
	}

	public static void main(String[] args) throws Exception {
		
		String model = "/Users/sorourjahanbin/git/Intelligent-partitioning/org.eclipse.epsilon.partialloading/model/result0_5.xmi";
		String metamodel = "/Users/sorourjahanbin/git/Intelligent-partitioning/org.eclipse.epsilon.partialloading/model/CCL.ecore";//args[1];
		EPackage ePackage = null;
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
			ePackage = (EPackage) o;
			resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
			EPackage.Registry.INSTANCE.put(ePackage.getNsURI(), ePackage);
		}
		

		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new PartialXMIResourceFactory());
		XMIResource resource = (XMIResource) resourceSet.createResource(URI.createFileURI(model));
		
		PartialXMILoadConfiguration configuration = new PartialXMILoadConfiguration();
		configuration.addAllOfKind((EClass)(ePackage.getEClassifier("Connector")));
		configuration.addAllOfKind((EClass)(ePackage.getEClassifier("OutPort")));
		configuration.addFeature((EClass)(ePackage.getEClassifier("Connector")), ((EClass)(ePackage.getEClassifier("Connector"))).getEStructuralFeature("source"));
		configuration.addFeature((EClass)(ePackage.getEClassifier("OutPort")), ((EClass)(ePackage.getEClassifier("OutPort"))).getEStructuralFeature("outgoing"));
		
		HashMap<String, Object> loadOptions = new HashMap<>();
		loadOptions.put(OPTION_PARTIAL_LOADING_CONFIGURATION, configuration);
		resource.load(loadOptions);
		
		resource.save(System.out, null);
	}
	
	@Override
	protected XMLLoad createXMLLoad(Map<?,?> options) {
		return new PartialXMILoadImpl(new PartialXMIHelper(this, options));
	};
	
	@Override
	public void load(Map<?, ?> options) throws IOException {
		PartialXMILoadConfiguration configuration = null; 
		if (options!=null) {
			configuration = (PartialXMILoadConfiguration) options.get(OPTION_PARTIAL_LOADING_CONFIGURATION);
		}
		super.load(options);
		if (configuration != null) EcoreUtil.deleteAll(configuration.getPlaceholders(), true);
	}
}
