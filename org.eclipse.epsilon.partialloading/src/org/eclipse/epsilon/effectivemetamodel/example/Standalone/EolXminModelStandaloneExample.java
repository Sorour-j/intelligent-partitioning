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
import org.eclipse.epsilon.eol.launch.EolRunConfiguration;
/**
 * This example demonstrates using the Epsilon Object Language, the core language of Epsilon, in a stand-alone manner 
 * 
 * @author Sina Madani
 * @author Dimitrios Kolovos
 */
public class EolXminModelStandaloneExample {
	
	public static void main(String[] args) throws Exception {
		Path root = Paths.get(EolXminModelStandaloneExample.class.getResource("").toURI()),
			modelsRoot = root.getParent().resolve("standalone");
		
		StringProperties modelProperties = new StringProperties();
		modelProperties.setProperty(XMIN.PROPERTY_NAME, "EnergyProvider");
		modelProperties.setProperty(XMIN.PROPERTY_FILE_BASED_METAMODEL_URI,
			modelsRoot.resolve("EnergyConsumption.ecore").toAbsolutePath().toUri().toString()
			
		);
		modelProperties.setProperty(XMIN.PROPERTY_METAMODEL_URI,"http://www.lowcomote.eu/EnergyProvider");
		modelProperties.setProperty("type", "XMIN");
		modelProperties.setProperty(XMIN.PROPERTY_MODEL_URI,
			modelsRoot.resolve("LCLModel_40.xmi").toAbsolutePath().toUri().toString()
		);
		modelProperties.setProperty(XMIN.PROPERTY_STOREONDISPOSAL, "true");
		
		EolRunConfiguration runConfig = EolRunConfiguration.Builder()
			.withScript(root.resolve("energy.eol"))
			.withModel(new XMIN(), modelProperties)
			.withParameter("Thread", Thread.class)
			.withProfiling()
			.build();
		EolXminModelRunConfiguration xminRunconfig = new EolXminModelRunConfiguration(runConfig);
		xminRunconfig.run();
		runConfig.dispose();
		xminRunconfig.dispose();
		
		//System.out.println(sm.getResult());
	}
}
