package org.eclipse.epsilon.emc.neo4j;

import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.eol.staticanalyser.IModelFactory;

public class SubModelFactory implements IModelFactory {

	@Override
	public IModel createModel(String driver) {

		if (driver.equals("Neo4j")) {
			Neo4jModel model = new Neo4jModel();
			return model;
		} 
		return null;
	}

}
