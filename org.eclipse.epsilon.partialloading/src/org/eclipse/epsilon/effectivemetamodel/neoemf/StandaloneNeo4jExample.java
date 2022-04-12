package org.eclipse.epsilon.effectivemetamodel.neoemf;

import java.util.Collection;
import java.util.HashMap;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.summary.ResultSummary;

public class StandaloneNeo4jExample implements AutoCloseable{
	private final Driver driver;

	
	public static void main( String... args ) throws Exception
    {
    	//"bolt://localhost:7687", "neo4j", "13716340" 
    	//TestDBMS   
        try ( StandaloneNeo4jExample greeter = new StandaloneNeo4jExample("bolt://localhost:7687", "neo4j", "password" ) )
        {
            greeter.load();
        }
    }
	
    public StandaloneNeo4jExample( String uri, String user, String password )
    {

    	driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    	
        //driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }

    public void load() throws Exception
    {//SessionConfig.forDatabase("neo4j")
//        try ( Session session = driver.session())
//        {
			long dbHit = 0;
        	try (Session session = driver.session(SessionConfig.forDatabase("neo4j"))) {

    			Result re3 = session.run("MATCH (u : User) RETURN u");//.name AS name, u.screen_name As scname
    			//Result re = session.run("MATCH (u : User) RETURN u.name AS name, u.screen_name As scname");
    			
    			while (re3.hasNext()) {
    				Record record = re3.next();
    				record.get(0).get("name");
    				record.get(0).get("screen_name");
    			}

    			ResultSummary rs2 = re3.consume();
					//System.out.println("DBhit: " + rs2.plan().children().get(0).arguments().get("DbHits"));
					dbHit = rs2.plan().children().get(0).arguments().get("DbHits").asLong();
					
					//System.out.println("DBhit: " + rs2.plan().children().get(0).children().get(0).arguments().get("DbHits"));
					dbHit += rs2.plan().children().get(0).children().get(0).arguments().get("DbHits").asLong();
					
				//	System.out.println("Time: " + rs2.plan().children().get(0).children().get(0).arguments().get("Time"));
				//	time = rs2.plan().children().get(0).children().get(0).arguments().get("Time").asLong();
				//	System.out.println("Records: " + rs2.profile().records());
					
//					Result re22 = session.run("Profile MATCH (u : User) RETURN u.screen_name As scname");
//
//					while (re22.hasNext())
//		            {
//		                Record record = re22.next();
//		                record.get("scname");
//		               // System.out.println(record.get("scname"));
//		            }

//					ResultSummary rs22 = re22.consume();
//					//System.out.println("DBhit: " + rs22.plan().children().get(0).arguments().get("DbHits"));
//					dbHit += rs22.plan().children().get(0).arguments().get("DbHits").asLong();
//					
//					//System.out.println("DBhit: " + rs22.plan().children().get(0).children().get(0).arguments().get("DbHits"));
//					dbHit += rs22.plan().children().get(0).children().get(0).arguments().get("DbHits").asLong();
//					
//					//System.out.println("Time: " + rs22.plan().children().get(0).children().get(0).arguments().get("Time"));
//					time += rs22.plan().children().get(0).children().get(0).arguments().get("Time").asLong();
//	
//					System.out.println("Records: " + rs22.profile().records());
				
					Result re2 = session.run("Profile MATCH (u : User) RETURN u.name AS name, u.screen_name As scname");
					
					while (re2.hasNext())
		            {
		                Record record = re2.next();
		                record.get("name");
		                record.get("scname");
		            }
					ResultSummary rs3 = re2.consume();
					System.out.println("*****One Query*****");
					//System.out.println(rs3.plan().toString());
					//System.out.println("DBhit: " + rs3.plan().children().get(0).arguments().get("DbHits"));
					dbHit -= rs3.plan().children().get(0).arguments().get("DbHits").asLong();
					//System.out.println("DBhit: " + rs3.plan().children().get(0).children().get(0).arguments().get("DbHits"));
					dbHit -= rs3.plan().children().get(0).children().get(0).arguments().get("DbHits").asLong();
					//System.out.println("Time: " + rs3.plan().children().get(0).children().get(0).arguments().get("Time"));
				//	time -= rs3.plan().children().get(0).children().get(0).arguments().get("Time").asLong();
					System.out.println("DBhit: " + dbHit);
				/*	System.out.println("Final Time: " + time + " milliseconds");
					System.out.println("Records: " + rs3.profile().records());	
					*/	
				}
	}
    @Override
    public void close() throws Exception
    {
        driver.close();
    }
}

