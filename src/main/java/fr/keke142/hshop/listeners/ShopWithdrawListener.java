package fr.keke142.hshop.listeners;

import java.io.IOException;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import fr.keke142.hshop.HShopPlugin;
import fr.keke142.hshop.HcGson;
import fr.keke142.hshop.Lang;
import fr.keke142.hshop.managers.ShopManager;
import fr.keke142.hshop.objects.Shop;

public class ShopWithdrawListener implements Listener {
  private HShopPlugin plugin;
  private ShopManager shopManager;

  public ShopWithdrawListener(HShopPlugin instance) {
    this.plugin = instance;
    this.shopManager = plugin.getShopManager();
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
    if (!p.hasPermission("hshop.manage")) {
      return;
    }

    final Block b = e.getClickedBlock();
    final Shop shop = shopManager.getShopAt(b.getLocation());
    if (shop == null) {
      return;
    }

    if (shop.getAdmin() == false) {
      if (!p.getUniqueId().toString().contentEquals(shop.getPlayerUuid())) {
        return;
      }
    }
    if (shop.getAdmin()) {
      return;
    }

    if (shop.getItemCount() < shop.getUnitAmount()) {
      p.sendMessage(Lang.PREFIX.toString() + Lang.NOMITEM.toString());
      return;
    }

    ItemStack item = HcGson.deserializeItemStack(shop.getItemSerialized());
    item.setAmount(shop.getUnitAmount());
    shop.substractItem();
    shop.setPlayerName(p.getName());
    shopManager.saveShopItemCountAndPlayerName(shop);
    p.getInventory().addItem(item);
    ItemStack stack = HcGson.deserializeItemStack(shop.getItemSerialized());
    p.sendMessage(Lang.PREFIX
        + Lang.SHOPWHITDRAW
            .toString()
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
