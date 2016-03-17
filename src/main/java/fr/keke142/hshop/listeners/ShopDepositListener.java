package fr.keke142.hshop.listeners;

import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
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

public class ShopDepositListener implements Listener {
  private HShopPlugin plugin;
  private ShopManager shopManager;

  public ShopDepositListener(HShopPlugin instance) {
    this.plugin = instance;
    this.shopManager = plugin.getShopManager();
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

    final ItemStack handStack = p.getInventory().getItemInMainHand();
    if (handStack.getType() == Material.AIR) {
      return;
    }

    final Block b = e.getClickedBlock();
    final Shop shop = shopManager.getShopAt(b.getLocation());
    if (shop == null) {
      return;
    }

    if (!p.hasPermission("hshop.manage")) {
      p.sendMessage(Lang.PREFIX.toString() + Lang.NOPERMISSION.toString());
      return;
    }


    if (shop.isItemSerialized() && shop.getAdmin()) {
      return;
    }

    if (!p.getUniqueId().toString().contentEquals(shop.getPlayerUuid())) {
      return;
    }

    if (handStack.getAmount() < shop.getUnitAmount()) {
      p.sendMessage(Lang.PREFIX
          + Lang.NOENGOUTHITEM.toString().replaceAll("%number", "" + shop.getUnitAmount()));
      return;
    }

    plugin.getPlace().add(p);
    new BukkitRunnable() {

      @Override
      public void run() {
        plugin.getPlace().remove(p);
      }

    }.runTaskLater(this.plugin, 10);


    final ItemStack shopStack = handStack.clone();
    shopStack.setAmount(shop.getUnitAmount());

    if (!shop.isItemSerialized()) {

      shop.setItemSerialized(HcGson.serializeItemStack(shopStack));
      shopManager.saveShopItemSerialized(shop);
      shop.addItem();
      shopManager.saveShopItemCount(shop);
      if (handStack.getAmount() > shop.getUnitAmount()) {
        handStack.setAmount(handStack.getAmount() - shop.getUnitAmount());
      } else {
        p.getInventory().setItemInMainHand(null);
      }
      final Sign sign = (Sign) e.getClickedBlock().getState();
      sign.setLine(1, ChatColor.GOLD + "" + handStack.getType() + ":"
          + handStack.getData().getData());
      sign.update();
      ItemStack stack = HcGson.deserializeItemStack(shop.getItemSerialized());
      p.sendMessage(Lang.PREFIX
          + Lang.SHOPDEPOSIT
              .toString()
              .replaceAll("%number", "" + shop.getUnitAmount())
              .replaceAll("%item", "" + stack.getType())
              .replaceAll("%data", "" + stack.getData().getData())
              .replaceAll(
                  "%name",
                  stack.getItemMeta().hasDisplayName() ? stack.getItemMeta().getDisplayName()
                      : "No Name").replaceAll("%durability", "" + stack.getDurability())
              .replaceAll("%enchants", "" + stack.getEnchantments()));



    } else {

      String itemSerialized = HcGson.serializeItemStack(shopStack);
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


      if (p.isSneaking()) {
        shop.addItem(p.getInventory().getItemInMainHand().getAmount());
        p.getInventory().setItemInMainHand(null);

      } else {
        shop.addItem();
        if (handStack.getAmount() > shop.getUnitAmount()) {
          handStack.setAmount(handStack.getAmount() - shop.getUnitAmount());
        } else {
          p.getInventory().setItemInMainHand(null);
        }

      }

      shop.setPlayerName(p.getName());
      shopManager.saveShopItemCountAndPlayerName(shop);

      ItemStack stack = HcGson.deserializeItemStack(shop.getItemSerialized());
      p.sendMessage(Lang.PREFIX
          + Lang.SHOPDEPOSIT
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
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent e) {
    plugin.getPlace().remove(e.getPlayer());
  }

  @EventHandler
  public void onBlockPlace(BlockPlaceEvent e) {
    if (plugin.getPlace().contains(e.getPlayer())) {
      e.setCancelled(true);
    }
  }
}
