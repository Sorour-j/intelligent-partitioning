package org.eclipse.epsilon.effectivemetamodel.neoemf;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import fr.inria.atlanmod.neoemf.config.BaseConfig;
import fr.inria.atlanmod.neoemf.config.ImmutableConfig;
import fr.inria.atlanmod.neoemf.data.blueprints.neo4j.config.BlueprintsNeo4jConfig;
import fr.inria.atlanmod.neoemf.data.blueprints.util.BlueprintsUriFactory;
import fr.inria.atlanmod.neoemf.data.mapdb.util.MapDbUriFactory;


public class Main {
    /*
     * Copyright (c) 2013 Atlanmod.
     *
     * All rights reserved. This program and the accompanying materials are made
     * available under the terms of the Eclipse Public License v2.0 which accompanies
     * this distribution, and is available at https://www.eclipse.org/legal/epl-2.0/
     */

    /**
     * Code from the online NeoEMF tutorial.
     * <p>
     * This class contains the tutorial code and additional examples showing how to instantiate {@link PersistentResource}s
     * relying on Neo4j, MapDB, and HBase.
     *
     * <b>Note:</b> HBase resource creation is presented in this file but not used to perform read/write operations, because
     * HBase needs to be installed separately and started to store a model. To enable HBase storage see the HBase
     * Configuration page on the wiki.
     */

        /**
         * Creates a new {@link PersistentResource} relying on a Blueprints datastore on top of a Neo4j database to perform
         * modeling operations.
         *
         * @return the created resource
         */
	public static Resource createBlueprintsResource() throws IOException {
        Resource resource = new ResourceSetImpl().createResource(new BlueprintsUriFactory().createLocalUri("databases/myGraph.graphdb"));

        /*
         * Specify that Neo4j is used as the underlying blueprints backend.
         * Note using the BlueprintsNeo4jOptions class to create the option map automatically sets Neo4j as the graph
         * backend.
         */
        resource.save(new BlueprintsNeo4jConfig().toMap());
       
        return resource;
    }

        /**
         * Creates a new {@link PersistentResource} using the MapDB datastore to perform modeling operations.
         *
         * @return the created resource
         */
        public static Resource createMapDbResource() {

            return new ResourceSetImpl().createResource(new MapDbUriFactory().createLocalUri("databases/myGraph.mapdb"));
        }

        /**
         * Creates a new {@link PersistentResource} using the HBase datastore connected to a HBase server running on {@code
         * localhost:2181} to perfrom modeling operations.
         *
         * @return the created resource
         */
//        public static Resource createHBaseResource() {
//            return new ResourceSetImpl().createResource(new HBaseUriFactory().createRemoteUri("localhost", 2181, "myModel.hbase"));
//        }

        /**
         * Reads the content of the provided {@code resource} and print it in the console.
         *
         * @param resource the resource to read
         *
         * @throws IOException if an error occurs when loading the resource
         */
        public static void read(Resource resource) throws IOException {
            resource.load(new BaseConfig().toMap());
            Graph graph = (Graph) resource.getContents().get(0);
            for (Edge each : graph.getEdges()) {
                System.out.println(each.getFrom().getLabel() + "--->" + each.getTo().getLabel());
            }
        }

    public static void readVertices(Resource resource) throws IOException {
    	
        resource.load(new BlueprintsNeo4jConfig().toMap());
        Graph graph = (Graph) resource.getContents().get(0);
        for (Vertice v : graph.getVertices()) {
            System.out.println(v.getLabel());
            System.out.println(v.getName());
        }
    }


        /**
         * Adds elements to the provided {@code resource} with new elements representing a basic graph.
         *
         * @param resource the resource to add the element to
         *
         * @throws IOException if an error occurs when saving the resource
         */
        public static void write(Resource resource) throws IOException {
           
        	
        	GraphFactory factory = GraphFactory.eINSTANCE;
            Graph graph = factory.createGraph();

            for (int i = 0; i < 10; i++) {
                Vertice v1 = factory.createVertice();
                v1.setLabel("Vertice " + i + "a");
                v1.setName("FirstName " + i);
                Vertice v2 = factory.createVertice();
                v2.setLabel("Vertice " + i + "b");
                v2.setName("LastName " + i);
               // System.out.println(v2.getLabel() + "//" + v2.getName());
                Edge e = factory.createEdge();
                e.setFrom(v1);
                e.setTo(v2);

                graph.getEdges().add(e);
                graph.getVertices().add(v1);
                graph.getVertices().add(v2);
            }

            resource.getContents().add(graph);
            //resource.save(new BaseConfig().toMap());
     //       ImmutableConfig config = new MapDbConfig()
            ImmutableConfig config = new BlueprintsNeo4jConfig()
                    .cacheContainers()
                //    .withLists()
                    .cacheMetaClasses();

            resource.save(config.toMap());
        }

        /**
         * Run the tutorial using a Blueprints and a MapDB resource.
         *
         * <b>Note:</b> HBase resource creation is presented in this file but not used to perform read/write operations,
         * because HBase needs to be installed separately and started to store a model. To enable HBase storage see the
         * HBase Configuration page on the wiki.
         *
         * @throws IOException if a resource cannot be saved or loaded
         */
        public static void main(String[] args) throws IOException {

//            Resource[] resources = {
//                 createBlueprintsResource()
////                    ,
////                    createMapDbResource()
//            };

//            for (Resource resource : resources) {
//                write(resource);
//            }
//
//            for (Resource resource : resources) {
//                readVertices(resource);
//            }
            
        	ResourceSet rSet = new ResourceSetImpl();
            Resource persistentResource = rSet.createResource(
            		new BlueprintsUriFactory().createLocalUri(
            	        new File("databases/books.graphdb")
            	    )
            	);
            ImmutableConfig config = new BlueprintsNeo4jConfig()
                    .cacheContainers()
                    .cacheMetaClasses();

            	// save the resource before moving the objects to create the Neo4j database
            	persistentResource.save(config.toMap());
            	
            	
            	//ResourceSet resourceSet = new ResourceSetImpl();
        		
        		ResourceSet ecoreResourceSet = new ResourceSetImpl();
        		ecoreResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
        		Resource ecoreResource = ecoreResourceSet.createResource(URI.createFileURI(new File("src/org/eclipse/epsilon/effectivemetamodel/neoemf/EnergyConsumption.ecore").getAbsolutePath()));
        		ecoreResource.load(null);
        		for (EObject o : ecoreResource.getContents()) {
        			EPackage ePackage = (EPackage) o;
        			rSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
        		}
        		
        		rSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
        		Resource xmiResource = rSet.createResource(URI.createFileURI(new File("src/org/eclipse/epsilon/effectivemetamodel/neoemf/LCLModel_40.xmi").getAbsolutePath()));
            	
            	//Resource xmiResource = rSet.createResource(URI.createURI("LCLModel_40.xmi"));
            	//Resource xmiResource = rSet.createResource(URI.createFileURI(new File("LCLModel_40.xmi").getAbsolutePath()));
            	xmiResource.load(Collections.emptyMap());

            	persistentResource.getContents().addAll(xmiResource.getContents());
            	// "regular" save
            	persistentResource.save(config.toMap());
            	System.out.print(persistentResource.getContents().get(0));
        }
}
