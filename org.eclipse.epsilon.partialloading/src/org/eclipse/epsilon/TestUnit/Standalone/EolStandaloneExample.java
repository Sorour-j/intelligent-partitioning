/*******************************************************************************
 * Copyright (c) 2008 The University of York.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     Dimitrios Kolovos - initial API and implementation
 ******************************************************************************/
package org.eclipse.epsilon.TestUnit.Standalone;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.emf.common.EMFPlugin;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.effectivemetamodel.XMIN;
import org.eclipse.epsilon.eol.launch.EolRunConfiguration;
import org.eclipse.epsilon.emc.emf.EmfModel;

/**
 * This example demonstrates using the Epsilon Object Language, the core language of Epsilon, in a stand-alone manner 
 * 
 * @author Sina Madani
 * @author Dimitrios Kolovos
 */
public class EolStandaloneExample {
	
	public static void main(String[] args) throws Exception {
		Path root = Paths.get(EolStandaloneExample.class.getResource("").toURI()),
			modelsRoot = root.getParent().resolve("standalone");
		
		StringProperties modelProperties = new StringProperties();
		modelProperties.setProperty(EmfModel.PROPERTY_NAME, "javaMM");
		modelProperties.setProperty(EmfModel.PROPERTY_FILE_BASED_METAMODEL_URI,
			modelsRoot.resolve("java.ecore").toAbsolutePath().toUri().toString()
			
		);
	//	modelProperties.setProperty(EmfModel.PROPERTY_METAMODEL_URI,"http://www.eclipse.org/MoDisco/Java/0.2.incubation/java");
		modelProperties.setProperty("type", "EMF");
		modelProperties.setProperty(EmfModel.PROPERTY_MODEL_URI,
			modelsRoot.resolve("eclipseModel-1.0.xmi").toAbsolutePath().toUri().toString()
		);
		
		EolRunConfiguration runConfig = EolRunConfiguration.Builder()
			.withScript(root.resolve("java_findbugs_emf.eol"))
			.withModel(new EmfModel(), modelProperties)
		//	.withParameter("Thread", Thread.class)
			.withProfiling()
			.build();
	
		runConfig.run();
		//runConfig.run();
			
//	System.out.println(runConfig.getResult());
	}
	
}
