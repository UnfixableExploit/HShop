package fr.keke142.hshop.commands;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import fr.keke142.hshop.HShopPlugin;
import fr.keke142.hshop.Lang;
import fr.keke142.hshop.managers.ShopManager;
import fr.keke142.hshop.objects.Shop;

public class ShopAdminCommand implements Listener, CommandExecutor {
    HShopPlugin plugin;
    private ShopManager shopManager;
    
    public ShopAdminCommand(HShopPlugin instance) {
        this.plugin = instance;
        this.shopManager = plugin.getShopManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("hshopadmin")) {
            Player p = (Player) sender;
            Block b = p.getTargetBlock((Set<Material>) null, 500);
            
            Shop shop = shopManager.getShopAt(b.getLocation());
            
            if (shop == null) {
              p.sendMessage(Lang.PREFIX.toString() + Lang.NOSHOP.toString());
              return true;
            }
            shop.setAdmin(Boolean.parseBoolean(args[0]));
            shopManager.saveShopAdmin(shop);
            p.sendMessage(Lang.PREFIX.toString() + Lang.TURNEDADMIN.toString().replaceAll("%boolean", "" + Boolean.parseBoolean(args[0])));

        }
        return false;
    }
}
