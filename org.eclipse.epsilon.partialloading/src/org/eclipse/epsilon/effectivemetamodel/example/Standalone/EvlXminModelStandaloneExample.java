/*******************************************************************************
 * Copyright (c) 2008 The University of York.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     Dimitrios Kolovos - initial API and implementation
 ******************************************************************************/
package org.eclipse.epsilon.effectivemetamodel.example.Standalone;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.effectivemetamodel.XMIN;
import org.eclipse.epsilon.evl.launch.EvlRunConfiguration;

/**
 * This example demonstrates using the Epsilon Object Language, the core
 * language of Epsilon, in a stand-alone manner
 * 
 * @author Sina Madani
 * @author Dimitrios Kolovos
 */
public class EvlXminModelStandaloneExample {

	public static void main(String... args) throws Exception {
		Path root = Paths.get(EvlStandaloneExample.class.getResource("").toURI()),
				modelsRoot = root.getParent().resolve("standalone");

		StringProperties modelProperties = new StringProperties();
		modelProperties.setProperty(XMIN.PROPERTY_NAME, "javaMM");
		modelProperties.setProperty(XMIN.PROPERTY_FILE_BASED_METAMODEL_URI,
				modelsRoot.resolve("java.ecore").toAbsolutePath().toUri().toString()

		);
		modelProperties.setProperty(XMIN.PROPERTY_METAMODEL_URI,
				"http://www.eclipse.org/MoDisco/Java/0.2.incubation/java");
		modelProperties.setProperty("type", "XMIN");
		modelProperties.setProperty(XMIN.PROPERTY_MODEL_URI,
				modelsRoot.resolve("test.xmi").toAbsolutePath().toUri().toString());

		EvlRunConfiguration runConfig = EvlRunConfiguration.Builder().withScript(root.resolve("test.evl"))
				.withModel(new XMIN(), modelProperties)
				// .withParameter("greeting", "Hello from ")
				.withProfiling()
				// .withResults()
				.build();
		EvlXminModelRunConfiguration sm = new EvlXminModelRunConfiguration(runConfig);
		sm.run();
		// runConfig.postExecute();
		// System.out.println(sm.getResult());
	}
}
