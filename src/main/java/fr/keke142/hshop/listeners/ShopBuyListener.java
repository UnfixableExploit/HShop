package fr.keke142.hshop.listeners;

import java.io.IOException;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import fr.keke142.hshop.HShopPlugin;
import fr.keke142.hshop.HcGson;
import fr.keke142.hshop.Lang;
import fr.keke142.hshop.managers.ShopManager;
import fr.keke142.hshop.objects.Shop;

public class ShopBuyListener implements Listener {
  private HShopPlugin plugin;
  private ShopManager shopManager;
  private Economy economy;

  public ShopBuyListener(HShopPlugin instance) {
    this.plugin = instance;
    this.shopManager = plugin.getShopManager();
    this.economy = plugin.getEconomy();
  }

  @SuppressWarnings("deprecation")
  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent e) throws IOException {
    if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    if (!(e.getClickedBlock().getType() == Material.WALL_SIGN
        || e.getClickedBlock().getType() == Material.SIGN_POST || e.getClickedBlock().getType() == Material.SIGN)) {
      return;
    }

    final Player p = e.getPlayer();
    if (!p.hasPermission("hshop.buy")) {
      return;
    }

    final Block b = e.getClickedBlock();
    final Shop shop = shopManager.getShopAt(b.getLocation());
    if (shop == null) {
      return;
    }

    if (shop.getAdmin() == false) {
      if (p.getUniqueId().toString().contentEquals(shop.getPlayerUuid())) {
        return;
      }
    }

    if (shop.getItemCount() < shop.getUnitAmount()) {
      p.sendMessage(Lang.PREFIX.toString() + Lang.NOMITEM.toString());
      return;
    }

    if (p.getInventory().firstEmpty() == -1) {
      p.sendMessage(Lang.PREFIX.toString() + Lang.INVENTORYFULL.toString());
      return;
    }

    ItemStack stack = HcGson.deserializeItemStack(shop.getItemSerialized());

    EconomyResponse r = economy.withdrawPlayer(p.getPlayer(), shop.getBuyPrice());
    if (r.transactionSuccess()) {
      if (shop.getAdmin()) {
        giveItem(p, shop, stack);
        return;
      }
      EconomyResponse r2 = economy.depositPlayer(shop.getPlayerName(), shop.getBuyPrice());
      if (r2.transactionSuccess()) {
        shop.substractItem();
        shopManager.saveShopItemCount(shop);
        giveItem(p, shop, stack);
      }

    } else {
      p.sendMessage(Lang.PREFIX.toString() + Lang.NOMONEY.toString());
    }

    Player t = Bukkit.getPlayer(shop.getPlayerName());
    if (shop.getAdmin() == false) {
      if (t != null) {
        t.sendMessage(Lang.PREFIX.toString()
            + Lang.SHOPBUYCREATOR
                .toString()
                .replaceAll("%buyer", p.getName())
                .replaceAll("%number", "" + shop.getUnitAmount())
                .replaceAll("%item", "" + stack.getType())
                .replaceAll("%data", "" + stack.getData().getData())
                .replaceAll(
                    "%name",
                    stack.getItemMeta().hasDisplayName() ? stack.getItemMeta().getDisplayName()
                        : "No Item").replaceAll("%durability", "" + stack.getDurability())
                .replaceAll("%enchants", "" + stack.getEnchantments()));

      }
    }
  }

  public void giveItem(final Player p, final Shop shop, final ItemStack stack) {

    stack.setAmount(shop.getUnitAmount());

    new BukkitRunnable() {
      @SuppressWarnings("deprecation")
      @Override
      public void run() {
        p.getInventory().addItem(stack);
        p.sendMessage(Lang.PREFIX.toString()
            + Lang.SHOPBUY
                .toString()
                .replaceAll("%number", "" + shop.getUnitAmount())
                .replaceAll("%item", "" + stack.getType())
                .replaceAll("%data", "" + stack.getData().getData())
                .replaceAll(
                    "%name",
                    stack.getItemMeta().hasDisplayName() ? stack.getItemMeta().getDisplayName()
                        : "No Item").replaceAll("%durability", "" + stack.getDurability())
                .replaceAll("%enchants", "" + stack.getEnchantments()));

      }
    }.runTask(plugin);
  }
}
