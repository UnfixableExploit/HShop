package fr.keke142.hshop.managers;

import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import fr.keke142.hshop.HShopPlugin;
import fr.keke142.hshop.objects.Shop;

public class ShopManager {

	private DatabaseManager databaseManager;
    private Collection<Shop> shops;
    
    public ShopManager(HShopPlugin plugin) {
    	databaseManager = plugin.getDatabaseManager();
    }

    public void loadShops() { // NE PAS APPELER !!!
        shops = databaseManager.getShopDatabase().getShops();
    }

    public Shop createShop(String playerName, String playerUuid, String worldName, int x, int y, int z, int unitAmount, double sellPrice, double buyPrice) {
        Shop shop = getShopAt(worldName, x, y, z);
        if (shop != null) {
            // PAS NORMAL !!! LOG !
        } else {
            shop = databaseManager.getShopDatabase().createShop(playerName, playerUuid,
                worldName, x, y, z, unitAmount, sellPrice, buyPrice, false);
            if (shop == null) {
                // PAS NORMAL !!! LOG !
                return null;
            }
            shops.add(shop);
        }
        return shop;
    }
    
    // appel pour creer un nouveau shop
    public Shop createShop(Player player, Location location, int unitAmount, double sellPrice, double buyPrice) {
        return createShop(player.getName(), player.getUniqueId().toString(),
            location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ(),
            unitAmount, sellPrice, buyPrice);
    }

    // appel ça pour qu 'il enregistre l'item
    public void saveShopItemSerialized(Shop shop) {
        databaseManager.getShopDatabase().saveShopItemSerialized(shop);      
    }
    
    // appel ça pour qu 'il enregistre le count de l'item 
    public void saveShopItemCount(Shop shop) {
        databaseManager.getShopDatabase().saveShopItemCount(shop);
    }

    // appel ça pour qu 'il enregistre le count de l'item et le playername 
    public void saveShopItemCountAndPlayerName(Shop shop) {
        databaseManager.getShopDatabase().saveShopItemCountAndPlayerName(shop);
    }

    public void saveShopAdmin(Shop shop) {
        databaseManager.getShopDatabase().saveShopAdmin(shop);
    }
    
    // appel pour detruire un shop
    public void destroyShop(Shop shop) {
        shops.remove(shop);
        databaseManager.getShopDatabase().deleteShop(shop);
    }
   
    public Shop getShopAt(String worldName, int x, int y, int z) {
        for (Shop shop : shops) {
            if (shop.getWorldName().contentEquals(worldName) && shop.getX() == x && shop.getY() == y && shop.getZ() == z) {
                return shop;
            }
        }
        return null;
    }
    
    public Shop getShopAt(Location loc) {
        return getShopAt(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }  
}
