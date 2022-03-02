/*******************************************************************************
 * Copyright (c) 2008 The University of York.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     Dimitrios Kolovos - initial API and implementation
 ******************************************************************************/
package org.eclipse.epsilon.emc.neo4j.example.standalone;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.effectivemetamodel.XMIN;
import org.eclipse.epsilon.eol.launch.EolRunConfiguration;
import org.eclipse.epsilon.emc.neo4j.Neo4jModel;

public class EolNeo4jModelStandaloneExample {
	
	public static void main(String[] args) throws Exception {
		Path root = Paths.get(EolNeo4jModelStandaloneExample.class.getResource("").toURI()),
			modelsRoot = root.getParent().resolve("standalone");
		
		StringProperties modelProperties = new StringProperties();
		modelProperties.setProperty(Neo4jModel.PROPERTY_NAME, "javaMM");
		modelProperties.setProperty(Neo4jModel.PROPERTY_USERNAME, "neo4j");
		modelProperties.setProperty(Neo4jModel.PROPERTY_URI, "bolt://localhost:7687");
		modelProperties.setProperty(Neo4jModel.PROPERTY_PASSWORD,"password");
		modelProperties.setProperty(Neo4jModel.PROPERTY_DATABASE, "neo4j");
		modelProperties.setProperty("type", "Neo4jModel");
		modelProperties.setProperty(Neo4jModel.PROPERTY_DATABASEPATH, "target/JavaDB");
	//	modelProperties.setProperty(XMIN.PROPERTY_METAMODEL_URI,"http://www.eclipse.org/MoDisco/Java/0.2.incubation/java");
		
		EolRunConfiguration runConfig = EolRunConfiguration.Builder()
			.withScript(root.resolve("test.eol"))
			.withModel(new Neo4jModel(), modelProperties)
			.withParameter("Thread", Thread.class)
			.withProfiling()
			.build();
		//EolXminModelRunConfiguration xminRunconfig = new EolXminModelRunConfiguration(runConfig);
		EolNeoEMFRunConfiguration neo = new EolNeoEMFRunConfiguration(runConfig);
		//xminRunconfig.run();
		neo.run();
	}
}
