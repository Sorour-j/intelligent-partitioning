/*******************************************************************************
 * Copyright (c) 2008 The University of York.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     Dimitrios Kolovos - initial API and implementation
 ******************************************************************************/
package org.eclipse.epsilon.xmin.partitioning;


import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.effectivemetamodel.XMIN;
import org.eclipse.epsilon.evl.launch.EvlRunConfiguration;
//import org.eclipse.epsilon.loading.PartialEvlModule;

/**
 * This example demonstrates using the Epsilon Object Language, the core
 * language of Epsilon, in a stand-alone manner
 * 
 * @author Sina Madani
 * @author Dimitrios Kolovos
 */
public class EvlXminModelStandaloneExample {

	public static void main(String... args) throws Exception {
		
		String model = "/Users/sorourjahanbin/git/Intelligent-partitioning/org.eclipse.epsilon.partialloading/model/component_5M.xmi";
		String metamodel = "/Users/sorourjahanbin/git/Intelligent-partitioning/org.eclipse.epsilon.partialloading/model/CCL.ecore";//args[1];
		String script = "/Users/sorourjahanbin/git/Intelligent-partitioning/org.eclipse.epsilon.partialloading/queries/component2.evl";
		StringProperties modelProperties = new StringProperties();
		modelProperties.setProperty(XMIN.PROPERTY_NAME, "XMIN");
		modelProperties.setProperty("type", "XMIN");
		modelProperties.setProperty(XMIN.PROPERTY_MODEL_FILE,model);
		modelProperties.setProperty(XMIN.PROPERTY_METAMODEL_FILE, metamodel);
		modelProperties.setProperty(XMIN.PROPERTY_METAMODEL_URI, "http://componentlanguage");
		
		EvlRunConfiguration runConfig = EvlRunConfiguration.Builder()
			.withModule(new PartialEvlModule())
			.withScript(script)
			.withModel(new XMIN(), modelProperties)
			.withParameter("Thread", Thread.class)
		///	.withResults()
			.withProfiling()
			.build();
	
		EvlPartitioningXMINRunConfiguration sm = new EvlPartitioningXMINRunConfiguration(runConfig);
		sm.run();
	}
}