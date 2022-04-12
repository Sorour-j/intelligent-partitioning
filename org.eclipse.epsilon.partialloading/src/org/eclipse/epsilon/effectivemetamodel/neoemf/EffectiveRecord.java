package org.eclipse.epsilon.effectivemetamodel.neoemf;

import java.util.List;

import org.neo4j.driver.Value;
import org.neo4j.driver.internal.InternalRecord;

public class EffectiveRecord extends InternalRecord{

	private String typeName = "";
	
	public EffectiveRecord(List<String> keys, Value[] values) {
		super(keys, values);
		this.setTypeName("");
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
}
