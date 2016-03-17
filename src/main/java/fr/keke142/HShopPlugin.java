package fr.keke142.hshop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import fr.keke142.hshop.commands.ShopAdminCommand;
import fr.keke142.hshop.commands.ShopCommand;
import fr.keke142.hshop.commands.ShopConvertCommand;
import fr.keke142.hshop.commands.ShopReloadCommand;
import fr.keke142.hshop.database.ConnectionHandler;
import fr.keke142.hshop.listeners.ShopBuyListener;
import fr.keke142.hshop.listeners.ShopDepositListener;
import fr.keke142.hshop.listeners.ShopDestroyListener;
import fr.keke142.hshop.listeners.ShopPlaceListener;
import fr.keke142.hshop.listeners.ShopSellListener;
import fr.keke142.hshop.listeners.ShopWithdrawListener;
import fr.keke142.hshop.managers.LangManager;
import fr.keke142.hshop.managers.ShopManager;
import fr.keke142.hshop.managers.ConfigManager;
import fr.keke142.hshop.managers.DatabaseManager;

public class HShopPlugin extends JavaPlugin {

  private ConfigManager configManager;
  private ShopManager shopManager;
  private DatabaseManager databaseManager;
  private LangManager langLoader;

  private ShopAdminCommand shopadminCommand;
  private ShopReloadCommand shopreloadCommand;
  private ShopCommand shopCommand;
  private ShopConvertCommand shopconvertCommand;

  private Economy econ;

  private String pluginName = getDescription().getName();
  private ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

  private ArrayList<Player> place = new ArrayList<Player>();

  @Override
  public void onEnable() {

    configManager = new ConfigManager(this);
    databaseManager = new DatabaseManager(this);
    shopManager = new ShopManager(this);
    langLoader = new LangManager(this);
    
    
    databaseManager.loadDatabase();
    
    getConfig().options().copyDefaults(true);
    saveConfig();

    langLoader.loadLang();

    if (!setupEconomy()) {
      getConsole().sendMessage(Lang.PREFIX + Lang.FAILVAULT.toString());
      getServer().getPluginManager().disablePlugin(this);
      return;
    }
    setupEconomy();

    Bukkit.getServer().getPluginManager().registerEvents(new ShopPlaceListener(this), this);
    Bukkit.getServer().getPluginManager().registerEvents(new ShopDepositListener(this), this);
    Bukkit.getServer().getPluginManager().registerEvents(new ShopDestroyListener(this), this);
    Bukkit.getServer().getPluginManager().registerEvents(new ShopWithdrawListener(this), this);
    Bukkit.getServer().getPluginManager().registerEvents(new ShopBuyListener(this), this);
    Bukkit.getServer().getPluginManager().registerEvents(new ShopSellListener(this), this);

    shopadminCommand = new ShopAdminCommand(this);
    getCommand("hshopadmin").setExecutor(shopadminCommand);
    getCommand("hshopadmin").setPermissionMessage(Lang.NOPERMISSION.toString());

    shopconvertCommand = new ShopConvertCommand(this);
    getCommand("hshopconvert").setExecutor(shopconvertCommand);
    getCommand("hshopconvert").setPermissionMessage(Lang.NOPERMISSION.toString());

    shopreloadCommand = new ShopReloadCommand(this);
    getCommand("hshopreload").setExecutor(shopreloadCommand);
    getCommand("hshopreload").setPermissionMessage(Lang.NOPERMISSION.toString());

    shopCommand = new ShopCommand(this);
    getCommand("hshop").setExecutor(shopCommand);
    getCommand("hshop").setPermissionMessage(Lang.NOPERMISSION.toString());

    getLogger().info(Lang.PREFIX.toString() + Lang.TRYINGPOOL.toString());
    ConnectionHandler connectionHandler = databaseManager.getConnectionPool().getConnection();
    connectionHandler.release();

    try {
      Metrics metrics = new Metrics(this);
      metrics.start();
    } catch (IOException e) {
      getConsole().sendMessage(Lang.PREFIX + Lang.FAILMETRICS.toString());
    }
    getConsole().sendMessage(new String[] {
        ChatColor.GOLD + "--------------------------",
        ChatColor.YELLOW + pluginName + " " + ChatColor.GOLD + getDescription().getVersion()
            + ChatColor.GREEN + " by " + ChatColor.GOLD + getDescription().getAuthors()
            + ChatColor.GREEN + " is now enable.", ChatColor.GOLD + "--------------------------"});
  }

  @Override
  public void onDisable() {
    databaseManager.getConnectionPool().closeConnections();
    getConsole().sendMessage(new String[] {
        ChatColor.GOLD + "--------------------------",
        ChatColor.YELLOW + pluginName + " " + ChatColor.GOLD + getDescription().getVersion()
            + ChatColor.GREEN + " by " + ChatColor.GOLD + getDescription().getAuthors()
            + ChatColor.RED + " is now disable.", ChatColor.GOLD + "--------------------------"});
  }
  
  private boolean setupEconomy() {
    if (getServer().getPluginManager().getPlugin("Vault") == null) {
      return false;
    }
    RegisteredServiceProvider<Economy> rsp =
        getServer().getServicesManager().getRegistration(Economy.class);
    if (rsp == null) {
      return false;
    }
    econ = rsp.getProvider();
    return econ != null;
  }

  public Economy getEconomy() {
    return econ;
  }

  public ConfigManager getConfigManager() {
    return configManager;
  }

  public ShopManager getShopManager() {
    return shopManager;
  }

  public DatabaseManager getDatabaseManager() {
    return databaseManager;
  
  }
  
  public ArrayList<Player> getPlace() {
    return place;
  }

  public void setPlace(ArrayList<Player> place) {
    this.place = place;
  }
  
  public ConsoleCommandSender getConsole() {
    return console;
  }

  public void setConsole(ConsoleCommandSender console) {
    this.console = console;
  }
  
  /**
   * Create a default configuration file from the .jar.
   * 
   * @param actual The destination file
   * @param defaultName The name of the file inside the jar's defaults folder
   */
  public void createDefaultConfiguration(File actual, String defaultName) {

    // Make parent directories
    File parent = actual.getParentFile();
    if (!parent.exists()) {
      parent.mkdirs();
    }

    if (actual.exists()) {
      return;
    }

    JarFile file = null;
    InputStream input = null;
    try {
      file = new JarFile(getFile());
      ZipEntry copy = file.getEntry(defaultName);
      if (copy == null) {
        file.close();
        throw new FileNotFoundException();
      }
      input = file.getInputStream(copy);
    } catch (IOException e) {
      getLogger().severe("Unable to read default configuration: " + defaultName);
    }

    if (input != null) {
      FileOutputStream output = null;

      try {
        output = new FileOutputStream(actual);
        byte[] buf = new byte[8192];
        int length = 0;
        while ((length = input.read(buf)) > 0) {
          output.write(buf, 0, length);
        }

        getLogger().info("Default configuration file written: " + actual.getAbsolutePath());
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          if (input != null) {
            input.close();
          }
        } catch (IOException ignore) {
        }

        try {
          if (output != null) {
            output.close();
          }
        } catch (IOException ignore) {
        }
      }
    }
    if (file != null) {
      try {
        file.close();
      } catch (IOException ignore) {
      }
    }
  }
}