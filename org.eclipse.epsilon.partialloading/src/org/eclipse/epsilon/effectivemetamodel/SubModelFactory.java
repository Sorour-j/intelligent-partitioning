package org.eclipse.epsilon.effectivemetamodel;

import org.eclipse.epsilon.emc.emfmysql.EmfMySqlModel;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.eol.staticanalyser.IModelFactory;

public class SubModelFactory implements IModelFactory{

	@Override
	public IModel createModel(String driver) {
		
		if (driver.equals("XMIN")) {
			XMIN model = new XMIN();
			return  model;
		}
		else if (driver.equals("EMFSQL")) {
			EmfMySqlModel model = new EmfMySqlModel();
			return  model;
		}
		return null;
	}

	
}
