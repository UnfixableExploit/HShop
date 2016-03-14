package fr.keke142.hshop.configs;

import org.bukkit.configuration.Configuration;

public class DatabaseConfig {
	private boolean inited;
	private int threads;
	private String host;
	private int port;
	private String database;
	private String username;
	private String password;

	public void load(Configuration config) {
		inited = config.getBoolean("database.inited", false);
		threads = config.getInt("database.threads", 5);
		host = config.getString("database.host", "localhost");
		port = config.getInt("database.port", 3306);
		database = config.getString("database.database", "db");
		username = config.getString("database.username", "user");
		password = config.getString("database.password", "passw");
	}
	
	public void save(Configuration config) {
		config.set("database.inited", inited);
		config.set("database.threads", threads);
		config.set("database.host", host);
		config.set("database.port", port);
		config.set("database.database", database);		
		config.set("database.username", username);
		config.set("database.password", password);
	}
	
	public boolean getInited() {
		return inited;
	}
	
	public void setInited() {
		inited = true;
	}
	
	public int getThreads() {
		return threads;
	}
	
	public String getHost() {
		return host;
	}
	
	public int getPort() {
		return port;
	}
	
	public String getDatabase() {
		return database;
	}

	public String getUserName() {
		return username;
	}

	public String getPassword() {
		return password;
	}
}
