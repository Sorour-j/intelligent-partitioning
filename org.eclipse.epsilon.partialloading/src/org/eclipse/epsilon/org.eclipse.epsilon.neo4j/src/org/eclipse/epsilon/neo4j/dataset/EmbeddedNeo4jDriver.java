package org.eclipse.epsilon.neo4j.dataset;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.neo4j.configuration.connectors.BoltConnector;
import org.neo4j.configuration.connectors.HttpConnector;
import org.neo4j.configuration.helpers.SocketAddress;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.Value;

public class EmbeddedNeo4jDriver implements AutoCloseable {

	private final Driver driver;
	private DatabaseManagementService managementService;
	private static final Path databaseDirectory = Paths.get("target/javaDB");
	ResourceSet xmiResourceSet = new ResourceSetImpl();
	ResourceSet ecoreResourceSet = new ResourceSetImpl();
	Resource resource;
	ArrayList<String> className = new ArrayList<String>();
	
    public EmbeddedNeo4jDriver( String uri, String user, String password )
    {
    	System.out.println("Starting database ...");
		 managementService = new DatabaseManagementServiceBuilder(databaseDirectory)
			    .setConfig( BoltConnector.enabled, true )
			    .setConfig( HttpConnector.enabled, true )
			    .setConfig( BoltConnector.listen_address, new SocketAddress( "localhost", 7687 ) )
			    .build();
		 
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }

    public void Read() {
    	 try ( Session session = driver.session(SessionConfig.forDatabase("neo4j")))
	        {
    		// xmiResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new EcoreResourceFactoryImpl());
    			
    		// resource = xmiResourceSet.createResource(URI.createURI("JavaNeo4j"));
    	//	 EFactory factory = EcoreFactory.eINSTANCE.createEFactory();
    		 
    		 
    		 	//Result rs = session.run("MATCH (c:ClassDeclaration)-[:superClass*]->(cls)-[t:type]->(class) return c As source,class As target");
    		 //	Result rs = session.run("MATCH (c:ClassDeclaration)-[:superClass*]->(cls)-[t:type]->(class) return c.name As name, c.proxy As proxy, labels(c) As label, ID(class)");
    		 Result rs = session.run("MATCH (methoddeclaration:MethodDeclaration) WITH methoddeclaration.name As methoddeclarationname, MATCH (model:Model) WITH model.name As modelname return methoddeclarationname,modelname");

    		 	while (rs.hasNext()) {
 				Record record = rs.next();
 				System.out.println(record.keys().get(0));
 				System.out.println(record.values().get(0));
 				System.out.println(record.keys().get(1));
 				System.out.println(record.values().get(1));
 				
 		/*		EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
 				EAttribute attr = EcoreFactory.eINSTANCE.createEAttribute();
 				pkg.setNsURI("Neo4jModel");
 				pkg.setName("javaMM");
 				
 				//eClass.setName(record.get("name").asNode().labels().getClass());
 				
 				for (String s : record.keys()) {
 					EClass eClass = EcoreFactory.eINSTANCE.createEClass();
 					eClass.setName(record.get("label").toString());
 					
 					attr.setName(s);
 					attr.setEType(EcorePackage.Literals.ESTRING);
 					//attr.setDefaultValue(record.values().get(0).toString());
 					eClass.getEStructuralFeatures().add(attr);
 					pkg.getEClassifiers().add(eClass);
 	 				
 	 				pkg.setEFactoryInstance(factory);
 	 				EObject cls = factory.create(eClass);
 	 				cls.eSet(attr,record.values().get(0).toString());
 	 				
 	 				
 				}
 				resource.getContents().add(pkg);*/

 			}
	        	System.out.println("Done!");
	        }
    	 
    	 
//    	
//    	 resource.getContents().add();
    }
    @Override
    public void close() throws Exception
    {
        driver.close();
    }
    
    void shutDown() {
		System.out.println();
		System.out.println("Shutting down database ...");
		// tag::shutdownServer[]
		managementService.shutdown();
		// end::shutdownServer[]
	}

	// tag::shutdownHook[]
	private static void registerShutdownHook(final DatabaseManagementService managementService) {
		// Registers a shutdown hook for the Neo4j instance so that it
		// shuts down nicely when the VM exits (even if you "Ctrl-C" the
		// running application).
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				managementService.shutdown();
			}
		});
	}

    public static void main( String... args ) throws Exception
    {
        try ( EmbeddedNeo4jDriver driverIns = new EmbeddedNeo4jDriver("bolt://localhost:7687", "neo4j", "password" ) )
        {
        	driverIns.Read();
        	driverIns.shutDown();
        }
    }
    
    void createObjectFromRecord(Value record) {
    	
    }
    
    void RegisterEcore(String metamodel) {
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
			for (EClassifier cls : ePackage.getEClassifiers()) {
				className.add(cls.getName());
			}
		}
		System.out.println("Registeration Done!......");
	}
}
