package org.eclipse.epsilon.emc.neo4j;

import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.execute.context.IEolContext;
import org.eclipse.epsilon.eol.execute.introspection.AbstractPropertyGetter;
import org.neo4j.driver.Record;

public class ResultPropertyGetter extends AbstractPropertyGetter {

protected Neo4jModel model;
	
public ResultPropertyGetter(Neo4jModel neo4jModel) {
	this.model = neo4jModel;
}

	@Override
	public Object invoke(Object object, String property, IEolContext context) throws EolRuntimeException {
		
		Object value = ((Record)object).get(property);
		return value;
	}

}
