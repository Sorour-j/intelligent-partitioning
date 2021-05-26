package org.eclipse.epsilon.effectivemetamodel.Junittest;

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
import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodelComputationVisitor;
import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodelExtraction;
import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodelExtractor;
import org.eclipse.epsilon.effectivemetamodel.SubModelFactory;
import org.eclipse.epsilon.effectivemetamodel.XMIN;
import org.eclipse.epsilon.effectivemetamodel.extraction.EolEffectiveMetamodelComputationVisitor;
import org.eclipse.epsilon.effectivemetamodel.extraction.EvlEffectiveMetamodelComputationVisitor;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.eol.IEolModule;
import org.eclipse.epsilon.eol.staticanalyser.EolStaticAnalyser;
import org.eclipse.epsilon.evl.EvlModule;
import org.eclipse.epsilon.evl.staticanalyser.EvlStaticAnalyser;
import org.eclipse.epsilon.loading.EffectiveMetamodelInjector;

public class main {

	public static XMIN calculation(String file) {

		XMIN efModel = null;
		String path = "src/org/eclipse/epsilon/TestUnit/Parser/flowchart.ecore";
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
		try {
			module.parse(new File(file));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (module instanceof EvlModule) {
			EvlStaticAnalyser staticAnalyser = new EvlStaticAnalyser();
			staticAnalyser.getContext().setModelFactory(new SubModelFactory());
			staticAnalyser.validate(module);
			efModel = new EvlEffectiveMetamodelComputationVisitor(staticAnalyser).preValidate(module);
			return efModel;
		}
			
		else {
			EolStaticAnalyser staticAnalyser = new EolStaticAnalyser();
			staticAnalyser.getContext().setModelFactory(new SubModelFactory());
			staticAnalyser.validate(module);
			efModel = new EolEffectiveMetamodelComputationVisitor(staticAnalyser).preValidate(module);
			return efModel;
		}
		

	}
}

