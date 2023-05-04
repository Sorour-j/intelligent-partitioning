package org.eclipse.epsilon.xmin.partitioning;

import org.eclipse.epsilon.effectivemetamodel.XMIN;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.eol.staticanalyser.IModelFactory;

public class SubModelFactory implements IModelFactory {

	@Override
	public IModel createModel(String driver) {

		if (driver.equals("XMIN")) {
			XMIN model = new XMIN();
			return model;
		} 
		return null;
	}

}
