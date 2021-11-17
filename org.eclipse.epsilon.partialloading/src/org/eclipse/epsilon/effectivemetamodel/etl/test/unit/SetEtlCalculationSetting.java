package org.eclipse.epsilon.effectivemetamodel.etl.test.unit;

import java.io.File;
import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodel;
import org.eclipse.epsilon.effectivemetamodel.SubModelFactory;
import org.eclipse.epsilon.effectivemetamodel.extraction.EtlEffectiveMetamodelComputationVisitor;
import org.eclipse.epsilon.etl.EtlModule;
import org.eclipse.epsilon.etl.staticanalyser.EtlStaticAnalyser;

public class SetEtlCalculationSetting {

	public static EffectiveMetamodel calculation(String file, String path) {

		EffectiveMetamodel efModel = null;
		EtlModule module = new EtlModule();
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
		
			EtlStaticAnalyser staticAnalyser = new EtlStaticAnalyser();
			staticAnalyser.getContext().setModelFactory(new SubModelFactory());
			staticAnalyser.validate(module);
			efModel = new EtlEffectiveMetamodelComputationVisitor().setExtractor(module,staticAnalyser);
			return efModel;
	}
}

