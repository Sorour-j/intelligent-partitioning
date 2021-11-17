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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.epsilon.effectivemetamodel.EffectiveFeature;
import org.eclipse.epsilon.effectivemetamodel.EffectiveType;
import org.eclipse.epsilon.emc.jdbc.ForeignKey;
import org.eclipse.epsilon.emc.jdbc.ResultSetList;
import org.eclipse.epsilon.emc.jdbc.ResultSetListSelectOperation;
import org.eclipse.epsilon.emc.jdbc.Table;
import org.eclipse.epsilon.eol.execute.operations.AbstractOperation;

public class EffectiveResultSetList extends ResultSetList {

	protected static EffectiveResultSetListSelectOperation resultSetListSelectOperation = new EffectiveResultSetListSelectOperation();

	public EffectiveResultSetList(EmfMySqlModel model, Table table, String condition, List<Object> parameters,
			boolean streamed, boolean one) {
		super(model, table, condition, parameters, streamed, one);
	}

	@Override
	public String getSelection() {
		String attrbs = null;
		
		String name = table.getName();
		name = name.replace("`", "");
		EffectiveType type = ((EmfMySqlModel) model).getEffectiveMetamodel().getFromAllOfKind(name);
		ArrayList<EffectiveFeature> attr = type.getAttributes();
		for (EffectiveFeature f : attr)
			if (attrbs == null)	
				attrbs = f.getName();
			else
			    attrbs+=", " + f.getName();
		if (attrbs == null)
			attrbs = getReferenceQuery();
		return attrbs;
	}
	public String getReferenceQuery() {
		String query = null;
		String name = table.getName();
		name = name.replace("`", "");
		EffectiveType type = ((EmfMySqlModel) model).getEffectiveMetamodel().getFromAllOfKind(name);
		ArrayList<EffectiveFeature> ref = type.getReferences();
		for (EffectiveFeature r : ref) {
			for (ForeignKey fk : table.getOutgoing()) {
				if (fk.getForeignTable().getName().equalsIgnoreCase("`"+ r.getName() +"`"))
					 query = table.getName() + "inner join " + fk.getForeignTable().getName() + " on "+ fk.getColumn() + " = " + fk.getForeignColumn();
			}
		}
		System.out.println("Query = " + query);
		return query;
	}
	
	@Override
	public AbstractOperation getAbstractOperation(String name) {
		if ("select".equals(name)) {
			return resultSetListSelectOperation;
		}
		else if ("collect".equals(name)) {
			return resultSetBackedListCollectOperation;
		}
		else return null;
	}
}