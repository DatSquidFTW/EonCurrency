package me.squid.eoncurrency.listeners;

import me.squid.eoncurrency.Eoncurrency;
import me.squid.eoncurrency.managers.CoinManager;
import me.squid.eoncurrency.managers.EconomyManager;
import me.squid.eoncurrency.managers.VaultEconManager;
import me.squid.eoncurrency.menus.EcoMenu;
import me.squid.eoncurrency.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class ShopMenuListener implements Listener {

    Eoncurrency plugin;
    EcoMenu ecoMenu = new EcoMenu();
    VaultEconManager vaultEconManager = new VaultEconManager();

    public ShopMenuListener(Eoncurrency plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onMainMenuClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        // Main Menu
        if (e.getView().getTitle().equalsIgnoreCase(Utils.chat("&5&lCurrency Menu"))) {
            switch (e.getCurrentItem().getType()) {
                case EMERALD_BLOCK:
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1, 1);
                    p.openInventory(ecoMenu.ShopCategory());
                    break;
                case ZOMBIE_HEAD:
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1, 1);
                    p.closeInventory();
                    p.sendMessage(Utils.chat("&7[&b&lEonEco&r&7] &aMob Arena Shop Coming Soon!"));
                    break;
            }
            e.setCancelled(true);
        }

        // Balance Top Menu Listener
        if (e.getView().getTitle().equalsIgnoreCase(Utils.chat("&b&lTop Balances"))) e.setCancelled(true);

        // Eon Normal Money Category Selector
        if (e.getView().getTitle().equalsIgnoreCase(Utils.chat("&5&lCategories"))){
            switch (e.getCurrentItem().getType()) {
                case STONE_BRICKS:
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1, 1);
                    p.openInventory(ecoMenu.BlockShop());
                    break;
                case OAK_BOAT:
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1, 1);
                    p.openInventory(ecoMenu.MiscShop());
                    break;
                case HOPPER:
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                    p.openInventory(ecoMenu.HopperShop());
                    break;
            }
            e.setCancelled(true);
        }

        // Blocks Part of the Shop
        if (e.getView().getTitle().equalsIgnoreCase(Utils.chat("&f&lBlocks"))){
            switch (e.getCurrentItem().getType()){
                case SAND:
                case COBBLESTONE:
                case GRAVEL:
                case DIRT:
                case STONE:
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                    p.openInventory(ecoMenu.QuantityMenu(e.getCurrentItem().getType()));
                    break;
            }
            e.setCancelled(true);
        }

        // Misc Shop
        if (e.getView().getTitle().equalsIgnoreCase(Utils.chat("&f&lMisc"))) {
            if (!e.getCurrentItem().getType().equals(Material.BLACK_STAINED_GLASS)) {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                p.openInventory(ecoMenu.QuantityMenu(e.getCurrentItem().getType()));
            }
        }

        // Hopper Shop Menu
        if (e.getView().getTitle().equalsIgnoreCase(Utils.chat("&a&lHopper Shop"))){
            if (e.getCurrentItem().getType().equals(Material.HOPPER)){
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                p.openInventory(ecoMenu.HopperAmount());
                e.setCancelled(true);
            }
        }

        // Hopper Amount Menu
        if (e.getView().getTitle().equalsIgnoreCase(Utils.chat("&a&lHopper Amount"))) {
            switch (e.getCurrentItem().getType()) {
                case GREEN_STAINED_GLASS_PANE:
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                    p.openInventory(ecoMenu.StackMenu(Material.HOPPER));
                    break;
                case HOPPER:
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1, 1);
                    addShopItemToPlayer(p, Material.HOPPER, getPrice(Material.HOPPER), e.getCurrentItem().getAmount());
                    break;
            }
            e.setCancelled(true);
        }

        // Main Amount Menu
        if (e.getView().getTitle().equalsIgnoreCase(Utils.chat("&a&lAmount"))){
            int amount = e.getCurrentItem().getAmount();
            if (e.getCurrentItem().getType() == Material.GREEN_STAINED_GLASS_PANE) {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1, 1);
                p.openInventory(ecoMenu.StackMenu(((e.getClickedInventory()).getItem(12)).getType()));
            } else {
                p.closeInventory();
                addShopItemToPlayer(p, e.getCurrentItem().getType(), getPrice(e.getCurrentItem().getType()), amount);
            }
            e.setCancelled(true);
        }

        // When the player clicks 64, this is the menu that shows up so they can buy lots of stacks
        if (e.getView().getTitle().equalsIgnoreCase(Utils.chat("&a&lStacks"))) {
            for (int i = 0; i <= 8; i++){
                if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase((e.getClickedInventory().getItem(i)).getItemMeta().getDisplayName())) {
                    ItemStack item = new ItemStack(e.getCurrentItem().getType(), 64);

                    if (vaultEconManager.has(p, 64 * (i + 1) * getPrice(item.getType()))){
                        for (int j = 0; j <= i; j++) {
                            p.getInventory().addItem(item);
                        }
                        vaultEconManager.withdrawPlayer(p, 64 * (i + 1) * getPrice(item.getType()));
                        p.sendMessage(Utils.chat("&7[&b&lEonEco&r&7] &aYou have spent $" + (64 * (i + 1) * getPrice(item.getType()))));
                    } else {
                        p.sendMessage(Utils.chat("&7[&b&lEonEco&r&7] &4Insufficient Funds"));
                    }
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1, 1);
                    p.closeInventory();
                    break;
                }
            }
            e.setCancelled(true);
        }
    }

    // Method to compact the code above
    public void addShopItemToPlayer(Player p, Material material, double price, int amount) {
        ItemStack item;
        item = new ItemStack(material, amount);
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1, 1);
        if (vaultEconManager.has(p, price * amount)) {
            vaultEconManager.withdrawPlayer(p, price * amount);
            p.sendMessage(Utils.chat("&7[&b&lEonEco&r&7] &aYou have spent $" + price * amount));
            p.getInventory().addItem(item);
        } else {
            p.closeInventory();
            p.sendMessage(Utils.chat("&7[&b&lEonEco&r&7] &4Insufficient Funds"));
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1, 1);
        }
    }

    public double getPrice(Material material) {
        double price = 1;
        switch (material){
            case HOPPER:
                price = 500;
                break;
            case END_CRYSTAL:
                price = 1000;
                break;
            case STONE:
                price = 1.5;
                break;
            case GRAVEL:
            case DIRT:
            case COBBLESTONE:
                price = 1;
                break;
            case SAND:
                price = 2;
                break;
            case PHANTOM_MEMBRANE:
                price = 100;
                break;
            case ITEM_FRAME:
            case SHULKER_SHELL:
                price = 750;
                break;
        }
        return price;
    }
}
