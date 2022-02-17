package org.eclipse.epsilon.effectivemetamodel.example.Standalone;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
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

public class PartialXMIResourceTest {
	
	public static final String OPTION_PARTIAL_LOADING_CONFIGURATION = "PARTIAL_LOADING_CONFIGURATION";
	public static ResourceSet ecoreResourceSet = new ResourceSetImpl();
	public static ResourceSet xmiResourceSet = new ResourceSetImpl();
	public static Resource resource;
	public static String metamodel ="src/org/eclipse/epsilon/effectivemetamodel/example/Standalone/EnergyConsumption.ecore";
	protected PartialXMILoadConfiguration configuration;
	static String uri = "http://www.lowcomote.eu/EnergyProvider";
	
	public static void main(String[] args) throws Exception {
		
		RegisterEcore(metamodel);
		xmiResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new PartialXMIResourceFactory());
		resource = (XMIResource) xmiResourceSet.createResource(URI.createFileURI("src/org/eclipse/epsilon/effectivemetamodel/example/Standalone/LCLModel_40.xmi"));

		PartialXMILoadConfiguration configuration = new PartialXMILoadConfiguration();
		
		EClass eclass1 = (EClass)xmiResourceSet.getPackageRegistry().getEPackage(uri).getEClassifiers().get(3);
		EStructuralFeature feature1 = eclass1.getEAllReferences().get(0);
		configuration.addAllOfKind(eclass1);
		configuration.addFeature(eclass1, feature1);
		
		EClass eclass2 = (EClass)xmiResourceSet.getPackageRegistry().getEPackage(uri).getEClassifiers().get(2);
		EStructuralFeature feature2 = eclass2.getEAllAttributes().get(0);
		configuration.addFeature(eclass2, feature2);
		
		HashMap<String, Object> loadOptions = new HashMap<>();
		loadOptions.put(OPTION_PARTIAL_LOADING_CONFIGURATION, configuration);
		resource.load(loadOptions);
		
		resource.save(System.out, null);
	}
	
	static void RegisterEcore(String metamodel) {
		System.out.println("Registering Ecore:......");
		
		ecoreResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		Resource ecoreResource = ecoreResourceSet
				.createResource(URI.createFileURI(new File(metamodel).getAbsolutePath()));
		try {
			ecoreResource.load(null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		for (EObject o : ecoreResource.getContents()) {
			EPackage ePackage = (EPackage) o;
			xmiResourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
		}
		System.out.println("Registeration Done!......");
	}
}
