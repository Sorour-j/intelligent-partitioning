package org.eclipse.epsilon.effectivemetamodel;

import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.eol.compile.context.IModelFactory;
import org.eclipse.epsilon.eol.models.IModel;

public class SubModelFactory implements IModelFactory{

	@Override
	public IModel createModel(String driver) {
		// TODO Auto-generated method stub
		XMIN model = new XMIN();
		return  model;
	}

	
}
