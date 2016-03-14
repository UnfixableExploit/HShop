package fr.keke142.hshop.managers;

import fr.keke142.hshop.HShopPlugin;
import fr.keke142.hshop.database.Shops;
import fr.keke142.hshop.database.ConnectionPool;

public class DatabaseManager {
	private HShopPlugin plugin;
    private ConnectionPool connectionPool;
	private Shops shop;

	public DatabaseManager(HShopPlugin plugin) {
		this.plugin = plugin;
	}
	
	public void loadDatabase() {
		shop = new Shops(this);
		
        connectionPool = new ConnectionPool(plugin);
        connectionPool.addRepository(shop);
        connectionPool.initializeConnections();
        
        plugin.getShopManager().loadShops();
	}
	
	public ConnectionPool getConnectionPool() {
		return connectionPool;
	}
	
	public Shops getShopDatabase() {
		return shop;
	}
}
