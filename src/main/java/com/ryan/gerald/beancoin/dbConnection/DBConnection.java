package com.ryan.gerald.beancoin.dbConnection;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


@Deprecated
public abstract class DBConnection {
	protected EntityManagerFactory emf = null;
	protected EntityManager em = null;
	private String pUName = "beancoin";
	private String pUNameDev = "beancoin-dev";
	public static int openConnectionCount = 0;

	public void connect() {
		System.err.println("Persistence Class: " + Persistence.class);
//		if (com.ryan.gerald.beancoin.initializors.Config.DB_DEV) {
		System.out.println("DEV ENV IS " + System.getenv("DEV"));
		if (System.getenv("DEV") != null) {
			System.out.println("Running local database");
			this.emf = Persistence.createEntityManagerFactory(pUNameDev);
		} else {
			System.out.println("Running production database");
			this.emf = Persistence.createEntityManagerFactory(pUName);
		}
		this.em = emf.createEntityManager();
		DBConnection.openConnectionCount++;
		System.out.println("Number of Entity Manager instances (Connections): " + DBConnection.openConnectionCount);
	}

	public void disconnect() {
		if (this.em != null) {
			em.close();
		}
		if (this.emf != null) {
			emf.close();
		}
		DBConnection.openConnectionCount--;
		System.out.println("Number of Entity Manager instances (Connections): " + DBConnection.openConnectionCount);
	}

	/**
	 * Heroku function in order to connect to jawsdb
	 *
	 * @return
	 * @throws URISyntaxException
	 * @throws SQLException
	 */
	private static Connection getConnection() throws URISyntaxException, SQLException {
		URI jdbUri = new URI(System.getenv("JAWSDB_URL"));

		String username = jdbUri.getUserInfo().split(":")[0];
		String password = jdbUri.getUserInfo().split(":")[1];
		String port = String.valueOf(jdbUri.getPort());
		String jdbUrl = "jdbc:mysql://" + jdbUri.getHost() + ":" + port + jdbUri.getPath();

		return DriverManager.getConnection(jdbUrl, username, password);
	}
}
