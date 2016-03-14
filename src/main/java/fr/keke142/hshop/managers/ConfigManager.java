package fr.keke142.hshop.managers;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;

import fr.keke142.hshop.HShopPlugin;
import fr.keke142.hshop.configs.DatabaseConfig;

public class ConfigManager {

	private HShopPlugin plugin;
	private DatabaseConfig database = new DatabaseConfig();
	
	private File file;
	private FileConfiguration config;	
	
	public ConfigManager(HShopPlugin plugin) {
		this.plugin = plugin;
		file = new File(plugin.getDataFolder(), "config.yml");
		plugin.createDefaultConfiguration(file, "config.yml");
        
		config = plugin.getConfig();
        config.options().copyDefaults(true);
        plugin.saveDefaultConfig();

		database.load(config);
	}
	
	public void save() {
		try {
			database.save(config);
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void reload() {
		config = plugin.getConfig();
        config.options().copyDefaults(true);
        plugin.saveDefaultConfig();

		database.load(config);
	}
	
	public DatabaseConfig getDatabaseConfig() {
		return database;
	}
}
