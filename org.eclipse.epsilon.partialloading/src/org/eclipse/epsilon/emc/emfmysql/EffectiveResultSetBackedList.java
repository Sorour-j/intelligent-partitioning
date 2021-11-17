/*******************************************************************************
 * Copyright (c) 2008 The University of York.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     Dimitrios Kolovos - initial API and implementation
 ******************************************************************************/
package org.eclipse.epsilon.emc.emfmysql;

import java.sql.ResultSet;
import java.util.List;

import org.eclipse.epsilon.emc.jdbc.ResultSetBackedList;
import org.eclipse.epsilon.emc.jdbc.Table;
import org.eclipse.epsilon.emc.mysql.MySqlModel;

public abstract class EffectiveResultSetBackedList<T>  extends ResultSetBackedList<T> {
	
	protected MySqlModel model = null;
	public EffectiveResultSetBackedList(MySqlModel model, Table table, String condition, List<Object> parameters,
			boolean streamed, boolean one) {
		super(model, table, condition, parameters, streamed, one);
		this.model = model;
	}
	
	protected ResultSet getResultSet() {
		if (resultSet == null) {
			resultSet = model.getResultSet(getSelection(), condition, parameters, table, streamed, isOne());
		}
		return resultSet;
	}
	
}
