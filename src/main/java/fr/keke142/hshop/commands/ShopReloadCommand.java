package fr.keke142.hshop.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import fr.keke142.hshop.HShopPlugin;
import fr.keke142.hshop.Lang;
import fr.keke142.hshop.managers.LangManager;

public class ShopReloadCommand implements Listener, CommandExecutor {
    HShopPlugin plugin;
    private LangManager langManager;

    public ShopReloadCommand(HShopPlugin instance) {
        this.plugin = instance;
        this.langManager = plugin.getLangManager();
        
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("hshopreload")) {
            Player p = (Player) sender;
            plugin.reloadConfig();
            langManager.loadLang();
            p.sendMessage(Lang.PREFIX.toString() + Lang.SHOPRELOAD.toString());

        }
        return false;
    }
}
