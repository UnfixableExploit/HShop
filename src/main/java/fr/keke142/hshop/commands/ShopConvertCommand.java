package fr.keke142.hshop.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import fr.keke142.hshop.HShopPlugin;
import fr.keke142.hshop.HcGson;
import fr.keke142.hshop.managers.ShopManager;
import fr.keke142.hshop.objects.Shop;

public class ShopConvertCommand implements Listener, CommandExecutor {
  HShopPlugin plugin;
  private ShopManager shopManager;

  public ShopConvertCommand(HShopPlugin instance) {
    this.plugin = instance;
    this.shopManager = plugin.getShopManager();
  }

  @SuppressWarnings("deprecation")
  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (cmd.getName().equalsIgnoreCase("hshopconvert")) {
      Player p = (Player) sender;

      File f = new File("plugins/HShop/Shops/");
      File[] fList = f.listFiles();
      for (File file : fList) {
        if (file.isDirectory()) {
          File f2 = new File("plugins/HShop/Shops/" + file.getName());
          File[] fList2 = f2.listFiles();
          for (File file2 : fList2) {
            if (file2.isDirectory()) {
              File f3 = new File("plugins/HShop/Shops/" + file.getName() + "/" + file2.getName());
              File[] fList3 = f3.listFiles();
              for (File file3 : fList3) {
                if (file3.isDirectory()) {
                  File f4 =
                      new File("plugins/HShop/Shops/" + file.getName() + "/" + file2.getName()
                          + "/" + file3.getName());
                  File[] fList4 = f4.listFiles();
                  for (File file4 : fList4) {
                    if (file4.isDirectory()) {
                      File f5 =
                          new File("plugins/HShop/Shops/" + file.getName() + "/" + file2.getName()
                              + "/" + file3.getName() + "/" + file4.getName());
                      File[] fList5 = f5.listFiles();
                      for (File file5 : fList5) {
                        if (file5.isDirectory()) {
                          File f6 =
                              new File("plugins/HShop/Shops/" + file.getName() + "/"
                                  + file2.getName() + "/" + file3.getName() + "/" + file4.getName()
                                  + "/" + file5.getName() + "/");
                          File[] fList6 = f6.listFiles();
                          for (File file6 : fList6) {
                            if (file6.isFile()) {
                              YamlConfiguration yamlFile =
                                  YamlConfiguration.loadConfiguration(file6);

                              Shop shop =
                                  shopManager.createShop(yamlFile.getString("name"),
                                      yamlFile.getString("owner"), file.getName(),
                                      Integer.valueOf(file4.getName()),
                                      Integer.valueOf(file5.getName()),
                                      Integer.valueOf(file6.getName()), yamlFile.getInt("by"),
                                      yamlFile.getDouble("sell"), yamlFile.getDouble("buy"));
                              
                              shop.setAdmin(yamlFile.getBoolean("adminshop"));
                              shop.setItemCount(yamlFile.getInt("iteminfos.amount"));
                              shopManager.saveShopAdmin(shop);
                              shopManager.saveShopItemCount(shop);

                              if (yamlFile.getString("iteminfos.name") != null) {
                                ItemStack itemRestore =
                                    new ItemStack(Material.getMaterial(yamlFile
                                        .getString("iteminfos.name")), yamlFile.getInt("by"));

                                if (yamlFile.getString("iteminfos.name").equals("SKULL_ITEM")) {

                                  SkullMeta itemRestoreMeta1 =
                                      (SkullMeta) itemRestore.getItemMeta();

                                  itemRestoreMeta1.setDisplayName(yamlFile
                                      .getString("iteminfos.displayname"));

                                  itemRestoreMeta1.setOwner(yamlFile.getString("iteminfos.skull"));



                                }

                                else if (yamlFile.getString("iteminfos.name").equals(
                                    "ENCHANTED_BOOK")) {
                                  @SuppressWarnings("unchecked")
                                  ArrayList<String> ens2 =
                                      (ArrayList<String>) yamlFile
                                          .getList("iteminfos.storedenchants");

                                  EnchantmentStorageMeta itemRestoreMeta2 =
                                      (EnchantmentStorageMeta) itemRestore.getItemMeta();

                                  itemRestoreMeta2.setDisplayName(yamlFile
                                      .getString("iteminfos.displayname"));

                                  for (String s : ens2) {
                                    String[] ss = s.split(":");
                                    itemRestoreMeta2.addStoredEnchant(Enchantment.getByName(ss[0]),
                                        Integer.parseInt(ss[1]), true);
                                    itemRestore.setItemMeta(itemRestoreMeta2);

                                  }
                                } else if (!yamlFile.getString("iteminfos.name").equals(
                                    "ENCHANTED_BOOK")
                                    && !yamlFile.getString("iteminfos.name").equals("SKULL_ITEM")) {

                                  ItemMeta itemRestoreMeta = itemRestore.getItemMeta();

                                  itemRestoreMeta.setDisplayName(yamlFile
                                      .getString("iteminfos.displayname"));

                                  itemRestore.setItemMeta(itemRestoreMeta);
                                }

                                itemRestore.getData().setData(
                                    (byte) Integer.parseInt(yamlFile.getString("iteminfos.data")));


                                itemRestore.setDurability((short) yamlFile
                                    .getInt("iteminfos.durability"));

                                @SuppressWarnings("unchecked")
                                ArrayList<String> ens =
                                    (ArrayList<String>) yamlFile.getList("iteminfos.enchants");
                                HashMap<Enchantment, Integer> enchantmentIntegerMap =
                                    new HashMap<Enchantment, Integer>();
                                for (String s : ens) {
                                  String[] ss = s.split(":");
                                  enchantmentIntegerMap.put(Enchantment.getByName(ss[0]),
                                      Integer.parseInt(ss[1]));
                                }
                                itemRestore.addEnchantments(enchantmentIntegerMap);



                                shop.setItemSerialized(HcGson.serializeItemStack(itemRestore));
                                shopManager.saveShopItemSerialized(shop);


                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }

        }
        p.sendMessage(ChatColor.DARK_PURPLE + "[HShop] " + ChatColor.GREEN + "All datas have been converted!");
      }


      /*
       * YamlConfiguration yamlFile = YamlConfiguration.loadConfiguration(f);
       * shopManager.createShop(player, location, unitAmount, sellPrice, buyPrice)
       */

    }
    return false;
  }
}
