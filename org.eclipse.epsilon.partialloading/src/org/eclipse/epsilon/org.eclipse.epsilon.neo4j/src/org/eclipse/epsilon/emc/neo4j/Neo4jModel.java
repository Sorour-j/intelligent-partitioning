package org.eclipse.epsilon.emc.neo4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodel;
import org.eclipse.epsilon.effectivemetamodel.EffectiveType;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.models.IRelativePathResolver;
import org.eclipse.epsilon.smartsaxparser.EffectiveMetamodelReconciler;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;

public class Neo4jModel extends EmfModel implements AutoCloseable {

	public static final String PROPERTY_URI = "uri";
	public static final String PROPERTY_USERNAME = "username";
	public static final String PROPERTY_PASSWORD = "password";
	public static final String PROPERTY_DATABASE = "database";
	public static final String PROPERTY_DATABASEPATH = "databasepath";

	protected String uri;
	protected String username;
	protected String password;
	protected String database;
	protected String databasePath;
	protected EffectiveMetamodel effectiveMetamodel = null;

	protected Collection<Record> records = new ArrayList<Record>();
	HashMap<String, Collection<Record>> queryKeys    = new HashMap<String, Collection<Record>>();

	private Driver driver;
	EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
	EFactory factory = EcoreFactory.eINSTANCE.createEFactory();
	
	ResourceSet resourceSet = new ResourceSetImpl();
	Resource resource = new XMIResourceImpl();

	public EffectiveMetamodelReconciler effectiveMetamodelReconciler = new EffectiveMetamodelReconciler();

	public Neo4jModel() {

		propertyGetter = new ResultPropertyGetter(this);
	}

	@Override
	public void load(StringProperties properties, IRelativePathResolver resolver) throws EolModelLoadingException {
		super.load(properties, resolver);
		this.uri = properties.getProperty(PROPERTY_URI);
		this.username = properties.getProperty(PROPERTY_USERNAME);
		this.password = properties.getProperty(PROPERTY_PASSWORD);
		this.database = properties.getProperty(PROPERTY_DATABASE);
		this.databasePath = properties.getProperty(PROPERTY_DATABASEPATH);
		load();
	}

