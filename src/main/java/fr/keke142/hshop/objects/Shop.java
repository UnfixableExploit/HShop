package fr.keke142.hshop.objects;

public class Shop {

    private int id;
	private String playerName;
	private String playerUuid;
	private String worldName;
	private int x;
	private int y;
	private int z;
	private String itemSerialized;
	private int itemCount;
	private int unitAmount;
	private double sellPrice;
	private double buyPrice;
	private boolean admin;
    public Shop(int id, String playerName, String playerUuid, String worldName, int x, int y, int z, 
                String itemSerialized, int itemCount, int unitAmount, double sellPrice, double buyPrice, boolean admin) {
        this.id = id;
        this.playerName = playerName;
        this.playerUuid = playerUuid;
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.itemSerialized = itemSerialized;
        this.itemCount = itemCount;
        this.unitAmount = unitAmount;
        this.sellPrice = sellPrice;
        this.buyPrice = buyPrice;
        this.admin = admin;
    }

    public int getId() {
        return id;
    }

    public int setId() {
      return id;
    }
    
    public String getPlayerName() {
    	return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerUuid() {
    	return playerUuid;
    }

    public String getWorldName() {
      return worldName;
    }
    
    public int getX() {
    	return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getZ() {
        return z;
    }
       
    public String getItemSerialized() {
        return itemSerialized;
    }

    public void setItemSerialized(String itemSerialized) {
        this.itemSerialized= itemSerialized;
    }

    public boolean isItemSerialized() {
      return !itemSerialized.contentEquals("NOT INITIALIZED");
    }
    
    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }
    
    public int getUnitAmount() {
        return unitAmount;
    }
    
    public void addItem() {
        itemCount += unitAmount; 
    }
    
    public void addItem(int amount) {
        itemCount += amount; 
    }

    public void substractItem() {
      itemCount -= unitAmount; 
    }
    
    public double getSellPrice() {
        return sellPrice;
    }
    
    public double getBuyPrice() {
        return buyPrice;
    }
    
    public boolean getAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
