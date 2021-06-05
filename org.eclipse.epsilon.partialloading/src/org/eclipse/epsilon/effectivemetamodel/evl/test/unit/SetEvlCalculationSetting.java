package org.eclipse.epsilon.effectivemetamodel.evl.test.unit;

import java.io.File;
import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.effectivemetamodel.SubModelFactory;
import org.eclipse.epsilon.effectivemetamodel.XMIN;
import org.eclipse.epsilon.effectivemetamodel.extraction.EvlEffectiveMetamodelComputationVisitor;
import org.eclipse.epsilon.evl.EvlModule;
import org.eclipse.epsilon.evl.staticanalyser.EvlStaticAnalyser;

public class SetEvlCalculationSetting {

	public static XMIN calculation(String file, String path) {

		XMIN efModel = null;
		EvlModule module = new EvlModule();
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
		
		
		EvlStaticAnalyser staticAnalyser = new EvlStaticAnalyser();
			staticAnalyser.getContext().setModelFactory(new SubModelFactory());
			staticAnalyser.validate(module);
			efModel = new EvlEffectiveMetamodelComputationVisitor().setExtractor(module,staticAnalyser);
			return efModel;
		

	}
}

