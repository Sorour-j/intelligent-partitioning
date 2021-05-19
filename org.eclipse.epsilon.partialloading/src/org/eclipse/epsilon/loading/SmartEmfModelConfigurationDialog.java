/*******************************************************************************
 * Copyright (c) 2008-2012 The University of York, Antonio García-Domínguez.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     Dimitrios Kolovos - initial API and implementation
 *     Antonio García-Domínguez - allow loading more than one metamodel
 ******************************************************************************/
package org.eclipse.epsilon.loading;

import org.eclipse.epsilon.emc.emf.dt.EmfModelConfigurationDialog;

public class SmartEmfModelConfigurationDialog extends EmfModelConfigurationDialog {

	@Override
	protected String getModelName() {
		return "XMIN model";
	}
	
	@Override
	protected String getModelType() {
		return "XMIN";
	}
}
