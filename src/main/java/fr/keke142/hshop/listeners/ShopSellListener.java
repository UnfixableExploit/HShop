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
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import fr.keke142.hshop.HShopPlugin;
import fr.keke142.hshop.HcGson;
import fr.keke142.hshop.Lang;
import fr.keke142.hshop.managers.ShopManager;
import fr.keke142.hshop.objects.Shop;

public class ShopSellListener implements Listener {
  private HShopPlugin plugin;
  private ShopManager shopManager;
  private Economy economy;

  public ShopSellListener(HShopPlugin instance) {
    this.plugin = instance;
    this.shopManager = plugin.getShopManager();
    this.economy = plugin.getEconomy();
  }

  @SuppressWarnings("deprecation")
  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent e) throws IOException {
    if (e.getAction() != Action.LEFT_CLICK_BLOCK) {
      return;
    }

    if (!(e.getClickedBlock().getType() == Material.WALL_SIGN
        || e.getClickedBlock().getType() == Material.SIGN_POST || e.getClickedBlock().getType() == Material.SIGN)) {
      return;
    }

    final Player p = e.getPlayer();
    if (!p.hasPermission("hshop.sell")) {
      return;
    }

    final ItemStack handStack = p.getInventory().getItemInMainHand();
    if (handStack.getType() == Material.AIR) {
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
    
    final ItemStack shopStackSell = handStack.clone();
    shopStackSell.setAmount(shop.getUnitAmount());
    String itemSerialized = HcGson.serializeItemStack(shopStackSell);
    if (!itemSerialized.contentEquals(shop.getItemSerialized())) {
      ItemStack stack = HcGson.deserializeItemStack(shop.getItemSerialized());
      p.sendMessage(Lang.PREFIX
          + Lang.INVALIDITEM
              .toString()
              .replaceAll("%number", "" + shop.getUnitAmount())
              .replaceAll("%item", "" + stack.getType())
              .replaceAll("%data", "" + stack.getData().getData())
              .replaceAll(
                  "%name",
                  stack.getItemMeta().hasDisplayName() ? stack.getItemMeta().getDisplayName()
                      : "No Name").replaceAll("%durability", "" + stack.getDurability())
              .replaceAll("%enchants", "" + stack.getEnchantments()));
      return;
    }
    
    plugin.place.add(p);
    new BukkitRunnable() {

      @Override
      public void run() {
        plugin.place.remove(p);
      }

    }.runTaskLater(this.plugin, 10);

    final ItemStack shopStack = HcGson.deserializeItemStack(shop.getItemSerialized());
    if (!itemSerialized.contentEquals(shop.getItemSerialized())) {
      p.sendMessage(Lang.PREFIX
          + Lang.INVALIDITEM
              .toString()
              .replaceAll("%number", "" + shop.getUnitAmount())
              .replaceAll("%item", "" + shopStack.getType())
              .replaceAll("%data", "" + shopStack.getData().getData())
              .replaceAll(
                  "%name",
                  shopStack.getItemMeta().hasDisplayName() ? shopStack.getItemMeta()
                      .getDisplayName() : "No Name")
              .replaceAll("%durability", "" + shopStack.getDurability())
              .replaceAll("%enchants", "" + shopStack.getEnchantments()));
      return;
    }

    if (handStack.getAmount() < shop.getUnitAmount()) {
      p.sendMessage(Lang.PREFIX + Lang.NOENGOUTHITEM.toString());
      return;
    }

    if (!shop.isItemSerialized()) {
      return;
    }

    if (shop.getAdmin()) {
      EconomyResponse r = economy.depositPlayer(p, shop.getSellPrice());
      if (r.transactionSuccess()) {
        sellSucess(p, shop, handStack);
      }
      return;
    }

    EconomyResponse r2 = economy.withdrawPlayer(shop.getPlayerName(), shop.getSellPrice());
    if (r2.transactionSuccess()) {
      EconomyResponse r = economy.depositPlayer(p, shop.getSellPrice());
      if (r.transactionSuccess()) {

        shop.addItem();
        shopManager.saveShopItemCount(shop);

        sellSucess(p, shop, handStack);
      }
    }

    Player t = Bukkit.getPlayer(shop.getPlayerName());
    if (t != null) {
      ItemStack stack = HcGson.deserializeItemStack(shop.getItemSerialized());
      t.sendMessage(Lang.PREFIX.toString()
          + Lang.SHOPSELLCREATOR
              .toString()
              .replaceAll("%seller", p.getName())
              .replaceAll("%number", "" + shop.getUnitAmount())
              .replaceAll("%item", "" + stack.getType())
              .replaceAll("%data", "" + stack.getData().getData())
              .replaceAll(
                  "%name",
                  stack.getItemMeta().hasDisplayName() ? stack.getItemMeta().getDisplayName()
                      : "No Name").replaceAll("%durability", "" + stack.getDurability())
              .replaceAll("%enchants", "" + stack.getEnchantments()));

    }
  }

  @SuppressWarnings("deprecation")
  public void sellSucess(Player p, Shop shop, ItemStack handStack) {
    if (handStack.getAmount() > shop.getUnitAmount()) {
      handStack.setAmount(handStack.getAmount() - shop.getUnitAmount());
    } else {
      p.getInventory().setItemInMainHand(null);
    }
    p.sendMessage(Lang.PREFIX.toString()
        + Lang.SHOPSELL
            .toString()
            .replaceAll("%number", "" + shop.getUnitAmount())
            .replaceAll("%item", "" + handStack.getType())
            .replaceAll("%data", "" + handStack.getData().getData())
            .replaceAll(
                "%name",
                handStack.getItemMeta().hasDisplayName() ? handStack.getItemMeta().getDisplayName()
                    : "No Name").replaceAll("%durability", "" + handStack.getDurability())
            .replaceAll("%enchants", "" + handStack.getEnchantments()));

  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent e) {
    plugin.place.remove(e.getPlayer());
  }

  @EventHandler
  public void onBlockPlace(BlockPlaceEvent e) {
    if (plugin.place.contains(e.getPlayer())) {
      e.setCancelled(true);
    }
  }
}
