package org.eclipse.epsilon.TestUnit.Parser;

import java.io.File;
import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.SmartSaxParser.Demonstration;
import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodelExtraction;
import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodelExtractor;
import org.eclipse.epsilon.effectivemetamodel.SubModelFactory;
import org.eclipse.epsilon.effectivemetamodel.XMIN;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.eol.staticanalyser.EolStaticAnalyser;
import org.eclipse.epsilon.loading.EffectiveMetamodelInjector;

public class main {

	public static XMIN calculation(String file) {

		String path = "src/org/eclipse/epsilon/TestUnit/Parser/flowchart.ecore";
		String model = "src/org/eclipse/epsilon/TestUnit/Parser/flowchart2.xmi";
//		String file = "src/org/eclipse/epsilon/TestUnit/Parser/AllInstancesTest.eol";
		
		
		EolModule module = new EolModule();
		ResourceSet resourceSet = new ResourceSetImpl();
		
		ResourceSet ecoreResourceSet = new ResourceSetImpl();
		ecoreResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		Resource ecoreResource = ecoreResourceSet.
				createResource(URI.createFileURI(new File(path).getAbsolutePath()));
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
//		System.out.println(EPackage.Registry.INSTANCE.getEPackage(((EPackage)ecoreResource.getContents().get(0)).getNsURI()));
//		
		try {
			
			module.parse(new File(file));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	//	module.getCompilationContext().setModelFactory(new SubModelFactory());
	//	new EolStaticAnalyser().validate(module);
		
	//	EffectiveMetamodelExtraction ef = new EffectiveMetamodelExtraction(path, file);
		XMIN ef = new EffectiveMetamodelExtractor().geteffectiveMetamodel(module);
		
	//	Demonstration demo = new Demonstration(model); 
	//	demo.setEfMetamodel(ef.getEffectiveMetamodel(module));
	//	demo.setEfMetamodel(ef);
	//	demo.demo();
		return ef;
	}
}
//flowchart, flowchart2
//AllInstancesTest  AllofTypeforSubClassTest  AllofTypeforSuperClassTest  CollectionofModelElementTest
//NonContainmentRefrenceTest  SubClassTest SuperClassTest  UserDefinedOperationTest


//componentLanguage.ecore  ComponentDiagram.xmi

//ContainmentRefrenceTest EnumerationTypeTest
