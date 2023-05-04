package org.eclipse.epsilon.effectivemetamodel.neoemf;

import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.eol.launch.EolRunConfiguration;

public class EolNeomEMFStandaloneExample {
	public static void main(String[] args) throws Exception {
	
	//		String db = "target/neoemf-set2";//args[0];
	//		String metamodel = "/Users/sorourjahanbin/git/mainandstaticanalysis/org.eclipse.epsilon.neo4j/src/org/eclipse/epsilon/neo4j/benchmarks/JDTAST.ecore";//args[1];
			String script = "/Users/sorourjahanbin/git/mainandstaticanalysis/org.eclipse.epsilon.neo4j/src/org/eclipse/epsilon/neo4j/benchmarks/grabats.eol";//args[2];
	
			StringProperties modelProperties = new StringProperties();
			modelProperties.setProperty(NeoEMFModel.PROPERTY_NAME, "Neo4jModel");
			modelProperties.setProperty("type", "Neo4jModel");
			
			EolRunConfiguration runConfig = EolRunConfiguration.Builder()
				.withScript(script)
				.withModel(new NeoEMFModel(), modelProperties)
				.withParameter("Thread", Thread.class)
				.withProfiling()
				.build();
			runConfig.run();
		}
}