package fr.keke142.hshop.commands;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import fr.keke142.hshop.HShopPlugin;
import fr.keke142.hshop.HcGson;
import fr.keke142.hshop.Lang;
import fr.keke142.hshop.managers.ShopManager;
import fr.keke142.hshop.objects.Shop;

public class ShopCommand implements Listener, CommandExecutor {
    HShopPlugin plugin;
    private ShopManager shopManager;
    
    public ShopCommand(HShopPlugin instance) {
        this.plugin = instance;
        this.shopManager = plugin.getShopManager();
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("hshop")) {
            Player p = (Player) sender;
            Block b = p.getTargetBlock((Set<Material>) null, 500);
            
            Shop shop = shopManager.getShopAt(b.getLocation());
            
            if (shop == null) {
              p.sendMessage(Lang.PREFIX.toString() + Lang.NOSHOP.toString());
              return true;
            }
            
           p.sendMessage(Lang.PREFIX + Lang.SHOPCMD1.toString().replaceAll("%player", shop.getPlayerName()));
           if (shop.isItemSerialized()) {
             ItemStack stack = HcGson.deserializeItemStack(shop.getItemSerialized());
             p.sendMessage(Lang.PREFIX + Lang.SHOPCMD2.toString().replaceAll("%number", "" + shop.getItemCount())
                 .replaceAll("%item", "" + stack.getType())
                 .replaceAll("%data", "" + stack.getData().getData())
                 .replaceAll(
                     "%name",
                     stack.getItemMeta().hasDisplayName() ? stack.getItemMeta().getDisplayName()
                         : "No Item").replaceAll("%durability", "" + stack.getDurability())
                 .replaceAll("%enchants", "" + stack.getEnchantments()));
           }

        }
        return false;
    }
}
