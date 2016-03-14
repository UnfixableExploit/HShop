package fr.keke142.hshop.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.scheduler.BukkitRunnable;

import fr.keke142.hshop.HShopPlugin;
import fr.keke142.hshop.managers.ConfigManager;

public class ConnectionPool {
	private HShopPlugin plugin;
	private ConfigManager configManager;
    private ArrayList<IRepository> repositories = new ArrayList<IRepository>();
    private ArrayList<ConnectionHandler> connections = new ArrayList<ConnectionHandler>();

    public ConnectionPool(HShopPlugin plugin) {
    	this.plugin = plugin;
    	configManager = plugin.getConfigManager();
    }
    
    public void addRepository(IRepository repository) {
        repositories.add(repository);
    }

    public boolean initializeConnections() {
        for (int i = 0; i < configManager.getDatabaseConfig().getThreads(); i++) {
            ConnectionHandler ch = null;

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection("jdbc:mysql://" + configManager.getDatabaseConfig().getHost() + ":" + configManager.getDatabaseConfig().getPort() + "/" + configManager.getDatabaseConfig().getDatabase(), configManager.getDatabaseConfig().getUserName(), configManager.getDatabaseConfig().getPassword());

                ch = new ConnectionHandler(connection);
                for(IRepository repository : repositories) {
                    repository.registerPreparedStatements(ch);
                }
            } catch (SQLException ex) {
            	plugin.getLogger().severe("SQL is unable to connect!");
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
            	plugin.getLogger().severe("SQL is unable to connect!");
                ex.printStackTrace();
            }

            if (ch != null) { 
            	connections.add(ch);
            }
        }

        new BukkitRunnable() {
        	@Override
            public void run() {
                Iterator<ConnectionHandler> cons = connections.iterator();
                while (cons.hasNext()) {
                    ConnectionHandler con = cons.next();

                    if (!con.isUsed() && con.isOldConnection()) {
                        con.closeConnection();
                        cons.remove();
                    }
                }
            }
        }.runTaskTimerAsynchronously(plugin, 10 * 20, 10 * 20);

        if (!configManager.getDatabaseConfig().getInited()) {
        	plugin.getLogger().info("Creating SQL tables.");
            for(IRepository repository : repositories) {
                String[] tableInformation = repository.getTable();

                if (!doesTableExist(tableInformation[0])) {
                    try {
                        standardQuery("CREATE TABLE IF NOT EXISTS `"+ tableInformation[0] +"` (" + tableInformation[1] + ") " + tableInformation[2] + ";");
                    } catch (SQLException e) {
                        e.printStackTrace();
                        plugin.getLogger().severe("Could not create Table!");
                    }
                }
            }

            configManager.getDatabaseConfig().setInited();
            configManager.save();
        }

        return true;
    }

    /**
     * @return Returns a free connection from the pool of connections. Creates a new connection if there are none available
     */
    public synchronized ConnectionHandler getConnection() {
        for (ConnectionHandler c : connections) {
            if (!c.isUsed()) {
                return c;
            }
        }

        // create a new connection as none are free
        ConnectionHandler ch;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://" + configManager.getDatabaseConfig().getHost() + ":" + configManager.getDatabaseConfig().getPort() + "/" + configManager.getDatabaseConfig().getDatabase(), configManager.getDatabaseConfig().getUserName(), configManager.getDatabaseConfig().getPassword());

            ch = new ConnectionHandler(connection);
            for(IRepository repository : repositories) {
                repository.registerPreparedStatements(ch);
            }
        } catch (SQLException ex) {
        	plugin.getLogger().severe("SQL is unable to connect!");
            return null;        	
        } catch (ClassNotFoundException ex) {
        	plugin.getLogger().severe("SQL is unable to connect!");
            return null;
        }

        connections.add(ch);

        plugin.getLogger().info("Created new sql connection!");

        return ch;

    }

    private void standardQuery(String query) throws SQLException {
        ConnectionHandler ch = getConnection();

        Statement statement = ch.getConnection().createStatement();
        statement.executeUpdate(query);
        statement.close();

        ch.release();
    }

    private boolean doesTableExist(String table) {
        ConnectionHandler ch = getConnection();
        boolean check = checkTable(table, ch.getConnection());
        ch.release();

        return check;
    }

    private boolean checkTable(String table, Connection connection) {
        DatabaseMetaData dbm = null;
        try {
            dbm = connection.getMetaData();
        } catch (SQLException e2) {
            e2.printStackTrace();
        }

        ResultSet tables = null;
        try {
            tables = dbm.getTables(null, null, table, null);
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        boolean check = false;
        try {
            check = tables.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return check;
    }

    public void closeConnections() {
        for (ConnectionHandler c : connections) {
            c.closeConnection();
        }
    }
}
