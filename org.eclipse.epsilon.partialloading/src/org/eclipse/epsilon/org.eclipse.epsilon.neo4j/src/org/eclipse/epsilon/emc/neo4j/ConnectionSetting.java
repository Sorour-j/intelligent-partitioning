package org.eclipse.epsilon.emc.neo4j;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.neo4j.configuration.connectors.BoltConnector;
import org.neo4j.configuration.connectors.HttpConnector;
import org.neo4j.configuration.helpers.SocketAddress;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

public class ConnectionSetting implements AutoCloseable {

	private final Driver driver;
	public DatabaseManagementService managementService;

	public ConnectionSetting(String uri, String user, String password, String dbPath, int port) {

		final Path databaseDirectory = Paths.get(dbPath);
		System.out.println("Starting database ...");
		managementService = new DatabaseManagementServiceBuilder(databaseDirectory)
				.setConfig(BoltConnector.enabled, true).setConfig(HttpConnector.enabled, true)
				.setConfig(BoltConnector.listen_address, new SocketAddress("localhost", port)).build();
		
		driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
	}
	
	@Override
	public void close() throws Exception {
		driver.close();
	}

	public void registerShutdownHook() {
				managementService.shutdown();
	}
	
	public Driver getDriver() {
		return driver;
	}
}
