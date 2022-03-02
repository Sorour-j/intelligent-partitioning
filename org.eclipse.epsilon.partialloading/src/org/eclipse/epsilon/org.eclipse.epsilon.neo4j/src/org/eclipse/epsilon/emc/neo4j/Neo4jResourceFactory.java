package org.eclipse.epsilon.emc.neo4j;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

public class Neo4jResourceFactory{

	ResourceSet resourceSet = new ResourceSetImpl();
	Resource resource = resourceSet.createResource(URI.createURI("Neo4jResource"));
	
	public Neo4jResourceFactory() {
		resource.getContents().add(null);
	}
}