	@Override
	public void load() throws EolModelLoadingException {

		if (effectiveMetamodel == null)
			return;

		ConnectionSetting connection = new ConnectionSetting(uri, username, password, databasePath, 7687);
		driver = connection.getDriver();
//		pkg.setNsURI("javaMM");
//		pkg.setName("javaMM");
//		pkg.setEFactoryInstance(factory);
//		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
//		// actual objects to load from reconciler would be a better idea, Maybe!
//		resourceSet.getPackageRegistry().put(pkg.getNsURI(), pkg);
//		resource = resourceSet.createResource(URI.createFileURI("javaMM"));
		createResource();
		String query = generateQuery(effectiveMetamodel);

		try (Session session = driver.session(SessionConfig.forDatabase(database))) {

			Result result = session.run(query);
			while (result.hasNext()) {
				Record record = result.next();
				createObjectFromRecord(record);
			}
		}
	//	System.out.println(resource.getContents().size());
		try {
			resource.save(null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.setResource(resource);
		connection.registerShutdownHook();
	}

	void createObjectFromRecord(Record record) {

//		String label = record.get("label").toString();
//		EClass eClass = null;
//		label = label.replaceAll("\\[", "");
//		label = label.replaceAll("\\]", "");
//		label = label.replaceAll("\"", "");
//		
//		for (EClassifier c : pkg.getEClassifiers()) {
//			if (c.getName().equals(label)) {
//				eClass = (EClass) pkg.getEClassifier(label);
//			}
//		}
//		if (eClass == null) {
//			eClass = EcoreFactory.eINSTANCE.createEClass();
//			eClass.setName(label);
//			pkg.getEClassifiers().add(eClass);
//			
//			for (String s : record.keys()) {
//				if (!s.contains("label")) {
//					EAttribute attr = EcoreFactory.eINSTANCE.createEAttribute();
//					attr.setName(s.replaceAll(label.toLowerCase() + ".", ""));
//					attr.setEType(EcorePackage.Literals.ESTRING);
//					eClass.getEStructuralFeatures().add(attr);
//				}
//			}
//		}        
		for (EClassifier c : pkg.getEClassifiers()) {
				EObject object = factory.create((EClass)c);
			for (EStructuralFeature f : ((EClass)c).getEStructuralFeatures()) 
					object.eSet(f,record.get(c.getName().toLowerCase()+"."+f.getName()).toString());
			resource.getContents().add(object);
		}
		
//		EObject cls = factory.create(eClass);
//
//		for (String s : record.keys()) {
//			if (!s.equals("label"+eClass.getName())) {
//				s = s.replaceAll(label.toLowerCase() + ".", "");
//				String value = record.values().get(0).toString().replaceAll("\"", "");
//				cls.eSet(eClass.getEStructuralFeature(s), value);
//				resource.getContents().add(cls);
//			}
//		}
	}

	@Override
	public String toString() {
		return "Neo4j Model [name=" + getName() + "]";
	}

	@Override
	public void close() {
		driver.close();
	}

	public void setEffectiveMteamodel(EffectiveMetamodel efMetamodel) {
		this.effectiveMetamodel = efMetamodel;
		effectiveMetamodel.setName(this.name);
	}

	public EffectiveMetamodel getEffectiveMteamodel() {
		return this.effectiveMetamodel;
	}

	public String generateQuery(EffectiveMetamodel efMetamodel) {

		String query = "";
		String source = null;
		String matchString = null;
		ArrayList<String> matches = new ArrayList<String>();
		ArrayList<String> returns = new ArrayList<String>();

		EList<EClassifier> classes = pkg.getEClassifiers();
		//ArrayList<EffectiveType> classes = efMetamodel.getAllOfEffectiveTypes();
		//ArrayList<EClass> visitedClasses = new ArrayList<EClass>();
		
		for (EClassifier cls : classes) {

			//returnString = "";
			EClass c = (EClass) cls;
			source = c.getName().toLowerCase();
			//visitedClasses.add(c);
			matchString = "(" + source + ":" + c.getName() + "),";
			//matches.add("(" + source + ":" + c.getName() + ")");

			for (EStructuralFeature f : c.getEStructuralFeatures()) {
				if (f instanceof EReference) {
					//String target = f.getEType().getName();
					matchString += "(" + source + ")-[r:" + f.getName() + "]->(" + f.getEType().getName().toLowerCase() + ":"
							+ f.getEType().getName() + "),";
					//matches.add("(" + source + ")-[r:" + f.getName() + "]->(" + f.getEType().getName().toLowerCase() + ":"
					//+ f.getEType().getName() + "),");
					
				} else if (f instanceof EAttribute) {
					//returnString += source + "." + f.getName() + " As " + source + f.getName() + ",";
					returns.add(source + "." + f.getName());
				}
			}
			StringBuilder sb = new StringBuilder(matchString);
			sb.deleteCharAt(matchString.length() - 1);
			matchString = sb.toString();
			matches.add(matchString);
			//returns.add("labels(" + source+")");
		}

		
		for (String st : matches) {
			query += "MATCH ";
			query += st + " ";
		}
//		StringBuilder sb = new StringBuilder(query);
//		sb.deleteCharAt(query.length() - 1);
//		query = sb.toString();

		query += "RETURN ";
		for (String st : returns)
			query += st + ",";

//		query += "labels(model) AS label";
		StringBuilder sb = new StringBuilder(query);
		//sb = new StringBuilder(query);
		sb.deleteCharAt(query.length() - 1);
		query = sb.toString();

		System.out.println(query);

		return query;
	}
	
	public void createResource(){
		//EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
		//factory = EcoreFactory.eINSTANCE.createEFactory();

		pkg.setNsURI("javaMM");
		pkg.setName("javaMM");
		pkg.setEFactoryInstance(factory);
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		// actual objects to load from reconciler would be a better idea, Maybe!
		resourceSet.getPackageRegistry().put(pkg.getNsURI(), pkg);
		resource = resourceSet.createResource(URI.createFileURI("javaMM"));
		
		Set<EClass> classes = effectiveMetamodelReconciler.getActualObjectsToLoad().get(name).keySet();
		for (EClass c : classes) {
			EClass eClass = EcoreFactory.eINSTANCE.createEClass();
			eClass.setName(c.getName());
			pkg.getEClassifiers().add(eClass);
		//}
		for (EStructuralFeature feature :effectiveMetamodelReconciler.getActualObjectsToLoad().get(name).get(c)) {
//		EAttribute attr = EcoreFactory.eINSTANCE.createEAttribute();
//		attr.setName(s.replaceAll(label.toLowerCase() + ".", ""));
//		attr.setEType(EcorePackage.Literals.ESTRING);
		eClass.getEStructuralFeatures().add(feature);
	}
		}
	}
}
