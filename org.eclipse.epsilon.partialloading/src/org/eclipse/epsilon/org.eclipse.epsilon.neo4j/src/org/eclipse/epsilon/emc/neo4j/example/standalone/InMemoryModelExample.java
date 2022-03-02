package org.eclipse.epsilon.emc.neo4j.example.standalone;

import java.io.File;
import java.io.IOException;

import javax.naming.spi.Resolver;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodel;
import org.eclipse.epsilon.effectivemetamodel.SubModelFactory;
import org.eclipse.epsilon.effectivemetamodel.XMIN;
import org.eclipse.epsilon.effectivemetamodel.extraction.EolEffectiveMetamodelComputationVisitor;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.emc.neo4j.Neo4jModel;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.eol.staticanalyser.EolStaticAnalyser;

public class InMemoryModelExample {
    
	public static void main(String[] args) throws Exception {
		
		//Register Meta-model
		String metamodel = "src/org/eclipse/epsilon/emc/neo4j/example/standalone/Java.ecore";
		
		ResourceSet resourceSet = new ResourceSetImpl();
		ResourceSet ecoreResourceSet = new ResourceSetImpl();
		ecoreResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		Resource ecoreResource = ecoreResourceSet.
				createResource(URI.createFileURI(new File(metamodel).getAbsolutePath()));
		try {
			ecoreResource.load(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (EObject o : ecoreResource.getContents()) {
			EPackage ePackage = (EPackage) o;
			resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
			EPackage.Registry.INSTANCE.put(ePackage.getNsURI(), ePackage);
		}
		
		Neo4jModel model = new Neo4jModel();
		model.setName("javaMM");
		
		StringProperties modelProperties = new StringProperties();
		modelProperties.setProperty(Neo4jModel.PROPERTY_NAME, "javaMM");
		modelProperties.setProperty(Neo4jModel.PROPERTY_USERNAME, "neo4j");
		modelProperties.setProperty(Neo4jModel.PROPERTY_URI, "bolt://localhost:7687");
		modelProperties.setProperty(Neo4jModel.PROPERTY_PASSWORD,"password");
		modelProperties.setProperty(Neo4jModel.PROPERTY_DATABASE, "neo4j");
		modelProperties.setProperty("type", "Neo4jModel");
		modelProperties.setProperty(Neo4jModel.PROPERTY_DATABASEPATH, "target/JavaDB");
		modelProperties.setProperty(XMIN.PROPERTY_METAMODEL_URI,"http://www.eclipse.org/MoDisco/Java/0.2.incubation/java");
		
		EolModule module = new EolModule();
		module.parse("model javaMM driver Neo4j {\n"
				+ "nsuri = \"http://www.eclipse.org/MoDisco/Java/0.2.incubation/java\" \n"
				+ "};\"Start\".println();\n"
				+ "var m : Model;for (m in Model.all()){\n"
				+ "	m.name.println();\n"
				+ "}");
			//	+ "v.name.println(\"Name: \");}");
		
		model.setMetamodelFileUri(URI.createFileURI(metamodel));
		
		EolStaticAnalyser staticanalyser = new EolStaticAnalyser();
		staticanalyser.getContext().setModelFactory(new SubModelFactory());
		staticanalyser.validate(module);
		
		EffectiveMetamodel efMetamodel = new EffectiveMetamodel();
		efMetamodel = new EolEffectiveMetamodelComputationVisitor().setExtractor(module, staticanalyser);
		model.setEffectiveMteamodel(efMetamodel);
		model.effectiveMetamodelReconciler.addPackages(resourceSet.getPackageRegistry().values());
		model.effectiveMetamodelReconciler.addEffectiveMetamodel(efMetamodel);
		model.effectiveMetamodelReconciler.reconcile();

		model.load();

		Resource resource = model.getResource();
		InMemoryEmfModel emfModel = new InMemoryEmfModel(resource);
		emfModel.setName("M");
		
		module.getContext().getModelRepository().addModel(emfModel);
		module.execute();
		resource.save(System.out, null);
	}
}
