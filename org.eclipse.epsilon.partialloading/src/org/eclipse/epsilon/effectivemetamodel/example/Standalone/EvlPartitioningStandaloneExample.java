package org.eclipse.epsilon.effectivemetamodel.example.Standalone;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.effectivemetamodel.XMIN;
import org.eclipse.epsilon.evl.launch.EvlRunConfiguration;
import org.eclipse.epsilon.loading.PartialEvlModule;

public class EvlPartitioningStandaloneExample {

	public static void main(String... args) throws Exception {
		Path root = Paths.get(EvlStandaloneExample.class.getResource("").toURI()),
				modelsRoot = root.getParent().resolve("standalone");

		StringProperties modelProperties = new StringProperties();
		modelProperties.setProperty(XMIN.PROPERTY_NAME, "javaMM");
		modelProperties.setProperty(XMIN.PROPERTY_FILE_BASED_METAMODEL_URI,
				modelsRoot.resolve("Java.ecore").toAbsolutePath().toUri().toString()

		);
		modelProperties.setProperty(XMIN.PROPERTY_METAMODEL_URI,"http://www.eclipse.org/MoDisco/Java/0.2.incubation/java");
		modelProperties.setProperty("type", "XMIN");
		modelProperties.setProperty(XMIN.PROPERTY_MODEL_URI,
				modelsRoot.resolve("eclipseModel-0.1.xmi").toAbsolutePath().toUri().toString());

		EvlRunConfiguration runConfig = EvlRunConfiguration.Builder()
				.withModule(new PartialEvlModule())
				.withScript(root.resolve("test1.evl"))
				.withModel(new XMIN(), modelProperties)
				// .withParameter("greeting", "Hello from ")
				.withProfiling()
				// .withResults()
				.build();
		EvlPartitioningRunConfiguration sm = new EvlPartitioningRunConfiguration(runConfig);
		sm.run();
		// runConfig.postExecute();
		// System.out.println(sm.getResult());
	}
}
