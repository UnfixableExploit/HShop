package fr.keke142.hshop.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Sign;

import fr.keke142.hshop.HShopPlugin;
import fr.keke142.hshop.HcGson;
import fr.keke142.hshop.Lang;
import fr.keke142.hshop.managers.ShopManager;
import fr.keke142.hshop.objects.Shop;

public class ShopDestroyListener implements Listener {
  private HShopPlugin plugin;
  private ShopManager shopManager;

  public ShopDestroyListener(HShopPlugin instance) {
    this.plugin = instance;
    this.shopManager = plugin.getShopManager();
  }

  @EventHandler
  public void onBlockBreak(BlockBreakEvent e) {
    Block b = e.getBlock();
    Player p = e.getPlayer();
    Shop shop = shopManager.getShopAt(b.getLocation());
    if (shop == null) {
      return;
    }
    if (!p.hasPermission("hshop.create")) {
      return;
    }
    shopManager.destroyShop(shop);
    e.getPlayer().sendMessage(
        Lang.PREFIX.toString()
            + Lang.SHOPDESTROY.toString().replaceAll("%x", Integer.toString(b.getX()))
                .replaceAll("%y", Integer.toString(b.getY()))
                .replaceAll("%z", Integer.toString(b.getZ()))
                .replaceAll("%world", b.getWorld().getName()));
    ItemStack stack = HcGson.deserializeItemStack(shop.getItemSerialized());
    stack.setAmount(shop.getItemCount());
    if (shop.getItemCount() <= 0) {
      return;
    }
    Bukkit.getWorld(shop.getWorldName()).dropItemNaturally(b.getLocation(), stack);

  }

  @EventHandler
  public void onBlockPhysics(BlockPhysicsEvent e) {
    Block b = e.getBlock();
    Shop shop = shopManager.getShopAt(b.getLocation());
    if (shop == null) {
      return;
    }
    if (b.getType() == Material.WALL_SIGN || b.getType() == Material.SIGN_POST) {
      Sign s = (Sign) b.getState().getData();
      Block attachedBlock = b.getRelative(s.getAttachedFace());
      if (attachedBlock.getType() == Material.AIR) {
        shopManager.destroyShop(shop);
        ItemStack stack = HcGson.deserializeItemStack(shop.getItemSerialized());
        stack.setAmount(shop.getItemCount());
        if (shop.getItemCount() <= 0) {
          return;
        }
        Bukkit.getWorld(shop.getWorldName()).dropItemNaturally(b.getLocation(), stack);

      }
    }
  }

}
