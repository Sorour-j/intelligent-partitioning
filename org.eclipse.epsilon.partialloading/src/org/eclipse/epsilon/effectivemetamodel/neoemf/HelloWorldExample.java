package org.eclipse.epsilon.effectivemetamodel.neoemf;

	import org.neo4j.driver.AuthTokens;
	import org.neo4j.driver.Driver;
	import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
	import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;

import static org.neo4j.driver.Values.parameters;

	public class HelloWorldExample implements AutoCloseable
	{
	    private final Driver driver;

	    public HelloWorldExample( String uri, String user, String password )
	    {
	    	
	        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
	    }

	    @Override
	    public void close() throws Exception
	    {
	        driver.close();
	    }

	    public void printGreeting( final String message )
	    {//SessionConfig.forDatabase("neo4j")
	        try ( Session session = driver.session(SessionConfig.forDatabase("neo4j")))
	        {
//	            String greeting = session.writeTransaction( tx ->
//	                                                        {
//	                                                            Result result = tx.run( "CREATE (a:Greeting) " +
//	                                                                                    "SET a.message = $message " +
//	                                                                                    "RETURN a.message + ', from node ' + id(a)",
//	                                                                                    parameters( "message", message ) );
//	                                                            return result.single().get( 0 ).asString();
//	                                                        } );
	       // 	Result rs = session.run("MATCH p = (u:User)-[r:FOLLOWS]->(s:User) RETURN s.name");//.screen_name As scname
	        	//Result rs = session.run("MATCH (u: Process) Return count(u)");//.screen_name As scname
	        //	Result rs = session.run("MATCH (u:User)-[r:FOLLOWS]->(s:User) RETURN s");
	        //	Result rs = session.run("MATCH (a:Application)-[d:DEPENDS_ON]->(p:Process) RETURN p.id");
	        	Result rs = session.run("MATCH (u : User) RETURN u");
				
	        	while(rs.hasNext()) {
	        		Record record = rs.next();
	        		System.out.println(record.get(0).toString().substring(5, 6));
        	}
	            
	        }
	    }

	    public static void main( String... args ) throws Exception
	    {//bolt://localhost:7687
	    	//neo4j+s://demo.neo4jlabs.com:7687
	        try ( HelloWorldExample greeter = new HelloWorldExample("bolt://localhost:7687", "neo4j", "password" ) )
	        {
	            greeter.printGreeting( "hello, world" );
	        }
	    }
}
