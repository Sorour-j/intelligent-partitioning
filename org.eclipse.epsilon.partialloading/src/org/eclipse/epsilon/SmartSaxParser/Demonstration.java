package org.eclipse.epsilon.SmartSaxParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
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
import org.eclipse.epsilon.effectivemetamodel.XMIN;
import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodelExtraction;
import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodelExtractionforEVL;
import org.eclipse.epsilon.eol.EolModule;

public class Demonstration {

	// public static ArrayList<EffectiveMetamodel> effectiveMetamodels = new
	// ArrayList<EffectiveMetamodel>();
	public static XMIN effectiveMetamodel;
	protected static String model = null;

	// "src/org/eclipse/epsilon/TestUnit/Parser/flowchart2.xmi";
	public Demonstration(String model) {
		this.model = model;
	}

//	public void generateEffectiveMetamodel(EolModule module)
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String metamodel = "src/org/eclipse/epsilon/TestUnit/Parser/java.ecore";
		String evlFile = "src/org/eclipse/epsilon/TestUnit/Parser/java_1Constraint.evl";
		EffectiveMetamodelExtractionforEVL ef = new EffectiveMetamodelExtractionforEVL(metamodel, evlFile);
		// effectiveMetamodel = ef.getEffectiveMetamodel();
		effectiveMetamodel = ef.getEffectiveMetamodel();
		model = "src/org/eclipse/epsilon/TestUnit/Parser/eclipseModel-0.1.xmi";
//		try {
//			demo();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	public EList<EObject> demo() throws IOException {

		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl() {
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
		Map<Object, Object> defaultLoadOptions = resourceSet.getLoadOptions();
		defaultLoadOptions.put(XMLResource.OPTION_RECORD_UNKNOWN_FEATURE, Boolean.TRUE);

		defaultLoadOptions.put(XMLResource.OPTION_RECORD_ANY_TYPE_NAMESPACE_DECLARATIONS, Boolean.TRUE);

		XMIResource inputResource = (XMIResource) resourceSet
				.getResource(URI.createFileURI(new File(model).getAbsolutePath()), true);
		try {
			/*-Loading model from xmi file */
			inputResource.load(defaultLoadOptions);
			Object modelRoot = inputResource.getContents().get(0);

		} catch (IOException e) {
			e.printStackTrace();
		}
		/*-Saving model back to the same xmi file */

		try {
			inputResource.save(defaultLoadOptions);
		} catch (IOException e) {
			e.printStackTrace();
		}

	//	 ResourceSet resourceSet = new ResourceSetImpl();
		// "src/org/eclipse/epsilon/TestUnit/Parser/flowchart.ecore";
		String path = effectiveMetamodel.getPath();
		ResourceSet ecoreResourceSet = new ResourceSetImpl();
		ecoreResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		Resource ecoreResource = ecoreResourceSet.createResource(URI.createFileURI(new File(path).getAbsolutePath()));

		ecoreResource.load(null);
		for (EObject o : ecoreResource.getContents()) {
			EPackage ePackage = (EPackage) o;
			resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
		}

		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new SmartSAXResourceFactory());

		Resource resource = resourceSet.createResource(URI.createFileURI(new File(model).getAbsolutePath()));

		EffectiveMetamodelReconciler effectiveMetamodelReconciler = new EffectiveMetamodelReconciler();
		effectiveMetamodelReconciler.addPackages(resourceSet.getPackageRegistry().values());
		effectiveMetamodelReconciler.addEffectiveMetamodel(effectiveMetamodel);
		effectiveMetamodelReconciler.reconcile();

		Map<String, Object> loadOptions = new HashMap<String, Object>();
		loadOptions.put(SmartSAXXMIResource.OPTION_EFFECTIVE_METAMODEL_RECONCILER, effectiveMetamodelReconciler);
		loadOptions.put(SmartSAXXMIResource.OPTION_LOAD_ALL_ATTRIBUTES, false);
		loadOptions.put(SmartSAXXMIResource.OPTION_RECONCILE_EFFECTIVE_METAMODELS, true);

		long startTime = System.nanoTime();
		resource.load(loadOptions);
		long endTime = System.nanoTime();

		long duration = (endTime - startTime); // divide by 1000000 to get milliseconds.
		System.out.println("**** Time ****");
		System.out.println("**** Loaded Objects ****");
		for (EObject o : resource.getContents()) {
			System.out.println(o);
		}

		System.out.println(resource.getContents().size());

		return resource.getContents();
	}

	public void setModel(String model) {
		this.model = model;
	}

	public void setEfMetamodel(XMIN effectivemetamodel) {
		this.effectiveMetamodel = effectivemetamodel;
	}
//	public static void main(String[] args) throws Exception {
//
//		Demonstration.demo_1();		
//	}
}
