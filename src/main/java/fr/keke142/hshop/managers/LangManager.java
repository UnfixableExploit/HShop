package fr.keke142.hshop.managers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.keke142.hshop.HShopPlugin;
import fr.keke142.hshop.Lang;

public class LangManager {
  private HShopPlugin plugin;
  public LangManager(HShopPlugin instance) {
    this.plugin = instance;
  }
  
  private String langName;
  
  private YamlConfiguration LANG;
  private File LANG_FILE;
  
  public YamlConfiguration getLang() {
    return LANG;
  }

  public File getLangFile() {
    return LANG_FILE;
  }

  public YamlConfiguration loadLang() {
    langName = plugin.getConfig().getString("Language.language-name");
    
    File lang = new File(plugin.getDataFolder(), "lang_" + langName + ".yml");
    if (!lang.exists()) {
      try {
        plugin.getConsole().sendMessage(ChatColor.RED + "[HShop] lang_" + langName
            + ".yml does not exist. Creating...");
        plugin.getDataFolder().mkdir();
        lang.createNewFile();
        InputStream defConfigStream = plugin.getResource("lang_" + langName + ".yml");
        if (defConfigStream != null) {
          YamlConfiguration defConfig =
              YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
          defConfig.save(lang);
          Lang.setFile(defConfig);
          return defConfig;
        }
      } catch (IOException e) {
        e.printStackTrace();
        plugin.getConsole().sendMessage(ChatColor.RED + "[HShop] Couldn't create language file.");
        plugin.getConsole().sendMessage(ChatColor.RED + "[HShop] This is a fatal error. Now disabling");
      }
    }
    YamlConfiguration conf = YamlConfiguration.loadConfiguration(lang);
    for (Lang item : Lang.values()) {
      if (conf.getString(item.getPath()) == null) {
        conf.set(item.getPath(), item.getDefault());
      }
    }
    Lang.setFile(conf);
    LANG = conf;
    LANG_FILE = lang;
    try {
      conf.save(getLangFile());
    } catch (IOException e) {
      plugin.getConsole().sendMessage(ChatColor.RED + "[HShop] Failed to save " + "lang_" + langName + ".yml");
      plugin.getConsole().sendMessage(ChatColor.RED + "[HShop] Report this stack trace to keke142.");
      e.printStackTrace();
    }
    return conf;
  }
}
