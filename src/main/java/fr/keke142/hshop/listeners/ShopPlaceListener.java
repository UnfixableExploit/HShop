package fr.keke142.hshop.listeners;

import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import fr.keke142.hshop.HShopPlugin;
import fr.keke142.hshop.Lang;
import fr.keke142.hshop.managers.ShopManager;

public class ShopPlaceListener implements Listener {
  private HShopPlugin plugin;
  private ShopManager shopManager;

  public ShopPlaceListener(HShopPlugin instance) {
    this.plugin = instance;
    this.shopManager = plugin.getShopManager();
  }

  @EventHandler
  public void onShopPlace(SignChangeEvent e) throws IOException {

    Player p = e.getPlayer();
    if (e.getLine(0).equalsIgnoreCase("[HShop]")) {
      String[] line2split = e.getLine(1).split("/");
      String buy = line2split[0];
      if (!e.getLine(1).contains(buy)) {
        p.sendMessage(Lang.PREFIX.toString() + Lang.DEFINEBUYPRICE);
        return;
      }

      if (!p.hasPermission("hshop.create")) {
        return;
      }

      String sell = line2split[1];

      if (!e.getLine(1).contains(sell)) {
        e.getBlock().breakNaturally();
        p.sendMessage(Lang.PREFIX.toString() + Lang.DEFINESELLPRICE);
        return;
      }
      if (e.getLine(2) == null) {
        e.getBlock().breakNaturally();
        p.sendMessage(Lang.PREFIX.toString() + Lang.DEFINEQUANTITY);
        return;
      }
      if (e.getLine(2).equals("0")) {
        e.getBlock().breakNaturally();
        p.sendMessage(Lang.PREFIX.toString() + Lang.DEFINEGREATER);
        return;
      }
      if (e.getLine(1).contains("-")) {
        e.getBlock().breakNaturally();
        p.sendMessage(Lang.PREFIX.toString() + Lang.NOUSECHAR);
        return;
      }
      if (e.getLine(1).contains("+")) {
        e.getBlock().breakNaturally();
        p.sendMessage(Lang.PREFIX.toString() + Lang.NOUSECHAR);
        return;
      }
      if (e.getLine(2).contains("-")) {
        e.getBlock().breakNaturally();
        p.sendMessage(Lang.PREFIX.toString() + Lang.NOUSECHAR);
        return;
      }
      if (e.getLine(2).contains("+")) {
        e.getBlock().breakNaturally();
        p.sendMessage(Lang.PREFIX.toString() + Lang.NOUSECHAR);
        return;
      }
      if (plugin.isDouble(buy) == false) {
        e.getBlock().breakNaturally();
        p.sendMessage(Lang.PREFIX.toString() + Lang.USEINTORDOUBLEB);
        return;


      }
      if (plugin.isDouble(sell) == false) {
        e.getBlock().breakNaturally();
        p.sendMessage(Lang.PREFIX.toString() + Lang.USEINTORDOUBLES);
        return;
      }

      String by = e.getLine(2);

      if (plugin.isInteger(by) == false) {
        e.getBlock().breakNaturally();
        p.sendMessage(Lang.PREFIX.toString() + Lang.DEFINEQUANTITY);
        return;
      }

      Location b = e.getBlock().getLocation();

      shopManager.createShop(p, b, Integer.valueOf(by), Double.valueOf(sell), Double.valueOf(buy));

      e.setLine(0, Lang.LINE1.toString().replaceAll("%number", by));
      e.setLine(1, Lang.LINE2.toString());
      e.setLine(2, Lang.LINE3.toString().replaceAll("%buy", buy));
      e.setLine(3, Lang.LINE4.toString().replaceAll("%sell", sell));
      p.sendMessage(Lang.PREFIX.toString()
          + Lang.SHOPCREATE.toString().replaceAll("%x", Integer.toString(b.getBlockX()))
              .replaceAll("%y", Integer.toString(b.getBlockY()))
              .replaceAll("%z", Integer.toString(b.getBlockZ()))
              .replaceAll("%world", b.getWorld().getName()));
    }
  }
}
