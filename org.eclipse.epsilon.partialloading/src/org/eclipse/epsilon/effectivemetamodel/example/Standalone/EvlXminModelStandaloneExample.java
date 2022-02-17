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
import org.eclipse.epsilon.evl.EvlModule;
import org.eclipse.epsilon.evl.launch.EvlRunConfiguration;
import org.eclipse.epsilon.loading.PartialEvlModule;

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
		modelProperties.setProperty(XMIN.PROPERTY_NAME, "EnergyProvider");
		modelProperties.setProperty(XMIN.PROPERTY_FILE_BASED_METAMODEL_URI,
				modelsRoot.resolve("EnergyConsumption.ecore").toAbsolutePath().toUri().toString()

		);
		modelProperties.setProperty(XMIN.PROPERTY_METAMODEL_URI,"http://www.lowcomote.eu/EnergyProvider");
		modelProperties.setProperty("type", "XMIN");
		modelProperties.setProperty(XMIN.PROPERTY_MODEL_URI,
				modelsRoot.resolve("LCLModel.xmi").toAbsolutePath().toUri().toString());

		EvlRunConfiguration runConfig = EvlRunConfiguration.Builder()
				.withScript(root.resolve("TestId.evl"))
				.withModel(new XMIN(), modelProperties)
				// .withParameter("greeting", "Hello from ")
				.withProfiling()
				.withModule(new EvlModule())
				// .withResults()
				.build();
		EvlXminModelRunConfiguration sm = new EvlXminModelRunConfiguration(runConfig);
		sm.run();
		// runConfig.postExecute();
		// System.out.println(sm.getResult());
	}
}
