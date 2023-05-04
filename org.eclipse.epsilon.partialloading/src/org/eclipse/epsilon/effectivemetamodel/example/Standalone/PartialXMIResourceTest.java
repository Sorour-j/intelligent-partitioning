package org.eclipse.epsilon.effectivemetamodel.example.Standalone;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
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
	public static String metamodel ="src/org/eclipse/epsilon/effectivemetamodel/example/Standalone/java2.ecore";
	protected PartialXMILoadConfiguration configuration;
	static String uri = "http://www.eclipse.org/MoDisco/Java/0.2.incubation/java";
	//static String uri = "http://movies/1.0";

	public static void main(String[] args) throws Exception {
		
		RegisterEcore(metamodel);
		xmiResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new PartialXMIResourceFactory());
		resource = (XMIResource) xmiResourceSet.createResource(URI.createFileURI("src/org/eclipse/epsilon/effectivemetamodel/example/Standalone/eclipseModel-1.0.xmi"));

		PartialXMILoadConfiguration configuration = new PartialXMILoadConfiguration();

		EClass eclass1 = (EClass)xmiResourceSet.getPackageRegistry().getEPackage(uri).getEClassifiers().get(60);//add MethodInvocation class
		EStructuralFeature feature1 = eclass1.getEAllReferences().get(3); //add method reference >> containment: false. resolvedType: MathodDeclaration
		configuration.addAllOfKind(eclass1); //MethodInvocation
		configuration.addFeature(eclass1, feature1); //MethodInvocation, method
		
		EClass eclass2 = (EClass)xmiResourceSet.getPackageRegistry().getEPackage(uri).getEClassifiers().get(0);//MathodDeclaration
	//	EClass eclass3 = (EClass)xmiResourceSet.getPackageRegistry().getEPackage(uri).getEClassifiers().get(4);//MathodDeclaration
		
		EStructuralFeature feature2 = eclass2.getEAllAttributes().get(0);
		configuration.addFeature(eclass2, feature2); //MathodDeclaration, name
		
//		EStructuralFeature feature3 = eclass2.getEAllAttributes().get(0);
//		configuration.addFeature(eclass3, feature3); //MathodDeclaration, name
//		
		HashMap<String, Object> loadOptions = new HashMap<>();
		loadOptions.put(OPTION_PARTIAL_LOADING_CONFIGURATION, configuration);
		resource.load(loadOptions);
		
		resource.save(System.out, null);
	}
	
	static void RegisterEcore(String metamodel) {
		
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
