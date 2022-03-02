package org.eclipse.epsilon.emc.neo4j.example.standalone;


import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.summary.ResultSummary;

public class StandaloneExample implements AutoCloseable {

	private final Driver driver;

	public static void main(String... args) throws Exception {
		// "bolt://localhost:7687", "neo4j", "13716340"
		// TestDBMS
		try (StandaloneExample greeter = new StandaloneExample("neo4j+s://demo.neo4jlabs.com:7687", "twitter",
				"twitter")) {
			greeter.load();
		}
	}

	public StandaloneExample(String uri, String user, String password) {

		driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));

		// driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
	}

	public void load() throws Exception {

		long dbHit = 0;
		long time = 0;
		
		try (Session session = driver.session(SessionConfig.forDatabase("twitter"))) {
			
			for (int i =0; i<10; i++) {
			Result re3 = session.run("Profile MATCH (u : User) RETURN u.name AS name, u.screen_name As scname");

			while (re3.hasNext()) {
				Record record = re3.next();
				record.get("name");
				record.get("scname");
////		                System.out.println(record.get("name"));
////		                System.out.println(record.get("scname"));
			}
			
			ResultSummary rs3 = re3.consume();
			
			long dbHit3 = 0;
			
			System.out.println("*****Last Query*****");
			// System.out.println(rs3.plan().toString());
			//System.out.println("DBhit: " +
			dbHit3 = rs3.plan().children().get(0).arguments().get("DbHits").asLong();
			dbHit -= rs3.plan().children().get(0).arguments().get("DbHits").asLong();
			// System.out.println("DBhit: " +
			dbHit3 += rs3.plan().children().get(0).children().get(0).arguments().get("DbHits").asLong();
			dbHit -= rs3.plan().children().get(0).children().get(0).arguments().get("DbHits").asLong();
			System.out.println("DBhit: " + dbHit3);
			System.out.println("Time: " + rs3.plan().children().get(0).children().get(0).arguments().get("Time"));
			
			}
			String q = "Profile MATCH (u : User) RETURN u.name AS name";
//			StringBuilder sb = new StringBuilder(q);
//			sb.deleteCharAt(q.length() - 1);
//			q = sb.toString();
			Result re2 = session.run(q);

			

			while (re2.hasNext()) {
				Record record = re2.next();
				record.get("name");
			}

			ResultSummary rs2 = re2.consume();
			System.out.println("****First query****");
			//System.out.println("DBhit:" + rs2.plan().children().get(0).arguments().get("DbHits"));
			dbHit = rs2.plan().children().get(0).arguments().get("DbHits").asLong();
			dbHit += rs2.plan().children().get(0).children().get(0).arguments().get("DbHits").asLong();

			System.out.println("DBhit:" + dbHit);
			System.out.println("Time: " + rs2.plan().children().get(0).children().get(0).arguments().get("Time"));
			time = rs2.plan().children().get(0).children().get(0).arguments().get("Time").asLong();
			System.out.println("Records: " + rs2.profile().records());

			System.out.println("****Second query****");
			Result re22 = session.run("Profile MATCH (u : User) RETURN u.screen_name As scname");

			while (re22.hasNext()) {
				Record record = re22.next();
				record.get("scname");
				// System.out.println(record.get("scname"));
			}

			ResultSummary rs22 = re22.consume();
			long dbhit2 = 0;
			// System.out.println("DBhit: " +
			// rs22.plan().children().get(0).arguments().get("DbHits"));
			dbHit += rs22.plan().children().get(0).arguments().get("DbHits").asLong();
			dbhit2 = rs22.plan().children().get(0).arguments().get("DbHits").asLong();
			// System.out.println("DBhit: " +
			// rs22.plan().children().get(0).children().get(0).arguments().get("DbHits"));
			dbHit += rs22.plan().children().get(0).children().get(0).arguments().get("DbHits").asLong();
			dbhit2 += rs22.plan().children().get(0).children().get(0).arguments().get("DbHits").asLong();
			
			System.out.println("DBhit: " + dbhit2);
			
			System.out.println("Time: " +  rs22.plan().children().get(0).children().get(0).arguments().get("Time"));
			time += rs22.plan().children().get(0).children().get(0).arguments().get("Time").asLong();

			System.out.println("Records: " + rs22.profile().records());

			
			
//			System.out.println("****Final result***");
//			time -= rs3.plan().children().get(0).children().get(0).arguments().get("Time").asLong();
//			System.out.println("Final DBhit: " + dbHit);
//			System.out.println("Final Time: " + time );
//			System.out.println("Records: " + rs3.profile().records());
		}
	}

	@Override
	public void close() throws Exception {
		driver.close();
	}
}
