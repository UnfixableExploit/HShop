package fr.keke142.hshop.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;

import fr.keke142.hshop.managers.DatabaseManager;
import fr.keke142.hshop.objects.Shop;

public class Shops implements IRepository {
	
	private DatabaseManager databaseManager;
	
	public Shops(DatabaseManager databaseManager) {
		this.databaseManager = databaseManager;
	}
	
	public Collection<Shop> getShops() {
        ConnectionHandler connectionHandler = databaseManager.getConnectionPool().getConnection();
        Collection<Shop> shops = new ArrayList<Shop>();

        try {
            PreparedStatement getShops = connectionHandler.getPreparedStatement("getShops");

            ResultSet res = getShops.executeQuery();
            while (res.next()) {
            	shops.add(new Shop(res.getInt("id"), res.getString("playername"), res.getString("uuid"), res.getString("world"), res.getInt("x"), res.getInt("y"), res.getInt("z"),
            	    res.getString("itemserialized"), res.getInt("itemcount"), res.getInt("unitamount"), res.getDouble("sellprice"), res.getDouble("buyprice"), res.getBoolean("admin")));
            }
            res.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	connectionHandler.release();
        }

        return shops;
	}
	
	public void deleteShop(Shop shop) {
        ConnectionHandler connectionHandler = databaseManager.getConnectionPool().getConnection();

        try {
            PreparedStatement deleteShop = connectionHandler.getPreparedStatement("deleteShop");
            deleteShop.setInt(1, shop.getId());

            deleteShop.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	connectionHandler.release();
        }	
	}

	public Shop createShop(String playerName, String playerUuid, String worldName, int x, int y, int z, int unitAmount, double sellPrice, double buyPrice, boolean admin) {
	    ConnectionHandler connectionHandler = databaseManager.getConnectionPool().getConnection();
        Shop shop = null;

        try {
            PreparedStatement insertShop = connectionHandler.getPreparedStatement("insertShop");
            insertShop.setString(1, playerName);
            insertShop.setString(2, playerUuid);
            insertShop.setString(3, worldName);
            insertShop.setInt(4, x);
            insertShop.setInt(5, y);
            insertShop.setInt(6, z);
            insertShop.setString(7, "NOT INITIALIZED");
            insertShop.setInt(8, 0);
            insertShop.setInt(9, unitAmount);
            insertShop.setDouble(10, sellPrice);
            insertShop.setDouble(11, buyPrice);          
            
            insertShop.executeUpdate();
            
            PreparedStatement getShop = connectionHandler.getPreparedStatement("getShop");
            getShop.setString(1, worldName);
            getShop.setInt(2, x);
            getShop.setInt(3, y);
            getShop.setInt(4, z);

            ResultSet res = getShop.executeQuery();
            while (res.next()) {
              shop = new Shop(res.getInt("id"), res.getString("playername"), res.getString("uuid"), res.getString("world"), res.getInt("x"), res.getInt("y"), res.getInt("z"),
                  res.getString("itemserialized"), res.getInt("itemcount"), res.getInt("unitamount"), res.getDouble("sellprice"), res.getDouble("buyprice"), res.getBoolean("admin"));
            }
            res.close();
            
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	connectionHandler.release();
        }
        return shop;
	}

    public void saveShopItemSerialized(Shop shop) {
        ConnectionHandler connectionHandler = databaseManager.getConnectionPool().getConnection();

        try {
            PreparedStatement updateShop = connectionHandler.getPreparedStatement("updateShopItemSerialized");
            updateShop.setString(1, shop.getItemSerialized());
            updateShop.setInt(2, shop.getId());

            updateShop.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connectionHandler.release();
        }       
    }

    public void saveShopItemCountAndPlayerName(Shop shop) {
        ConnectionHandler connectionHandler = databaseManager.getConnectionPool().getConnection();

        try {
            PreparedStatement updateShop = connectionHandler.getPreparedStatement("updateShopItemCountAndPlayerName");
            updateShop.setInt(1, shop.getItemCount());
            updateShop.setString(2, shop.getPlayerName());
            updateShop.setInt(3, shop.getId());

            updateShop.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connectionHandler.release();
        }       
    }
    
	public void saveShopAdmin(Shop shop) {
        ConnectionHandler connectionHandler = databaseManager.getConnectionPool().getConnection();

        try {
            PreparedStatement updateShop = connectionHandler.getPreparedStatement("updateShopAdmin");
            updateShop.setBoolean(1, shop.getAdmin());
            updateShop.setInt(2, shop.getId());

            updateShop.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	connectionHandler.release();
        }		
	}
	
	   public void saveShopItemCount(Shop shop) {
	        ConnectionHandler connectionHandler = databaseManager.getConnectionPool().getConnection();

	        try {
	            PreparedStatement updateShop = connectionHandler.getPreparedStatement("updateShopItemCount");
	            updateShop.setInt(1, shop.getItemCount());
	            updateShop.setInt(2, shop.getId());

	            updateShop.executeUpdate();
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            connectionHandler.release();
	        }       
	    }
	
	@Override
	public String[] getTable() {
        return new String[]{"hshops", "`id` INT UNSIGNED NOT NULL AUTO_INCREMENT, " +
                "`playername` VARCHAR(32) NOT NULL," +
                "`uuid` VARCHAR(36) NOT NULL, " +
                "`world` VARCHAR(128) NOT NULL, " +
                "`x` INT, " +
                "`y` INT, " +
                "`z` INT, " +
                "`itemserialized` VARCHAR(16384) NOT NULL, " +
                "`itemcount` INT, " +
                "`unitamount` INT, " +
                "`sellprice` DOUBLE, " +
                "`buyprice` DOUBLE, " +       
                "`admin` BOOLEAN, " +  
                "PRIMARY KEY (`id`), UNIQUE KEY uniqueLocation (`world`, `x`, `y`, `z`)",
                "ENGINE=MyISAM DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci"};
	}

	@Override
	public void registerPreparedStatements(ConnectionHandler connection) {
        connection.addPreparedStatement("getShops", "SELECT * FROM hshops");
        connection.addPreparedStatement("getShop", "SELECT * FROM hshops WHERE world=? AND x=? AND y=? AND z=?");
        connection.addPreparedStatement("deleteShop", "DELETE FROM hshops WHERE id=?");
        connection.addPreparedStatement("insertShop", "INSERT INTO hshops (playername,uuid,world,x,y,z,itemserialized,itemcount,unitamount,sellprice,buyprice,admin) VALUES(?,?,?,?,?,?,?,?,?,?,?,false)");
        connection.addPreparedStatement("updateShopItemCount", "UPDATE hshops SET itemcount=? WHERE id=?");
        connection.addPreparedStatement("updateShopItemSerialized", "UPDATE hshops SET itemserialized=? WHERE id=?");
        connection.addPreparedStatement("updateShopItemCountAndPlayerName", "UPDATE hshops SET itemcount=?, playername=? WHERE id=?");
        connection.addPreparedStatement("updateShopAdmin", "UPDATE hshops SET admin=? WHERE id=?");
	}
}
