package org.eclipse.epsilon.effectivemetamodel.neoemf;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

public class EmbeddedNeo4jDriver implements AutoCloseable {

	private final Driver driver;

    public EmbeddedNeo4jDriver( String uri, String user, String password )
    {
    	
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }

    @Override
    public void close() throws Exception
    {
        driver.close();
    }

}
