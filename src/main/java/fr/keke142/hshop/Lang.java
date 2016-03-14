package fr.keke142.hshop;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

public enum Lang {
    PREFIX("prefix", "&5[HShop]"),
    FAILMETRICS("shop-failmetrics", "&cFailed to load Metrics."),
    FAILVAULT("shop-failvault", "&cFailed to load Vault."),
    TRYINGPOOL("shop-tryingpool", "&cTrying to enable database connection pool..."),
    NOPERMISSION("shop-nopermission", "&cYou do not have permission."),
    SHOPCMD1("shop-cmd1", "&aThis shop belongs to &6%player."),
    SHOPCMD2("shop-cmd2", "&aIt contains &6%number %item:%data &anamed: &6%name &awith the durability &6%durability &aand enchantments: &6%enchants."),
    SHOPDEPOSIT("shop-deposit", "&a+ &6%number %item:%data &anamed: &6%name &awith the durability &6%durability &aand enchantments: &6%enchants."),
    SHOPWHITDRAW("shop-whitdraw", "&c- &6%number %item:%data &cnamed: &6%name &cwith the durability &6%durability &cand enchantments: &6%enchants."),
    SHOPDEPOSITALL("shop-deposital", "&a+ &6All %item:%data &anamed: &6%name &awith the durability &6%durability &aand enchantments: &6%enchants."),
    SHOPWHITDRAWALL("shop-whitdrawall", "&c- &6All %item:%data &cnamed: &6%name &cwith the durability &6%durability &cand enchantments: &6%enchants."),
    SHOPBUY("shop-buy", "&aYou bought &6%number %item:%data &anamed: &6%name &awith the durability &6%durability &aand enchantments: &6%enchants."),
    SHOPSELL("shop-sell", "&aYou sold &6%number %item:%data &anamed: &6%name &awith the durability &6%durability &aand enchantments: &6%enchants."),
    SHOPBUYCREATOR("shop-buycreator", "&a%buyer bought &6%number %item:%data &anamed: &6%name &awith the durability &6%durability &aand enchantments: &6%enchants &ain your shop."),
    SHOPSELLCREATOR("shop-sellcreator", "&a%seller has sold &6%number %item:%data &anamed: &6%name &awith the durability &6%durability &aand enchantments: &6%enchants &ain your shop."),
    SHOPCREATE("shop-create", "&aYou create a shop to coordinates &6%x %y %z &ain the world &6%world."),
    SHOPDESTROY("shop-destroy", "&aYou destroyed your shop to the coordinates &6%x %y %z &ain the world &6%world."),
    NOMONEY("shop-nomoney", "&cYou do not have enough money."),
    NOMONEYOTHER("shop-nomoneyother", "&c%player does not have enough money."),
    NOMITEM("shop-noitem", "&cYour shop does not have enough items to take."),
    TURNEDADMIN("shop-turnedadmin", "&aAdminShop have been set to: %boolean for the targeted shop."),
    NOSHOP("shop-noshop", "&cYou should aim for a shop."),
    DEFINEBPRICE("shop-dbprice", "&cPlease set a buy price."),
    USEINTORDOUBLEB("shop-useintordoubleb", "&cPlease use double or integer for buy price."),
    USEINTORDOUBLES("shop-useintordoubles", "&cPlease use double or integer for sell price."),
    NOUSECHAR("shop-nousechar", "&cThis type of character is not allowed."),
    DEFINEBUYPRICE("shop-definebuyprice", "&cPlease define buy price."),
    DEFINESELLPRICE("shop-definesellprice", "&cPlease define sell price."),
    DEFINEQUANTITY("shop-definequantity", "&cPlease define quantity."),
    DEFINEGREATER("shop-definegreater", "&cThank you define a quantity greater than 0."),
    NOENGOUTHITEM("shop-noengouthitem", "&cYou must have x%number this item."),
    SHOPRELOAD("shop-reload", "&aReload request have been send."),
    INVALIDITEM("shop-invaliditem", "&cYou must have &6%number %item:%data &cnamed: &6%name &cwith the durability &6%durability &cand enchantments: &6%enchants."),
    LINE1("signs.line1", "&8B/S by %number."),
    LINE2("signs.line2", "&6Empty."),
    LINE3("signs.line3", "&aB: %buy$."),
    LINE4("signs.line4", "&cS: %sell$");
    
    private String path;
    private String def;
    private static YamlConfiguration LANG;
 
    Lang(String path, String start) {
        this.path = path;
        this.def = start;
    }

    public static void setFile(YamlConfiguration config) {
        LANG = config;
    }
 
    @Override
    public String toString() {
        if (this == PREFIX)
            return ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, def)) + " ";
        return ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, def));
    }
 
    public String getDefault() {
        return this.def;
    }
 
    public String getPath() {
        return this.path;
    }
}
